package com.trible.scontact.components.widgets;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.adpater.ChooseActionAdapter;
import com.trible.scontact.components.adpater.TypeHandler;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.utils.TimeUtil;
import com.trible.scontact.value.GlobalValue;

public class ChooseContactActionDialog{
	
	LoadingDialog mLoadingDialog;
	PopupDialogger dialogger;
	
	CustomSherlockFragmentActivity mContext;
	View contentView;
	ListView mActionList;
	ChooseActionAdapter mAdapter;
	List<String> mActions;
	
	String contact;
	public String name;//the contact's owner name
	ContactInfo mContactInfo;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  ChooseContactActionDialog(Context context,ContactInfo contactinfo) {
		mContext = (CustomSherlockFragmentActivity) context;
		this.contact = contactinfo.getContact();
		mContactInfo = contactinfo;
		mAdapter = new ChooseActionAdapter(context);
		mActions = new ArrayList<String>();
		contentView = createContentView();
	}
	public void addAction(String action){
		if ( TextUtils.isEmpty(action) )return;
		mActions.add(action);
	}
//	public void setMutilVisible(boolean canCall,boolean canMsg,boolean canInvite,
//			boolean canEdit,boolean canDelete,boolean canInvite){
//		mActions.clear();
//		if ( canCall ){
//			mActions.add(mContext.getString(R.string.call_lable));
//		}
//		if ( canMsg ){
//			mActions.add(mContext.getString(R.string.message_lable));
//		}
//		if ( canInvite ){
//			mActions.add(mContext.getString(R.string.invite_lable));
//		}
//		mAdapter.setDatas( mActions );
//	}
	private View createContentView(){
		View view = LayoutInflater.from(mContext).
				inflate(R.layout.popup_choose_friend_action, null);
		mActionList = (ListView) view.findViewById(R.id.friend_action_list_view);
		mActionList.setAdapter(mAdapter);
		mActionList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String action = mActions.get(position);
				if ( mContext.getString(R.string.call_lable).equals(action) ){
					IntentUtil.call(mContext, contact);
				} else if ( mContext.getString(R.string.message_lable).equals(action) ){
					IntentUtil.sendMsg(mContext, contact,"");
				} else if ( mContext.getString(R.string.send_email_lable).equals(action) ){
					IntentUtil.sendEmail(mContext, contact, "", "");
				} else if ( mContext.getString(R.string.invite_lable).equals(action) ){
					if ( StringUtil.isValidEmail(contact) ){
						IntentUtil.sendEmail(mContext,
								contact
								, "", mContext.getString(R.string.promote_email));
					} else if ( StringUtil.isValidPhoneNumber(contact) ){
						IntentUtil.sendMsg(mContext, contact,mContext.getString(R.string.promote_msg));
					}
				} else if ( mContext.getString(R.string.copy_lable).equals(action) ){
					if (Build.VERSION.SDK_INT < 11 ){
						android.text.ClipboardManager older = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
						older.setText(contact);
					} else {
						ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
						cm.setPrimaryClip(ClipData.newPlainText(contact, contact));
					}
					Bog.toast(contact + mContext.getString(R.string.added_to_clip));
				} else if ( mContext.getString(R.string.add_to_local_lable).equals(action) ){
					IntentUtil.insertContact(mContext, name, mContactInfo);
				}
				if ( mListener != null ){
					mListener.onActionClick(mContactInfo,action);
				}
				dialogger.dismissDialogger();
			}
			
		});
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setUseNoneScrollRootViewId();
		if ( GlobalValue.CSTATUS_UNUSED.equals(mContactInfo.getStatus()) ){
			dialogger.setTitleText(
					mContext.getString(R.string.action_disable) + contact);
		} else {
			dialogger.setTitleText(contact);
			new TypeHandler(mContext) {
				@Override
				protected void onPhone() {
					long ms = 0;
					if ( mContactInfo.getUpdatedAt() != null ){
						ms = mContactInfo.getUpdatedAt().getTime();
					}
					if ( TimeUtil.withinWeek(ms) ){
						dialogger.setTitleText(
								mContext.getString(R.string.contact_used_within_week) + contact);
					} else if (TimeUtil.withinWeek(ms)){
						dialogger.setTitleText(
								mContext.getString(R.string.contact_used_within_month) + contact);
					} 
				}
			}.handle(mContactInfo.getType());
//			if ( GlobalValue.CTYPE_PHONE.equals(mContactInfo.getType()) ){
//			}
			
		}
		if ( ListUtil.isNotEmpty(mActions) ){
			mAdapter.setDatas(mActions);
			dialogger.showDialog(mContext,contentView);
		} else {
			Bog.toast(R.string.unsupport_type);
		}

	}
	
	OnContactActionClickListener mListener;
	public OnContactActionClickListener getmListener() {
		return mListener;
	}


	public void setmListener(OnContactActionClickListener mListener) {
		this.mListener = mListener;
	}
	public interface OnContactActionClickListener{
		void onActionClick(ContactInfo c,String action);
	}
}
