package com.trible.scontact.components.activity;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.trible.scontact.R;

public class CustomSherlockFragmentActivity extends SherlockFragmentActivity {

	
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
		ActionBar bar = getSupportActionBar();
		if ( bar != null ){
			if ( this instanceof SContactMainActivity ){
				bar.setDisplayShowHomeEnabled(true);
				bar.setHomeButtonEnabled(true);
			} else if ( !(this instanceof SignInUpActivity) ){
				bar.setDisplayHomeAsUpEnabled(true);
			}

			bar.setBackgroundDrawable(getResources().getDrawable(R.color.white_f0));
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
}
