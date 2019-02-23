package com.oasis.onebox.model;

public class Upload {

	private String id;
	private String title;
	private String author;
	private String date;
	private String path;
	private String ip_add;
	private String fileName;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Upload() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	public String getIp_add() {
		return ip_add;
	}

	public void setIp_add(String ip_add) {
		this.ip_add = ip_add;
	}

	@Override
	public String toString() {
		return "Upload [id=" + id + ", title=" + title + ", author=" + author + ", date=" + date + ", path=" + path
				+ "]";
	}
	
}
