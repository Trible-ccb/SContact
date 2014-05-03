package com.trible.scontact.components.activity;

import org.apache.http.Header;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.PhoneAndGroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.utils.Bog;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Trible Chen
 * here you can see the details of a special group and do some action;
 */
public class ViewGroupDetailsActivity extends CustomSherlockFragmentActivity {

	ImageView mGroupImg,mOwnerImg;
	TextView mGroupName,mOwnerName,mGroupDesc,mOwnerDesc;
	
	GroupInfo mGroupInfo;
	AccountInfo mOwnerInfo;
	Boolean mUserInGroup;
	
	ChooseGroupActionDialog mGroupActionDialog;
	
	public static Bundle getInentMyself(GroupInfo info) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewGroupDetailsActivity.class);
		b.putSerializable("ViewGroup", info);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mGroupInfo = (GroupInfo) getIntent().getSerializableExtra("ViewGroup");
		if ( mGroupInfo == null )finish();
		setContentView(R.layout.activity_view_group);
		initView();
	}

	void initView(){
		mGroupImg = (ImageView) findViewById(R.id.group_img);
		mGroupName = (TextView) findViewById(R.id.group_name);
		mGroupDesc = (TextView) findViewById(R.id.group_desc);
		mOwnerDesc = (TextView) findViewById(R.id.owner_desc);
		mOwnerImg = (ImageView) findViewById(R.id.owner_img);
		mOwnerName = (TextView) findViewById(R.id.owner_name);
		initViewData();
		
	}
	void initViewData(){
		mGroupDesc.setText(mGroupInfo.getDescription());
		mGroupName.setText(mGroupInfo.getDisplayName());
		loadOwnerData();
		checkAccountInGroup();
	}
	void loadOwnerData(){
		SContactAsyncHttpClient.post(
				AccountParams.getAccountByIdParams(mGroupInfo.getOwnerId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						mOwnerInfo = GsonHelper.getInfoFromJson(arg2, AccountInfo.class);
						if ( mOwnerInfo != null && mOwnerInfo.getId() != null ){
							
							mOwnerName.setText(mOwnerInfo.getDisplayName());
							mOwnerDesc.setText(mOwnerInfo.getDescription());
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
		});
	}
	void checkAccountInGroup(){
		SContactAsyncHttpClient.post(
				PhoneAndGroupParams.getCheckUserInGroupParams(
						mGroupInfo.getId(),AccountInfo.getInstance().getId()),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						PhoneAndGroupInfo result = GsonHelper.getInfoFromJson(arg2, PhoneAndGroupInfo.class);
						if ( result != null && result.getId() != null){
							mUserInGroup = true;
						} else {
							Bog.toastErrorInfo(arg2);
							mUserInGroup = false;
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Bog.toast(R.string.connect_server_err);
					}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_view_group_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_on_view_group:
				mGroupActionDialog = new ChooseGroupActionDialog(this, mGroupInfo);
				
				if ( AccountInfo.getInstance().getId().equals( mGroupInfo.getOwnerId() )){
					mGroupActionDialog.setMutilVisible(false, true, true, false, false);
				} else {
					if ( mUserInGroup != null ){
						if( mUserInGroup ){
							mGroupActionDialog.setMutilVisible(false, false, true, true, false);
						} else {
							mGroupActionDialog.setMutilVisible(false, false, true, true, true);
						}
					}
				}
				mGroupActionDialog.show();
				break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
