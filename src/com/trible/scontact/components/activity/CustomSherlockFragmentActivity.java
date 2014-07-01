package com.trible.scontact.components.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.trible.scontact.R;
import com.umeng.analytics.MobclickAgent;

public class CustomSherlockFragmentActivity extends SherlockFragmentActivity
												{

	ActionBar mBar;
	OnGestureListener mGestureListener;
	GestureDetector mGestureDetector;
	public static final int FLINGSLOP = 100;
	
	public void simpleDisplayActivity(Class<?> clazz){
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		doSimpleInOutAnim();
	}
	public void simpleDisplayActivityAndFinish(Class<?> clazz){
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
		finish();
		doSimpleInOutAnim();
		
	}
	public void simpleGetResultFormActivity(Class<?> clazz,int requestCode){
		Intent intent = new Intent(this, clazz);
		startActivityForResult(intent, requestCode);
		doSimpleInOutAnim();
	}
	
	public void simpleDisplayActivity(Bundle b){
		Intent intent = new Intent(this, (Class<?>) b.getSerializable("clazz"));
		intent.putExtras(b);
		startActivity(intent);
		doSimpleInOutAnim();
	}
	public void simpleDisplayActivityAndFinish(Bundle b){
		Intent intent = new Intent(this, (Class<?>) b.getSerializable("clazz"));
		intent.putExtras(b);
		startActivity(intent);
		finish();
		doSimpleInOutAnim();
		
	}
	
	public void simpleGetResultFromActivityWithData(int requestCode,Bundle b){
		Intent intent = new Intent(this, (Class<?>) b.getSerializable("clazz"));
		intent.putExtras(b);
		startActivityForResult(intent, requestCode);
		doSimpleInOutAnim();
	}
	
	private void doSimpleInOutAnim(){
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left_slow);
	}
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		mGestureListener = new MymGestureListener();
		mGestureDetector = new GestureDetector(this, mGestureListener);
		mBar = getSupportActionBar();
		if ( mBar != null ){
			if ( this instanceof SContactMainActivity ){
				mBar.setDisplayShowHomeEnabled(true);
				mBar.setHomeButtonEnabled(true);
			} else if ( !(this instanceof SignInUpActivity) ){
				mBar.setDisplayHomeAsUpEnabled(true);
			}
			mBar.setDisplayShowHomeEnabled(true);
			mBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white_title));
			setTitle("");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
	
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void setTitle(int titleId,int color){
		setTitle(getText(titleId).toString(),color);
	}
	
	public void setTitle(String s,int color){
		StringBuffer sb = new StringBuffer("<font color=\"" + getResources().getColor(color) +"\">");
		sb.append(s);
		sb.append("</font>");
		setTitle(Html.fromHtml(sb.toString()));
	}
	
	public void setLogo(int rid){
		if ( mBar != null )
		mBar.setLogo(rid);
	}
	public void setBarBackgroup(int rid){
		if ( mBar != null )
			mBar.setBackgroundDrawable(getResources().getDrawable(rid));
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	class MymGestureListener extends SimpleOnGestureListener{
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if ( e2.getX() - e1.getX() > FLINGSLOP ){
				onFlingToRight();
			} else if (e1.getX() - e2.getX() > FLINGSLOP){
				onFlingToLeft();
			} else {
				return false;
			}
			return true;
		}
	}
	
	protected void onFlingToRight(){
		onBackPressed();
	};
	protected void onFlingToLeft(){
		
	};
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
