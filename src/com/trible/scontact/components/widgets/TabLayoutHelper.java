package com.trible.scontact.components.widgets;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class TabLayoutHelper implements OnClickListener{

	Context mContext;
	ViewGroup mViewGroup;
	OnItemClickListener mChildListner;
	
	int mDefaultSelection = -1;
	
	public TabLayoutHelper(ViewGroup vg){
		mViewGroup = vg;
		int childs = vg.getChildCount();
		for ( int i = 0 ; i < childs ; i++ ){
			View v = vg.getChildAt(i);
			v.setOnClickListener(this);
			v.setTag(i);
		}
	}
	
	public TabLayoutHelper(ViewGroup tabContrainer,int[] dataResId){
		this(tabContrainer);
		String[] ret = new String[dataResId.length];
		for ( int i = 0 ; i < dataResId.length ; i++ ){
			String s = tabContrainer.getResources().getString(dataResId[i]);
			ret[i] = s;
		}
		updateWithText(ret);
	}
	
	public TabLayoutHelper(ViewGroup vg,String[] dataText){
		this(vg);
		updateWithText(dataText);
	}
	void updateWithText(String[] texts){
		if ( texts == null || texts.length == 0 )return;
		for ( int i = 0 ; i < mViewGroup.getChildCount() ; i++ ){
			TextView v = (TextView) mViewGroup.getChildAt(i);
			v.setText(texts[i]);
		}
	}
	
	public void setDefaultSelection(int i){
		if ( i < 0 || i >= mViewGroup.getChildCount() )return;
		mDefaultSelection = i;
		resetChilds();
		mViewGroup.getChildAt(i).performClick();
	}
	@Override
	public void onClick(View v) {
		if ( !v.isSelected() ){
			resetChilds();
			v.setSelected(true);
			if ( mChildListner != null ){
				mChildListner.onItemClick(null, v, (Integer)v.getTag(), v.getId());
			}
		}
	}
	
	private void resetChilds(){
		int childs = mViewGroup.getChildCount();
		for ( int i = 0 ; i < childs ; i++ ){
			View v = mViewGroup.getChildAt(i);
			v.setSelected(false);
		}
	}
	public void setOnItemClickListner(OnItemClickListener l){
		mChildListner = l;
	}
}
