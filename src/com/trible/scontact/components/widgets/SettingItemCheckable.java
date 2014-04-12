package com.trible.scontact.components.widgets;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.trible.scontact.R;

public class SettingItemCheckable {

	View mRoot,mSettingItem;
	CheckBox mCheckBox;
	TextView titleView;
	TextView hintView;
	
	/**
	 * @param itemRoot {@link setting_layout_item_checkable.xml}
	 */
	public SettingItemCheckable(View itemRoot){
		mRoot = itemRoot;
		mSettingItem = mRoot.findViewById(R.id.setting_item);
		titleView = (TextView) mRoot.findViewById(R.id.setting_item_checkabel_title);
		hintView = (TextView) mRoot.findViewById(R.id.setting_item_checkabel_hint);
		mCheckBox = (CheckBox) mRoot.findViewById(R.id.settings_checkbox);
		mCheckBox.setVisibility(View.GONE);
		
		//default listener
		mSettingItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleCheck();
			}
		});
	}
	
	public void setOnclickListner(OnClickListener l){
		mSettingItem.setOnClickListener(l);
	}
	public void setOnCheckedChangeListener(OnCheckedChangeListener l){
		mCheckBox.setOnCheckedChangeListener(l);
		if ( l != null ){
			mCheckBox.setVisibility(View.VISIBLE);
		} else {
			mCheckBox.setVisibility(View.GONE);
		}
	}
	public void setTitle(int resid){
		titleView.setText(resid);
	}
	public void setTitle(CharSequence s){
		titleView.setText(s);
	}
	public void setHintText(int resid){
		setHintText(hintView.getResources().getString(resid));
	}
	public void setHintText(CharSequence s){
		if ( TextUtils.isEmpty(s) ){
			hintView.setVisibility(View.GONE);
		} else {
			hintView.setVisibility(View.VISIBLE);
			hintView.setText(s);
		}
	}
	public boolean getCheck(){
		return mCheckBox.isChecked();
	}
	public void setCheck(boolean isCheck){
		mCheckBox.setChecked(isCheck);
	}
	public void toggleCheck(){
		mCheckBox.setChecked(!getCheck());
	}
	public void setVisibility(int v){
		mRoot.setVisibility(v);
	}
}
