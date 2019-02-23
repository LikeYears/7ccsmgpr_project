package com.oasis.onebox.service;

import java.util.List;

import com.oasis.onebox.model.Upload;

public interface UploadService {
	
	public int createNewRecord(Upload upload);
	
	public List<Upload> selectByTitle(String title);
	
	public List<Upload> selectByAuthor(String author);
	
	public List<Upload> selectByIp(String ip);
	
	public List<String> selectPath();
	
	public List<Upload> selectAll();

}
