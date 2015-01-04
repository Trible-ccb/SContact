package com.trible.scontact.components.activity;

import java.util.List;

import org.apache.http.Header;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.SaveCallback;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.ContactTypeSpinnerAdapter;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.widgets.AddUpdateContactDialog;
import com.trible.scontact.components.widgets.AddUpdateContactDialog.OnSubmitContactListener;
import com.trible.scontact.components.widgets.ChooseContactActionDialog;
import com.trible.scontact.components.widgets.ChooseContactActionDialog.OnContactActionClickListener;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog;
import com.trible.scontact.components.widgets.YesOrNoTipDialog.OnButtonClickListner;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.ContactParams;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.value.GlobalValue;


/**
 * @author Trible Chen
 *here you can manager your contacts
 */
public class MyContactsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener,
										OnClickListener,
										OnContactActionClickListener
												{

	ListView mContactsListView;
	FriendContactsListAdapter mAdapter;
	ContactTypeSpinnerAdapter mTypeAdapter;
	AddUpdateContactDialog mContactDialog;
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
		mContactDialog.getDialog().setTitleText(getString(R.string.add_contact));
		mDialog = new LoadingDialog(this);
		mContactsListView = (ListView) findViewById(R.id.my_contacts_list_view);
		mAddContactBtn = (Button) findViewById(R.id.add_contact_btn);
		mAddContactBtn.setOnClickListener(this);
	}
	void initViewData(){
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
		ContactInfo info = mAdapter.getContact(position);
		ChooseContactActionDialog actionDialog =
				new ChooseContactActionDialog(this, info);
		actionDialog.addAction(getString(R.string.action_edit));
		if ( GlobalValue.CSTATUS_USED.equals(info.getStatus()) ){
			actionDialog.addAction(getString(R.string.action_disable));
		} else {
			actionDialog.addAction(getString(R.string.action_enable));
		}
		actionDialog.addAction(getString(R.string.action_delete));
		actionDialog.addAction(getString(R.string.copy_lable));
		
		actionDialog.setmListener(this);
		actionDialog.show();
	}
	void refreshTitle(){
		setTitle(String.format(getString(R.string.format_title_contacts),
				mAdapter.getCount()),R.color.blue_qq);
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
		mAddContactBtn.setText(R.string.add_contact);
		isShowInput = false;
	}
	void showAddAction(){
		mAddContactBtn.setText(R.string.submit);
		isShowInput = true;
	}
	
	void doAddContact(final ContactInfo info){
		mDialog.setTipText(R.string.processing);
		mDialog.show();
		info.setFetchWhenSave(true);
		info.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(AVException arg0) {
				mDialog.getDialog().dismissDialogger();
				if ( arg0 == null ){
					mAdapter.addData(info);
					refreshTitle();
					mContactChange = true;
					closeAddAction();
					mContactDialog.clear();
				} else {
					Bog.toast(arg0.getMessage());
				}
			}
		});
	}
	void doUpdateContact(final ContactInfo info){
		mDialog.setTipText(R.string.processing);
		mDialog.show();
		info.saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException arg0) {
				mDialog.getDialog().dismissDialogger();
				if ( arg0 == null ){
					int idx = mAdapter.indexOfContact(info);
					if ( idx != -1 ){
						mContacts.set(idx, info);
					}
					mAdapter.notifyDataSetChanged();
					mContactChange = true;
					closeAddAction();
				} else {
					Bog.toast(arg0.getMessage());
				}

			}
		});
	}
	void doDeleteContact(final ContactInfo info){
		final YesOrNoTipDialog dialog = new YesOrNoTipDialog(
				this, getString(R.string.promote_remove_contact), null);
		dialog.setOnButtonClickListner(new OnButtonClickListner() {
			
			@Override
			public void onYesButton() {
				dialog.getDialog().dismissDialogger();
				mDialog.setTipText(R.string.processing);
				mDialog.show();
				info.deleteInBackground(new DeleteCallback() {
					@Override
					public void done(AVException arg0) {
						mDialog.getDialog().dismissDialogger();
						if ( arg0 == null ){
							mContacts.remove(info);
							mAdapter.setData(mContacts);
							refreshTitle();
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
		dialog.show();
	}
	@Override
	public void onActionClick(final ContactInfo c,String action) {
		if ( getString(R.string.action_edit).equals(action) ){
			final AddUpdateContactDialog contactDialog = new AddUpdateContactDialog(this);
//			contactDialog.setSelectType(c.getType(), c.getContact());
			contactDialog.setSelectConactInfo(c);
			contactDialog.setmListener(new OnSubmitContactListener() {
				@Override
				public void onSubmit(ContactInfo info) {
//					info.setId(c.getId());
					info.setStatus(c.getStatus());
//					info.setUserId(c.getUserId());
					info.setOwner(c.getOwner());
//					info.setLastestUsedTime(System.currentTimeMillis());
					doUpdateContact(info);
					contactDialog.getDialog().dismissDialogger();
				}
			});
			contactDialog.show();
		} else if ( getString(R.string.action_delete).equals(action) ){
			doDeleteContact(c);
		} else if ( getString(R.string.action_disable).equals(action) ){
			ContactInfo copy = c.copy();
			copy.setStatus(GlobalValue.CSTATUS_UNUSED);
			doUpdateContact(copy);
		} else if ( getString(R.string.action_enable).equals(action) ){
			ContactInfo copy = c.copy();
			copy.setStatus(GlobalValue.CSTATUS_USED);
			doUpdateContact(copy);
		}
	}
	
}
