package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.SearchResultAdapter;
import com.trible.scontact.components.adpater.SearchResultAdapter.SectionData;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.components.widgets.TabLayoutHelper;
import com.trible.scontact.controller.impl.SearchLocalFriendControl;
import com.trible.scontact.controller.impl.SearchLocalGroupControl;
import com.trible.scontact.controller.impl.SearchRemoteFriendControl;
import com.trible.scontact.controller.impl.SearchRemoteGroupControl;
import com.trible.scontact.models.Friendinfo;
import com.trible.scontact.networks.NetWorkEvent;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.SimpleAsynTask;
import com.trible.scontact.networks.SimpleAsynTask.AsynTaskListner;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.ErrorInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.InputUtil;
import com.trible.scontact.utils.ListUtil;
import com.umeng.common.net.t;


/**
 * @author Trible Chen
 * a class for searching group or friends by name
 *
 */
public final class SearchGroupOrFriendActivity extends CustomSherlockFragmentActivity 
											implements OnItemClickListener{

	public static final String SEARCH_NAME = "SearchName";
	String mQueryString;
	
	ListView mSearchResultListView;
	List<Friendinfo> mResultFriendsData;
	List<GroupInfo> mResultGroupData;
	
	LoadingDialog mLoadingDialog;
	
	ChooseGroupActionDialog mGroupActionDialog;
	
	TabLayoutHelper mTabLayoutHelper;
	SearchResultAdapter mAdapter;
	SimpleAsynTask mSearchLocalTask,mSearchReomteTask;
	
	public static Bundle getIntentMyselfBundle(String qStr){
		Bundle b = new Bundle();
		b.putSerializable("clazz", SearchGroupOrFriendActivity.class);
		b.putString(SEARCH_NAME, qStr);
		return b;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search);
		
		initData();
		initView();
	}

	void initData(){
		mSearchLocalTask = new SimpleAsynTask();
		mSearchReomteTask = new SimpleAsynTask();
		mResultFriendsData = new ArrayList<Friendinfo>();
		mResultGroupData = new ArrayList<GroupInfo>();
		mAdapter = new SearchResultAdapter(this);
	}
	void initView(){
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.setTipText(R.string.waiting);
		
		mTabLayoutHelper = new TabLayoutHelper((ViewGroup) findViewById(R.id.tabs_layout),
				new int[]{R.string.search_people_lable,R.string.search_group_lable});
		mTabLayoutHelper.setDefaultSelection(0);
		mTabLayoutHelper.setOnItemClickListner(this);
		mSearchResultListView = (ListView) findViewById(R.id.search_list_view);
		mSearchResultListView.setOnItemClickListener(this);
		mSearchResultListView.setAdapter(mAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_search_menu, menu);
		MenuItem sItem = menu.findItem(R.id.action_search);
		final SearchView mSearchable = (SearchView) sItem.getActionView();
		final SearchAutoComplete mQueryTextView = (SearchAutoComplete) mSearchable.findViewById(R.id.abs__search_src_text);
		if ( mSearchable != null ){
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			mSearchable.setSearchableInfo(searchManager.getSearchableInfo( getComponentName()));
			mSearchable.setOnQueryTextListener(new OnQueryTextListener() {
				
				@Override
				public boolean onQueryTextSubmit(String query) {
					mQueryString = query;
					onSubmit();
					InputUtil.hideIME(SearchGroupOrFriendActivity.this);
					return true;
				}
				
				@Override
				public boolean onQueryTextChange(String newText) {
					return false;
				}
			});
			View.OnKeyListener mTextKeyListener = new View.OnKeyListener() {
			        public boolean onKey(View v, int keyCode, KeyEvent event) {
			        	if ( keyCode == KeyEvent.KEYCODE_SEARCH 
			        			&& event.getAction() == KeyEvent.ACTION_UP ){
			        		mQueryString = mQueryTextView.getText().toString();
			        		onSubmit();
			        		InputUtil.hideIME(SearchGroupOrFriendActivity.this);
							return true;
			        	}
			            return false;
			        }
			};
			mQueryTextView.setOnKeyListener(mTextKeyListener);
			sItem.expandActionView();
			mSearchable.setIconifiedByDefault(false);
			mQueryTextView.requestFocus();
		
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( parent == null ){//tab item click
			switch (position) {
				case 0:
					mAdapter.displayFriend();
					break;
				case 1:
					mAdapter.displayGroup();
					break;
				default:
					break;
			}
		} else {//list item click
			Object tgt =  mAdapter.getItem(position);
			if ( tgt instanceof GroupInfo ){
				GroupInfo tmp = (GroupInfo) tgt;
				simpleDisplayActivity(
						ViewGroupDetailsActivity.getInentMyself(tmp));
			} else if ( tgt instanceof AccountInfo){
				AccountInfo tmp = (AccountInfo) tgt;
				simpleDisplayActivity(
						ViewFriendDetailsActivity.getInentMyself(tmp,null));
			}
		}
	}
	void onSubmit(){
		mLoadingDialog.show();
		mSearchLocalTask = new SimpleAsynTask();
		mSearchReomteTask = new SimpleAsynTask();
		mAdapter.clear();
		mSearchLocalTask.doTask(new AsynTaskListner() {
			List<GroupInfo> glocal;
			List<AccountInfo> flocal;
			@Override
			public void onTaskDone(NetWorkEvent event) {
				mAdapter.addFriendSection(
						new SectionData(getString(R.string.local_contacts)), flocal);
				mAdapter.addGroupSection(
						new SectionData(getString(R.string.local_contacts)), glocal);
			}
			
			@Override
			public void doInBackground() {
				flocal = new SearchLocalFriendControl().searchByName(mQueryString);
				glocal = new SearchLocalGroupControl().searchByName(mQueryString);
			}
		});
		SContactAsyncHttpClient.post(
				GroupParams.getSearchGroupParams(mQueryString),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						List<GroupInfo> results = GsonHelper.getInfosFromJson(arg2,new GroupInfo().listType());
						mAdapter.addGroupSection(
								new SectionData(getString(R.string.remote_contacts)), results);
						if ( results == null ){
							ErrorInfo err = GsonHelper.getInfoFromJson(arg2, ErrorInfo.class);
							Bog.toast(err == null ? ErrorInfo.getUnkownErr().toString() : err.toString());
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
		SContactAsyncHttpClient.post(
				AccountParams.getSearchAccountInfosParams(mQueryString),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						List<AccountInfo> results = GsonHelper.getInfosFromJson(arg2,new AccountInfo().listType());
						mAdapter.addFriendSection(new SectionData(getString(R.string.remote_contacts)), results);
						if ( results == null ){
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
}
