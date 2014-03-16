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
	
	public CustomPasswordInput(View rootView) {
//		mContext = rootView.getContext();
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

	public EditText getmEditText() {
		return mEditText;
	}

	public CheckBox getCheckBox() {
		return mBox;
	}
	
}
