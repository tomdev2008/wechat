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

package com.glaf.wechat.web.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.glaf.core.util.PageResult;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.wechat.domain.WxCategory;
import com.glaf.wechat.query.WxCategoryQuery;
import com.glaf.wechat.service.WxCategoryService;

@Controller
@Path("/rs/wx/wxCategory")
public class WxCategoryResourceRest {
	protected static final Log logger = LogFactory
			.getLog(WxCategoryResourceRest.class);

	protected WxCategoryService wxCategoryService;

	@POST
	@Path("/deleteAll")
	@ResponseBody
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public byte[] deleteAll(@Context HttpServletRequest request)
			throws IOException {
		String rowIds = request.getParameter("ids");
		if (rowIds != null) {
			List<Long> ids = StringTools.splitToLong(rowIds);
			if (ids != null && !ids.isEmpty()) {
				wxCategoryService.deleteByIds(ids);
			}
		}
		return ResponseUtils.responseJsonResult(true);
	}

	@POST
	@Path("/delete")
	@ResponseBody
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public byte[] deleteById(@Context HttpServletRequest request)
			throws IOException {
		wxCategoryService.deleteById(RequestUtils.getLong(request, "id"));
		return ResponseUtils.responseJsonResult(true);
	}

	@GET
	@POST
	@Path("/list")
	@ResponseBody
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public byte[] list(@Context HttpServletRequest request) throws IOException {
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxCategoryQuery query = new WxCategoryQuery();
		Tools.populate(query, params);

		String gridType = ParamUtils.getString(params, "gridType");
		if (gridType == null) {
			gridType = "easyui";
		}
		int start = 0;
		int limit = 10;
		String orderName = null;
		String order = null;

		int pageNo = ParamUtils.getInt(params, "page");
		limit = ParamUtils.getInt(params, "rows");
		start = (pageNo - 1) * limit;
		orderName = ParamUtils.getString(params, "sortName");
		order = ParamUtils.getString(params, "sortOrder");

		if (start < 0) {
			start = 0;
		}

		if (limit <= 0) {
			limit = PageResult.DEFAULT_PAGE_SIZE;
		}

		JSONObject result = new JSONObject();
		int total = wxCategoryService.getWxCategoryCountByQueryCriteria(query);
		if (total > 0) {
			result.put("total", total);
			result.put("totalCount", total);
			result.put("totalRecords", total);
			result.put("start", start);
			result.put("startIndex", start);
			result.put("limit", limit);
			result.put("pageSize", limit);

			if (StringUtils.isNotEmpty(orderName)) {
				query.setSortOrder(orderName);
				if (StringUtils.equals(order, "desc")) {
					query.setSortOrder(" desc ");
				}
			}

			List<WxCategory> list = wxCategoryService
					.getWxCategorysByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (WxCategory wxCategory : list) {
					JSONObject rowJSON = wxCategory.toJsonObject();
					rowJSON.put("id", wxCategory.getId());
					rowJSON.put("wxCategoryId", wxCategory.getId());
					rowJSON.put("startIndex", ++start);
					rowsJSON.add(rowJSON);
				}

			}
		} else {
			JSONArray rowsJSON = new JSONArray();
			result.put("rows", rowsJSON);
			result.put("total", total);
		}
		return result.toJSONString().getBytes("UTF-8");
	}

	@POST
	@Path("/saveWxCategory")
	@ResponseBody
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public byte[] saveWxCategory(@Context HttpServletRequest request) {
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxCategory wxCategory = new WxCategory();
		try {
			Tools.populate(wxCategory, params);

			wxCategory.setParentId(RequestUtils.getLong(request, "parentId"));
			wxCategory.setTreeId(request.getParameter("treeId"));
			wxCategory.setSort(RequestUtils.getInt(request, "sort"));
			wxCategory.setIcon(request.getParameter("icon"));
			wxCategory.setIconCls(request.getParameter("iconCls"));
			wxCategory.setCoverIcon(request.getParameter("coverIcon"));
			wxCategory.setIndexShow(RequestUtils.getInt(request, "indexShow"));
			wxCategory.setLocked(RequestUtils.getInt(request, "locked"));
			wxCategory.setName(request.getParameter("name"));
			wxCategory.setCode(request.getParameter("code"));
			wxCategory.setDesc(request.getParameter("desc"));
			wxCategory.setEventType(request.getParameter("eventType"));
			wxCategory.setUrl(request.getParameter("url"));
			wxCategory.setUuid(request.getParameter("uuid"));
			wxCategory.setCreateBy(request.getParameter("createBy"));
			wxCategory.setCreateDate(RequestUtils
					.getDate(request, "createDate"));

			this.wxCategoryService.save(wxCategory);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setWxCategoryService(WxCategoryService wxCategoryService) {
		this.wxCategoryService = wxCategoryService;
	}

	@GET
	@POST
	@Path("/view")
	@ResponseBody
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public byte[] view(@Context HttpServletRequest request) throws IOException {
		WxCategory wxCategory = null;
		if (StringUtils.isNotEmpty(request.getParameter("id"))) {
			wxCategory = wxCategoryService.getWxCategory(RequestUtils.getLong(
					request, "id"));
		}
		JSONObject result = new JSONObject();
		if (wxCategory != null) {
			result = wxCategory.toJsonObject();
			result.put("id", wxCategory.getId());
			result.put("wxCategoryId", wxCategory.getId());
		}
		return result.toJSONString().getBytes("UTF-8");
	}
}