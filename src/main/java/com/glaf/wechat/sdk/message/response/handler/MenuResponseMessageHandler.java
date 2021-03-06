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
package com.glaf.wechat.sdk.message.response.handler;

import com.glaf.wechat.sdk.message.Message;
import com.glaf.wechat.sdk.message.ResponseMenuMessage;

/**
 * message response menu handler
 * 
 */
public class MenuResponseMessageHandler extends AbstractResponseMessageHandler {

	@Override
	public String response(Message message) {
		ResponseMenuMessage menuMessage = (ResponseMenuMessage) message;

		StringBuffer buffer = new StringBuffer();
		StringBuffer items = new StringBuffer();
		StringBuffer item = new StringBuffer();

		item.append(wrapperContent(TAG_TITLE, menuMessage.getTitle(), true))
				.append(wrapperContent(TAG_DESCRIPTION,
						menuMessage.getDescription(), true))
				.append(wrapperContent(TAG_PICURL, menuMessage.getPicUrl(),
						true))
				.append(wrapperContent(TAG_URL, menuMessage.getUrl(), true));
		items.append(wrapperContent(TAG_ITEM, item.toString(), false));

		String articles = wrapperContent(TAG_ARTICLES, items.toString(), false);
		buffer.append(
				wrapperContent(TAG_TOUSERNAME, menuMessage.getToUserName(),
						true))
				.append(wrapperContent(TAG_FROMUSERNAME,
						menuMessage.getFromUserName(), true))
				.append(wrapperContent(TAG_CREATETIME,
						menuMessage.getCreateTime() + "", false))
				.append(wrapperContent(TAG_MSGTYPE, menuMessage.getMsgType(),
						true))
				.append(wrapperContent(TAG_ARTICLECOUNT, 1 + "", false))
				.append(articles)
				.append(wrapperContent(TAG_FUNCFLAG, 1 + "", false));
		return wrapperXmlContent(buffer.toString());

	}

}
