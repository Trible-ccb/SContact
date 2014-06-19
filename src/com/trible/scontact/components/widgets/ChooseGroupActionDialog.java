package com.trible.scontact.components.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.activity.CreateOrUpdateGroupActivity;
import com.trible.scontact.components.activity.CustomSherlockFragmentActivity;
import com.trible.scontact.components.activity.SelectContactsActivity;
import com.trible.scontact.components.activity.SContactMainActivity;
import com.trible.scontact.components.activity.ViewGroupDetailsActivity;
import com.trible.scontact.components.adpater.ChooseActionAdapter;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.networks.params.PhoneAndGroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.ListUtil;
import com.trible.scontact.utils.MD5FileUtil;
import com.trible.scontact.value.RequestCode;

public class ChooseGroupActionDialog{
	
	PopupDialogger dialogger;
	LoadingDialog mLoadingDialog;
	
	public static final String VIEW = "View Group";
	public static final String APART = "Apart Group";
	public static final String EDIT = "Edit Group";
	public static final String JOIN = "Join Group";
	public static final String EXIT = "Exit Group";
	
	CustomSherlockFragmentActivity mContext;
	View contentView;
	ListView mActionList;
	ChooseActionAdapter mAdapter;
	List<String> mGroupActions;
	
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
		mAdapter = new ChooseActionAdapter(context);
		mGroupActions = new ArrayList<String>();
		contentView = createContentView();
	}
	
	public void setMutilVisible(boolean canview,boolean canapart,boolean canedit,boolean canexit,boolean canjoin ){
		mGroupActions.clear();
		if ( canview ){
			mGroupActions.add(VIEW);
		}
		if ( canedit ){
			mGroupActions.add(EDIT);
		}
		if ( canapart ){
			mGroupActions.add(APART);
		}
		if ( canjoin ){
			mGroupActions.add(JOIN);
		}
		if ( canexit ){
			mGroupActions.add(EXIT);
		}
		mAdapter.setDatas( mGroupActions );
	}
	
	private View createContentView(){
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_choose_group_action, null);
		mActionList = (ListView) view.findViewById(R.id.group_action_list_view);
		mActionList.setAdapter(mAdapter);
		mActionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String action = mGroupActions.get(position);
				if ( VIEW.equals(action) ){
					mContext.simpleGetResultFromActivityWithData(RequestCode.VIEW_GROUP,
							ViewGroupDetailsActivity.getInentMyself(gInfo));
				} else if ( APART.equals(action) ){
					apartGroup();
				} else if ( EDIT.equals(action) ){
					if ( ListUtil.isEmpty(SContactMainActivity.myAllContacts) ){
						Bog.toast(R.string.load_contacts_err);
					} else {
						mContext.simpleDisplayActivity(
								CreateOrUpdateGroupActivity.getIntentMyself(gInfo));
					}
				} else if ( EXIT.equals(action) ){
					exitGroup();
				} else if ( JOIN.equals(action) ){
//					mContext.simpleDisplayActivity(
//							SelectContactsActivity.getIntentMyself(gInfo));
				}
				dialogger.dismissDialogger();
			}
			
		});
		dialogger = PopupDialogger.createDialog(mContext);
		return view;
	}
	public void show(){
		dialogger.setTitleText(null);
		dialogger.setUseNoneScrollRootViewId();
		if ( ListUtil.isNotEmpty(mGroupActions) ){
			dialogger.showDialog(mContext,contentView);
		} else {
			Bog.toast(R.string.unsupport_type);
		}
		
	}
	
	public void apartGroup(){
		mLoadingDialog = new LoadingDialog(mContext);
		mLoadingDialog.setTipText(R.string.waiting);
		mLoadingDialog.getDialog().setmDismisslistener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				SContactAsyncHttpClient.cancel(mContext, true);
			}
		});
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
							Intent intent = new Intent();
							intent.putExtra(APART, gInfo);
							mContext.setResult(mContext.RESULT_OK, intent);
							mContext.finish();
						} else {
							Bog.toastErrorInfo(arg2);
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
	
	public void exitGroup(){
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
							Intent intent = new Intent();
							intent.putExtra(EXIT, gInfo);
							mContext.setResult(mContext.RESULT_OK, intent);
							mContext.finish();
						} else {
							Bog.toastErrorInfo(arg2);
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
