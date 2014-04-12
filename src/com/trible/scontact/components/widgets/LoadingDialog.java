package com.trible.scontact.components.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;

public class LoadingDialog {
	
	PopupDialogger dialogger;

	Context mContext;
	View contentView;
	TextView mTipTextView;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  LoadingDialog(Context context) {
		mContext = context;
		contentView = createContentView();
	}
	
	private View createContentView(){
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_loading, null);
		mTipTextView = (TextView) view.findViewById(R.id.loading_tip_content);
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(null);
		dialogger.showDialog(mContext,contentView);
	}
	public void showAndDoTask(AsynTaskListner l){
		dialogger.setTitleText(null);
		final SimpleAsynTask task = new SimpleAsynTask();
		task.doTask(l);
		dialogger.showDialog(mContext,contentView,new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				task.cancelTask();
			}
		});
	}
	public void setTipText(String text){
		if ( mTipTextView == null )return;
		mTipTextView.setText(text);
	}
	public void setTipText(int textId){
		mTipTextView.setText(textId);
	}
}
