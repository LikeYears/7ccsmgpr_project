package com.oasis.onebox.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.huel.dao.UploadDao;
import cn.huel.model.Upload;
import cn.huel.service.UploadService;
import cn.huel.unit.DateUtil;

@Service("uploadService")
public class UploadServiceImpl implements UploadService {

	@Autowired
	private UploadDao uploadDao;

	@Override
	public List<String> selectPath() {
		return uploadDao.selectPath();
	}

	@Override
	public int createNewRecord(Upload upload) {
		int flag = 0;
		String id = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
		String title = upload.getTitle();
		String author = upload.getAuthor();
		String date = DateUtil.formatLongDateTime(new Date());
		String path = upload.getPath();
		String ip_add = upload.getIp_add();
		String fileName = upload.getFileName();
		try {
			flag = uploadDao.createNewRecord(id, title, author, date, path, ip_add, fileName);

		} catch (Exception e) {

			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public List<Upload> selectByTitle(String title) {
		// TODO Auto-generated method stub
		return uploadDao.selectByTitle(title);
	}

	@Override
	public List<Upload> selectByAuthor(String author) {
		// TODO Auto-generated method stub
		return uploadDao.selectByAuthor(author);
	}

	@Override
	public List<Upload> selectByIp(String ip) {
		// TODO Auto-generated method stub
		return uploadDao.selectByIp(ip);
	}

	@Override
	public List<Upload> selectAll() {
		// TODO Auto-generated method stub
		return uploadDao.selectAll();
	}

}
