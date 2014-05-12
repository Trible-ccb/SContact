package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.GroupsListAdapter;
import com.trible.scontact.components.fragment.FriendsListFragment;
import com.trible.scontact.components.fragment.FriendsListFragment.OnFragmentListener;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog.OnGroupActionListener;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.SherlockMenuItemToMenuItem;
import com.trible.scontact.controller.IGroupControl;
import com.trible.scontact.controller.impl.LocalGroupControlller;
import com.trible.scontact.controller.impl.RemoteGroupControlller;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.DeviceUtil;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.value.RequestCode;

/**
 * @author Trible Chen
 *the main class is to show list of friend and groups
 */
public final class SContactMainActivity extends CustomSherlockFragmentActivity 
									implements OnItemClickListener
									,OnClickListener
									,OnItemLongClickListener
								 	,OnGroupActionListener
									,OnFragmentListener{

	DrawerLayout mDrawerLayout;
	ListView mGroupListView;
	GroupsListAdapter mGroupListAdapter;
	ActionBarDrawerToggle mDrawerToggle;
	FriendsListFragment mFriendsListFragment;
	TextView mTipView;
	
	Menu mMenu;
	
//	TabLayoutHelper mContactTabHelper;
	LayoutInflater mInflater;
	LoadingDialog mLoadingDialog;
	
	IGroupControl mGroupController,mLocalGroupController,mReomoteGroupController;
	List<GroupInfo> mRemoteGroupsInfos,mLocalGroupsInfos,mCurGroupsInfos;
	GroupInfo mFriendGroup,mLocalFriendGroup;
	public static List<ContactInfo> myAllContacts;
	ChooseGroupActionDialog mGroupAcionDialog;
	
	AccountInfo mUserInfo;
	int mSelectedGroupPos = -1;
	
	public static final Long GROUP_OF_FRIEND = -1L;
	public static final Long GROUP_OF_LOCAL_FRIEND = -2L;
	
	// override life circle methods===============
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scontact);
		initDefaultData();
		initView();
		myAllContacts = ContactInfo.getFromPref();
		if ( ListUtil.isEmpty(myAllContacts) ){
			loadMyAllContacts();
		}
	}

	void initDefaultData(){
		mFriendGroup = new GroupInfo();
		mFriendGroup.setDisplayName(getString(R.string.friends_list_lable));
		mFriendGroup.setId(GROUP_OF_FRIEND);
		mFriendGroup.setCapacity(null);
		mLocalFriendGroup = new GroupInfo();
		mLocalFriendGroup.setDisplayName(getString(R.string.local_friends));
		mLocalFriendGroup.setId(GROUP_OF_LOCAL_FRIEND);
		mLocalFriendGroup.setCapacity(null);
		mUserInfo = AccountInfo.getInstance();
		mGroupListAdapter = new GroupsListAdapter(this);
		mLocalGroupController = new LocalGroupControlller(this);
		mReomoteGroupController = new RemoteGroupControlller(this);
		
	}
	void initView(){
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.setTipText(R.string.waiting);
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
		mFriendsListFragment.setOnFragmentListener(this);
		mTipView = (TextView) findViewById(R.id.tip);
		mTipView.setVisibility(View.GONE);
	}
	void initLeftDrawer(){
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		mGroupListView = (ListView) findViewById(R.id.group_list_view);
		mGroupListView.setAdapter(mGroupListAdapter);
		mGroupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mGroupListView.setOnItemClickListener(this);
		mGroupListView.setOnItemLongClickListener(this);
		mDrawerToggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, 
				R.drawable.ic_drawer,
				R.string.open_drawer,
				R.string.close_drawer);
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
		mMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if ( arg1 == RESULT_OK ){
			switch (arg0) {
				case RequestCode.CREATE_GROUP:
					GroupInfo newGroup = (GroupInfo) arg2.getSerializableExtra(CreateOrUpdateGroupActivity.RESULT_GROUP);
					onCreatedGroup(newGroup);
					break;
				case RequestCode.CHANGE_SETTING:
					boolean signout = arg2.getBooleanExtra(SettingsActivity.SIGN_OUT_TAG, false);
					if ( signout ){
						finish();
					}
					break;
				case RequestCode.VIEW_GROUP:
					GroupInfo result;
					if ( arg2.hasExtra(ChooseGroupActionDialog.APART) ){
						result = (GroupInfo) arg2.getSerializableExtra(ChooseGroupActionDialog.APART);
						onAparted(result);
					} else if (arg2.hasExtra(ChooseGroupActionDialog.EXIT)){
						result = (GroupInfo) arg2.getSerializableExtra(ChooseGroupActionDialog.EXIT);
						onExited(result);
					}
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
				simpleDisplayActivity(SearchGroupOrFriendActivity.getIntentMyselfBundle(""));
				break;
			case android.R.id.home:
				mDrawerToggle.onOptionsItemSelected(SherlockMenuItemToMenuItem.getMenuItem(item));
				return true;
			case R.id.action_add_group:
				simpleGetResultFormActivity(CreateOrUpdateGroupActivity.class
						,RequestCode.CREATE_GROUP);
				break;
			case R.id.action_settings:
				simpleGetResultFormActivity(SettingsActivity.class, RequestCode.CHANGE_SETTING);
				break;
			case R.id.action_refresh:
				loadRemoteGroups();
				loadMyAllContacts();
				break;
			case R.id.action_my_profile:
				simpleDisplayActivity(MyProfileActivity.class);
				break;
			case R.id.action_my_msg:
				simpleDisplayActivity(MyMsgsActivity.class);
				break;
			
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( parent == null ){//drawer tabs item click
		} else {//drawer group item click
			Long gid = mGroupListAdapter.getItemId(position);
			GroupInfo info = mGroupListAdapter.getGroupInfoInPosition(position);
			if ( GROUP_OF_FRIEND.equals(gid) ){
				mFriendsListFragment.loadRomoteFriendsByUserId(info);
			} else if ( GROUP_OF_LOCAL_FRIEND.equals(gid) ){
				mFriendsListFragment.loadLocalFriendsByGroup(info);
			} else {
				mFriendsListFragment.loadRomoteFriendsByGroup(info);
			}
			mSelectedGroupPos = position;
			mGroupListAdapter.setSelected(mSelectedGroupPos);
			mDrawerLayout.closeDrawers();
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		GroupInfo info = mGroupListAdapter.getGroupInfoInPosition(position);
		if ( info == null
				|| info.getId() < 0 )return true;//only handle remote group
		if ( mRemoteGroupsInfos != null && mRemoteGroupsInfos.get(position) != null ){
			mGroupAcionDialog = new ChooseGroupActionDialog(this, mRemoteGroupsInfos.get(position));
			AccountInfo uInfo = AccountInfo.getInstance();
			if ( uInfo.getId().equals(info.getOwnerId())){
				mGroupAcionDialog.setMutilVisible(true, false, true, false, false);
			} else {
				mGroupAcionDialog.setMutilVisible(true, false, true, false, false);
			}
			mGroupAcionDialog.show();
		} else {
			Bog.toast(R.string.invalid);
		}

		return false;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ( keyCode == KeyEvent.KEYCODE_MENU 
				&& event.getAction() == KeyEvent.ACTION_UP){
			mMenu.performIdentifierAction(R.id.action_overflow, 0);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	//end override listener methods===============

	//private methods===============
	private void updateUILoadingGroup(){
//		mLoadingDialog.getDialog().dismissDialogger();
//		mLoadingDialog.show();
		showTip(getString(R.string.loading_groups));
	}
	private void updateUILoadGroupDone(){
		mLoadingDialog.getDialog().dismissDialogger();
		mGroupListAdapter.setData(mCurGroupsInfos);
		mGroupListAdapter.setSelected(mSelectedGroupPos);
		hideTip();
	}
	
	public void showTip(String txt){
		mTipView.setVisibility(View.VISIBLE);
		mTipView.setText(txt);
	}
	
	public void hideTip(){
		mTipView.setVisibility(View.GONE);
	}

	private void loadGroups(){
		
			if ( mRemoteGroupsInfos != null ){
				mCurGroupsInfos = mRemoteGroupsInfos;
				updateUILoadGroupDone();
			} else {
				mCurGroupsInfos = new ArrayList<GroupInfo>();
				mCurGroupsInfos.add(mLocalFriendGroup);
				mCurGroupsInfos.add(mFriendGroup);
				mGroupListAdapter.setData(mCurGroupsInfos);
				loadRemoteGroups();
			}
	}
	private void loadRemoteGroups(){
		
		mUserInfo = AccountInfo.getInstance();
		updateUILoadingGroup();
		SContactAsyncHttpClient.post(
				GroupParams.getUserGroupParams(mUserInfo.getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onFinish() {
						updateUILoadGroupDone();
					}
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						List<GroupInfo> infos = GsonHelper.getInfosFromJson(arg2, new TypeToken<List<GroupInfo>>(){}.getType());
						if ( infos == null ){
							Bog.toastErrorInfo(arg2);
						}
						if ( mRemoteGroupsInfos == null ){
							mRemoteGroupsInfos = new ArrayList<GroupInfo>();
						}
//						if ( mContactTabHelper.getSelection() == 0 ){
						mRemoteGroupsInfos.clear();
						mRemoteGroupsInfos.add(mLocalFriendGroup);
						mRemoteGroupsInfos.add(mFriendGroup);
						mRemoteGroupsInfos.addAll(infos);
						mCurGroupsInfos = mRemoteGroupsInfos;
//						}
						updateUILoadGroupDone();
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(getString(R.string.connect_server_err));
					}
				});
	}
	
	
	void loadMyAllContacts(){
		SContactAsyncHttpClient.post(ContactParams.getUserContactsParams(mUserInfo.getId()),
				null, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				myAllContacts = GsonHelper.getInfosFromJson(arg2, new ContactInfo().listType());
				if ( myAllContacts != null ){
					ContactInfo.saveToPref(myAllContacts);
				} else {
					Bog.toastErrorInfo(arg2);
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				Bog.toast( R.string.connect_server_err );
			}
			@Override
			public void onFinish() {
			}
		});
		
	}
	//end private methods===========
	public void onCreatedGroup(GroupInfo g){
		GroupInfo cur = mGroupListAdapter.getSeletedGroupInfo();
		mRemoteGroupsInfos.add(g);
		mGroupListAdapter.setData(mRemoteGroupsInfos);
		int idx = mRemoteGroupsInfos.indexOf(cur);
		mGroupListAdapter.setSelected(idx);
	}
	@Override
	public void onAparted(GroupInfo g) {
		onExited(g);
	}
	@Override
	public void onExited(GroupInfo g) {
		GroupInfo cur = mGroupListAdapter.getSeletedGroupInfo();
		long id = Long.MIN_VALUE;
		if ( cur != null ){
			id = cur.getId();
		}
		if ( mRemoteGroupsInfos != null && g != null ){
			for ( GroupInfo info : mRemoteGroupsInfos ){
				if ( g.getId().equals(info.getId() )){
					mRemoteGroupsInfos.remove(info);
					break;
				}
			}
		}
		if ( id != Long.MIN_VALUE ){
			for ( int i = 0 ; i < mRemoteGroupsInfos.size(); i++ ){
				GroupInfo info = mRemoteGroupsInfos.get(i);
				if ( info.getId().equals(id)){
					id = i;
					break;
				}
			}
		}
		if ( id == Long.MIN_VALUE )id = 0;
		mGroupListAdapter.setData(mRemoteGroupsInfos);
		if ( cur != null && g != null && cur.getId().equals(g.getId() )){
			onItemClick(mGroupListView, mGroupListView.getChildAt(0), 0, 0);
		} else {
			mSelectedGroupPos = (int) id;
			mGroupListAdapter.setSelected(mSelectedGroupPos);
			
		}
		
	}

	@Override
	public void onFragmentCreated() {
		loadGroups();
		onItemClick(mGroupListView, mGroupListView.getChildAt(0), 0, 0);
	}
}
