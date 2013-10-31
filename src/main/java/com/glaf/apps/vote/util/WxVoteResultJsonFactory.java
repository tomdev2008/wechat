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

package com.glaf.apps.vote.util;

import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.glaf.core.util.DateUtils;
import com.glaf.apps.vote.domain.*;

public class WxVoteResultJsonFactory {

	public static java.util.List<WxVoteResult> arrayToList(JSONArray array) {
		java.util.List<WxVoteResult> list = new java.util.ArrayList<WxVoteResult>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			WxVoteResult model = jsonToObject(jsonObject);
			list.add(model);
		}
		return list;
	}

	public static WxVoteResult jsonToObject(JSONObject jsonObject) {
		WxVoteResult model = new WxVoteResult();
		if (jsonObject.containsKey("id")) {
			model.setId(jsonObject.getLong("id"));
		}
		if (jsonObject.containsKey("voteId")) {
			model.setVoteId(jsonObject.getLong("voteId"));
		}
		if (jsonObject.containsKey("result")) {
			model.setResult(jsonObject.getString("result"));
		}
		if (jsonObject.containsKey("ip")) {
			model.setIp(jsonObject.getString("ip"));
		}
		if (jsonObject.containsKey("voteDate")) {
			model.setVoteDate(jsonObject.getDate("voteDate"));
		}
		if (jsonObject.containsKey("actorId")) {
			model.setActorId(jsonObject.getString("actorId"));
		}

		return model;
	}

	public static JSONArray listToArray(java.util.List<WxVoteResult> list) {
		JSONArray array = new JSONArray();
		if (list != null && !list.isEmpty()) {
			for (WxVoteResult model : list) {
				JSONObject jsonObject = model.toJsonObject();
				array.add(jsonObject);
			}
		}
		return array;
	}

	public static JSONObject toJsonObject(WxVoteResult model) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		jsonObject.put("voteId", model.getVoteId());
		if (model.getResult() != null) {
			jsonObject.put("result", model.getResult());
		}
		if (model.getIp() != null) {
			jsonObject.put("ip", model.getIp());
		}
		if (model.getVoteDate() != null) {
			jsonObject.put("voteDate", DateUtils.getDate(model.getVoteDate()));
			jsonObject.put("voteDate_date",
					DateUtils.getDate(model.getVoteDate()));
			jsonObject.put("voteDate_datetime",
					DateUtils.getDateTime(model.getVoteDate()));
		}
		if (model.getActorId() != null) {
			jsonObject.put("actorId", model.getActorId());
		}
		return jsonObject;
	}

	public static ObjectNode toObjectNode(WxVoteResult model) {
		ObjectNode jsonObject = new ObjectMapper().createObjectNode();
		jsonObject.put("id", model.getId());
		jsonObject.put("_id_", model.getId());
		jsonObject.put("_oid_", model.getId());
		jsonObject.put("voteId", model.getVoteId());
		if (model.getResult() != null) {
			jsonObject.put("result", model.getResult());
		}
		if (model.getIp() != null) {
			jsonObject.put("ip", model.getIp());
		}
		if (model.getVoteDate() != null) {
			jsonObject.put("voteDate", DateUtils.getDate(model.getVoteDate()));
			jsonObject.put("voteDate_date",
					DateUtils.getDate(model.getVoteDate()));
			jsonObject.put("voteDate_datetime",
					DateUtils.getDateTime(model.getVoteDate()));
		}
		if (model.getActorId() != null) {
			jsonObject.put("actorId", model.getActorId());
		}
		return jsonObject;
	}

	private WxVoteResultJsonFactory() {

	}

}
