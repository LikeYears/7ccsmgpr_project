package com.oasis.onebox.service;

import java.util.List;

import com.oasis.onebox.model.Receipt;

public interface ReceiptService {
	
	public int createNewRecord(Receipt receipt);
	
	public Receipt selectById(String id);
	
	public List<Receipt> selectAllRecord();
	
	public List<Receipt> selectByName(String name);
	
	public List<Receipt> selectByParam(String param1, String param2);
	
	public List<Receipt> selectByParams(String param1, String param2);

}
