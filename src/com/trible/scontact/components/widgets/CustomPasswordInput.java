package com.trible.scontact.components.widgets;

import android.app.Activity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.trible.scontact.R;
import com.trible.scontact.utils.InputUtil;

public class CustomPasswordInput {

	Activity mContext;
	EditText mEditText;
	CheckBox mBox;
	View mRoot;
	
	public CustomPasswordInput(View rootView) {
//		mContext = rootView.getContext();
		mRoot = rootView;
		mEditText = (EditText) rootView.findViewById(R.id.edt_input_password);
		mBox = (CheckBox) rootView.findViewById(R.id.checkbox);
		mBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked ){
					mEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
					mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				InputUtil.moveInputToEnd(mEditText);
			}
		});
	}

	public View getmRoot() {
		return mRoot;
	}

	public void setmRoot(View mRoot) {
		this.mRoot = mRoot;
	}
	public void show(){
		getmRoot().setVisibility(View.VISIBLE);
	}
	public void hide(){
		getmRoot().setVisibility(View.GONE);
	}
	public EditText getmEditText() {
		return mEditText;
	}

	public CheckBox getCheckBox() {
		return mBox;
	}
	
}
