package com.oasis.onebox.model;

public class Receipt {
	private String id;
	private String name;
	private String gender;
	private String identity;
	private String workplace;
	private String position;
	private String address;
	private String telephone;
	private String e_mail;
	private String report;
	private String check_in;
	private String check_out;
	private String accommodation;
	private String remark;
	
	
	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}


	public Receipt() {
		super();
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getIdentity() {
		return identity;
	}


	public void setIdentity(String identity) {
		this.identity = identity;
	}


	public String getWorkplace() {
		return workplace;
	}


	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}


	public String getPosition() {
		return position;
	}


	public void setPosition(String position) {
		this.position = position;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getTelephone() {
		return telephone;
	}


	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}


	public String getE_mail() {
		return e_mail;
	}


	public void setE_mail(String e_mail) {
		this.e_mail = e_mail;
	}


	public String getReport() {
		return report;
	}


	public void setReport(String report) {
		this.report = report;
	}


	public String getCheck_in() {
		return check_in;
	}


	public void setCheck_in(String check_in) {
		this.check_in = check_in;
	}


	public String getCheck_out() {
		return check_out;
	}


	public void setCheck_out(String check_out) {
		this.check_out = check_out;
	}


	public String getAccommodation() {
		return accommodation;
	}


	public void setAccommodation(String accommodation) {
		this.accommodation = accommodation;
	}


	@Override
	public String toString() {
		return "Receipt [id=" + id + ", name=" + name + ", gender=" + gender + ", identity=" + identity + ", workplace="
				+ workplace + ", position=" + position + ", address=" + address + ", telephone=" + telephone
				+ ", e_mail=" + e_mail + ", report=" + report + ", check_in=" + check_in + ", check_out=" + check_out
				+ ", accommodation=" + accommodation + "]";
	}

	
}
