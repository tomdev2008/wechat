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
import com.glaf.core.util.StringTools;
import com.glaf.core.util.Tools;
import com.glaf.wechat.domain.WxCategory;
import com.glaf.wechat.domain.WxContent;
import com.glaf.wechat.query.WxContentQuery;
import com.glaf.wechat.service.WxCategoryService;
import com.glaf.wechat.service.WxContentService;

@Controller("/wx/wxContent")
@RequestMapping("/wx/wxContent")
public class WxContentController {
	protected static final Log logger = LogFactory
			.getLog(WxContentController.class);

	protected WxCategoryService wxCategoryService;

	protected WxContentService wxContentService;

	public WxContentController() {

	}

	@RequestMapping("/articleList/{accountId}")
	public ModelAndView articleList(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) {
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

		Long categoryId = RequestUtils.getLong(request, "categoryId");
		if (categoryId != null && categoryId > 0) {
			WxCategory category = wxCategoryService.getWxCategory(categoryId);
			request.setAttribute("category", category);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/wx/content/articleList", modelMap);
	}

	@RequestMapping("/choose")
	public ModelAndView choose(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxContent.choose");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/content/choose_contents", modelMap);
	}

	@RequestMapping("/chooseOne")
	public ModelAndView chooseOne(HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxContent.chooseOne");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/content/choose_single_content", modelMap);
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
					WxContent wxContent = wxContentService.getWxContent(Long
							.valueOf(x));
					/**
					 * 此处业务逻辑需自行调整
					 */

					if (wxContent != null
							&& (StringUtils.equals(wxContent.getCreateBy(),
									loginContext.getActorId()) || loginContext
									.isSystemAdministrator())) {
						wxContentService.deleteById(wxContent.getId());
					}
				}
			}
		} else if (id != null) {
			WxContent wxContent = wxContentService.getWxContent(Long
					.valueOf(id));
			/**
			 * 此处业务逻辑需自行调整
			 */
			if (wxContent != null
					&& (StringUtils.equals(wxContent.getCreateBy(),
							loginContext.getActorId()) || loginContext
							.isSystemAdministrator())) {
				wxContentService.deleteById(wxContent.getId());
			}
		}
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		RequestUtils.setRequestParameterToAttribute(request);

		WxContent wxContent = wxContentService.getWxContent(RequestUtils
				.getLong(request, "id"));
		if (wxContent != null
				&& (StringUtils.equals(wxContent.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			request.setAttribute("wxContent", wxContent);
		}

		if (wxContent != null && StringUtils.isNotEmpty(wxContent.getMsgType())) {
			return this.editMedia(request, modelMap);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxContent.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/content/edit", modelMap);
	}

	@RequestMapping("/editMedia")
	public ModelAndView editMedia(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		RequestUtils.setRequestParameterToAttribute(request);

		WxContent wxContent = wxContentService.getWxContent(RequestUtils
				.getLong(request, "id"));
		if (wxContent != null
				&& (StringUtils.equals(wxContent.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			request.setAttribute("wxContent", wxContent);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxContent.editMedia");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/content/editMedia", modelMap);
	}

	@RequestMapping("/editPPT")
	public ModelAndView editPPT(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		RequestUtils.setRequestParameterToAttribute(request);

		WxContent wxContent = wxContentService.getWxContent(RequestUtils
				.getLong(request, "id"));
		if (wxContent != null
				&& (StringUtils.equals(wxContent.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			request.setAttribute("wxContent", wxContent);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxContent.editPPT");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/content/editPPT", modelMap);
	}

	@RequestMapping("/indexPPT")
	public ModelAndView indexPPT(HttpServletRequest request, ModelMap modelMap) {
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

		return new ModelAndView("/wx/content/indexPPT", modelMap);
	}

	@RequestMapping("/json/{accountId}")
	@ResponseBody
	public byte[] json(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxContentQuery query = new WxContentQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);
		query.setAccountId(accountId);

		Long categoryId = RequestUtils.getLong(request, "categoryId", 0);
		if (categoryId > 0) {
			query.categoryId(categoryId);
		}

		String actorId = loginContext.getActorId();
		query.createBy(actorId);

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
		int total = wxContentService.getWxContentCountByQueryCriteria(query);
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

			List<WxContent> list = wxContentService
					.getWxContentsByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (WxContent wxContent : list) {
					JSONObject rowJSON = wxContent.toJsonObject();
					rowJSON.put("id", wxContent.getId());
					rowJSON.put("wxContentId", wxContent.getId());
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

		return new ModelAndView("/wx/content/list", modelMap);
	}

	@RequestMapping("/ppt/{accountId}")
	public ModelAndView ppt(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		request.setAttribute("accountId", accountId);
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
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/wx/content/ppt", modelMap);
	}

	@RequestMapping("/query/{accountId}")
	public ModelAndView query(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		request.setAttribute("accountId", accountId);
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}
		String x_view = ViewProperties.getString("wxContent.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/wx/content/query", modelMap);
	}

	@RequestMapping("/save")
	public ModelAndView save(HttpServletRequest request, ModelMap modelMap) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		params.remove("status");
		params.remove("wfStatus");

		WxContent wxContent = new WxContent();
		Tools.populate(wxContent, params);
		Long accountId = RequestUtils.getLong(request, "accountId");
		wxContent.setCategoryId(RequestUtils.getLong(request, "categoryId"));
		wxContent.setTitle(request.getParameter("title"));
		wxContent.setContent(request.getParameter("content"));
		wxContent.setDetailShowCover(request.getParameter("detailShowCover"));
		wxContent.setAuthor(request.getParameter("author"));
		wxContent.setStatus(RequestUtils.getInt(request, "status"));
		wxContent.setPriority(RequestUtils.getInt(request, "priority"));
		wxContent.setType(request.getParameter("type"));
		wxContent.setMsgType(request.getParameter("msgType"));
		wxContent.setKeywords(request.getParameter("keywords"));
		wxContent.setKeywordsMatchType(request
				.getParameter("keywordsMatchType"));
		wxContent.setSort(RequestUtils.getInt(request, "sort"));
		wxContent.setRelationIds(request.getParameter("relationIds"));
		wxContent.setRecommendationIds(request
				.getParameter("recommendationIds"));
		wxContent.setSummary(request.getParameter("summary"));
		wxContent.setIcon(request.getParameter("icon"));
		wxContent.setBigIcon(request.getParameter("bigIcon"));
		wxContent.setSmallIcon(request.getParameter("smallIcon"));
		wxContent.setUrl(request.getParameter("url"));
		wxContent.setPicUrl(request.getParameter("picUrl"));
		wxContent.setLatitude(RequestUtils.getDouble(request, "latitude"));
		wxContent.setLongitude(RequestUtils.getDouble(request, "longitude"));
		wxContent.setScale(request.getParameter("scale"));
		wxContent.setLabel(request.getParameter("label"));
		wxContent.setCreateBy(actorId);
		wxContent.setAccountId(accountId);

		wxContentService.save(wxContent);

		return this.list(request, modelMap);
	}

	@ResponseBody
	@RequestMapping("/saveWxContent")
	public byte[] saveWxContent(HttpServletRequest request) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		Long accountId = RequestUtils.getLong(request, "accountId");
		WxContent wxContent = new WxContent();
		try {
			Tools.populate(wxContent, params);
			wxContent
					.setCategoryId(RequestUtils.getLong(request, "categoryId"));
			wxContent.setTitle(request.getParameter("title"));
			wxContent.setContent(request.getParameter("content"));
			wxContent.setDetailShowCover(request
					.getParameter("detailShowCover"));
			wxContent.setAuthor(request.getParameter("author"));
			wxContent.setStatus(RequestUtils.getInt(request, "status"));
			wxContent.setPriority(RequestUtils.getInt(request, "priority"));
			wxContent.setType(request.getParameter("type"));
			wxContent.setMsgType(request.getParameter("msgType"));
			wxContent.setKeywords(request.getParameter("keywords"));
			wxContent.setKeywordsMatchType(request
					.getParameter("keywordsMatchType"));
			wxContent.setSort(RequestUtils.getInt(request, "sort"));
			wxContent.setRelationIds(request.getParameter("relationIds"));
			wxContent.setRecommendationIds(request
					.getParameter("recommendationIds"));
			wxContent.setSummary(request.getParameter("summary"));
			wxContent.setIcon(request.getParameter("icon"));
			wxContent.setBigIcon(request.getParameter("bigIcon"));
			wxContent.setSmallIcon(request.getParameter("smallIcon"));
			wxContent.setUrl(request.getParameter("url"));
			wxContent.setPicUrl(request.getParameter("picUrl"));
			wxContent.setLatitude(RequestUtils.getDouble(request, "latitude"));
			wxContent
					.setLongitude(RequestUtils.getDouble(request, "longitude"));
			wxContent.setScale(request.getParameter("scale"));
			wxContent.setLabel(request.getParameter("label"));
			wxContent.setCreateBy(actorId);
			wxContent.setAccountId(accountId);
			this.wxContentService.save(wxContent);

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

	@javax.annotation.Resource
	public void setWxContentService(WxContentService wxContentService) {
		this.wxContentService = wxContentService;
	}

	@ResponseBody
	@RequestMapping("/treeJson/{accountId}")
	public byte[] treeJson(@PathVariable("accountId") Long accountId,
			HttpServletRequest request) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		String selecteds = request.getParameter("selecteds");
		List<String> checkIds = new java.util.concurrent.CopyOnWriteArrayList<String>();
		if (StringUtils.isNotEmpty(selecteds)) {
			checkIds = StringTools.split(selecteds);
		}

		String type = request.getParameter("type");
		JSONObject result = new JSONObject();
		List<WxCategory> categories = wxCategoryService.getCategoryList(
				accountId, type);
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
			WxContentQuery query = new WxContentQuery();
			query.categoryIds(categoryIds);
			query.createBy(loginContext.getActorId());
			List<WxContent> contents = wxContentService.list(query);
			if (contents != null && !contents.isEmpty()) {
				for (WxContent content : contents) {
					TreeModel parent = treeMap.get(content.getCategoryId());
					TreeModel tree = new BaseTree();
					tree.setId(content.getId());
					tree.setParentId(parent.getId());
					tree.setName(content.getTitle());
					tree.setSortNo(content.getSort());
					tree.setCreateBy(content.getCreateBy());
					tree.setIconCls("tree_leaf");
					tree.setUrl(content.getUrl());
					if (checkIds.contains(String.valueOf(content.getId()))) {
						tree.setChecked(true);
					}
					treeModels.add(tree);
				}
			}
			logger.debug("treeModels:" + treeModels.size());
			TreeHelper treeHelper = new TreeHelper();
			JSONArray jsonArray = treeHelper.getTreeJSONArray(treeModels);
			logger.debug(jsonArray.toJSONString());
			return jsonArray.toJSONString().getBytes("UTF-8");
		}
		return result.toJSONString().getBytes("UTF-8");
	}

	@RequestMapping("/treeList")
	public ModelAndView treeList(HttpServletRequest request, ModelMap modelMap) {
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

		return new ModelAndView("/wx/content/treeList", modelMap);
	}

	@RequestMapping("/update/{id}")
	public ModelAndView update(@PathVariable("id") Long id,
			HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		params.remove("status");
		params.remove("wfStatus");

		WxContent wxContent = wxContentService.getWxContent(id);
		if (wxContent != null
				&& (StringUtils.equals(wxContent.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			wxContent
					.setCategoryId(RequestUtils.getLong(request, "categoryId"));
			wxContent.setTitle(request.getParameter("title"));
			wxContent.setContent(request.getParameter("content"));
			wxContent.setDetailShowCover(request
					.getParameter("detailShowCover"));
			wxContent.setAuthor(request.getParameter("author"));
			wxContent.setStatus(RequestUtils.getInt(request, "status"));
			wxContent.setPriority(RequestUtils.getInt(request, "priority"));
			wxContent.setType(request.getParameter("type"));
			wxContent.setMsgType(request.getParameter("msgType"));
			wxContent.setKeywords(request.getParameter("keywords"));
			wxContent.setKeywordsMatchType(request
					.getParameter("keywordsMatchType"));
			wxContent.setSort(RequestUtils.getInt(request, "sort"));
			wxContent.setRelationIds(request.getParameter("relationIds"));
			wxContent.setRecommendationIds(request
					.getParameter("recommendationIds"));
			wxContent.setSummary(request.getParameter("summary"));
			wxContent.setIcon(request.getParameter("icon"));
			wxContent.setBigIcon(request.getParameter("bigIcon"));
			wxContent.setSmallIcon(request.getParameter("smallIcon"));
			wxContent.setUrl(request.getParameter("url"));
			wxContent.setPicUrl(request.getParameter("picUrl"));
			wxContent.setLatitude(RequestUtils.getDouble(request, "latitude"));
			wxContent
					.setLongitude(RequestUtils.getDouble(request, "longitude"));
			wxContent.setScale(request.getParameter("scale"));
			wxContent.setLabel(request.getParameter("label"));
			wxContent.setLastUpdateBy(loginContext.getActorId());

			wxContentService.save(wxContent);
		}

		return this.list(request, modelMap);
	}

	@RequestMapping("/view/{id}")
	public ModelAndView view(@PathVariable("id") Long id,
			HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);

		WxContent wxContent = wxContentService.getWxContent(id);
		request.setAttribute("wxContent", wxContent);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view);
		}

		String x_view = ViewProperties.getString("wxContent.view");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view);
		}

		return new ModelAndView("/wx/content/view");
	}

}
