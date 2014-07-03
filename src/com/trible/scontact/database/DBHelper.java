package com.trible.scontact.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.trible.scontact.pojo.ContactInfo;
import com.trible.scontact.pojo.GroupInfo;
import com.trible.scontact.pojo.PhoneAndGroupInfo;
import com.trible.scontact.pojo.UserRelationInfo;

public class DBHelper extends OrmLiteSqliteOpenHelper{

	static Context mContext;
	static DBHelper mInstance;
	
	public static void init(Context c){
		mContext = c;
	}
	public static void reset(){
		mInstance = null;
		DaoMangers.reset();
	}
	public static DBHelper getInstance(){
		if ( mInstance == null ){
			mInstance = new DBHelper(mContext);
		}
		return mInstance;
	}
	public DBHelper(Context c){
		super(c,DBConstants.DBNAME,null,DBConstants.VERSION);
		mContext = c;
	}
	public DBHelper(Context context, String databaseName,
			CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			TableUtils.createTableIfNotExists(arg1, ContactInfo.class);
			TableUtils.createTableIfNotExists(arg1, GroupInfo.class);
			TableUtils.createTableIfNotExists(arg1, PhoneAndGroupInfo.class);
			TableUtils.createTableIfNotExists(arg1, UserRelationInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2,
			int arg3) {
		onCreate(arg0, arg1);
	}


}
