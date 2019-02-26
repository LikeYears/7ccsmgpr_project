package com.oasis.onebox.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;


import com.oasis.onebox.model.Upload;
import com.oasis.onebox.service.AdminService;
import com.oasis.onebox.service.UploadService;

@Controller
public class AdminController {

	@Autowired
	AdminService adminService;
	
	@Autowired
	UploadService uploadService;
	


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String admin() {
		return "login";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.POST)
	public String login(@RequestParam("username") String username, @RequestParam("password") String password) {
		if (adminService.login(username, password)) {
			return "admin";
		} else {
			return "error";
		}
	}
	
	@RequestMapping(value = "/queryFiles", method = RequestMethod.GET)
	public String queryFiles(ModelMap map) {
		return "fileQuery";
	}
	
	@RequestMapping(value="/queryReceipts",method=RequestMethod.GET)
	public String queryReceipts() {
		return "receiptQuery";
	}

	@RequestMapping(value = "/getFiles", method = RequestMethod.POST)
	public void getFiles(HttpServletResponse response,HttpServletRequest request) throws IOException {
		List<Upload> lists = uploadService.selectAll();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Gson gson = new Gson();
		out.println(gson.toJson(lists));
		out.flush();
		out.close();
	}

}
