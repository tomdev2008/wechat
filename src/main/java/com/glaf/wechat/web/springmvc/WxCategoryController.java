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

package com.glaf.wechat.web.springmvc;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.glaf.core.base.BaseTree;
import com.glaf.core.base.TreeModel;
import com.glaf.core.config.ViewProperties;
import com.glaf.core.identity.User;
import com.glaf.core.security.LoginContext;
import com.glaf.core.tree.helper.TreeHelper;
import com.glaf.core.util.JsonUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;
import com.glaf.wechat.domain.WxCategory;
import com.glaf.wechat.query.WxCategoryQuery;
import com.glaf.wechat.service.WxCategoryService;

@Controller("/wx/wxCategory")
@RequestMapping("/wx/wxCategory")
public class WxCategoryController {
	protected static final Log logger = LogFactory
			.getLog(WxCategoryController.class);

	protected WxCategoryService wxCategoryService;

	public WxCategoryController() {

	}

	@ResponseBody
	@RequestMapping("/delete")
	public void delete(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Long id = RequestUtils.getLong(request, "id");
		String ids = request.getParameter("ids");
		if (StringUtils.isNotEmpty(ids)) {
			StringTokenizer token = new StringTokenizer(ids, ",");
			while (token.hasMoreTokens()) {
				String x = token.nextToken();
				if (StringUtils.isNotEmpty(x)) {
					WxCategory wxCategory = wxCategoryService
							.getWxCategory(Long.valueOf(x));
					if (wxCategory != null
							&& (StringUtils.equals(wxCategory.getCreateBy(),
									loginContext.getActorId()) || loginContext
									.isSystemAdministrator())) {
						wxCategoryService.deleteById(wxCategory.getId());
					}
				}
			}
		} else if (id != null) {
			WxCategory wxCategory = wxCategoryService.getWxCategory(Long
					.valueOf(id));
			if (wxCategory != null
					&& (StringUtils.equals(wxCategory.getCreateBy(),
							loginContext.getActorId()) || loginContext
							.isSystemAdministrator())) {
				wxCategoryService.deleteById(wxCategory.getId());
			}
		}
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		RequestUtils.setRequestParameterToAttribute(request);
		request.removeAttribute("canSubmit");
		String type = request.getParameter("type");
		if (StringUtils.isEmpty(type)) {
			type = "category";
		}

		WxCategory wxCategory = wxCategoryService.getWxCategory(RequestUtils
				.getLong(request, "id"));
		if (wxCategory != null
				&& (StringUtils.equals(wxCategory.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			request.setAttribute("wxCategory", wxCategory);
		}

		Long accountId = RequestUtils.getLong(request, "accountId");
		List<WxCategory> categories = wxCategoryService.getCategoryList(
				accountId, type);
		if (wxCategory != null) {
			List<WxCategory> subCategories = wxCategoryService
					.getCategoryList(wxCategory.getId());
			if (categories != null && !categories.isEmpty()) {
				if (subCategories != null && !subCategories.isEmpty()) {
					categories.removeAll(subCategories);
				}
				categories.remove(wxCategory);
				for (WxCategory cat : categories) {
					if (StringUtils.isNotEmpty(cat.getTreeId())) {
						StringTokenizer token = new StringTokenizer(
								cat.getTreeId(), "|");
						cat.setDeep(token.countTokens());
					}
				}
			}
		}

		request.setAttribute("categories", categories);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxCategory.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/category/edit", modelMap);
	}

	@RequestMapping("/json/{accountId}")
	@ResponseBody
	public byte[] json(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxCategoryQuery query = new WxCategoryQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);
		String actorId = loginContext.getActorId();
		query.createBy(actorId);
		query.setAccountId(accountId);

		Long parentId = RequestUtils.getLong(request, "parentId", 0);
		query.parentId(parentId);

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
			limit = Paging.DEFAULT_PAGE_SIZE;
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
					rowJSON.put("pId", wxCategory.getParentId());
					rowJSON.put("categoryId", wxCategory.getId());
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

	@RequestMapping
	public ModelAndView list(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String x_query = request.getParameter("x_query");
		if (StringUtils.equals(x_query, "true")) {
			Map<String, Object> paramMap = RequestUtils
					.getParameterMap(request);
			String x_complex_query = JsonUtils.encode(paramMap);
			x_complex_query = RequestUtils.encodeString(x_complex_query);
			request.setAttribute("x_complex_query", x_complex_query);
		} else {
			request.setAttribute("x_complex_query", "");
		}

		String requestURI = request.getRequestURI();
		logger.debug("requestURI:" + requestURI);
		logger.debug("queryString:" + request.getQueryString());
		request.setAttribute(
				"fromUrl",
				RequestUtils.encodeURL(requestURI + "?"
						+ request.getQueryString()));

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/wx/category/list", modelMap);
	}

	@RequestMapping("/query/{accountId}")
	public ModelAndView query(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("wxCategory.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/wx/category/query", modelMap);
	}

	@RequestMapping("/save")
	public ModelAndView save(HttpServletRequest request, ModelMap modelMap) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		params.remove("status");
		params.remove("wfStatus");

		WxCategory wxCategory = new WxCategory();
		Tools.populate(wxCategory, params);
		Long accountId = RequestUtils.getLong(request, "accountId");
		wxCategory.setParentId(RequestUtils.getLong(request, "parentId"));
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
		wxCategory.setCreateBy(actorId);
		wxCategory.setLastUpdateBy(actorId);
		wxCategory.setAccountId(accountId);

		wxCategoryService.save(wxCategory);

		return this.list(request, modelMap);
	}

	@ResponseBody
	@RequestMapping("/saveWxCategory")
	public byte[] saveWxCategory(HttpServletRequest request) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxCategory wxCategory = new WxCategory();
		try {
			Tools.populate(wxCategory, params);
			Long accountId = RequestUtils.getLong(request, "accountId");
			wxCategory.setParentId(RequestUtils.getLong(request, "parentId"));
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
			wxCategory.setCreateBy(actorId);
			wxCategory.setLastUpdateBy(actorId);
			wxCategory.setAccountId(accountId);
			this.wxCategoryService.save(wxCategory);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setWxCategoryService(WxCategoryService wxCategoryService) {
		this.wxCategoryService = wxCategoryService;
	}

	@ResponseBody
	@RequestMapping("/treeJson/{accountId}")
	public byte[] treeJson(@PathVariable("accountId") Long accountId,
			HttpServletRequest request) throws IOException {
		JSONArray array = new JSONArray();
		String type = request.getParameter("type");
		Long parentId = RequestUtils.getLong(request, "parentId", 0);
		List<WxCategory> categories = null;
		if (parentId != null && parentId > 0) {
			categories = wxCategoryService.getCategoryList(accountId, parentId);
		} else if (StringUtils.isNotEmpty(type)) {
			categories = wxCategoryService.getCategoryList(accountId, type);
		}

		if (categories != null && !categories.isEmpty()) {
			Map<Long, TreeModel> treeMap = new java.util.concurrent.ConcurrentHashMap<Long, TreeModel>();
			List<TreeModel> treeModels = new java.util.concurrent.CopyOnWriteArrayList<TreeModel>();
			List<Long> categoryIds = new java.util.concurrent.CopyOnWriteArrayList<Long>();
			for (WxCategory category : categories) {
				TreeModel tree = new BaseTree();
				tree.setId(category.getId());
				tree.setParentId(category.getParentId());
				tree.setCode(category.getCode());
				tree.setName(category.getName());
				tree.setSortNo(category.getSort());
				tree.setDescription(category.getDesc());
				tree.setCreateBy(category.getCreateBy());
				tree.setIconCls("tree_folder");
				tree.setTreeId(category.getTreeId());
				tree.setUrl(category.getUrl());
				treeModels.add(tree);
				categoryIds.add(category.getId());
				treeMap.put(category.getId(), tree);
			}
			logger.debug("treeModels:" + treeModels.size());
			TreeHelper treeHelper = new TreeHelper();
			JSONArray jsonArray = treeHelper.getTreeJSONArray(treeModels);
			logger.debug(jsonArray.toJSONString());
			return jsonArray.toJSONString().getBytes("UTF-8");
		}
		return array.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping("/update/{id}")
	public ModelAndView update(@PathVariable("id") Long id,
			HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		params.remove("status");
		params.remove("wfStatus");

		WxCategory wxCategory = wxCategoryService.getWxCategory(id);
		if (wxCategory != null
				&& (StringUtils.equals(wxCategory.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			wxCategory.setParentId(RequestUtils.getLong(request, "parentId"));
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
			wxCategory.setLastUpdateBy(loginContext.getActorId());
			wxCategoryService.save(wxCategory);
		}

		return this.list(request, modelMap);
	}

	@RequestMapping("/view/{id}")
	public ModelAndView view(@PathVariable("id") Long id,
			HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		WxCategory wxCategory = wxCategoryService.getWxCategory(id);
		request.setAttribute("wxCategory", wxCategory);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view);
		}

		String x_view = ViewProperties.getString("wxCategory.view");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view);
		}

		return new ModelAndView("/wx/category/view");
	}

}
