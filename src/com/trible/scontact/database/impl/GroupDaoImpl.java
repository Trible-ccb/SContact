package com.trible.scontact.database.impl;

import java.sql.SQLException;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.trible.scontact.database.DBConstants;
import com.trible.scontact.database.DaoMangers;
import com.trible.scontact.database.dao.GroupDao;
import com.trible.scontact.pojo.GroupInfo;

public class GroupDaoImpl extends BaseDaoImpl<GroupInfo, Long> implements GroupDao{

	public GroupDaoImpl(ConnectionSource connectionSource,
			Class<GroupInfo> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	protected GroupDaoImpl(Class<GroupInfo> dataClass) throws SQLException {
		super(dataClass);
	}

	@Override
	public void deleteAll() {
		try {
			DaoMangers.getGroupDao().executeRawNoArgs("delete from " + DBConstants.GroupFieldName.table_name );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
