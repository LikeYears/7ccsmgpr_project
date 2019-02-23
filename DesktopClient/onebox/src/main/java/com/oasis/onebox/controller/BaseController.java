package com.oasis.onebox.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.oasis.onebox.model.Receipt;
import com.oasis.onebox.model.Upload;
import com.oasis.onebox.service.AdminService;
import com.oasis.onebox.service.ReceiptService;
import com.oasis.onebox.service.UploadService;
import com.oasis.onebox.unit.DateUtil;

@Controller
@RequestMapping("/")
public class BaseController {

	@Autowired
	AdminService adminService;

	@Autowired
	UploadService uploadService;

	@Autowired
	ReceiptService receiptService;

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public void fileUpload(@RequestParam(value = "title", required = true) String title,
			@RequestParam(value = "author", required = true) String author,
			@RequestParam(value = "file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter outprint = response.getWriter();
		String message = "";
		if (title == null || title.isEmpty() || title.length() == 0) {
			message = "请输入稿件标题！(Please type your file\\'s title!)";
		} else if (author == null || author.isEmpty() || "".equals(author) || author.length() == 0) {
			message = "请输入稿件作者！(Please type your file\\'s author!)";
		} else {
			String se = File.separator;
			String uploadDir = "1D9670801B48A24FAE01C527F625CAF20937AF7A";
			String savePath = request.getServletContext().getRealPath(uploadDir);
			savePath = savePath + se + author;
			File newFile = new File(savePath);
			if (!newFile.exists() && !newFile.isDirectory()) {
				newFile.mkdir();
			}
			String fileName = file.getOriginalFilename();
		//	if (checkFile(fileName)) {
			if ( 1==1) {
				try {

					InputStream in = file.getInputStream();
					byte[] bs = new byte[2048];
					FileOutputStream out = new FileOutputStream(savePath + se + fileName);
					int length = 0;
					while ((length = in.read(bs)) > 0) {
						out.write(bs, 0, length);
					}
					in.close();
					out.close();

					Upload upload = new Upload();
					upload.setAuthor(author);
					upload.setTitle(title);
					upload.setPath(request.getContextPath() + se + uploadDir + se + author + se + fileName);
					upload.setFileName(fileName);
					upload.setIp_add(getIp(request));
					uploadService.createNewRecord(upload);
					message = "上传成功！(Upload Success!)";
				} catch (IOException e) {
					message = "上传失败！(Upload Failed!)";
					e.printStackTrace();
				}
			} else {
				message = "请选择正确格式的文件！";
			}
		}
		String text = "<script>alert('" + message + "');window.location.href='tougao.jsp';</script>";

		outprint.write(text);
		outprint.flush();
		outprint.close();

	}

	// @RequestMapping("upload2")
	// public String test(ModelMap map) {
	// // if (adminService.login("root", "1234567"))
	// // return "redirect:/upload/abcd.txt";
	// List<String> lists = uploadService.selectPath();
	// map.addAttribute("paths", lists);
	// return "index";
	// }
	@RequestMapping(value = "receipt", method = RequestMethod.POST)
	public void receipt(Receipt receipt, HttpServletRequest request, HttpServletResponse response) {
		int flag = 0;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			int diff = DateUtil.getTimeDifference(receipt.getCheck_in(), receipt.getCheck_out());
			if (diff > 0) {
				flag = receiptService.createNewRecord(receipt);
			} else if (diff < 0) {
				flag = 0;
			}
			out.println(flag);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String getIp(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private boolean checkFile(String fileName) {
		String suffixList = "txt,doc,docx,pdf,xls,xlsx";
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (suffixList.contains(suffix.trim().toLowerCase())) {
			return true;
		}
		return false;
	}

}
