package com.trible.scontact.components.widgets;

import org.apache.http.Header;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.activity.CreateOrUpdateGroupActivity;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.activity.JoinGroupActivity;
import com.trible.scontact.components.activity.SContactMainActivity;
import com.trible.scontact.components.activity.ViewGroupDetailsActivity;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.networks.params.PhoneAndGroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;

public class ChooseGroupActionDialog implements OnClickListener{
	
	PopupDialogger dialogger;
	LoadingDialog mLoadingDialog;
	
	CustomSherlockFragmentActivity mContext;
	View contentView;
	TextView see,apart,edit,exit,join;
	
	GroupInfo gInfo;
	
	OnGroupActionListener mGroupActionListener;
	
	public PopupDialogger getDialog() {
		return dialogger;
	}


	public  ChooseGroupActionDialog(Context context,GroupInfo gInfo) {
		this.gInfo = gInfo;
		mContext = (CustomSherlockFragmentActivity) context;
		if ( context instanceof OnGroupActionListener ){
			mGroupActionListener = (OnGroupActionListener) context;
		}
		contentView = createContentView();
	}
	
	public void setMutilVisible(boolean canview,boolean canapart,boolean canedit,boolean canexit,boolean canjoin ){
		see.setVisibility(canview ? 0 : View.GONE );
		apart.setVisibility(canapart ? 0 : View.GONE );
		edit.setVisibility(canedit ? 0 : View.GONE );
		exit.setVisibility(canexit ? 0 : View.GONE );
		join.setVisibility(canjoin ? 0 : View.GONE );
	}
	
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_choose_group_action, null);
		see = (TextView) view.findViewById(R.id.view_group_action);
		apart = (TextView) view.findViewById(R.id.apart_group_action);
		edit = (TextView) view.findViewById(R.id.edit_group_action);
		exit = (TextView) view.findViewById(R.id.exit_group_action);
		join = (TextView) view.findViewById(R.id.join_group_action);
		
		see.setOnClickListener(this);
		apart.setOnClickListener(this);
		edit.setOnClickListener(this);
		exit.setOnClickListener(this);
		join.setOnClickListener(this);
		AccountInfo uInfo = AccountInfo.getInstance();
		if ( uInfo.getId().equals(gInfo.getOwnerId())){
			setMutilVisible(true, true, true, false, false);
		} else {
			setMutilVisible(true, false, false, true, true);
		}
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(null);
		dialogger.showDialog(mContext,contentView);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.view_group_action:
				mContext.simpleDisplayActivity(
						ViewGroupDetailsActivity.getInentMyself(gInfo));
				break;
			case R.id.edit_group_action:
				if ( ListUtil.isEmpty(SContactMainActivity.myAllContacts) ){
					Bog.toast(R.string.load_contacts_err);
				} else {
					mContext.simpleDisplayActivity(
							CreateOrUpdateGroupActivity.getIntentMyself(gInfo));
				}
				break;
			case R.id.apart_group_action:
				apartGroup();
				break;
			case R.id.exit_group_action:
				exitGroup();
				break;
			case R.id.join_group_action:
				mContext.simpleDisplayActivity(
						JoinGroupActivity.getIntentMyself(gInfo));
				break;
		default:
			break;
		}
		dialogger.dismissDialogger();
	}
	
	void apartGroup(){
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.setTipText(R.string.waiting);
		mLoadingDialog.show();
		SContactAsyncHttpClient.post(
				GroupParams.getDeleteGroupParams(gInfo.getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						GroupInfo result = GsonHelper.getInfoFromJson(arg2, GroupInfo.class);
						if ( result != null && result.getId() != null ){
							if ( mGroupActionListener != null ){
								mGroupActionListener.onAparted(result);
							}
						} else {
							ErrorInfo msg = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
							Bog.toast(msg == null ? 
									ErrorInfo.getUnkownErr().toString() : msg.toString());
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						super.onFinish();
						mLoadingDialog.getDialog().dismissDialogger();
					}
				});
	}
	
	void exitGroup(){
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.setTipText(R.string.waiting);
		AccountInfo user = AccountInfo.getInstance();
		mLoadingDialog.show();
		SContactAsyncHttpClient.post(
				PhoneAndGroupParams.getExitGroupParams(gInfo.getId(), user.getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						GroupInfo result = GsonHelper.getInfoFromJson(arg2, GroupInfo.class);
						if ( result != null && result.getId() != null ){
							if ( mGroupActionListener != null ){
								mGroupActionListener.onExited(result);
							}
						} else {
							ErrorInfo msg = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
							Bog.toast(msg == null ? 
									ErrorInfo.getUnkownErr().toString() : msg.toString());
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Bog.toast(R.string.connect_server_err);
					}
					@Override
					public void onFinish() {
						super.onFinish();
						mLoadingDialog.getDialog().dismissDialogger();
					}
				});
	}
	
	public interface OnGroupActionListener{
		void onAparted(GroupInfo g);
		void onExited(GroupInfo g);
	}
}
