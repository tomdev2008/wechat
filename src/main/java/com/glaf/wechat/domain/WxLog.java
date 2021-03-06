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

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.base.JSONable;
import com.glaf.wechat.util.WxLogJsonFactory;

@Entity
@Table(name = "WX_LOG")
public class WxLog implements Serializable, JSONable {
	private static final long serialVersionUID = 3489584842305336744L;

	@Id
	@Column(name = "ID_", nullable = false)
	private long id;

	@Column(name = "ACCOUNTID_")
	private Long accountId;

	@Column(name = "ACTORID_", length = 50)
	private String actorId;

	@Column(name = "OPENID_", length = 200)
	private String openId;

	@Column(name = "IP_", length = 100)
	private String ip;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATETIME_")
	private Date createTime;

	@Column(name = "OPERATE_", length = 50)
	private String operate;

	@Column(name = "CONTENT_", length = 500)
	private String content;

	@Column(name = "FLAG_")
	private int flag;

	@javax.persistence.Transient
	private String suffix;

	public WxLog() {

	}

	public Long getAccountId() {
		return accountId;
	}

	public String getContent() {
		return content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public int getFlag() {
		return flag;
	}

	public long getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getOpenId() {
		return openId;
	}

	public String getOperate() {
		return operate;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WxLog other = (WxLog) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public WxLog jsonToObject(JSONObject jsonObject) {
		return WxLogJsonFactory.jsonToObject(jsonObject);
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public JSONObject toJsonObject() {
		return WxLogJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode() {
		return WxLogJsonFactory.toObjectNode(this);
	}

	public String toString() {
		return toJsonObject().toJSONString();
	}

}