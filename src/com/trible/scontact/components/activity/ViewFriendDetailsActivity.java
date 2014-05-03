package com.trible.scontact.components.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.adpater.FriendContactsListAdapter;
import com.trible.scontact.components.widgets.ChooseFriendActionDialog;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.value.GlobalValue;


/**
 * @author Trible Chen
 *here you can see the details of a special friend and do some action;
 */
public class ViewFriendDetailsActivity extends CustomSherlockFragmentActivity 
										implements OnItemClickListener
												{

	ImageView mFriendImg;
	TextView mFriendName,mFriendDesc;
	ListView mContactsListView;
	FriendContactsListAdapter mAdapter;
	
	ChooseFriendActionDialog mChooseFriendActionDialog;
	
	List<ContactInfo> mContacts;
	AccountInfo mFriend;

	public static Bundle getInentMyself(AccountInfo f) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewFriendDetailsActivity.class);
		b.putSerializable("ViewFriend", f);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mFriend = (AccountInfo) getIntent().getSerializableExtra("ViewFriend");
		setContentView(R.layout.activity_view_friend);
		initView();
		initViewData();
	}

	void initView(){
		
		mFriendName = (TextView) findViewById(R.id.friend_name);
		mFriendImg = (ImageView) findViewById(R.id.friend_img);
		mFriendDesc = (TextView) findViewById(R.id.friend_desc);
		mContactsListView = (ListView) findViewById(R.id.friend_contacts_list_view);
	}
	void initViewData(){
		mAdapter = new FriendContactsListAdapter(this);
		mContactsListView.setAdapter(mAdapter);
		mContactsListView.setOnItemClickListener(this);
		mContacts = mFriend.getContactsList();
		mAdapter.setData(mContacts);
		mFriendName.setText(mFriend.getDisplayName());
		mFriendDesc.setText(mFriend.getDescription());
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String type = mContacts.get(position).getType();
		String contact = mContacts.get(position).getContact();
		mChooseFriendActionDialog = new ChooseFriendActionDialog(this, contact);
		if ( GlobalValue.CTYPE_EMAIL.equals(type) ){
			IntentUtil.sendEmail(this, contact, "", "");
			return;
		} else if ( GlobalValue.CTYPE_PHONE.equals(type) ){
			mChooseFriendActionDialog.setMutilVisible(true, true, false);
		} else if ( GlobalValue.CTYPE_IM.equals(type) ){
			
		} 
		mChooseFriendActionDialog.show();
	}
}
