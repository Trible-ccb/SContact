package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.R.menu;
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
import com.trible.scontact.components.adpater.MessageListAdapter;
import com.trible.scontact.components.adpater.MessageListAdapter.OnHandleMessageAction;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ValidationParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.ValidateInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.value.RequestCode;


/**
 * @author Trible Chen
 */
public class MyMsgsActivity extends CustomSherlockFragmentActivity 
										implements 
										OnItemClickListener
										,OnHandleMessageAction
												{

	ListView mMesgsListView;
	
	LoadingDialog mDialog;
	MessageListAdapter mAdapter;
	
	List<ValidateInfo> mMessages;
	
	public static Bundle getInentMyself() {
		Bundle b = new Bundle();
		b.putSerializable("clazz", MyMsgsActivity.class);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_messages);
		initView();
		initViewData();
		loadMessages();
	}

	void initView(){
		mDialog = new LoadingDialog(this);
		mDialog.getDialog().setCancelable(false);
		mAdapter = new MessageListAdapter(this);
		mMesgsListView = (ListView) findViewById(R.id.my_messages_list_view);
		mMesgsListView.setAdapter(mAdapter);
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
			case R.id.action_add_group:
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
	void loadMessages(){
		mDialog.show();
		SContactAsyncHttpClient.post(
				ValidationParams.getMyValidateListParams(AccountInfo.getInstance().getId()),
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
						super.onFinish();
						mDialog.getDialog().dismissDialogger();
					}
				});
	}
	void refreshTitle(){
		
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
						super.onFinish();
						mDialog.getDialog().dismissDialogger();
					}
				});
	}

	@Override
	public void doAccept(final ValidateInfo info) {
		if ( info.getGroupId() == null ){
			Bundle b = SelectContactsActivity.getIntentMyself();
			b.putSerializable("GroupValidateInfo", info);
			simpleGetResultFromActivityWithData(RequestCode.SELECT_CONTACTS, b);
		} else {
			accept(info, null);
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
						super.onFinish();
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
