package com.trible.scontact.components.widgets;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.activity.MyContactsActivity;
import com.trible.scontact.components.activity.SContactMainActivity;
import com.trible.scontact.components.adpater.ChooseContactsListAdapter;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;

public class ChooseContactsListDialog{
	
	PopupDialogger dialogger;
	
	Context mContext;
	View contentView;
	public TextView sureBtn,createBtn;
	String titleText;
	List<ContactInfo> mContacts;
	ChooseContactsListAdapter mAdapter;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}
	public void setTileText(String titleText){
		this.titleText = titleText;
	}
	public  ChooseContactsListDialog(Context context) {
		mContext = context;
		this.mContacts = SContactMainActivity.myAllContacts;
		contentView = createContentView();
	}
	
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_contact_list, null);
		ListView mContactsListView = (ListView) view.findViewById(R.id.contacts_list_view);
//		hintTextView = (TextView) view.findViewById(R.id.select_contacts_hint_text);
		sureBtn = (TextView) view.findViewById(R.id.sure_btn);
		createBtn = (TextView) view.findViewById(R.id.create_btn);
		sureBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ListUtil.isEmpty(mAdapter.getCheckedContactInfos())){
					Bog.toast(R.string.selected_empty);
					return;
				}
				dialogger.dismissDialogger();
			}
		});
		createBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogger.dismissDialogger();
				Intent intent = new Intent(mContext, MyContactsActivity.class);
				mContext.startActivity(intent);
			}
		});
		dialogger = PopupDialogger.createDialog(mContext);
		dialogger.setUseNoneScrollRootViewId();
		mAdapter = new ChooseContactsListAdapter(mContext);
		mContactsListView.setAdapter(mAdapter);
		return view;
	}
	public void show(){
		mAdapter.setSelectAll(false);
		mAdapter.setData(mContacts);
//		if ( mAdapter.getCount() == 0 ){
//			Bog.toast(
//					StringUtil.catStringFromResId(
//							mContext, R.string.hint_contacts_list,R.string.empty));
//			return;
//		}
		if ( mAdapter.getCount() == 1 ){
			mAdapter.setSelectAll(true);
		}
		dialogger.setTitleText(titleText);
		dialogger.showDialog(mContext,contentView);
	}

	public List<ContactInfo> getSelectedContacts(){
		return mAdapter.getCheckedContactInfos();
	}
	public void setSelectedContacts(List<ContactInfo> infos){
		mAdapter.setSelectedData(infos);
	}
}
