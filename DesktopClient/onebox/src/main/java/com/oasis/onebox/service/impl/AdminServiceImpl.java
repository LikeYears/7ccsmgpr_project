package com.oasis.onebox.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oasis.onebox.dao.AdminDao;
import com.oasis.onebox.model.Admin;
import com.oasis.onebox.service.AdminService;

@Service("adminService")
public class AdminServiceImpl implements AdminService {
	@Autowired
	private AdminDao adminDao;

	@Override
	public boolean login(String username, String password) {
		Admin admin = this.adminDao.selectByName(username);
		return this.adminDao.selectByName(username) == null?false: admin.getPassword().equals(password);
	
	}

}
