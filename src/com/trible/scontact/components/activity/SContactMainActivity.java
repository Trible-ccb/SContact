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
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVQuery.CachePolicy;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
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
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.UserGroupRelationInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;
import com.trible.scontact.value.RequestCode;
import com.umeng.update.UmengUpdateAgent;

/**
 * @author Trible Chen
 *the main class is to show list of friend and groups
 */
public final class SContactMainActivity extends CustomSherlockFragmentActivity 
									implements OnItemClickListener
									,OnClickListener
								 	,OnGroupActionListener
									,OnFragmentListener{

	DrawerLayout mDrawerLayout;
	ListView mGroupListView;
	GroupsListAdapter mGroupListAdapter;
	ActionBarDrawerToggle mDrawerToggle;
	FriendsListFragment mFriendsListFragment;
	TextView mTipView;
	
	Menu mMenu;
	
	LayoutInflater mInflater;
	LoadingDialog mLoadingDialog;
	
	IGroupControl mGroupController,mLocalGroupController,mReomoteGroupController;
	List<GroupInfo> mRemoteGroupsInfos,mLocalGroupsInfos,mCurGroupsInfos;
	GroupInfo mFriendGroup,mLocalFriendGroup,mSelectGroupsInfo;
	public static List<ContactInfo> myAllContacts;
	ChooseGroupActionDialog mGroupAcionDialog;
	
	AccountInfo mUserInfo;
	public static boolean needRefreshFriendList,needRefeshGroupList,needRereshContactList;
	int mSelectedGroupPos = -1;
	Long mInboxNum = null;
//	public static final Long GROUP_ID_OF_FRIEND = -1L;
//	public static final Long GROUP_OF_LOCAL_FRIEND = -2L;
	public static final String GROUP_ID_OF_FRIEND = "-1";//用户好友组
	public static final String GROUP_OF_LOCAL_FRIEND = "-2";//手机本地联系人
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
		mFriendGroup.setId(GROUP_ID_OF_FRIEND);
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
		initActionBarSpinner();
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
	void initActionBarSpinner(){
//		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		getSupportActionBar().setListNavigationCallbacks(mGroupListAdapter,new OnNavigationListener() {
//			@Override
//			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//				onItemClick(mGroupListView, null, itemPosition, itemId);
//				return false;
//			}
//		});
	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if ( arg1 == RESULT_OK ){
			switch (arg0) {
				case RequestCode.CREATE_GROUP:
					GroupInfo newGroup = (GroupInfo) arg2.getSerializableExtra(CreateOrUpdateGroupActivity.RESULT_GROUP);
					newGroup = CreateOrUpdateGroupActivity.LATEST_GROUP;
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
					} else if (arg2.hasExtra(CreateOrUpdateGroupActivity.RESULT_GROUP)){
						result = (GroupInfo) arg2.getSerializableExtra(CreateOrUpdateGroupActivity.RESULT_GROUP);
						onEditedGroup(result);
					}
					break;
				default:
					break;
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_scontact_menu, menu);
		mMenu = menu;
//		mMenu.findItem(R.id.action_my_profile).setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if ( mInboxNum != null && mInboxNum != 0 ){
			mMenu.findItem(R.id.action_my_inbox).setTitle(getString(R.string.action_inbox)+"("+mInboxNum+")");
			mMenu.findItem(R.id.action_my_inbox).setIcon(R.drawable.icon_has_msg);
		} else {
			mMenu.findItem(R.id.action_my_inbox).setTitle(getString(R.string.action_inbox));
			mMenu.findItem(R.id.action_my_inbox).setIcon(R.drawable.ic_action_email);
		}
		if ( mSelectGroupsInfo.getOwner() != null ){
			mMenu.findItem(R.id.action_group_infos).setVisible(true);
		} else {
			mMenu.findItem(R.id.action_group_infos).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
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
//				if(ListUtil.isEmpty(myAllContacts)){
//					Bog.toast(R.string.empty_contacts_list);
//				} else {
					simpleGetResultFormActivity(CreateOrUpdateGroupActivity.class
							,RequestCode.CREATE_GROUP);
//				}
				break;
			case R.id.action_add_local_friend:
				IntentUtil.insertContact(this,null,null);
				break;
			case R.id.action_settings:
				simpleGetResultFormActivity(SettingsActivity.class, RequestCode.CHANGE_SETTING);
				break;
			case R.id.action_refresh:
//				loadRemoteGroups();
//				loadMyAllContacts();
				refreshCache();
				break;
			case R.id.action_my_profile:
				simpleDisplayActivity(MyProfileActivity.class);
				break;
			case R.id.action_group_infos:
				simpleGetResultFromActivityWithData(RequestCode.VIEW_GROUP,
						ViewGroupDetailsActivity.getInentMyself(mSelectGroupsInfo));
				break;
			case R.id.action_my_contacts:
				simpleDisplayActivity(MyContactsActivity.class);
				break;
			case R.id.action_my_inbox:
				mInboxNum = 0L;
				mMenu.findItem(R.id.action_my_inbox).setTitle(getString(R.string.action_inbox));
				simpleDisplayActivity(MyInboxActivity.class);
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
			if ( info == null )return;
			mSelectGroupsInfo = info;
			if ( GROUP_OF_LOCAL_FRIEND.equals(info.getId()) ){
				mFriendsListFragment.loadLocalFriendsByGroup(info);
			} else {
				mFriendsListFragment.loadRomoteAccountsByGroup(info);
			}
			mSelectedGroupPos = position;
			mGroupListAdapter.setSelected(mSelectedGroupPos);
			mDrawerLayout.closeDrawers();
			supportInvalidateOptionsMenu();
			setTitle(mSelectGroupsInfo.getDisplayName(), R.color.blue_qq);
		}
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ( keyCode == KeyEvent.KEYCODE_MENU ){
			mMenu.performIdentifierAction(R.id.action_overflow, 0);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	//end override listener methods===============

	//private methods===============
	
	private void refreshCache(){
		loadRemoteGroups();
		loadMyAllContacts();
		
	}
	private void updateUILoadingGroup(){
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

	private void addStaticGroup(List<GroupInfo> holder){
		holder.clear();
		holder.add(mFriendGroup);
		holder.add(mLocalFriendGroup);
		
	}
	private void loadGroups(){
		mRemoteGroupsInfos = GroupInfo.getGroupsFromSpf();
		if ( ListUtil.isNotEmpty(mRemoteGroupsInfos) ){
			mCurGroupsInfos = mRemoteGroupsInfos;
			updateUILoadGroupDone();
		} else {
			mRemoteGroupsInfos = new ArrayList<GroupInfo>();
			mCurGroupsInfos = new ArrayList<GroupInfo>();
			addStaticGroup(mCurGroupsInfos);
			mGroupListAdapter.setData(mCurGroupsInfos);
			loadRemoteGroups();
		}
	}
	private void loadRemoteGroupsFromAVOS(){
		mUserInfo = AccountInfo.getInstance();
		updateUILoadingGroup();
		AVQuery<UserGroupRelationInfo> myGroups = AVQuery.getQuery(UserGroupRelationInfo.class);
		myGroups.whereEqualTo(UserGroupRelationInfo.FieldName.user, mUserInfo);
		myGroups.include(UserGroupRelationInfo.FieldName.group);
		myGroups.include(UserGroupRelationInfo.FieldName.group+"."+GroupInfo.FieldName.OWNER);
		myGroups.include(UserGroupRelationInfo.FieldName.contacts);
		myGroups.include(UserGroupRelationInfo.FieldName.user);
		myGroups.setCachePolicy(CachePolicy.NETWORK_ELSE_CACHE);
		myGroups.findInBackground(new FindCallback<UserGroupRelationInfo>() {
			@Override
			public void done(List<UserGroupRelationInfo> arg0, AVException arg1) {
				List<GroupInfo> groupinfos = new ArrayList<GroupInfo>();
				if ( arg1 == null && arg0 != null ){
					for ( UserGroupRelationInfo ugri : arg0 ){
						if (GlobalValue.GSTATUS_USED.equals(ugri.getGroup().getStatus()))
						groupinfos.add(ugri.getGroup());
					}
					
					addStaticGroup(mRemoteGroupsInfos);
					mRemoteGroupsInfos.addAll(groupinfos);
					mCurGroupsInfos = mRemoteGroupsInfos;
					for ( GroupInfo g : mRemoteGroupsInfos ){//去加载群组内的好友
						if ( mFriendsListFragment != null ){
							mFriendsListFragment.addGroupForLoad(g);
						}
					}
				} else {
					Bog.toast(arg1.getMessage());
				}
				updateUILoadGroupDone();
			}
		});
	}
	private void loadRemoteGroups(){
		loadRemoteGroupsFromAVOS();
//		mUserInfo = AccountInfo.getInstance();
//		updateUILoadingGroup();
//		SContactAsyncHttpClient.post(
//				GroupParams.getUserGroupParams(mUserInfo.getId()),
//				null, new AsyncHttpResponseHandler(){
//					@Override
//					public void onFinish() {
//						updateUILoadGroupDone();
//					}
//					@Override
//					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//						List<GroupInfo> infos = GsonHelper.getInfosFromJson(arg2, new TypeToken<List<GroupInfo>>(){}.getType());
//						
//						if ( infos == null ){
//							Bog.toastErrorInfo(arg2);
//						} else {
//							if ( mRemoteGroupsInfos == null ){
//								mRemoteGroupsInfos = new ArrayList<GroupInfo>();
//							}
//							addStaticGroup(mRemoteGroupsInfos);
//							mRemoteGroupsInfos.addAll(infos);
//							mCurGroupsInfos = mRemoteGroupsInfos;
//							for ( GroupInfo g : mRemoteGroupsInfos ){
//								if ( mFriendsListFragment != null ){
//									mFriendsListFragment.addGroupForLoad(g);
//								}
//							}
//						}
//					}
//					@Override
//					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//							Throwable arg3) {
//						Bog.toast(getString(R.string.load_groups_err));
//					}
//				});
	}
	
	void loadInboxNumber(){
		mUserInfo = AccountInfo.getInstance();
		AVQuery<ValidateInfo> mymessages = AVQuery.getQuery(ValidateInfo.class);
		mymessages.whereEqualTo(ValidateInfo.FieldName.TO_USER, mUserInfo);
//		mymessages.include(ValidateInfo.FieldName.FROM_GROUP);
//		mymessages.include(ValidateInfo.FieldName.TO_USER);
//		mymessages.include(ValidateInfo.FieldName.FROM_USER);
//		mymessages.include(ValidateInfo.FieldName.WITH_CONTACT_IDS);
		mymessages.countInBackground(new CountCallback() {
			@Override
			public void done(int arg0, AVException arg1) {
				if ( arg1 == null ){
					mInboxNum = (long) arg0;
					supportInvalidateOptionsMenu();
				} else {
					
				}

			}
		});
//		SContactAsyncHttpClient.post(
//				ValidationParams.getMyInboxNumberParams(AccountInfo.getInstance().getId()),
//				null,
//				new AsyncHttpResponseHandler(){
//					
//					@Override
//					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//						try {
//							mInboxNum = Long.valueOf(StringUtil.getStringForByte(arg2));
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						
//					}
//					@Override
//					public void onFinish() {
//						supportInvalidateOptionsMenu();
//					}
//				});
	}
	void loadMyAllContacts(){
		mUserInfo = AccountInfo.getInstance();
		AVQuery<ContactInfo> mycontacts = AVQuery.getQuery(ContactInfo.class);
		mycontacts.whereEqualTo(ContactInfo.FieldName.owner, mUserInfo);
		mycontacts.findInBackground(new FindCallback<ContactInfo>() {
			@Override
			public void done(List<ContactInfo> arg0, AVException arg1) {
				if ( arg1 == null ){
					myAllContacts = arg0;
					ContactInfo.saveToPref(arg0);
				} else {
					Bog.toast( R.string.load_contacts_err );
				}
			}
		});
//		SContactAsyncHttpClient.post(ContactParams.getUserContactsParams(mUserInfo.getId()),
//				null, new AsyncHttpResponseHandler(){
//			@Override
//			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//				myAllContacts = GsonHelper.getInfosFromJson(arg2, new ContactInfo().listType());
//				if ( myAllContacts != null ){
//					ContactInfo.saveToPref(myAllContacts);
//				} else {
//					Bog.toastErrorInfo(arg2);
//				}
//			}
//			@Override
//			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
//					Throwable arg3) {
//				Bog.toast( R.string.load_contacts_err );
//			}
//		});
		
	}
	//end private methods===========
	public void onCreatedGroup(GroupInfo g){
		GroupInfo cur = mGroupListAdapter.getSeletedGroupInfo();
		mRemoteGroupsInfos.add(g);
		mGroupListAdapter.setData(mRemoteGroupsInfos);
		int idx = mRemoteGroupsInfos.indexOf(cur);
		mGroupListAdapter.setSelected(idx);
		mSelectedGroupPos = idx;
		onItemClick(mGroupListView,
				mGroupListView.getChildAt(mSelectedGroupPos), mSelectedGroupPos, 0);
		
	}
	public void onEditedGroup(GroupInfo g){
		boolean flg = mGroupListAdapter.editedGroupBy(g);
		if ( flg ){
			mSelectGroupsInfo = mGroupListAdapter.getSeletedGroupInfo();
			setTitle(mSelectGroupsInfo.getDisplayName(), R.color.blue_qq);
		}
	}
	@Override
	public void onAparted(GroupInfo g) {
		onExited(g);
	}
	@Override
	public void onExited(GroupInfo g) {
		GroupInfo cur = mGroupListAdapter.getSeletedGroupInfo();
//		long id = Long.MIN_VALUE;
		String id = "unknowid";
		int newIdx = 0;
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
		for ( int i = 0 ; i < mRemoteGroupsInfos.size(); i++ ){
			GroupInfo info = mRemoteGroupsInfos.get(i);
			if ( info.getId().equals(id)){
				newIdx = i;
				break;
			}
		}
		mGroupListAdapter.setData(mRemoteGroupsInfos);
		if ( cur != null && g != null && cur.getId().equals(g.getId() )){
			onItemClick(mGroupListView, mGroupListView.getChildAt(0), 0, 0);
		} else {
			mSelectedGroupPos = newIdx;
			mGroupListAdapter.setSelected(mSelectedGroupPos);
		}
		
	}

	@Override
	public void onFragmentCreated() {
		loadGroups();
		onItemClick(mGroupListView, mGroupListView.getChildAt(0), 0, 0);
		loadInboxNumber();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		refreshCache();
		if ( needRefreshFriendList ){
			onItemClick(mGroupListView,
					mGroupListView.getChildAt(mSelectedGroupPos), mSelectedGroupPos, 0);
			needRefreshFriendList = false;
		}
		if ( needRefeshGroupList ){
			needRefeshGroupList = false;
			loadRemoteGroups();
		}
		if ( needRereshContactList ){
			loadMyAllContacts();
			needRereshContactList = false;
		}
	}
}
