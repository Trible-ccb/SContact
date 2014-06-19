package com.trible.scontact.components.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.MixFriendsGroupsListAdapter;
import com.trible.scontact.components.adpater.MixFriendsGroupsListAdapter.SectionData;
import com.trible.scontact.components.widgets.ChooseGroupActionDialog;
import com.trible.scontact.components.widgets.LoadingDialog;
import com.trible.scontact.networks.SContactAsyncHttpClient;
import com.trible.scontact.networks.params.AccountParams;
import com.trible.scontact.networks.params.GroupParams;
import com.trible.scontact.pojo.AccountInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.GsonHelper;
import com.trible.scontact.utils.Bog;
import com.trible.scontact.utils.InputUtil;
import com.trible.scontact.utils.ListUtil;


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
	List<Object> mResultDatas;
	
	LoadingDialog mLoadingDialog;
	
	ChooseGroupActionDialog mGroupActionDialog;
	
	MixFriendsGroupsListAdapter mAdapter;
	
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
		mResultDatas = new ArrayList<Object>();
		mAdapter = new MixFriendsGroupsListAdapter(this);
		mAdapter.showEmptyView(false);
		
	}
	void initView(){
		mLoadingDialog = new LoadingDialog(this);
		mLoadingDialog.setTipText(R.string.searching);
		
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
		
		}
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( parent == null ){//tab item click
		} else {//list item click
			Object tgt =  mAdapter.getItem(position);
			if ( tgt instanceof GroupInfo ){
				GroupInfo tmp = (GroupInfo) tgt;
				simpleDisplayActivity(
						ViewGroupDetailsActivity.getInentMyself(tmp));
			} else if ( tgt instanceof AccountInfo){
				AccountInfo tmp = (AccountInfo) tgt;
				if (AccountInfo.getInstance().getId().equals(tmp.getId()) ){
					simpleDisplayActivity(MyProfileActivity.class);
				} else {
					simpleDisplayActivity(
							ViewFriendDetailsActivity.getInentMyself(tmp,null));
				}
			}
		}
	}
	void onSubmit(){
		mLoadingDialog.show();
		mAdapter.clear();
		mAdapter.showEmptyView(false);
		SContactAsyncHttpClient.post(
				GroupParams.getSearchGroupParams(mQueryString),
				null, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						List<GroupInfo> results = GsonHelper.getInfosFromJson(arg2,new GroupInfo().listType());
						if ( results == null ){
							Bog.toastErrorInfo(arg2);
						} else {
							if ( ListUtil.isNotEmpty(results) ){
								SectionData groupsection = new SectionData(getString(R.string.search_group_lable));
								mResultDatas.add(groupsection);
								mResultDatas.addAll(results);
							}
						}
					}
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
					}
					@Override
					public void onFinish() {
						SContactAsyncHttpClient.post(
								AccountParams.getSearchAccountInfosParams(mQueryString),
								null, new AsyncHttpResponseHandler(){
									@Override
									public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
										List<AccountInfo> results = GsonHelper.getInfosFromJson(arg2,new AccountInfo().listType());
										if ( results == null ){
											Bog.toastErrorInfo(arg2);
										} else {
											if ( ListUtil.isNotEmpty(results) ){
												SectionData peoplesection = new SectionData(getString(R.string.search_people_lable));
												mResultDatas.add(peoplesection);
												mResultDatas.addAll(results);
											}
										}
										mAdapter.mEmptyData.mText = getString(R.string.empty_search_result);
									}
									@Override
									public void onFailure(int arg0, Header[] arg1, byte[] arg2,
											Throwable arg3) {
										mAdapter.mEmptyData.mText = getString(R.string.connect_server_err);
									}
									@Override
									public void onFinish() {
										mLoadingDialog.getDialog().dismissDialogger();
										mAdapter.setData(mResultDatas);
										mAdapter.showEmptyView(true);
									}
								});
					}
				});
		

	}
}
