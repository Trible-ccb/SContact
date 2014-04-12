package com.trible.scontact.components.activity;

import java.util.List;

import org.apache.http.Header;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.widgets.ChooseFriendActionDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;


/**
 * @author Trible Chen
 *here you can see the details of a special friend and do some action;
 */
public class ViewAndAddMyContactsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener,OnClickListener
												{

	ListView mContactsListView;
	FriendContactsListAdapter mAdapter;
	EditText mContactNameET;
	Button mAddBtn;
	
	LoadingDialog mDialog;
	
	List<ContactInfo> mContacts;

	public static Bundle getInentMyself() {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewAndAddMyContactsActivity.class);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_view_my_contacts);
		initView();
		initViewData();
	}

	void initView(){
		mDialog = new LoadingDialog(this);
		mAddBtn = (Button) findViewById(R.id.add_btn);
		mContactNameET = (EditText) findViewById(R.id.contact_name);
		mContactsListView = (ListView) findViewById(R.id.my_contacts_list_view);
		mAddBtn.setOnClickListener(this);
	}
	void initViewData(){
		mAdapter = new FriendContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		mContactsListView.setOnItemClickListener(this);
		mContacts = SContactMainActivity.myAllContacts;
		mAdapter.setData(mContacts);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_my_contacts_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onResume() {
		super.onResume();
		refreshTitle();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:
				
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
	void refreshTitle(){
		setTitle(String.format(getString(R.string.title_contacts),
				mContacts == null ? 0 : mContacts.size()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_btn:
				doAddContact();
				break;

		default:
			break;
		}
	}
	void doAddContact(){
		String name = mContactNameET.getText().toString();
		if ( !StringUtil.isValidName(name) ){
			Bog.toast(R.string.empty);
			return;
		}
		mDialog.show();
		ContactInfo info = new ContactInfo();
		info.setUserId(AccountInfo.getInstance().getId());
		info.setContact(name);
		SContactAsyncHttpClient.post(
				ContactParams.getAddContactParams(info),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						ContactInfo result = GsonHelper.getInfoFromJson(arg2, ContactInfo.class);
						if ( result != null && result.getId() != null ){
							mContacts.add(result);
							mAdapter.setData(mContacts);
							refreshTitle();
							mContactNameET.setText("");
						} else {
							Bog.toastErrorInfo(arg2);
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
					}
					@Override
					public void onFinish() {
						super.onFinish();
						mDialog.getDialog().dismissDialogger();
					}
				});
	}
}
