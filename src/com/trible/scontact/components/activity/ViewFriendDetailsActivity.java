package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.components.widgets.ChooseContactActionDialog;
import com.trible.scontact.components.widgets.ChooseContactsListDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.networks.ListImageAsynTask;
import com.trible.scontact.networks.ListImageAsynTask.ItemImageLoadingListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.UserGroupRelationInfo;
import com.trible.scontact.pojo.UserRelationInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.value.GlobalValue;


/**
 * @author Trible Chen
 *here you can see the details of a special friend and do some action;
 */
public class ViewFriendDetailsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener
										,OnClickListener
												{

	Button mFriendAction;
	ListView mContactsListView;
	FriendContactsListAdapter mAdapter;
	TextView mFriendName,mFriendDesp;
	ImageView mFriendHead;
	Drawable mPhotoBitmap;
	
	ChooseContactsListDialog mContactsListDialog;
	ChooseContactActionDialog mChooseFriendActionDialog;
	LoadingDialog mLoadingDialog;
	
	List<ContactInfo> mContacts;
	AccountInfo mFriend;
	GroupInfo mGroupInfo;//which the friend belong to
	int mFirendFlag;//0 stranger,1 friend,2 local friend,3 myself
	public static AccountInfo viewedAccount;
	public static GroupInfo viewedAccountBelongToGroup;
	
	public static Bundle getInentMyself(AccountInfo f,GroupInfo gf) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewFriendDetailsActivity.class);
		b.putSerializable("ViewFriend", f);
		b.putSerializable("InGroup", gf);
		viewedAccount = f;
		viewedAccountBelongToGroup = gf;
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mFriend = (AccountInfo) getIntent().getSerializableExtra("ViewFriend");
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("InGroup");
		mFriend = viewedAccount;
		mGroupInfo = viewedAccountBelongToGroup;
		if ( mFriend == null ) {
			finish();
			return;
		}
		setContentView(R.layout.activity_view_friend);
		String title = getString(R.string.details_title);
		if ( mGroupInfo != null ){
			title = String.format(getString(R.string.friend_in_group_details_title),
					mGroupInfo.getDisplayName());
		}
		setTitle(title, R.color.blue_qq);
		initView();
		initViewData();
		checkIsFriend();
	}
	@Override
	protected void onDestroy() {
		viewedAccount = null;
		viewedAccountBelongToGroup = null;
		super.onDestroy();
	}
	void initView(){
		mContactsListDialog = new ChooseContactsListDialog(this);
		mFriendAction = (Button) findViewById(R.id.friend_action);
		mFriendAction.setOnClickListener(this);
		mFriendHead = (ImageView) findViewById(R.id.friend_head);
		mPhotoBitmap = getResources().getDrawable(R.drawable.icon_logo);
		mFriendName = (TextView) findViewById(R.id.friend_name);
		mFriendDesp = (TextView) findViewById(R.id.friend_desp);
		mContactsListView = (ListView) findViewById(R.id.friend_contacts_list_view);
		mLoadingDialog = new LoadingDialog(this);
	}
	void initViewData(){
		mAdapter = new FriendContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		mContactsListView.setOnItemClickListener(this);
		mContacts = mFriend.getContactsList();
		mAdapter.setData(mContacts);
		String gender = "";
		if ( GlobalValue.UGENDER_FEMALE.equals(mFriend.getGender()) ){
			gender = "(" + getString(R.string.gender_female) + ")";
			mFriendName.setTextColor(getResources().getColor(R.color.pink));
		} else if ( GlobalValue.UGENDER_MALE.equals(mFriend.getGender()) ){
			gender = "(" + getString(R.string.gender_male) + ")";
			mFriendName.setTextColor(getResources().getColor(R.color.green));
		}
		mFriendName.setText(mFriend.getDisplayName() + gender);
		mFriendDesp.setText(mFriend.getDescription());
		
		mFriendHead.setImageDrawable(mPhotoBitmap);
		ListImageAsynTask phototask = new ListImageAsynTask();
		phototask.setmLoadingListner(new ItemImageLoadingListner() {
			@Override
			public void onPreLoad() {
			}
			@Override
			public void onLoadDone(Bitmap doneBm) {
				mPhotoBitmap = new BitmapDrawable(getResources(),doneBm);
				if ( doneBm != null && mPhotoBitmap != null ){
					mFriendHead.setImageDrawable(mPhotoBitmap);
				}
			}
		});
		phototask.loadBitmapByScaleOfWinWidthForWidget(this,
				mFriend.getPhotoUrl(), 0.5f);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_view_friend_menu, menu);
		if ( mFirendFlag == 3 || mFirendFlag == 1 ){
			menu.findItem(R.id.action_edit).setVisible(true);
		} else {
			menu.findItem(R.id.action_edit).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				if ( mFirendFlag == 3 ){
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact,
									getString(R.string.group_lable) 
									+ mGroupInfo.getDisplayName()));
				} else if ( mFirendFlag == 1 ){
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact, 
									mFriend.getDisplayName()));
				} else {
					return false;
				}
				mContactsListDialog.sureBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						List<ContactInfo> c = mContactsListDialog.getSelectedContacts();
						if ( ListUtil.isEmpty(c) ){
							Bog.toast(R.string.selected_empty);
						} else {
							ValidateInfo info = new ValidateInfo();
							info.setFromUser(AccountInfo.getInstance());
							info.setContactsList(c);
							if ( mFirendFlag == 3 ){
								info.setGroupInfo(mGroupInfo);
								info.setToUser(mGroupInfo.getOwner());
							} else if ( mFirendFlag == 1 ){
								info.setToUser(mFriend);
							}
							onSureToUpdateVisibleContacts(info);
							mContactsListDialog.getDialog().dismissDialogger();
						}
					}
				});
				mContactsListDialog.show();
				mContactsListDialog.setSelectedContacts(mContacts);
				break;
	
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String type = mAdapter.getContact(position).getType();
		String contact = mAdapter.getContact(position).getContact();
		mChooseFriendActionDialog = new ChooseContactActionDialog(
				this, mAdapter.getContact(position));
		new TypeHandler(this) {
			@Override
			protected void onPhone() {
				mChooseFriendActionDialog.addAction(getString(R.string.call_lable));
				mChooseFriendActionDialog.addAction(getString(R.string.message_lable));
				if ( mFirendFlag == 2 ){
					mChooseFriendActionDialog.addAction(getString(R.string.invite_lable));
				} else {
					mChooseFriendActionDialog.name = mFriend.getDisplayName();
					mChooseFriendActionDialog.addAction(getString(R.string.add_to_local_lable));
				}
			}
			@Override
			protected void onEmail() {
				mChooseFriendActionDialog.addAction(getString(R.string.send_email_lable));
				if ( mFirendFlag == 2 ){
					mChooseFriendActionDialog.addAction(getString(R.string.invite_lable));
				} else {
					mChooseFriendActionDialog.name = mFriend.getDisplayName();
					mChooseFriendActionDialog.addAction(getString(R.string.add_to_local_lable));
				}
			}
			
		}.handle(type);
		mChooseFriendActionDialog.addAction(getString(R.string.copy_lable));
		mChooseFriendActionDialog.show();
	}
	void checkIsFriend(){
		if ( mGroupInfo != null ){
			mFriendAction.setVisibility(View.GONE);
			if ( SContactMainActivity.GROUP_ID_OF_FRIEND.equals(mGroupInfo.getId()) ){
				mFirendFlag = 1;
				mFriendAction.setText(R.string.remove_friend_lable);
				mFriendAction.setTextColor(getResources().getColor(R.color.red));
				mFriendAction.setVisibility(View.VISIBLE);
				return;
			} else if ( SContactMainActivity.GROUP_OF_LOCAL_FRIEND.equals(mGroupInfo.getId()) ){
				mFirendFlag = 2;
				return;
			} else {
				mFirendFlag = 0;
			}
		}
		if (AccountInfo.getInstance().getId().equals(mFriend.getId()) ){
			mFirendFlag = 3;
			return;
		}
		AVQuery<UserRelationInfo> check = AVQuery.getQuery(UserRelationInfo.class);
		check.whereEqualTo(UserRelationInfo.FieldName.user, AccountInfo.getInstance());
		check.whereEqualTo(UserRelationInfo.FieldName.follower, mFriend);
		check.findInBackground(new FindCallback<UserRelationInfo>() {
			
			@Override
			public void done(List<UserRelationInfo> arg0, AVException arg1) {
				if ( ListUtil.isNotEmpty(arg0) ){
					mFirendFlag = 1;
					mFriendAction.setText(R.string.remove_friend_lable);
					mFriendAction.setTextColor(getResources().getColor(R.color.red));
				} else {
					mFirendFlag = 0;
					mFriendAction.setText(R.string.add_friend_lable);
				}
				mFriendAction.setVisibility(View.VISIBLE);
				if(arg1 != null ){
					Bog.toast(R.string.connect_server_err);
					Bog.i(arg1.getMessage());
				}
				supportInvalidateOptionsMenu();
			}
		});
	}
	
	void onSureToUpdateVisibleContacts(final ValidateInfo info){
		mLoadingDialog.show();
		if (info.getGroupInfo() != null ){
			AVQuery<UserGroupRelationInfo> check = AVQuery.getQuery(UserGroupRelationInfo.class);
			check.whereEqualTo(UserGroupRelationInfo.FieldName.group, info.getGroupInfo());
			check.whereEqualTo(UserGroupRelationInfo.FieldName.user, info.getFromUser());
			check.getFirstInBackground(new GetCallback<UserGroupRelationInfo>() {
				
				@Override
				public void done(UserGroupRelationInfo arg0, AVException arg1) {
					if ( arg0 != null ){
						arg0.setContacts(info.getContactsList());
						arg0.saveInBackground(new SaveCallback() {
							@Override
							public void done(AVException arg0) {
								mLoadingDialog.getDialog().dismissDialogger();
								if ( arg0 == null ){
									Bog.toast(R.string.success);
									SContactMainActivity.needRefreshFriendList = true;
								} else {
									Bog.toast(R.string.connect_server_err);
								}
							}
						});
					} else {
						mLoadingDialog.getDialog().dismissDialogger();
						Bog.toast(R.string.connect_server_err);
					}
				}
			});
		}
	}
	void onSureToAddFriend(List<ContactInfo> contactids){
		if ( ListUtil.isEmpty(contactids) ){
			Bog.toast(R.string.selected_empty);
		} else {
			mLoadingDialog.show();
			ValidateInfo info = new ValidateInfo();
			info.setContactsList(contactids);
			info.setIsGroupToUser(false);
			info.setFromUser(AccountInfo.getInstance());
			info.setToUser(mFriend);
			info.saveInBackground(new SaveCallback() {
				@Override
				public void done(AVException arg0) {
					mLoadingDialog.getDialog().dismissDialogger();
					if (arg0 == null){
						Bog.toast(R.string.request_have_send);
						finish();
					} else {
						Bog.toast(R.string.connect_server_err);
					}
				}
			});
		}
	}
	void onSureToRemoveFriend(ValidateInfo info){
		final LoadingDialog ldialog = new LoadingDialog(ViewFriendDetailsActivity.this);
		ldialog.show();
		AVQuery<UserRelationInfo> check = AVQuery.getQuery(UserRelationInfo.class);
		check.whereEqualTo(UserRelationInfo.FieldName.user, info.getFromUser());
		check.whereEqualTo(UserRelationInfo.FieldName.follower, info.getToUser());
		AVQuery<UserRelationInfo> check2 = AVQuery.getQuery(UserRelationInfo.class);
		check2.whereEqualTo(UserRelationInfo.FieldName.follower, info.getFromUser());
		check2.whereEqualTo(UserRelationInfo.FieldName.user, info.getToUser());
		List<AVQuery<UserRelationInfo>> queries = new ArrayList<AVQuery<UserRelationInfo>>();
		queries.add(check);
		queries.add(check2);
		AVQuery<UserRelationInfo> checks = AVQuery.or(queries);
		checks.deleteAllInBackground(new DeleteCallback() {
			
			@Override
			public void done(AVException arg0) {
				ldialog.getDialog().dismissDialogger();
				if (arg0 == null){
					Bog.toast(R.string.success);
					SContactMainActivity.needRefreshFriendList = true;
					finish();
				} else {
					Bog.toast(R.string.connect_server_err);
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.friend_action:
				String ts = ((TextView) v).getText().toString();
				if ( getString(R.string.remove_friend_lable).equals(ts) ){//remove friend relationship
					final YesOrNoTipDialog dialog = new YesOrNoTipDialog(
							this,
							getString(R.string.remove_friend_lable),
							getText(R.string.promote_remove_friend).toString());
					dialog.setOnButtonClickListner(new OnButtonClickListner() {
						@Override
						public void onYesButton() {
							dialog.getDialog().dismissDialogger();
							ValidateInfo info = new ValidateInfo();
							info.setToUser(mFriend);
							info.setFromUser(AccountInfo.getInstance());

							onSureToRemoveFriend(info);
							dialog.getDialog().dismissDialogger();
						}
						@Override
						public void onNoButton() {
						}
					});
					dialog.show();
				} else if (getString(R.string.add_friend_lable).equals(ts) ){// add friend relationship
					
					mContactsListDialog.setTileText(
							getString(R.string.format_select_contact,
									mFriend.getDisplayName()));
					mContactsListDialog.sureBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							List<ContactInfo> c = mContactsListDialog.getSelectedContacts();
							if ( ListUtil.isEmpty(c) ){
								Bog.toast(R.string.selected_empty);
							} else {
								onSureToAddFriend(c);
								mContactsListDialog.getDialog().dismissDialogger();
							}
						}
					});
					mContactsListDialog.show();
				}
				break;
	
			default:
				break;
		}
	}
}
