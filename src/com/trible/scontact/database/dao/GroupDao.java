package com.trible.scontact.database.dao;

import com.j256.ormlite.dao.Dao;
import com.trible.scontact.pojo.GroupInfo;

public interface GroupDao extends Dao<GroupInfo, Long>{

	void deleteAll();
}
