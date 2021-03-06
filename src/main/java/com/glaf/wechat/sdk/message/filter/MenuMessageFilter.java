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
package com.glaf.wechat.sdk.message.filter;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.glaf.core.context.ContextFactory;
import com.glaf.wechat.domain.WxLog;
import com.glaf.wechat.domain.WxMenu;
import com.glaf.wechat.query.WxMenuQuery;
import com.glaf.wechat.sdk.message.EventMessage;
import com.glaf.wechat.sdk.message.Message;
import com.glaf.wechat.sdk.message.ResponseMenuMessage;
import com.glaf.wechat.service.WxMenuService;
import com.glaf.wechat.util.Constants;
import com.glaf.wechat.util.WxLogFactory;

public class MenuMessageFilter extends AbstractMessageFilter implements
		IMessageFilter {

	@Override
	public Message doSpecailMessageFilter(Message message) {
		EventMessage msg = (EventMessage) message;
		WxMenuService wxMenuService = ContextFactory.getBean("wxMenuService");
		WxMenuQuery query = new WxMenuQuery();
		query.accountId(message.getAccountId());
		query.createBy(message.getCustomer());
		query.key(msg.getEventKey());
		query.locked(0);
		
		List<WxMenu> rows = wxMenuService.list(query);
		if (rows != null && !rows.isEmpty()) {
			WxMenu menu = rows.get(0);
			ResponseMenuMessage menuMessage = new ResponseMenuMessage();
			menuMessage.setDescription(menu.getDesc());
			menuMessage.setTitle(menu.getName());

			if (StringUtils.isNotEmpty(menu.getUrl())) {
				if (StringUtils.startsWith(menu.getUrl(), "/website/wx/")) {
					String url = message.getServiceUrl() + menu.getUrl();
					menuMessage.setUrl(url);
				} else {
					menuMessage.setUrl(menu.getUrl());
				}
			}
			if (StringUtils.isNotEmpty(menu.getIcon())) {
				if (StringUtils.startsWith(menu.getIcon(), "/wx/upload/")) {
					String url = message.getServiceUrl() + menu.getIcon();
					menuMessage.setPicUrl(url);
				} else {
					menuMessage.setPicUrl(menu.getIcon());
				}
			}
			
			try {
				WxLog bean = new WxLog();
				bean.setOpenId(message.getFromUserName());
				bean.setActorId(message.getCustomer());
				bean.setCreateTime(new Date());
				bean.setFlag(Constants.MENU_LOG_FLAG);
				bean.setIp(message.getRemoteIPAddr());
				bean.setOperate("menu");
				WxLogFactory.create(bean);
			} catch (Exception ex) {
			}
			return menuMessage;
		}
		return null;
	}

}
