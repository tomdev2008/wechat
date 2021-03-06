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
package com.glaf.wechat.sdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.glaf.core.config.Configuration;
import com.glaf.core.util.IOUtils;
import com.glaf.core.util.RequestUtils;
import com.glaf.wechat.config.WechatConfiguration;
import com.glaf.wechat.domain.WxUser;
import com.glaf.wechat.sdk.message.IMessage;
import com.glaf.wechat.sdk.message.Message;
import com.glaf.wechat.sdk.message.EventMessage;
import com.glaf.wechat.sdk.message.ImageMessage;
import com.glaf.wechat.sdk.message.LinkMessage;
import com.glaf.wechat.sdk.message.LocationMessage;
import com.glaf.wechat.sdk.message.ResponseNewsMessage;
import com.glaf.wechat.sdk.message.TextMessage;
import com.glaf.wechat.sdk.message.VideoMessage;
import com.glaf.wechat.sdk.message.VoiceMessage;
import com.glaf.wechat.sdk.message.handler.EventMessageHandler;
import com.glaf.wechat.sdk.message.handler.IMessageHandler;
import com.glaf.wechat.sdk.message.handler.ImageMessageHandler;
import com.glaf.wechat.sdk.message.handler.LinkMessageHandler;
import com.glaf.wechat.sdk.message.handler.LocationMessageHandler;
import com.glaf.wechat.sdk.message.handler.TextMessageHandler;
import com.glaf.wechat.sdk.message.handler.VideoMessageHandler;
import com.glaf.wechat.sdk.message.handler.VoiceMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.IResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.ImageResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.MenuResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.MusicResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.NewsResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.TextResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.VideoResponseMessageHandler;
import com.glaf.wechat.sdk.message.response.handler.VoiceResponseMessageHandler;
import com.glaf.wechat.util.SignUtils;
import com.glaf.wechat.util.TimeUtils;
import com.glaf.wechat.util.WechatUtils;
import com.glaf.wechat.util.WxIdentityFactory;

/**
 * Weixin executor class
 * 
 */
public class WeixinExecutor implements IMessage {
	protected static final Log logger = LogFactory.getLog(WeixinExecutor.class);
	protected static Configuration conf = WechatConfiguration.create();
	private HttpServletRequest request;// request
	private HttpServletResponse response;// response
	private Message message;// message comes from
	private Message responseMessage;// message will response
	private IMessageHandler messageHandler;// handle message
	private IResponseMessageHandler responseMessageHandler;// handle
															// response
															// message

	public WeixinExecutor() {

	}

	public Message getMessage() {
		return message;
	}

	public IMessageHandler getMessageHandler() {
		return messageHandler;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Message getResponseMessage() {
		return responseMessage;
	}

	public IResponseMessageHandler getResponseMessageHandler() {
		return responseMessageHandler;
	}

	// parse the input to Message
	private void parseInputStream() throws Exception {
		SAXReader xmlReader = new SAXReader();
		Document doc = null;
		try {
			doc = xmlReader.read(request.getInputStream());
		} catch (DocumentException ex) {
			ex.printStackTrace();
			doc = xmlReader.read(new ByteArrayInputStream(request.getParameter(
					"xml").getBytes("UTF-8")));
		}

		String uri = request.getRequestURI();
		String id = uri.substring(uri.lastIndexOf("/") + 1);
		Long accountId = Long.parseLong(id);
		WxUser user = WxIdentityFactory.getUserByAccountId(accountId);

		Element root = doc.getRootElement();
		logger.debug(root.asXML());

		String type = root.elementText(IMessage.TAG_MSGTYPE);

		if (StringUtils.equalsIgnoreCase(type, MESSAGE_TEXT)) {
			message = new TextMessage();
			messageHandler = new TextMessageHandler();
		} else if (StringUtils.equalsIgnoreCase(type, MESSAGE_EVENT)) {
			// do subscribe event
			message = new EventMessage();
			messageHandler = new EventMessageHandler();
		} else if (StringUtils.equalsIgnoreCase(type, MESSAGE_IMAGE)) {
			message = new ImageMessage();
			messageHandler = new ImageMessageHandler();
		} else if (StringUtils.equalsIgnoreCase(type, MESSAGE_LINK)) {
			message = new LinkMessage();
			messageHandler = new LinkMessageHandler();
		} else if (StringUtils.equalsIgnoreCase(type, MESSAGE_LOCATION)) {
			message = new LocationMessage();
			messageHandler = new LocationMessageHandler();
		} else if (StringUtils.equalsIgnoreCase(type, MESSAGE_VOICE)) {
			message = new VoiceMessage();
			messageHandler = new VoiceMessageHandler();
		} else if (StringUtils.equalsIgnoreCase(type, MESSAGE_VIDEO)) {
			message = new VideoMessage();
			messageHandler = new VideoMessageHandler();
		}

		message.setRoot(root);
		message.setAccountId(accountId);
		message.setCustomer(user.getActorId());
		message.setContextPath(request.getContextPath());
		message.setRequestParameters(RequestUtils.getParameterMap(request));
		String serviceUrl = WechatUtils.getServiceUrl(request);
		message.setServiceUrl(serviceUrl);
		message.setRemoteIPAddr(RequestUtils.getIPAddress(request));

		// do the default/common parse!
		messageHandler.parseMessage(message, root);
	}

	// deal the message from user
	public void processMessage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		this.message = null;
		this.messageHandler = null;
		this.responseMessage = null;
		this.request = request;
		this.response = response;
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml;charset=UTF-8");
		Map<String, Object> params = RequestUtils.getParameterMap(request);
		logger.debug("params:" + params);
		String echostr = request.getParameter("echostr");
		String signature = request.getParameter("signature");
		String nonce = request.getParameter("nonce");
		String timestamp = request.getParameter("timestamp");
		String uri = request.getRequestURI();
		String id = uri.substring(uri.lastIndexOf("/") + 1);
		Long accountId = Long.parseLong(id);
		PrintWriter out = response.getWriter();
		if (signature != null
				&& SignUtils.checkSignature(accountId, signature, timestamp,
						nonce)) {
			if (echostr != null) {
				response.setContentType("text/plain");
				out.write(echostr);
			} else {// do post
				try {
					this.parseInputStream();// parse message
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error(ex);
				}

				logger.debug("message class:" + message.getClass().getName());
				logger.debug("messageHandler class:"
						+ messageHandler.getClass().getName());

				responseMessage = messageHandler.handleMessage(message);
				logger.debug("responseMessage type:"
						+ responseMessage.getMsgType());

				String responseMsgType = responseMessage.getMsgType();
				if (StringUtils.equalsIgnoreCase(responseMessage.getMsgType(),
						MESSAGE_RESPONSE_TEXT)) {
					responseMessageHandler = new TextResponseMessageHandler();
				} else if (StringUtils.equalsIgnoreCase(
						responseMessage.getMsgType(), MESSAGE_RESPONSE_NEWS)) {
					responseMessageHandler = new NewsResponseMessageHandler();
				} else if (StringUtils.equalsIgnoreCase(
						responseMessage.getMsgType(), MESSAGE_RESPONSE_IMAGE)) {
					responseMessageHandler = new ImageResponseMessageHandler();
				} else if (StringUtils.equalsIgnoreCase(
						responseMessage.getMsgType(), MESSAGE_RESPONSE_VOICE)) {
					responseMessageHandler = new VoiceResponseMessageHandler();
				} else if (StringUtils.equalsIgnoreCase(
						responseMessage.getMsgType(), MESSAGE_RESPONSE_VIDEO)) {
					responseMessageHandler = new VideoResponseMessageHandler();
				} else if (StringUtils.equalsIgnoreCase(
						responseMessage.getMsgType(), MESSAGE_RESPONSE_MUSIC)) {
					responseMessageHandler = new MusicResponseMessageHandler();
				} else if (StringUtils.equalsIgnoreCase(message.getMsgType(),
						"event")) {
					EventMessage msg = (EventMessage) message;
					if (StringUtils.equalsIgnoreCase(msg.getEvent(),
							"subscribe")) {
						responseMessageHandler = new NewsResponseMessageHandler();
					} else {
						responseMessageHandler = new MenuResponseMessageHandler();
					}
					responseMsgType = "news";
				} else {
					if (responseMessage instanceof ResponseNewsMessage) {
						responseMsgType = MESSAGE_RESPONSE_NEWS;
						responseMessageHandler = new NewsResponseMessageHandler();
					} else {
						responseMsgType = MESSAGE_RESPONSE_TEXT;
						responseMessageHandler = new TextResponseMessageHandler();
					}
				}

				responseMessage.setCreateTime(TimeUtils
						.getCurrentUnixTimestamp());
				responseMessage.setFromUserName(message.getToUserName());
				responseMessage.setToUserName(message.getFromUserName());
				responseMessage.setMsgType(responseMsgType);

				logger.debug("responseMessageHandler class:"
						+ responseMessageHandler);
				String responseContent = responseMessageHandler
						.response(responseMessage);
				logger.debug("response content:" + responseContent);
				out.print(responseContent);
				out.flush();
				IOUtils.closeStream(out);
			}
		} else {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			out.print("����ȷ������");
			out.flush();
			IOUtils.closeStream(out);
		}
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setMessageHandler(IMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setResponseMessage(Message responseMessage) {
		this.responseMessage = responseMessage;
	}

	public void setResponseMessageHandler(
			IResponseMessageHandler responseMessageHandler) {
		this.responseMessageHandler = responseMessageHandler;
	}

}
