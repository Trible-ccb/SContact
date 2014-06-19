package com.trible.scontact.components.widgets;

import com.trible.scontact.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PropertyKeyValue {

	public TextView mKey,mValue;
	public ImageView mNextIcon;
	public View mRoot;
	
	/**@{layout_property_key_value.xml} */
	public PropertyKeyValue(View v){
		mKey = (TextView) v.findViewById(R.id.property_key);
		mValue = (TextView) v.findViewById(R.id.property_value);
		mNextIcon = (ImageView) v.findViewById(R.id.next_icon);
		setKeyText("");
		setValueText("");
		mRoot = v;
	}
	
	public void setKeyText(CharSequence txt){
		mKey.setText(txt);
	}
	public void setValueText(CharSequence txt){
		mValue.setText(txt);
	}
	public void hideNextIcon(){
		mNextIcon.setVisibility(View.INVISIBLE);
	}
	public void setOnClickListener(OnClickListener l ){
		mRoot.setOnClickListener(l);
	}
}
