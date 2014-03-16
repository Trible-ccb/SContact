package com.trible.scontact.components.activity;

import java.security.spec.MGF1ParameterSpec;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.actionbarsherlock.widget.SearchView.SearchAutoComplete;
import com.trible.scontact.R;
import com.trible.scontact.components.adpater.SearchResultAdapter;
import com.trible.scontact.components.widgets.TabLayoutHelper;


/**
 * @author Trible Chen
 * a class for searching group or friends by name
 *
 */
public class SearchGroupOrFriendActivity extends CustomSherlockFragmentActivity 
											implements OnClickListener,OnItemClickListener{

	public static final String SEARCH_NAME = "SearchName";
	String mQueryString;
	
	ListView mSearchResultListView;
	TabLayoutHelper mTabLayoutHelper;
	SearchResultAdapter mAdapter;
	
	public static Bundle getIntentMyselfBundle(String qStr){
		Bundle b = new Bundle();
		b.putString(SEARCH_NAME, qStr);
		return b;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_search);
		initView();
		
	}

	void initView(){
		mTabLayoutHelper = new TabLayoutHelper((ViewGroup) findViewById(R.id.tabs_layout),
				new int[]{R.string.search_people_lable,R.string.search_group_lable});
		mTabLayoutHelper.setDefaultSelection(0);
		mTabLayoutHelper.setOnItemClickListner(this);
		mSearchResultListView = (ListView) findViewById(R.id.search_list_view);
		mSearchResultListView.setOnItemClickListener(this);
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
//					onSubmit();
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
//			        		onSubmit();
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
	public void onClick(View v) {
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( parent == null ){//tab item click
			switch (position) {
				case 0:
					
					break;
				case 1:
					break;
				default:
					break;
			}
		} else {//list item click
			
		}
	}
	
}
