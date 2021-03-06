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
import com.glaf.wechat.sdk.message.ResponseTextMessage;

/**
 * message response text handler
 */
public class TextResponseMessageHandler extends AbstractResponseMessageHandler {

	@Override
	public String response(Message message) {
		ResponseTextMessage textMessage = (ResponseTextMessage) message;
		StringBuffer buffer = new StringBuffer();
		buffer.append(
				wrapperContent(TAG_TOUSERNAME, textMessage.getToUserName(),
						true))
				.append(wrapperContent(TAG_FROMUSERNAME,
						textMessage.getFromUserName(), true))
				.append(wrapperContent(TAG_CREATETIME,
						textMessage.getCreateTime() + "", false))
				.append(wrapperContent(TAG_MSGTYPE, textMessage.getMsgType(),
						true))
				.append(wrapperContent(TAG_CONTENT, textMessage.getContent(),
						true))
				.append(wrapperContent(TAG_FUNCFLAG, textMessage.getFuncFlag()
						+ "", false));
		return wrapperXmlContent(buffer.toString());
	}

}
