package com.trible.scontact.components.widgets;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trible.scontact.R;

/**
 * @author chenchuibo
 *自定义提示框
 */
public class PopupDialogger extends Dialog implements AnimationListener{

	Context mContext;
	boolean cancelable = true;

	RelativeLayout popupBase;
	LinearLayout popupContainer;
//	LinearLayout popupAnim;
	TextView mTitleTextView;
	Animation outerAnim,innerAnim;
	OnDismissListener mDismisslistener;//also be a cancel listener
	


	PopupStatus popupStatus;
	String mTitle;
	
	int rootViewId = R.layout.popup_base_scroll;
	
	public void setUseNoneScrollRootViewId() {
		this.rootViewId = R.layout.popup_base;
	}

	
	public OnDismissListener getmDismisslistener() {
		return mDismisslistener;
	}

	public void setmDismisslistener(OnDismissListener mDismisslistener) {
		this.mDismisslistener = mDismisslistener;
	}
	
	public enum PopupStatus{
		kPopup_showing,
		kPopup_dismiss,
		kPopup_create,
		kPopup_closing,
		
	}
	public boolean isCancelable() {
		return cancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		super.setCancelable(cancelable);
		super.setCanceledOnTouchOutside(cancelable);
	}
	public PopupDialogger(Context context) {
		super(context);
		mContext = context;
	}

	public PopupDialogger(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
	}

	public PopupDialogger(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.popup_base);
		setContentView(rootViewId);
		getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
		innerAnim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
		outerAnim =	AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
		innerAnim.setAnimationListener(this);
		outerAnim.setAnimationListener(this);
		setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
//				View v = findViewById(R.id.popup_anim_layout);
//				v.startAnimation(innerAnim);
			}
		});
		popupBase = (RelativeLayout) findViewById(R.id.popup_outer_view);
		popupBase.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( cancelable ){
					dismissDialogger();
				}
				
			}
		});
		popupContainer = (LinearLayout) findViewById(R.id.popup_container);
		mTitleTextView = (TextView) findViewById(R.id.popup_dialog_title);
		
//		popupAnim = (LinearLayout) findViewById(R.id.popup_anim_layout);
		
		popupStatus = PopupStatus.kPopup_create;
		setCancelable(cancelable);
		
	}

	public static PopupDialogger createDialog(Context c){
		PopupDialogger popDialog = new PopupDialogger(c,R.style.CustomProgressDialog);
		popDialog.popupStatus = PopupStatus.kPopup_create;
		popDialog.setCancelable(true);
		return popDialog;
	}
	//关闭弹出框，无动画
	public void dismissDialogger(){
		dismiss();
		popupStatus = PopupStatus.kPopup_dismiss;
	}
	public void showDialog (Context c){
		showDialog(c, null);
	}
	public void showDialog(Context c,View v){
		showDialog(c, v, mDismisslistener);
	}
	public void showDialog(Context c,int resid){
		showDialog(c, LayoutInflater.from(c).inflate(resid, null), mDismisslistener);
	}
	public void showDialog(Context c,int resid,OnDismissListener withDismiss){
		showDialog(c, LayoutInflater.from(c).inflate(resid, null), withDismiss);
	}
	public void showDialog(Context c,View v ,OnDismissListener withDismiss){
			//dismiss();
		show();
		popupStatus = PopupStatus.kPopup_showing;
		if ( v != null ){
			addContentView(v);
		}
		mTitleTextView.setText(mTitle);
		if ( mTitle == null ){
			mTitleTextView.setVisibility(View.GONE);
		} else {
			mTitleTextView.setVisibility(View.VISIBLE);
		}
		mDismisslistener = withDismiss;
		setOnDismissListener(withDismiss);

	}
	
	public boolean isShowed(){
		if ( !isShowing() )return false;
		return popupStatus == PopupStatus.kPopup_showing;
	}
//	public void setOnCancelListner( OnCancelListener cancel){
//		if ( popDialog != null){
//			setOnCancelListener(cancel);
//		}
//
//	}
	public void setOnDissmisListner( OnDismissListener lsn) {
			setOnDismissListener(lsn);
	}
	public void addContentView(View v){
		popupContainer.removeAllViews();
		popupContainer.addView(v);
		
	}
	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onBackPressed() {
		if ( cancelable )
		dismissDialogger();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if ( animation == outerAnim){
			dismiss();
			popupStatus = PopupStatus.kPopup_dismiss;
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
	
	public void setTitleText(String titleString) {
		mTitle = titleString;
	}
	public String getTitleText(){
		return mTitle;
	}
}
