package com.trible.scontact.components.widgets;

import com.trible.scontact.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PropertyKeyValue {

	public TextView mKey,mValue;
	public View mRoot;
	
	/**@{layout_property_key_value.xml} */
	public PropertyKeyValue(View v){
		mKey = (TextView) v.findViewById(R.id.property_key);
		mValue = (TextView) v.findViewById(R.id.property_value);
		mRoot = v;
	}
	
	public void setKeyText(String txt){
		mKey.setText(txt);
	}
	public void setValueText(String txt){
		mValue.setText(txt);
	}
	
	public void setOnClickListener(OnClickListener l ){
		mRoot.setOnClickListener(l);
	}
}
