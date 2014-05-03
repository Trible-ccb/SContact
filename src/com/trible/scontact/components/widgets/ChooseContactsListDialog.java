package com.trible.scontact.components.widgets;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.trible.scontact.R;
import com.trible.scontact.components.activity.SContactMainActivity;
import com.trible.scontact.components.adpater.ContactsListAdapter;
import com.trible.scontact.pojo.ContactInfo;

public class ChooseContactsListDialog{
	
	PopupDialogger dialogger;
	
	Context mContext;
	View contentView;
	
	List<ContactInfo> mContacts;
	ContactsListAdapter mAdapter;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  ChooseContactsListDialog(Context context) {
		mContext = context;
		this.mContacts = SContactMainActivity.myAllContacts;
		contentView = createContentView();
	}
	
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_contact_list, null);
		ListView mContactsListView = (ListView) view.findViewById(R.id.contacts_list_view);
		dialogger = PopupDialogger.createDialog(mContext);
		dialogger.setUseNoneScrollRootViewId();
		mAdapter = new ContactsListAdapter(mContext);
		mContactsListView.setAdapter(mAdapter);
		return view;
	}
	public void show(){
		mAdapter.setSelectAll(false);
		mAdapter.setData(mContacts);
		dialogger.setTitleText(mContext.getString(R.string.choose_contacts));
		dialogger.showDialog(mContext,contentView);
	}

	public List<ContactInfo> getSelectedContacts(){
		return mAdapter.getCheckedContactInfos();
	}
}
