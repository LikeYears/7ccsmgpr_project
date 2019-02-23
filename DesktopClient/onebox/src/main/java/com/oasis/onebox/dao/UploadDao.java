package com.oasis.onebox.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.oasis.onebox.model.Upload;



public interface UploadDao {
	String CREATE_NEW_RECORD = "insert into upload values(#{0},#{1},#{2},#{3},#{4},#{5},#{6})";
	
	String SELECT_BY_TITLE="select * from upload where title=#{0}";
	
	String SELECT_BY_AUTHOR="select * from upload where author=#{0}";
	
//	String SELECT_BY_PATH="select * from upload where path=#{0}";
	
	String SELECT_BY_IP="select * from upload where ip_add=#{0}";
	
	String SELECT_PATH="select path from upload";
	
	String SELECT_ALL_RECORD="select * from upload";
	


	@Insert(CREATE_NEW_RECORD)
	public int createNewRecord(String id, String title, String author, String date, String path, String ip_add, String fileName);
	
	@Select(SELECT_BY_TITLE)
	public List<Upload> selectByTitle(String title);
	
	@Select(SELECT_BY_AUTHOR)
	public List<Upload> selectByAuthor(String author);
	
	@Select(SELECT_BY_IP)
	public List<Upload> selectByIp(String ip);
	
	@Select(SELECT_PATH)	
	public List<String> selectPath();
	
	@Select(SELECT_ALL_RECORD)
	public List<Upload> selectAll();
}
