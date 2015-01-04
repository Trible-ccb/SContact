package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.adpater.InboxListAdapter;
import com.trible.scontact.components.adpater.InboxListAdapter.OnHandleMessageAction;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.components.widgets.ChooseContactActionDialog;
import com.trible.scontact.components.widgets.ChooseContactsListDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.NotifyHelper;
import com.trible.scontact.components.widgets.PopupDialogger;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.UserGroupRelationInfo;
import com.trible.scontact.pojo.UserRelationInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.pojo.ValidateInfo.FieldName;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.value.RequestCode;


/**
 * @author Trible Chen
 */
public class MyInboxActivity extends CustomSherlockFragmentActivity 
										implements 
										OnItemClickListener
										,OnHandleMessageAction
												{

	ListView mMesgsListView,mViewContactListView;
	
	ChooseContactsListDialog mContactsListDialog;
	FriendContactsListAdapter mViewContactsListAdapter;
	PopupDialogger mViewContactsDialog;
	
	LoadingDialog mDialog;
	InboxListAdapter mAdapter;
	
	List<ValidateInfo> mMessages;
	
	public static Bundle getInentMyself() {
		Bundle b = new Bundle();
		b.putSerializable("clazz", MyInboxActivity.class);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_messages);
		
		initView();
		initViewData();
		loadMessages();
		refreshTitle();
	}

	void initView(){
		mContactsListDialog = new ChooseContactsListDialog(this);
		mViewContactsDialog = PopupDialogger.createDialog(this);
		mViewContactsDialog.setUseNoneScrollRootViewId();
		mDialog = new LoadingDialog(this);
		mAdapter = new InboxListAdapter(this);
		mMesgsListView = (ListView) findViewById(R.id.my_messages_list_view);
		mMesgsListView.setAdapter(mAdapter);
		mViewContactListView = new ListView(this);
		mViewContactsListAdapter = new FriendContactsListAdapter(this);
		mViewContactListView.setAdapter(mViewContactsListAdapter);
		mViewContactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String type = mViewContactsListAdapter.getContact(position).getType();
				final String contact = mViewContactsListAdapter.getContact(position).getContact();
				final ChooseContactActionDialog actionDialog =
						new ChooseContactActionDialog(
								MyInboxActivity.this, mViewContactsListAdapter.getContact(position));
				new TypeHandler(MyInboxActivity.this) {
					@Override
					protected void onPhone() {
						actionDialog.addAction(getString(R.string.call_lable));
						actionDialog.addAction(getString(R.string.message_lable));
					}
					protected void onEmail() {
						actionDialog.addAction(getString(R.string.send_email_lable));
					};
				}.handle(type);
				actionDialog.addAction(getString(R.string.copy_lable));
				mViewContactsDialog.dismissDialogger();
				actionDialog.show();				
			}
		});
	}
	void initViewData(){
		mMesgsListView.setOnItemClickListener(this);
		mMessages = new ArrayList<ValidateInfo>();
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onResume() {
		super.onResume();
		refreshTitle();
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		List<ContactInfo> infos = mAdapter.getValidateInfoItem(position).getContactsList();
		if ( ListUtil.isNotEmpty(infos) ){
			mViewContactsListAdapter.setData(infos);
			mViewContactsDialog.setTitleText(
					getString(R.string.format_shared_contacts,
							mAdapter.getValidateInfoItem(position).getFromUser().getUsername(),
							infos.size()));
			mViewContactsDialog.showDialog(this, mViewContactListView);
			
		}

	}
	void loadMessages(){
		mDialog.getDialog().setTitleText(getString(R.string.loading));
		mDialog.show();
		AVQuery<ValidateInfo> mymsg = AVQuery.getQuery(ValidateInfo.class);
		mymsg.include(FieldName.TO_USER);
		mymsg.include(FieldName.FROM_GROUP);
		mymsg.include(FieldName.FROM_USER);
		mymsg.include(FieldName.WITH_CONTACTS);
		mymsg.whereEqualTo(FieldName.TO_USER, AccountInfo.getInstance());
		
		mymsg.findInBackground(new FindCallback<ValidateInfo>() {
			
			@Override
			public void done(List<ValidateInfo> arg0, AVException arg1) {
				mDialog.getDialog().dismissDialogger();
				if ( arg1 == null ){
					mAdapter.setData(arg0);
				} else {
					Bog.toast(arg1.getMessage());
				}
			}
		});
	}
	void refreshTitle(){
		int m = ListUtil.isEmpty(mMessages) ? 0 : mMessages.size();
		setTitle(getString(R.string.action_inbox),R.color.blue_qq);
	}

	@Override
	public void doReject(final ValidateInfo info) {
		final YesOrNoTipDialog dialog = new YesOrNoTipDialog(this, "",
				getString(R.string.are_you_sure_to_delete_valid));
		dialog.show();
		dialog.setOnButtonClickListner(new OnButtonClickListner() {
			
			@Override
			public void onYesButton() {
				info.deleteInBackground(new DeleteCallback() {
					@Override
					public void done(AVException arg0) {
						dialog.getDialog().dismissDialogger();
						if(arg0 == null){
							mAdapter.remove(info);
						} else {
							Bog.toast(arg0.getMessage());
						}
					}
				});
			}
			@Override
			public void onNoButton() {
				
			}
		});
	}

	@Override
	public void doAccept(final ValidateInfo info) {
		if ( !info.isGroupToUser() && info.getGroupInfo() != null ){//同意某人进入我的群
			accept(info, null);//only if accept a user join in group,do not need select contacts
		} else {
			String target = null;
			if ( info.getGroupInfo() != null ){
				target = info.getGroupInfo().getDisplayName();
			} else {
				target = info.getFromUser().getDisplayName();
			}
			mContactsListDialog.setTileText(
					getString(R.string.format_select_contact, target));
			mContactsListDialog.sureBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					List<ContactInfo> c = mContactsListDialog.getSelectedContacts();
					if ( ListUtil.isEmpty(c) ){
						Bog.toast(R.string.selected_empty);
					} else {
						accept(info, c);
						mContactsListDialog.getDialog().dismissDialogger();
					}
				}
			});
			mContactsListDialog.show();
		}
	}
	
	void accept(final ValidateInfo info,List<ContactInfo> optContactids){
		mDialog.show();
		AccountInfo from = info.getFromUser();
		AccountInfo to = info.getToUser();
		GroupInfo group = info.getGroupInfo();
		boolean isgrouptouser = info.isGroupToUser();
		
		UserGroupRelationInfo ugri = null;
		List<AVObject> uris = new ArrayList<AVObject>();
		if ( isgrouptouser ){//同意群主的邀请进群 
			ugri = new UserGroupRelationInfo();
			ugri.setContacts(optContactids);
			ugri.setGroup(group);
			ugri.setUser(to);
		} else {
			if( group == null ){//好友之间的请求
				UserRelationInfo uri = new UserRelationInfo();
				uri.setUser(from);
				uri.setFollower(to);
				uri.setContacts(info.getContactsList());
				UserRelationInfo uri2 = new UserRelationInfo();
				uri2.setUser(to);
				uri2.setFollower(from);
				uri2.setContacts(optContactids);
				uris.add(uri);
				uris.add(uri2);
			} else {//同意对方加入我的群里
				ugri = new UserGroupRelationInfo();
				ugri.setContacts(info.getContactsList());
				ugri.setGroup(group);
				ugri.setUser(from);
			}
		}
		if( ugri != null ){
			uris.add(ugri);
		}
		AVObject.saveAllInBackground(uris, new SaveCallback() {
			@Override
			public void done(AVException arg0) {
				mDialog.getDialog().dismissDialogger();
				if ( arg0 == null ){
					mAdapter.remove(info);
					info.deleteInBackground();
					SContactMainActivity.needRefreshFriendList = true;
					SContactMainActivity.needRefeshGroupList = true;
				} else {
					Bog.toast(arg0.getMessage());
				}
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode == RESULT_OK ){
			if ( requestCode == RequestCode.SELECT_CONTACTS ){
//				String contactids = data.getStringExtra(SelectContactsActivity.SELECTED_CONTACT);
//				ValidateInfo info = (ValidateInfo) data.getSerializableExtra("GroupValidateInfo");
//				accept(info, contactids);
			}
		}
	}
}
