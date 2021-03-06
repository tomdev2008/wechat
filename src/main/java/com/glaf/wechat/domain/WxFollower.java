/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.glaf.wechat.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.base.JSONable;
import com.glaf.wechat.util.WxFollowerJsonFactory;

@Entity
@Table(name = "WX_FOLLOWER")
public class WxFollower implements Serializable, JSONable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_", nullable = false)
	protected Long id;

	@Column(name = "ACCOUNTID_")
	protected Long accountId;

	@Column(name = "ACTORID_", length = 50)
	protected String actorId;
	
	@Column(name = "SOURCEID_", length = 200)
	protected String sourceId;

	@Column(name = "OPENID_", length = 200)
	protected String openId;

	@Column(name = "NICKNAME_", length = 200)
	protected String nickName;

	@Column(name = "SEX_", length = 1)
	protected String sex;

	@Column(name = "MOBILE_", length = 20)
	protected String mobile;

	@Column(name = "MAIL_", length = 100)
	protected String mail;

	@Column(name = "TELEPHONE_", length = 50)
	protected String telephone;

	@Column(name = "HEADIMGURL_", length = 500)
	protected String headimgurl;

	@Column(name = "PROVINCE_", length = 100)
	protected String province;

	@Column(name = "CITY_", length = 100)
	protected String city;

	@Column(name = "COUNTRY_", length = 100)
	protected String country;

	@Column(name = "LANGUAGE_", length = 50)
	protected String language;

	@Column(name = "LOCKED_")
	protected Integer locked;

	@Column(name = "REMARK", length = 250)
	protected String remark;

	@Column(name = "SUBSCRIBETIME_")
	protected Long subscribeTime;

	@Column(name = "UNSUBSCRIBETIME_")
	protected Long unsubscribeTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATEDATE_")
	protected Date createDate;

	public WxFollower() {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WxFollower other = (WxFollower) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getAccountId() {
		return this.accountId;
	}
	
	

	public String getActorId() {
		return this.actorId;
	}

	public String getCity() {
		return this.city;
	}

	public String getCountry() {
		return this.country;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public String getHeadimgurl() {
		return this.headimgurl;
	}

	public Long getId() {
		return this.id;
	}

	public String getLanguage() {
		return this.language;
	}

	public Integer getLocked() {
		return this.locked;
	}

	public String getMail() {
		return this.mail;
	}

	public String getMobile() {
		return this.mobile;
	}

	public String getNickName() {
		return this.nickName;
	}

	public String getOpenId() {
		return this.openId;
	}

	public String getProvince() {
		return this.province;
	}

	public String getRemark() {
		return this.remark;
	}

	public String getSex() {
		return this.sex;
	}

	public String getSourceId() {
		return sourceId;
	}

	public Long getSubscribeTime() {
		return this.subscribeTime;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public Long getUnsubscribeTime() {
		return unsubscribeTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public WxFollower jsonToObject(JSONObject jsonObject) {
		return WxFollowerJsonFactory.jsonToObject(jsonObject);
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLocked(Integer locked) {
		this.locked = locked;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public void setSubscribeTime(Long subscribeTime) {
		this.subscribeTime = subscribeTime;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setUnsubscribeTime(Long unsubscribeTime) {
		this.unsubscribeTime = unsubscribeTime;
	}

	public JSONObject toJsonObject() {
		return WxFollowerJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return WxFollowerJsonFactory.toObjectNode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}
