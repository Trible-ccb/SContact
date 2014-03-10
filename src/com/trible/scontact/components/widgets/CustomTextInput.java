package com.trible.scontact.components.widgets;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.trible.scontact.R;

public class CustomTextInput implements TextWatcher , IInputEditedListner{

	Activity mContext;
	EditText mEditText;
	ImageButton mButton;
	View mRoot,mInputLine;
	TextView mHintView;
	
	boolean hadEdited = false;
	
	public CustomTextInput(View rootView) {
//		mContext = context;
		mRoot = rootView;
		mEditText = (EditText)rootView.findViewById(R.id.edt_input_box);
		mButton = (ImageButton)rootView.findViewById(R.id.edt_right_btn);
		mHintView = (TextView) rootView.findViewById(R.id.edit_hint_view);
		mInputLine = rootView.findViewById(R.id.edt_input_line);
		mEditText.addTextChangedListener(this);
	}
	public void setSingleLine(boolean f){
		mEditText.setSingleLine(f);
	}
	public EditText getmEditText() {
		return mEditText;
	}

	public ImageButton getImageButton() {
		return mButton;
	}
	
	public void showRootBackground(){
//		mRoot.setBackgroundResource(R.drawable.selector_edittext_background);
		mInputLine.setVisibility(View.VISIBLE);
	}
	public void hideRootBackground(){
//		mRoot.setBackgroundColor(mRoot.getResources().getColor(R.color.transparent));
		mInputLine.setVisibility(View.INVISIBLE);
	}
	public View getmInputLine() {
		return mInputLine;
	}
	public void setmInputLine(View mInputLine) {
		this.mInputLine = mInputLine;
	}
	public void show(){
		mRoot.setVisibility(View.VISIBLE);
	}
	public void hide(){
		mRoot.setVisibility(View.GONE);
	}
	public void hideRightButton(){
		mButton.setVisibility(View.INVISIBLE);
	}
	public void setHintText(String str){
		mHintView.setText(str);
	}
	public void setHintText(int resId){
		mHintView.setText(resId);
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if ( TextUtils.isEmpty(s.toString()) ){
			hadEdited = false;
		} else {
			hadEdited = true;
		}
	}
	@Override
	public boolean isEditedAndNotNull() {
		return hadEdited;
	}
}
