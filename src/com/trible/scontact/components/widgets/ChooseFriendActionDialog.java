package com.trible.scontact.components.widgets;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.adpater.ChooseActionAdapter;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.IntentUtil;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.StringUtil;
import com.trible.scontact.value.GlobalValue;

public class ChooseFriendActionDialog{
	
	LoadingDialog mLoadingDialog;
	PopupDialogger dialogger;
	
	public static final String CALL = "Call";
	public static final String MSG = "Message";
	public static final String INVITE = "Invite";
	
	CustomSherlockFragmentActivity mContext;
	View contentView;
	ListView mActionList;
	ChooseActionAdapter mAdapter;
	List<String> mActions;
	
	String contact;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  ChooseFriendActionDialog(Context context,String contact) {
		mContext = (CustomSherlockFragmentActivity) context;
		this.contact = contact;
		mAdapter = new ChooseActionAdapter(context);
		mActions = new ArrayList<String>();
		contentView = createContentView();
	}
	
	public void setMutilVisible(boolean canCall,boolean canMsg,boolean canInvite){
		mActions.clear();
		if ( canCall ){
			mActions.add(CALL);
		}
		if ( canMsg ){
			mActions.add(MSG);
		}
		if ( canInvite ){
			mActions.add(INVITE);
		}
		mAdapter.setDatas( mActions );
	}
	private View createContentView(){
		View view = LayoutInflater.from(mContext).
				inflate(R.layout.popup_choose_friend_action, null);
		mActionList = (ListView) view.findViewById(R.id.friend_action_list_view);
		mActionList.setAdapter(mAdapter);
		mActionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String action = mActions.get(position);
				if ( CALL.equals(action) ){
					IntentUtil.call(mContext, contact);
				} else if ( MSG.equals(action) ){
					IntentUtil.sendMsg(mContext, contact,"");
				} else if ( INVITE.equals(action) ){
					if ( StringUtil.isValidEmail(contact) ){
						IntentUtil.sendEmail(
								mContext, GlobalValue.CONTACT_EMAIL
								, "", mContext.getString(R.string.promote_email));
					} else if ( StringUtil.isValidPhoneNumber(contact) ){
						IntentUtil.sendMsg(mContext, contact,mContext.getString(R.string.promote_msg));
					}
				} else {
					Bog.toast(R.string.unsupport_type);
				}
					
				dialogger.dismissDialogger();
			}
			
		});
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setUseNoneScrollRootViewId();
		dialogger.setTitleText(null);
		if ( ListUtil.isNotEmpty(mActions) ){
			dialogger.showDialog(mContext,contentView);
		} else {
			Bog.toast(R.string.unsupport_type);
		}

	}
}
