package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.ChooseContactsListAdapter;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.networks.params.PhoneAndGroupParams;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;

@Deprecated
public class SelectContactsActivity extends CustomSherlockFragmentActivity 
										{

//	GroupInfo mGroupInfo;
	
	ListView mContactsListView;
	List<ContactInfo> myAllContacts;
	List<ContactInfo> chooseContacts;
	ChooseContactsListAdapter mAdapter;
	AccountInfo mUserInfo;
	
	LoadingDialog mLoadingDialog;
	
	public static final String SELECTED_CONTACT = "selected_contacts";
	
	public static Bundle getIntentMyself(){
		Bundle b = new Bundle();
		b.putSerializable("clazz", SelectContactsActivity.class);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.popup_contact_list);
		mUserInfo = AccountInfo.getInstance();
		initView();
		initData();
	}
	
	void initView(){
		mLoadingDialog = new LoadingDialog(this);
		mContactsListView = (ListView) findViewById(R.id.contacts_list_view);
	}
	void initData(){
		mAdapter = new ChooseContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		myAllContacts = new ArrayList<ContactInfo>();
		chooseContacts = new ArrayList<ContactInfo>();
		loadMyAllContacts();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_join_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_join:
				chooseContacts = mAdapter.getCheckedContactInfos();
				String contactids = ContactInfo.arrayToString(chooseContacts);
				Intent intent = new Intent();
				intent.putExtra(SELECTED_CONTACT, contactids);
				if ( getIntent().getExtras() != null )
				intent.putExtras(getIntent().getExtras());
				setResult(RESULT_OK, intent);
				finish();
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	void loadMyAllContacts(){
		mLoadingDialog.setTipText(R.string.waiting);
		mLoadingDialog.show();
		SContactAsyncHttpClient.post(ContactParams.getUserContactsParams(mUserInfo.getId()),
				null, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				myAllContacts = GsonHelper.getInfosFromJson(arg2, new ContactInfo().listType());
				if ( myAllContacts != null ){
					mAdapter.setData(myAllContacts);
				} else {
					ErrorInfo err = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
					Bog.toast( err == null ? "" : err.toString());
				}
			}
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				super.onFailure(arg0, arg1, arg2, arg3);
				Bog.toast( R.string.connect_server_err );
			}
			@Override
			public void onFinish() {
				super.onFinish();
				mLoadingDialog.getDialog().dismissDialogger();
			}
		});
	}
	
//	void onSureToJoin(){
//		chooseContacts = mAdapter.getCheckedContactInfos();
//		String contactids = ContactInfo.arrayToString(chooseContacts);
//		if ( TextUtils.isEmpty(contactids) ){
//			Bog.toast(R.string.selected_empty);
//		} else {
//			mLoadingDialog.show();
//			GroupValidateInfo info = new GroupValidateInfo();
//			info.setContact_ids(contactids);
//			info.setGroupId(mGroupInfo.getId());
//			info.setIs_group_to_user("0");
//			info.setStart_user_id(AccountInfo.getInstance().getId());
//			info.setEnd_user_id(mGroupInfo.getOwnerId());
//			SContactAsyncHttpClient.post(
//					ValidationParams.getAddRelationshipParams(info)
//					, null
//					, new AsyncHttpResponseHandler(){
//						@Override
//						public void onSuccess(int arg0, Header[] arg1,
//								byte[] arg2) {
//							super.onSuccess(arg0, arg1, arg2);
//							PhoneAndGroupInfo result = GsonHelper.getInfoFromJson(arg2, PhoneAndGroupInfo.class);
//							if ( result != null && result.getId() != null){
//								Bog.toast(R.string.success);
//								finish();
//							} else {
//								Bog.toastErrorInfo(arg2);
//							}
//						}
//						@Override
//						public void onFailure(int arg0, Header[] arg1,
//								byte[] arg2, Throwable arg3) {
//							super.onFailure(arg0, arg1, arg2, arg3);
//							Bog.toast(R.string.connect_server_err);
//						}
//						@Override
//						public void onFinish() {
//							super.onFinish();
//							mLoadingDialog.getDialog().dismissDialogger();
//						}
//					});
//		}
//	}
}
