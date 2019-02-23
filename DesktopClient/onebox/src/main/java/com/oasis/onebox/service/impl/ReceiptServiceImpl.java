package com.oasis.onebox.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huel.dao.ReceiptDao;
import cn.huel.model.Receipt;
import cn.huel.service.ReceiptService;

@Service("receiptService")
public class ReceiptServiceImpl implements ReceiptService {

	@Autowired
	ReceiptDao receiptDao;

	@Override
	public int createNewRecord(Receipt receipt) {
		int flag = 0;
		String id = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		String name = receipt.getName();
		String gender = receipt.getGender();
		String identity = receipt.getIdentity();
		String workplace = receipt.getWorkplace();
		String position = receipt.getPosition();
		String address = receipt.getAddress();
		String telephone = receipt.getTelephone();
		String e_mail = receipt.getE_mail();
		String report = receipt.getReport();
		String check_in = receipt.getCheck_in();
		String check_out = receipt.getCheck_out();
		String accommodation = receipt.getAccommodation();
		String remark = receipt.getRemark();
		try {
			flag = receiptDao.createNewRecord(id, name, gender, identity, workplace, position, address, telephone,
					e_mail, report, check_in, check_out, accommodation, remark);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public Receipt selectById(String id) {
		return receiptDao.selectById(id);
	}

	@Override
	public List<Receipt> selectAllRecord() {
		return receiptDao.selectAllRecord();
	}

	@Override
	public List<Receipt> selectByName(String name) {
		return receiptDao.selectByName(name);
	}

	@Override
	public List<Receipt> selectByParam(String param1, String param2) {
		return receiptDao.selectByParam(param1, param2);
	}

	@Override
	public List<Receipt> selectByParams(String param1, String param2) {
		return receiptDao.selectByParams(param1, param2);
	}

}
