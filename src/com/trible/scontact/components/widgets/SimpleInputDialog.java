package com.trible.scontact.components.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.trible.scontact.R;

public class SimpleInputDialog{
	
	PopupDialogger dialogger;
	
	Context mContext;
	View contentView;
	public EditText mEditText;
	public Button mSubmit;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}
	public void clear(){
		mEditText.setText(null);
	}
	public  SimpleInputDialog(Context context) {
		mContext = context;
		contentView = createContentView();
	}
	
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_simple_input, null);
		mSubmit = (Button) view.findViewById(R.id.add_contact_btn);
		mEditText = (EditText) view.findViewById(R.id.contact_name);
		dialogger = PopupDialogger.createDialog(mContext);
		dialogger.setUseNoneScrollRootViewId();
		mSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = mEditText.getText().toString();
				if ( mListener != null ){
					mListener.onSubmit(name);
				}
			}
		});
		return view;
	}
	public void show(){
		dialogger.showDialog(mContext,contentView);
	}

	OnSubmitInputListener mListener;
	public void setmListener(OnSubmitInputListener mListener) {
		this.mListener = mListener;
	}
	public interface OnSubmitInputListener{
		void onSubmit(String input);
	}
	
}
