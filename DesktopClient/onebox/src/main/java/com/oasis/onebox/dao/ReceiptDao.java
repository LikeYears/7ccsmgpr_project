package com.oasis.onebox.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.oasis.onebox.model.Receipt;

public interface ReceiptDao {

	String CREATE_NEW_RECORD = "insert into receipt values(#{0},#{1},#{2},#{3},#{4},#{5},#{6},#{7},#{8},#{9},#{10},#{11},#{12},#{13})";

	String SELECT_ALL_RECORD = "select * from receipt";

	String SELECT_BY_ID = "select * from receipt where id = #{0}";

	String SELECT_BY_NAME = "select * from receipt where name = #{0}";

	String SELECT_BY_PARAM = "select * from receipt where #{1} = #{0}";

	String SELECT_BY_PARAMS = "select * from receipt where #{1} like %#{0}%";

	String UPDATE_RECORD = "";
	/**
	 * todo
	 */
	
	String DELETE_RECORD = "delete receipt where id = #{0}";

	@Insert(CREATE_NEW_RECORD)
	public int createNewRecord(String id, String name, String gender, String identity, String workplace,
                               String position, String address, String telephone, String e_mail, String report, String check_in,
                               String check_out, String accommodation, String remark);

	@Select(SELECT_ALL_RECORD)
	public List<Receipt> selectAllRecord();
	
	@Select(SELECT_BY_ID)
	public Receipt selectById(String id);

	@Select(SELECT_BY_NAME)
	public List<Receipt> selectByName(String name);

	@Select(SELECT_BY_PARAM)
	public List<Receipt> selectByParam(String param1, String param2);

	@Insert(SELECT_BY_PARAMS)
	public List<Receipt> selectByParams(String param1, String param2);

}
