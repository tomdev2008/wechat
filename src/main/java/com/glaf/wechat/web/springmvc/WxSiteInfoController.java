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
import com.glaf.core.config.ViewProperties;
import com.glaf.core.identity.User;
import com.glaf.core.security.LoginContext;
import com.glaf.core.util.JsonUtils;
import com.glaf.core.util.Paging;
import com.glaf.core.util.ParamUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.core.util.ResponseUtils;
import com.glaf.core.util.Tools;
import com.glaf.wechat.domain.WxSiteInfo;
import com.glaf.wechat.query.WxSiteInfoQuery;
import com.glaf.wechat.service.WxSiteInfoService;

@Controller("/wx/wxSiteInfo")
@RequestMapping("/wx/wxSiteInfo")
public class WxSiteInfoController {
	protected static final Log logger = LogFactory
			.getLog(WxSiteInfoController.class);

	protected WxSiteInfoService wxSiteInfoService;

	public WxSiteInfoController() {

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
					WxSiteInfo wxSiteInfo = wxSiteInfoService
							.getWxSiteInfo(Long.valueOf(x));
					/**
					 * 此处业务逻辑需自行调整
					 */
					if (wxSiteInfo != null
							&& (StringUtils.equals(wxSiteInfo.getCreateBy(),
									loginContext.getActorId()) || loginContext
									.isSystemAdministrator())) {
						wxSiteInfoService.save(wxSiteInfo);
					}
				}
			}
		} else if (id != null) {
			WxSiteInfo wxSiteInfo = wxSiteInfoService.getWxSiteInfo(Long
					.valueOf(id));
			/**
			 * 此处业务逻辑需自行调整
			 */
			if (wxSiteInfo != null
					&& (StringUtils.equals(wxSiteInfo.getCreateBy(),
							loginContext.getActorId()) || loginContext
							.isSystemAdministrator())) {
				wxSiteInfoService.save(wxSiteInfo);
			}
		}
	}

	@RequestMapping("/edit")
	public ModelAndView edit(HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		RequestUtils.setRequestParameterToAttribute(request);
		request.removeAttribute("canSubmit");

		WxSiteInfo wxSiteInfo = wxSiteInfoService.getWxSiteInfo(RequestUtils
				.getLong(request, "id"));
		if (wxSiteInfo != null
				&& (StringUtils.equals(wxSiteInfo.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			request.setAttribute("wxSiteInfo", wxSiteInfo);
			JSONObject rowJSON = wxSiteInfo.toJsonObject();
			request.setAttribute("x_json", rowJSON.toJSONString());
		} else {
			Long accountId = RequestUtils.getLong(request, "accountId");
			wxSiteInfo = wxSiteInfoService.getWxSiteInfoByAccountId(accountId);
			request.setAttribute("wxSiteInfo", wxSiteInfo);
		}

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		String x_view = ViewProperties.getString("wxSiteInfo.edit");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}

		return new ModelAndView("/wx/siteInfo/edit", modelMap);
	}

	@RequestMapping("/json/{accountId}")
	@ResponseBody
	public byte[] json(@PathVariable("accountId") Long accountId,
			HttpServletRequest request, ModelMap modelMap) throws IOException {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxSiteInfoQuery query = new WxSiteInfoQuery();
		Tools.populate(query, params);
		query.deleteFlag(0);
		query.setActorId(loginContext.getActorId());
		query.setLoginContext(loginContext);

		String actorId = loginContext.getActorId();
		query.createBy(actorId);
		query.setAccountId(accountId);

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
		int total = wxSiteInfoService.getWxSiteInfoCountByQueryCriteria(query);
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

			List<WxSiteInfo> list = wxSiteInfoService
					.getWxSiteInfosByQueryCriteria(start, limit, query);

			if (list != null && !list.isEmpty()) {
				JSONArray rowsJSON = new JSONArray();

				result.put("rows", rowsJSON);

				for (WxSiteInfo wxSiteInfo : list) {
					JSONObject rowJSON = wxSiteInfo.toJsonObject();
					rowJSON.put("id", wxSiteInfo.getId());
					rowJSON.put("wxSiteInfoId", wxSiteInfo.getId());
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
		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view, modelMap);
		}

		return new ModelAndView("/wx/siteInfo/list", modelMap);
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
		String x_view = ViewProperties.getString("wxSiteInfo.query");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view, modelMap);
		}
		return new ModelAndView("/wx/siteInfo/query", modelMap);
	}

	@RequestMapping("/save")
	public ModelAndView save(HttpServletRequest request, ModelMap modelMap) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		params.remove("status");
		params.remove("wfStatus");

		WxSiteInfo wxSiteInfo = new WxSiteInfo();
		Tools.populate(wxSiteInfo, params);
		Long accountId = RequestUtils.getLong(request, "accountId");
		wxSiteInfo.setLinkman(request.getParameter("linkman"));
		wxSiteInfo.setTelephone(request.getParameter("telephone"));
		wxSiteInfo.setMobile(request.getParameter("mobile"));
		wxSiteInfo.setMail(request.getParameter("mail"));
		wxSiteInfo.setQq(request.getParameter("qq"));
		wxSiteInfo.setAddress(request.getParameter("address"));
		wxSiteInfo.setSiteUrl(request.getParameter("siteUrl"));
		wxSiteInfo.setRemark(request.getParameter("remark"));
		wxSiteInfo.setCreateBy(actorId);
		wxSiteInfo.setAccountId(accountId);

		wxSiteInfoService.save(wxSiteInfo);

		return this.list(request, modelMap);
	}

	@ResponseBody
	@RequestMapping("/saveWxSiteInfo")
	public byte[] saveWxSiteInfo(HttpServletRequest request) {
		User user = RequestUtils.getUser(request);
		String actorId = user.getActorId();
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		WxSiteInfo wxSiteInfo = new WxSiteInfo();
		try {
			Tools.populate(wxSiteInfo, params);
			Long accountId = RequestUtils.getLong(request, "accountId");
			wxSiteInfo.setLinkman(request.getParameter("linkman"));
			wxSiteInfo.setTelephone(request.getParameter("telephone"));
			wxSiteInfo.setMobile(request.getParameter("mobile"));
			wxSiteInfo.setMail(request.getParameter("mail"));
			wxSiteInfo.setQq(request.getParameter("qq"));
			wxSiteInfo.setAddress(request.getParameter("address"));
			wxSiteInfo.setSiteUrl(request.getParameter("siteUrl"));
			wxSiteInfo.setRemark(request.getParameter("remark"));
			wxSiteInfo.setCreateBy(actorId);
			wxSiteInfo.setAccountId(accountId);
			this.wxSiteInfoService.save(wxSiteInfo);

			return ResponseUtils.responseJsonResult(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
		return ResponseUtils.responseJsonResult(false);
	}

	@javax.annotation.Resource
	public void setWxSiteInfoService(WxSiteInfoService wxSiteInfoService) {
		this.wxSiteInfoService = wxSiteInfoService;
	}

	@RequestMapping("/update/{id}")
	public ModelAndView update(@PathVariable("id") Long id,
			HttpServletRequest request, ModelMap modelMap) {
		LoginContext loginContext = RequestUtils.getLoginContext(request);
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		params.remove("status");
		params.remove("wfStatus");

		WxSiteInfo wxSiteInfo = wxSiteInfoService.getWxSiteInfo(id);
		if (wxSiteInfo != null
				&& (StringUtils.equals(wxSiteInfo.getCreateBy(),
						loginContext.getActorId()) || loginContext
						.isSystemAdministrator())) {
			wxSiteInfo.setLinkman(request.getParameter("linkman"));
			wxSiteInfo.setTelephone(request.getParameter("telephone"));
			wxSiteInfo.setMobile(request.getParameter("mobile"));
			wxSiteInfo.setMail(request.getParameter("mail"));
			wxSiteInfo.setQq(request.getParameter("qq"));
			wxSiteInfo.setAddress(request.getParameter("address"));
			wxSiteInfo.setSiteUrl(request.getParameter("siteUrl"));
			wxSiteInfo.setRemark(request.getParameter("remark"));
			wxSiteInfo.setLastUpdateBy(loginContext.getActorId());

			wxSiteInfoService.save(wxSiteInfo);
		}

		return this.list(request, modelMap);
	}

	@RequestMapping("/view/{id}")
	public ModelAndView view(@PathVariable("id") Long id,
			HttpServletRequest request, ModelMap modelMap) {
		RequestUtils.setRequestParameterToAttribute(request);
		WxSiteInfo wxSiteInfo = wxSiteInfoService.getWxSiteInfo(id);
		request.setAttribute("wxSiteInfo", wxSiteInfo);

		String view = request.getParameter("view");
		if (StringUtils.isNotEmpty(view)) {
			return new ModelAndView(view);
		}

		String x_view = ViewProperties.getString("wxSiteInfo.view");
		if (StringUtils.isNotEmpty(x_view)) {
			return new ModelAndView(x_view);
		}

		return new ModelAndView("/wx/siteInfo/view");
	}

}
