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

package com.glaf.wechat.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.util.DateUtils;
import com.glaf.wechat.domain.WxLog;

public class WxLogJsonFactory {

	public static java.util.List<WxLog> arrayToList(JSONArray array) {
		java.util.List<WxLog> list = new java.util.ArrayList<WxLog>();
		for (int i = 0, len = array.size(); i < len; i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			WxLog model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	public static WxLog jsonToObject(JSONObject jsonObject) {
		WxLog model = new WxLog();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getLong("id"));
		}
		if (jsonObject.containsKey("accountId")) {
			model.setAccountId(jsonObject.getLong("accountId"));
		}
		if (jsonObject.containsKey("actorId")) {
			model.setActorId(jsonObject.getString("actorId"));
		}
		if (jsonObject.containsKey("ip")) {
			model.setIp(jsonObject.getString("ip"));
		}
		if (jsonObject.containsKey("createTime")) {
			model.setCreateTime(jsonObject.getDate("createTime"));
		}
		if (jsonObject.containsKey("operate")) {
			model.setOperate(jsonObject.getString("operate"));
		}
		if (jsonObject.containsKey("flag")) {
			model.setFlag(jsonObject.getInteger("flag"));
		}

		return model;
	}

	public static JSONArray listToArray(java.util.List<WxLog> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (WxLog model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static JSONObject toJsonObject(WxLog model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getAccountId() != null) {
			jsonObject.put("accountId", model.getAccountId());
		}
		if (model.getActorId() != null) {
			jsonObject.put("actorId", model.getActorId());
		}
		if (model.getIp() != null) {
			jsonObject.put("ip", model.getIp());
		}
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime",
					DateUtils.getDateTime(model.getCreateTime()));
		}
		if (model.getOperate() != null) {
			jsonObject.put("operate", model.getOperate());
		}
		jsonObject.put("flag", model.getFlag());
		return jsonObject;
	}

	public static ObjectNode toObjectNode(WxLog model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		if (model.getAccountId() != null) {
			jsonObject.put("accountId", model.getAccountId());
		}
		if (model.getActorId() != null) {
			jsonObject.put("actorId", model.getActorId());
		}
		if (model.getIp() != null) {
			jsonObject.put("ip", model.getIp());
		}
		if (model.getCreateTime() != null) {
			jsonObject.put("createTime",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_date",
					DateUtils.getDate(model.getCreateTime()));
			jsonObject.put("createTime_datetime",
					DateUtils.getDateTime(model.getCreateTime()));
		}
		if (model.getOperate() != null) {
			jsonObject.put("operate", model.getOperate());
		}
		jsonObject.put("flag", model.getFlag());
		return jsonObject;
	}

	private WxLogJsonFactory() {

	}

}
