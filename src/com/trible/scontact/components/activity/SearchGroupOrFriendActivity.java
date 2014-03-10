package com.trible.scontact.components.activity;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.trible.scontact.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * @author Trible Chen
 * a class for searching group or friends by name
 *
 */
public class SearchGroupOrFriendActivity extends CustomSherlockFragmentActivity {

	public static final String SEARCH_NAME = "SearchName";
	String mQueryString;
	
	public static void displayMyself(Activity a,String s){
		Intent intent = new Intent(a, SearchGroupOrFriendActivity.class);
		intent.putExtra("SEARCH_NAME", s);
		a.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_search_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	
}
