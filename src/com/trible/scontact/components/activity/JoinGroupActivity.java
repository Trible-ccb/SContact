package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.os.Bundle;
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
import com.trible.scontact.components.adpater.ContactsListAdapter;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.networks.params.PhoneAndGroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;

public class JoinGroupActivity extends CustomSherlockFragmentActivity implements
										OnItemClickListener{

	GroupInfo mGroupInfo;
	
	ListView mContactsListView;
	List<ContactInfo> myAllContacts;
	List<ContactInfo> chooseContacts;
	ContactsListAdapter mAdapter;
	AccountInfo mUserInfo;
	
	LoadingDialog mLoadingDialog;
	
	public static Bundle getIntentMyself(GroupInfo info){
		Bundle b = new Bundle();
		b.putSerializable("clazz", JoinGroupActivity.class);
		b.putSerializable("JoinGroup", info);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.popup_contact_list);
		mUserInfo = AccountInfo.getInstance();
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("JoinGroup");
		initView();
		initData();
	}
	
	void initView(){
		mLoadingDialog = new LoadingDialog(this);
		mContactsListView = (ListView) findViewById(R.id.friend_contacts_list_view);
	}
	void initData(){
		mAdapter = new ContactsListAdapter(this);
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
				onSureToJoin();
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
	
	void onSureToJoin(){
		chooseContacts = mAdapter.getCheckedContactInfos();
		StringBuilder sb = new StringBuilder();
		if ( ListUtil.isEmpty(chooseContacts) ){
			Bog.toast(R.string.selected_empty);
		} else {
			sb.append("[");
			for ( ContactInfo c : chooseContacts ){
//				cids.add(c.getId());
				sb.append(c.getId() + ",");
			}
			int idx = sb.lastIndexOf(",");
			sb.replace(idx, sb.length(), "]");
			mLoadingDialog.show();
			SContactAsyncHttpClient.post(
					PhoneAndGroupParams.getJoinGroupParams(mGroupInfo.getId(), sb.toString())
					, null
					, new AsyncHttpResponseHandler(){
						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							super.onSuccess(arg0, arg1, arg2);
							PhoneAndGroupInfo result = GsonHelper.getInfoFromJson(arg2, PhoneAndGroupInfo.class);
							if ( result != null && result.getId() != null){
								Bog.toast(R.string.success);
								finish();
							} else {
								Bog.toastErrorInfo(arg2);
							}
						}
						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							super.onFailure(arg0, arg1, arg2, arg3);
							Bog.toast(R.string.connect_server_err);
						}
						@Override
						public void onFinish() {
							super.onFinish();
							mLoadingDialog.getDialog().dismissDialogger();
						}
					});
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
}
