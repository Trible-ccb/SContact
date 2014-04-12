package com.trible.scontact.components.widgets;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.trible.scontact.R;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.utils.IntentUtil;

public class CreateContactDialog implements OnClickListener{
	
	PopupDialogger dialogger;

	CustomSherlockFragmentActivity mContext;
	View contentView;
	TextView call,msg,invite;
	AccountInfo mAccountInfo;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  CreateContactDialog(Context context,AccountInfo fInfo) {
		mContext = (CustomSherlockFragmentActivity) context;
		mAccountInfo = fInfo;
		contentView = createContentView();
	}
	
	public void setMutilVisible(boolean canCall,boolean canMsg,boolean canInvite){
		call.setVisibility(canCall ? 0 : View.GONE );
		msg.setVisibility(canMsg ? 0 : View.GONE );
		invite.setVisibility(canInvite ? 0 : View.GONE );
	}
	private View createContentView(){
		View view = LayoutInflater.from(mContext).
				inflate(R.layout.popup_choose_friend_action, null);
		call = (TextView) view.findViewById(R.id.call_action);
		msg = (TextView) view.findViewById(R.id.message_action);
		invite = (TextView) view.findViewById(R.id.invite_action);
		call.setOnClickListener(this);
		msg.setOnClickListener(this);
		invite.setOnClickListener(this);
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(null);
		dialogger.showDialog(mContext,contentView);
	}
	void actionOnContact(){
		if ( mAccountInfo != null ){
			List<ContactInfo> nums = mAccountInfo.getContactsList();
			if ( nums != null && nums.size() > 0 ){
				IntentUtil.call(mContext, nums.get(0).getContact());
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.call_action:
				actionOnContact();
				break;
			case R.id.message_action:
				actionOnContact();
				break;
			case R.id.invite_action:
				
				break;
		default:
			break;
		}
		dialogger.dismissDialogger();
	}
}
