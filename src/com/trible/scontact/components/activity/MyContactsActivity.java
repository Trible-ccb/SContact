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
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.ContactTypeSpinnerAdapter;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.widgets.AddUpdateContactDialog;
import com.trible.scontact.components.widgets.AddUpdateContactDialog.OnSubmitContactListener;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.StringUtil;


/**
 * @author Trible Chen
 *here you can see the details of a special friend and do some action;
 */
public class MyContactsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener,OnClickListener
												{

	ListView mContactsListView;
	FriendContactsListAdapter mAdapter;
	EditText mContactNameET;
	Spinner mContactTypeSpinner;
	ContactTypeSpinnerAdapter mTypeAdapter;
	AddUpdateContactDialog mContactDialog;
	View mCreateRootView;
	Button mAddContactBtn;
	
	LoadingDialog mDialog;
	
	List<ContactInfo> mContacts;

	boolean mContactChange;
	boolean isShowInput;
	
	public static Bundle getInentMyself() {
		Bundle b = new Bundle();
		b.putSerializable("clazz", MyContactsActivity.class);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_contacts);
		initView();
		initViewData();
	}

	void initView(){
		mContactDialog = new AddUpdateContactDialog(this);
		mDialog = new LoadingDialog(this);
		mCreateRootView = findViewById(R.id.create_contact_root);
		mContactTypeSpinner = (Spinner) findViewById(R.id.contact_type);
		mContactNameET = (EditText) findViewById(R.id.contact_name);
		mContactsListView = (ListView) findViewById(R.id.my_contacts_list_view);
		mAddContactBtn = (Button) findViewById(R.id.add_contact_btn);
		mAddContactBtn.setOnClickListener(this);
	}
	void initViewData(){
		mTypeAdapter = new ContactTypeSpinnerAdapter(mContactTypeSpinner);
		mContactTypeSpinner.setAdapter(mTypeAdapter);
		mAdapter = new FriendContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		mContactsListView.setOnItemClickListener(this);
		mContacts = SContactMainActivity.myAllContacts;
		mAdapter.setData(mContacts);
		closeAddAction();
		mContactDialog.setmListener(new OnSubmitContactListener() {
			
			@Override
			public void onSubmit(ContactInfo info) {
				doAddContact(info);
				mContactDialog.getDialog().dismissDialogger();
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
//		getSupportMenuInflater().inflate(R.menu.activity_my_contacts_menu, menu);
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
		if ( mContactChange )
		ContactInfo.saveToPref(mContacts);
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
		simpleDisplayActivity(
				ViewContactDetailsActivity.getInentMyself(
						mAdapter.getContact(position)));

	}
	void refreshTitle(){
		setTitle(String.format(getString(R.string.format_title_contacts),
				mAdapter.getCount()));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_contact_btn:
			mContactDialog.show();
			break;

		default:
			break;
		}
	}
	
	void closeAddAction(){
		mCreateRootView.setVisibility(View.GONE);
		mAddContactBtn.setText(R.string.add_contact);
		isShowInput = false;
	}
	void showAddAction(){
		mCreateRootView.setVisibility(View.VISIBLE);
		mAddContactBtn.setText(R.string.submit);
		isShowInput = true;
	}
	
	void doAddContact(ContactInfo info){
		mDialog.show();
		SContactAsyncHttpClient.post(
				ContactParams.getAddContactParams(info),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						ContactInfo result = GsonHelper.getInfoFromJson(arg2, ContactInfo.class);
						if ( result != null && result.getId() != null ){
							mAdapter.addData(result);
							refreshTitle();
							mContactNameET.setText("");
							mContactChange = true;
							closeAddAction();
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
}
