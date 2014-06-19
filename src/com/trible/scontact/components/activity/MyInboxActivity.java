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
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.ValidateInfo;
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
		NotifyHelper.setCallbackActivity(this);
	}

	void initView(){
		mContactsListDialog = new ChooseContactsListDialog(this);
		mViewContactsDialog = PopupDialogger.createDialog(this);
		mViewContactsDialog.setUseNoneScrollRootViewId();
		mDialog = new LoadingDialog(this);
		mDialog.getDialog().setCancelable(false);
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
						IntentUtil.sendEmail(MyInboxActivity.this, contact, "", "");
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
							mAdapter.getValidateInfoItem(position).getStartUser().getDisplayName(),
							infos.size()));
			mViewContactsDialog.showDialog(this, mViewContactListView);
			
		}

	}
	void loadMessages(){
		mDialog.show();
		SContactAsyncHttpClient.post(
				ValidationParams.getMyInboxListParams(AccountInfo.getInstance().getId()),
				null, 
				new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						List<ValidateInfo> result = GsonHelper.getInfosFromJson(arg2, new ValidateInfo().listType());
						if (result != null){
							mAdapter.setData(result);
						} else {
							Bog.toastErrorInfo(arg2);
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						mDialog.getDialog().dismissDialogger();
					}
				});
	}
	void refreshTitle(){
		int m = ListUtil.isEmpty(mMessages) ? 0 : mMessages.size();
		setTitle(getString(R.string.action_inbox) + " (" + m + ")",R.color.blue_qq);
	}

	@Override
	public void doReject(final ValidateInfo info) {
		mDialog.show();
		SContactAsyncHttpClient.post(
				ValidationParams.getNotAcceptOneValidateParams(info),
				null, 
				new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						ValidateInfo result = GsonHelper.getInfoFromJson(arg2, ValidateInfo.class);
						if (result != null && result.getId() != null){
							mAdapter.remove(info);
						} else {
							Bog.toastErrorInfo(arg2);
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						mDialog.getDialog().dismissDialogger();
					}
				});
	}

	@Override
	public void doAccept(final ValidateInfo info) {
		if ( info.getIs_group_to_user() == 0 && info.getGroupId() != null ){
			accept(info, null);//only if accept a user join in group,do not need select contacts
		} else {
			String target = null;
			if ( info.getGroupInfo() != null ){
				target = info.getGroupInfo().getDisplayName();
			} else {
				target = info.getStartUser().getDisplayName();
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
						accept(info, ContactInfo.arrayToString(c));
						mContactsListDialog.getDialog().dismissDialogger();
					}
				}
			});
			mContactsListDialog.show();
		}
}
	
	void accept(final ValidateInfo info,String optContactids){
		mDialog.show();
		SContactAsyncHttpClient.post(
				ValidationParams.getAcceptOneValidateParams(info,optContactids),
				null, 
				new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						ValidateInfo result = GsonHelper.getInfoFromJson(arg2, ValidateInfo.class);
						if (result != null && result.getId() != null){
							mAdapter.remove(info);
							SContactMainActivity.needRefreshFriendList = true;
							SContactMainActivity.needRefeshGroupList = true;
						} else {
							Bog.toastErrorInfo(arg2);
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						mDialog.getDialog().dismissDialogger();
					}
				});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ( resultCode == RESULT_OK ){
			if ( requestCode == RequestCode.SELECT_CONTACTS ){
				String contactids = data.getStringExtra(SelectContactsActivity.SELECTED_CONTACT);
				ValidateInfo info = (ValidateInfo) data.getSerializableExtra("GroupValidateInfo");
				accept(info, contactids);
			}
		}
	}
}
