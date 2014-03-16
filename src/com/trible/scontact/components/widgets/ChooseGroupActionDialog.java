package com.trible.scontact.components.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.trible.scontact.R;

public class ChooseGroupActionDialog {
	
	PopupDialogger dialogger;

	Context mContext;
	String mTitleString;
	View contentView;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  ChooseGroupActionDialog(Context context,String title) {
		mContext = context;
		mTitleString = title;
		contentView = createContentView();
	}
	
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_choose_group_action, null);
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(mTitleString);
		dialogger.showDialog(mContext,contentView);
	}
}
