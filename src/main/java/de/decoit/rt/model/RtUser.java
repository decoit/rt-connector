/* 
 * Copyright (C) 2015 DECOIT GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.decoit.rt.model;

import java.util.HashMap;
import java.util.Map;


/**
 * POJO data structure to represent a RT user.
 * It features fields for all attributes returned by the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtUser {
	private long id;
	private String name;
	private String emailAddress = "";
	private String realName = "";
	private String nickName = "";
	private String gecos = "";
	private String organization = "";
	private String address1 = "";
	private String address2 = "";
	private String city = "";
	private String state = "";
	private String zip = "";
	private String country = "";
	private String homePhone = "";
	private String workPhone = "";
	private String mobilePhone = "";
	private String pagerPhone = "";
	private String contactInfo = "";
	private String comments = "";
	private String signature = "";
	private String lang = "";
	private boolean privileged = false;
	private boolean disabled = false;
	private Map<String, String> customFields = new HashMap<>();


	public long getId() {
		return id;
	}


	public void setId(long id) {
		if(id > 0) {
			this.id = id;
		}
		else {
			throw new IllegalArgumentException("ID cannot be loss or equal 0");
		}
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmailAddress() {
		return emailAddress;
	}


	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public String getRealName() {
		return realName;
	}


	public void setRealName(String realName) {
		this.realName = realName;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getGecos() {
		return gecos;
	}


	public void setGecos(String gecos) {
		this.gecos = gecos;
	}


	public String getOrganization() {
		return organization;
	}


	public void setOrganization(String organization) {
		this.organization = organization;
	}


	public String getAddress1() {
		return address1;
	}


	public void setAddress1(String address1) {
		this.address1 = address1;
	}


	public String getAddress2() {
		return address2;
	}


	public void setAddress2(String address2) {
		this.address2 = address2;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getZip() {
		return zip;
	}


	public void setZip(String zip) {
		this.zip = zip;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getHomePhone() {
		return homePhone;
	}


	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}


	public String getWorkPhone() {
		return workPhone;
	}


	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}


	public String getMobilePhone() {
		return mobilePhone;
	}


	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}


	public String getPagerPhone() {
		return pagerPhone;
	}


	public void setPagerPhone(String pagerPhone) {
		this.pagerPhone = pagerPhone;
	}


	public String getContactInfo() {
		return contactInfo;
	}


	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public String getSignature() {
		return signature;
	}


	public void setSignature(String signature) {
		this.signature = signature;
	}


	public String getLang() {
		return lang;
	}


	public void setLang(String lang) {
		this.lang = lang;
	}


	public boolean isPrivileged() {
		return privileged;
	}


	public void setPrivileged(boolean privileged) {
		this.privileged = privileged;
	}


	public boolean isDisabled() {
		return disabled;
	}


	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}


	public void setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
	}


	public void addCustomField(String name, String value) {
		this.customFields.put(name, value);
	}
}
