package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.MixFriendsGroupsListAdapter;
import com.trible.scontact.components.adpater.SearchResultAdapter.SectionData;
import com.trible.scontact.components.widgets.AddUpdateContactDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.AddUpdateContactDialog.OnSubmitContactListener;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;


/**
 * @author Trible Chen
 *here you can see the friends and groups which the contact belong to;
 */
public class ViewContactDetailsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener
												{

	ListView mObjectsListView;
	List<Object> mObjects;
	MixFriendsGroupsListAdapter mAdapter;
	LoadingDialog mLoadingDialog;
	ContactInfo mContactInfo;
	AddUpdateContactDialog mContactDialog;
	
	public static Bundle getInentMyself(ContactInfo f) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewContactDetailsActivity.class);
		b.putSerializable("ViewContact", f);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_view_contact_detail);
		mContactInfo = (ContactInfo) getIntent().getSerializableExtra("ViewContact");
		mObjects = new ArrayList<Object>();
		initView();
		initViewData();
		loadContactInfos();
	}

	void initView(){
		mContactDialog = new AddUpdateContactDialog(this);
		mContactDialog.setSelectType(
				mContactInfo.getType(), mContactInfo.getContact());
		mObjectsListView = (ListView) findViewById(R.id.friend_groups_list_view);
		mLoadingDialog = new LoadingDialog(this);
	}
	void initViewData(){
		mAdapter = new MixFriendsGroupsListAdapter(this);
		mObjectsListView.setAdapter(mAdapter);
		mObjectsListView.setOnItemClickListener(this);
		mContactDialog.setmListener(new OnSubmitContactListener() {
			
			@Override
			public void onSubmit(ContactInfo info) {
				info.setId(mContactInfo.getId());
				info.setStatus(mContactInfo.getStatus());
				info.setUserId(mContactInfo.getUserId());
				info.setLastestUsedTime(System.currentTimeMillis());
				doEditContact(info);
				mContactDialog.getDialog().dismissDialogger();
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		getSupportMenuInflater().inflate(R.menu.activity_view_contact_detail_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				mContactDialog.show();
				break;
			case R.id.action_discard:
				doDeleteContact();
				break;
			default:
				break;
			}
		return super.onOptionsItemSelected(item);
	}
	void loadContactInfos(){
		final LoadingDialog dialog = new LoadingDialog(this);
		dialog.setTipText(R.string.processing);
		SContactAsyncHttpClient.post(
				GroupParams.getContactGroupParams(mContactInfo.getId()),
				null,
				new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						List<GroupInfo> result = GsonHelper.getInfosFromJson(
								arg2, new GroupInfo().listType());
						if ( result == null ){
						} else {
							if ( ListUtil.isNotEmpty(result) ){
								SectionData groupsection = new SectionData(
										getString(R.string.search_group_lable));
								mObjects.add(groupsection);
								mObjects.addAll(result);
							}
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						SContactAsyncHttpClient.post(
								AccountParams.getAccountByContactIdParams(mContactInfo.getId()),
								null,
								new AsyncHttpResponseHandler(){
									@Override
									public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
										List<AccountInfo> result = GsonHelper.getInfosFromJson(
												arg2, new AccountInfo().listType());
										if ( result == null ){
										} else {
											if ( ListUtil.isNotEmpty(result) ){
												SectionData friendsection = new SectionData(
														getString(R.string.search_people_lable));
												mObjects.add(friendsection);
												mObjects.addAll(result);
											}
											
										}
									}
									@Override
									public void onFailure(int arg0, Header[] arg1, byte[] arg2,
											Throwable arg3) {
										Bog.toast(R.string.connect_server_err);
									}
									@Override
									public void onFinish() {
										mAdapter.setData(mObjects);
										dialog.getDialog().dismissDialogger();
									}
						});
					}
		});
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
	
	void doEditContact(ContactInfo info){
		final LoadingDialog mDialog = new LoadingDialog(this);
		mDialog.setTipText(R.string.processing);
		mDialog.show();
		SContactAsyncHttpClient.post(
				ContactParams.getUpdateContactParams(info),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						ContactInfo result = GsonHelper.getInfoFromJson(arg2, ContactInfo.class);
						if ( result != null && result.getId() != null ){
							Bog.toast(R.string.success);
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
	void doDeleteContact(){
		final YesOrNoTipDialog dialog = new YesOrNoTipDialog(
				this, getString(R.string.promote_remove_contact), null);
		dialog.setOnButtonClickListner(new OnButtonClickListner() {
			
			@Override
			public void onYesButton() {
				dialog.getDialog().dismissDialogger();
				final LoadingDialog ldialog = new LoadingDialog(ViewContactDetailsActivity.this);
				ldialog.setTipText(R.string.processing);
				ldialog.show();
				SContactAsyncHttpClient.post(
						ContactParams.getDeleteContactParams(mContactInfo.getId()),
						null,
						new AsyncHttpResponseHandler(){
							@Override
							public void onSuccess(int arg0, Header[] arg1,
									byte[] arg2) {
								finish();
							}
							@Override
							public void onFailure(int arg0, Header[] arg1,
									byte[] arg2, Throwable arg3) {
								Bog.toast(R.string.connect_server_err);
							}
							@Override
							public void onFinish() {
								ldialog.getDialog().dismissDialogger();
							}
						});
			}
			@Override
			public void onNoButton() {
			}
		});
		dialog.show();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK){
		}
	}
}
