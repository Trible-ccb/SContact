package com.trible.scontact.components.activity;

import com.trible.scontact.pojo.GroupInfo;

import android.os.Bundle;


/**
 * @author Trible Chen
 * here you can see the details of a special group and do some action;
 */
public class ViewGroupDetailsActivity extends CustomSherlockFragmentActivity {

	public static Bundle getInentMyself(GroupInfo info) {
		Bundle b = new Bundle();
		b.putSerializable("clazz", ViewGroupDetailsActivity.class);
		b.putSerializable("ViewGroup", info);
		return b;
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

}
