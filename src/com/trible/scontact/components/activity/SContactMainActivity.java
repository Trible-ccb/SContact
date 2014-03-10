package com.trible.scontact.components.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBarDrawerToggle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.GroupsListAdapter;
import com.trible.scontact.components.fragment.FriendsListFragment;
import com.trible.scontact.models.Groupsinfo;
import com.trible.scontact.utils.Bog;

/**
 * @author Trible Chen
 *the main class is to show list of friend and groups
 */
public class SContactMainActivity extends CustomSherlockFragmentActivity 
									implements OnItemClickListener{

	DrawerLayout mDrawerLayout;
	ListView mGroupListView;
	GroupsListAdapter mGroupListAdapter;
	ActionBarDrawerToggle mDrawerToggle;
	FriendsListFragment mFriendsListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		setContentView(R.layout.activity_scontact);
		mGroupListAdapter = new GroupsListAdapter(this);
		FragmentManager manager = getSupportFragmentManager();
		mFriendsListFragment = (FriendsListFragment) manager
				.findFragmentByTag("FriendsFragment");
		if ( mFriendsListFragment == null ){
			FragmentTransaction tst = manager.beginTransaction();
			mFriendsListFragment = FriendsListFragment.getInstance();
			tst.replace(R.id.frame_users_list,mFriendsListFragment, "FriendsFragment");
			tst.commit();
		}

		initView();
	}

	void initView(){
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		mGroupListView = (ListView) findViewById(R.id.group_list_view);
		mGroupListView.setAdapter(mGroupListAdapter);
		mGroupListAdapter.setData(Groupsinfo.getTestData());
		mDrawerToggle = new ActionBarDrawerToggle(
				this, getSupportActionBar(), mDrawerLayout, 
				R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer);
		getSupportActionBar().setDisplayOptions(
		        ActionBar.DISPLAY_SHOW_TITLE |
		        ActionBar.DISPLAY_USE_LOGO |
		        ActionBar.DISPLAY_SHOW_HOME |
		        ActionBar.DISPLAY_HOME_AS_UP);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_scontact_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			SearchGroupOrFriendActivity.displayMyself(this, "");
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bog.i("click at " + position);
	}


}
