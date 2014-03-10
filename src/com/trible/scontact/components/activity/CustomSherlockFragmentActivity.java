package com.trible.scontact.components.activity;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.trible.scontact.R;

public class CustomSherlockFragmentActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.white_f7));
	}

	
}
