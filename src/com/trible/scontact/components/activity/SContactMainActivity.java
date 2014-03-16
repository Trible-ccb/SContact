package com.trible.scontact.components.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBarDrawerToggle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.GroupsListAdapter;
import com.trible.scontact.components.fragment.FriendsListFragment;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.components.widgets.TabLayoutHelper;
import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.controller.impl.LocalGroupControlller;
import com.trible.scontact.controller.impl.RemoteGroupControlller;
import com.trible.scontact.models.AccountInfo;
import com.trible.scontact.models.Groupsinfo;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.utils.Bog;

/**
 * @author Trible Chen
 *the main class is to show list of friend and groups
 */
public final class SContactMainActivity extends CustomSherlockFragmentActivity 
									implements OnItemClickListener,
									OnClickListener,OnItemLongClickListener{

	DrawerLayout mDrawerLayout;
	ListView mGroupListView;
	GroupsListAdapter mGroupListAdapter;
	ActionBarDrawerToggle mDrawerToggle;
	FriendsListFragment mFriendsListFragment;
	TabLayoutHelper mContactTabHelper;
	LayoutInflater mInflater;
	
	IGroupControl mGroupController,mLocalGroupController,mReomoteGroupController;
	List<Groupsinfo> mGroupsInfo;
	View mCreateGroup;
	ChooseGroupActionDialog mGroupAcionDialog;
	
	AccountInfo mUserInfo;
	
	
	// override life circle methods===============
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		setContentView(R.layout.activity_scontact);
		initDefaultData();
		initView();

	}

	void initDefaultData(){
		mUserInfo = AccountInfo.getInstance();
		mGroupListAdapter = new GroupsListAdapter(this);
		mLocalGroupController = new LocalGroupControlller(this);
		mReomoteGroupController = new RemoteGroupControlller(this);
		mGroupAcionDialog = new ChooseGroupActionDialog(this, getString(R.string.choose_action));
	}
	void initView(){
		initLeftDrawer();
		initContentView();
	}
	void initContentView(){

		FragmentManager manager = getSupportFragmentManager();
		mFriendsListFragment = (FriendsListFragment) manager
				.findFragmentByTag("FriendsFragment");
		if ( mFriendsListFragment == null ){
			FragmentTransaction tst = manager.beginTransaction();
			mFriendsListFragment = FriendsListFragment.getInstance();
			tst.replace(R.id.frame_users_list,mFriendsListFragment, "FriendsFragment");
			tst.commit();
		}
	}
	void initLeftDrawer(){
		mCreateGroup = findViewById(R.id.drawer_group_list_create_group_root);
		mCreateGroup.setOnClickListener(this);
		mContactTabHelper = new TabLayoutHelper((ViewGroup) findViewById(R.id.tabs_layout),
				new int[]{R.string.remote_contacts,R.string.local_contacts});
		mContactTabHelper.setOnItemClickListner(this);
		mContactTabHelper.setDefaultSelection(0);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		mGroupListView = (ListView) findViewById(R.id.group_list_view);
		mGroupListView.setAdapter(mGroupListAdapter);
		mGroupListView.setOnItemClickListener(this);
		mGroupListView.setOnItemLongClickListener(this);
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
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if ( arg1 == RESULT_OK ){
			switch (arg0) {
				case RequestCode.CREATE_GROUP:
					Groupsinfo newGroup = (Groupsinfo) arg2.getSerializableExtra(CreateGroupActivity.RESULT_GROUP);
					mGroupsInfo.add(newGroup);
					mGroupListAdapter.setData(mGroupsInfo);
					break;
	
				default:
					break;
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	//end override life circle methods===============
	
	//override listener methods===============
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				simpleDisplayActivityWithData(SearchGroupOrFriendActivity.class,
						SearchGroupOrFriendActivity.getIntentMyselfBundle(""));
				break;
			case android.R.id.home:
				mDrawerToggle.onOptionsItemSelected(item);
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.drawer_group_list_create_group_root:
				simpleGetResultFormActivity(CreateGroupActivity.class,RequestCode.CREATE_GROUP);
				break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( parent == null ){//drawer tabs item click
			switch (position) {
				case 1:
					mGroupController = mLocalGroupController;
					mCreateGroup.setVisibility(View.GONE);
					break;
				case 0 :
					mCreateGroup.setVisibility(View.VISIBLE);
					mGroupController = mReomoteGroupController;
					break;
				default:
					break;
			}
			loadGroups(AccountInfo.getInstance());
		} else {//drawer group item click
			loadLocalFriendsByGroup(mGroupsInfo.get(position).getGroupId());
			mDrawerLayout.closeDrawers();
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mGroupAcionDialog.show();
		return false;
	}
	
	//end override listener methods===============

	//private methods===============
	private void loadGroups(final AccountInfo info){
		SimpleAsynTask.doTask2(new AsynTaskListner() {
			
			@Override
			public void onTaskDone(NetWorkEvent event) {
				Bog.toastDebug("groups = " + mGroupsInfo.size());
				mGroupListAdapter.setData(mGroupsInfo);
			}
			
			@Override
			public void doInBackground() {
				mGroupsInfo = mGroupController.getGroupsInfoList(info.getmUserId());
			}
		});
	}
	
	private void loadLocalFriendsByGroup(long gid){
		mFriendsListFragment.loadLocalFriendsByGroup(gid);
	}
	
	//end private methods===========
}
