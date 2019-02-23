package com.oasis.onebox.dao;

import org.apache.ibatis.annotations.Select;

import com.oasis.onebox.model.Admin;

public interface AdminDao {

	String SELECT_BY_NAME = "select * from admin where username = #{0}";
	
	@Select(SELECT_BY_NAME)
	public Admin selectByName(String username) ;
		
}
