package com.trible.scontact.components.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.trible.scontact.R;
import com.trible.scontact.components.adpater.ContactTypeSpinnerAdapter;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.components.widgets.SimpleInputDialog.OnSubmitInputListener;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ContactTypes;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;

public class AddUpdateContactDialog{
	
	PopupDialogger dialogger;
	
	Context mContext;
	View contentView;
	Spinner mTypeSpinner;
	EditText mContactEditText;
	public Button mSubmit;
	ContactTypeSpinnerAdapter mTypeAdapter;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}
	public void clear(){
		mContactEditText.setText(null);
	}
	public  AddUpdateContactDialog(Context context) {
		mContext = context;
		contentView = createContentView();
	}
	
	public void setSelectType(String type,String contact){
		mTypeAdapter.setSelectedType(type);
		mContactEditText.setText(contact);
	}
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_add_update_contact, null);
		mTypeSpinner = (Spinner) view.findViewById(R.id.contact_type);
		mSubmit = (Button) view.findViewById(R.id.add_contact_btn);
		mContactEditText = (EditText) view.findViewById(R.id.contact_name);
		dialogger = PopupDialogger.createDialog(mContext);
		dialogger.setUseNoneScrollRootViewId();
		mTypeAdapter = new ContactTypeSpinnerAdapter(mTypeSpinner);
		mTypeSpinner.setAdapter(mTypeAdapter);
		mTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				new TypeHandler(mContext) {
					@Override
					protected void onCustom() {
						mTypeSpinner.setSelection(0);
						final SimpleInputDialog inputdialog = new SimpleInputDialog(mContext);
						inputdialog.getDialog().setTitleText(mContext.getString(R.string.custom));
						inputdialog.mEditText.setHint(R.string.hint_input_type);
						inputdialog.setmListener(new OnSubmitInputListener() {
							@Override
							public void onSubmit(String input) {
								if ( TextUtils.isEmpty(input) ){
									Bog.toast(R.string.pls_input_correct);
								} else if ( ContactTypes.getInstance().getCustomType().equals(input) ){
									Bog.toast(R.string.pls_input_others);
								} else {
									ContactTypes.getInstance().addType(input);
									mTypeAdapter.refresh();
									inputdialog.getDialog().dismissDialogger();
									mTypeAdapter.setSelectedType(input);
								}
							}
						});
						inputdialog.show();
					}
				}.handle(mTypeAdapter.getSelected());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = mContactEditText.getText().toString();
				if ( !StringUtil.isValidName(name) ){
					Bog.toast(R.string.pls_input_correct);
					return;
				}
				ContactInfo info = new ContactInfo();
				info.setUserId(AccountInfo.getInstance().getId());
				info.setContact(name);
				info.setType(mTypeAdapter.getSelected());
				if ( mListener != null ){
					mListener.onSubmit(info);
				}
			}
		});
		return view;
	}
	public void show(){
		dialogger.showDialog(mContext,contentView);
	}

	OnSubmitContactListener mListener;
	public void setmListener(OnSubmitContactListener mListener) {
		this.mListener = mListener;
	}
	public interface OnSubmitContactListener{
		void onSubmit(ContactInfo info);
	}
	
}
