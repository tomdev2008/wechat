<%--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
    String theme = com.glaf.core.util.RequestUtils.getTheme(request);
    request.setAttribute("theme", theme);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>投票</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/scripts/easyui/themes/${theme}/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/themes/${theme}/styles.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/icons/styles.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/core.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.form.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/glaf-base.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/glaf-core.js"></script>
<script type="text/javascript">
    var contextPath="<%=request.getContextPath()%>";

	function saveData(){
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '<%=request.getContextPath()%>/mx/wx/wxVote/saveWxVote',
				   data: params,
				   dataType:  'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						 alert(data.message);
					   } else {
						 alert('操作成功完成！');
					   }
					   if (window.opener) {
						window.opener.location.reload();
					   } else if (window.parent) {
						window.parent.location.reload();
					   }
				   }
			 });
	}

	function saveAsData(){
		document.getElementById("id").value="";
		document.getElementById("id").value="";
		var params = jQuery("#iForm").formSerialize();
		jQuery.ajax({
				   type: "POST",
				   url: '<%=request.getContextPath()%>/mx/wx/wxVote/saveWxVote',
				   data: params,
				   dataType:  'json',
				   error: function(data){
					   alert('服务器处理错误！');
				   },
				   success: function(data){
					   if(data != null && data.message != null){
						   alert(data.message);
					   } else {
						 alert('操作成功完成！');
					   }
				   }
			 });
	}

	function chooseImage(){
		var link = '<%=request.getContextPath()%>/mx/wx/wxFile/chooseFile?elementId=icon&elementName=icon';
		var x=100;
		var y=100;
		if(is_ie) {
			x=document.body.scrollLeft+event.clientX-event.offsetX-200;
			y=document.body.scrollTop+event.clientY-event.offsetY-200;
		}
		openWindow(link,self,x, y, 745, 480);
	}

</script>
</head>

<body>
<div style="margin:0;"></div>  

<div class="easyui-layout" data-options="fit:true">  
  <div data-options="region:'north',split:true,border:true" style="height:40px"> 
    <div class="toolbar-backgroud"> 
	<span class="x_content_title">编辑投票</span>
	<a href="#" class="easyui-linkbutton" data-options="plain:true, iconCls:'icon-save'" onclick="javascript:saveData();" >保存</a> 
    </div> 
  </div>

  <div data-options="region:'center',border:false,cache:true">
  <form id="iForm" name="iForm" method="post">
  <input type="hidden" id="id" name="id" value="${wxVote.id}"/>
  <table class="easyui-form" style="width:600px;" align="center">
    <tbody>
	<tr>
		<td width="20%" align="left">主题</td>
		<td align="left">
            <input id="title" name="title" type="text" 
			       class="easyui-validatebox"  style="width:415px;"
			       required="true" data-options="required:true" 
				   value="${wxVote.title}"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">投票说明</td>
		<td align="left">
			<textarea  id="desc" name="desc" class="x-textarea"  rows="5" cols="38" style="width:415px;height:80px;">${wxVote.desc}</textarea> 
		</td>
	</tr>
	<tr>
		<td width="15%" align="left" valign="middle">主题图片(720X400)</td>
		<td align="left" valign="top">
		     <c:if test="${not empty wxVote.icon }">
			     <img src="<%=request.getContextPath()%>/${wxVote.icon}" width="60" height="60" border="0"/>
				 <br>
			</c:if>
			<input id="icon" name="icon" type="text" 
			       class="easyui-validatebox x-text" size="38"
			       data-options="required:false" style="width:415px;"
				   value="${wxVote.icon}" onclick="javascript:chooseImage();"/>
			&nbsp; <img src="<%=request.getContextPath()%>/images/icon.gif" border="0"  onclick="javascript:chooseImage();"/>
           <br>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">是否有效</td>
		<td align="left">
			<select id="status" name="status">
				<option value="1">是</option>
				<option value="0">否</option>
			</select>
			<script type="text/javascript">
			   jQuery('#status').val('${wxVote.status}');
			</script>
		    &nbsp;&nbsp;&nbsp;&nbsp;是否记名&nbsp;
		    <select id="signFlag" name="signFlag">
				<option value="0" >否</option>
				<option value="1">是</option>
			</select>
			<script type="text/javascript">
			   jQuery('#signFlag').val('${wxVote.signFlag}');
			</script>
		   &nbsp;&nbsp;&nbsp;&nbsp;是否多选 &nbsp;
		    <select id="multiFlag" name="multiFlag">
				<option value="0" >否</option>
				<option value="1">是</option>
			</select>
			<script type="text/javascript">
			   jQuery('#multiFlag').val('${wxVote.multiFlag}');
			</script>
		   &nbsp;&nbsp;&nbsp;&nbsp;是否限制IP&nbsp;
		    <select id="limitFlag" name="limitFlag">
			    <option value="0" >否</option>
				<option value="1">是</option>
			</select>
			<script type="text/javascript">
			   jQuery('#limitFlag').val('${wxVote.signFlag}');
			</script>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">限制时间间隔</td>
		<td align="left">
			<input id="limitTimeInterval" name="limitTimeInterval" type="text" 
			       class="easyui-numberspinner" size="5" maxlength="5"
				   increment="60"  
				   value="${wxVote.limitTimeInterval}"/>&nbsp;（单位：分钟  1小时=60分钟，1天=1440分钟）
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">开始日期</td>
		<td align="left">
			<input id="startDate" name="startDate" type="text" 
			       class="easyui-datebox"
			       required="true" data-options="required:true" 
				   value="<fmt:formatDate value="${wxVote.startDate}" pattern="yyyy-MM-dd"/>"/>
		    &nbsp;&nbsp;&nbsp;&nbsp;结束日期&nbsp;
			<input id="endDate" name="endDate" type="text" 
			       class="easyui-datebox"
			 required="true" data-options="required:true" 
				  value="<fmt:formatDate value="${wxVote.endDate}" pattern="yyyy-MM-dd"/>"/>
		</td>
	</tr>
	<tr>
		<td width="20%" align="left">投票项目</td>
		<td align="left">
			<textarea id="content" name="content" class="x-textarea"  rows="5" cols="38" style="width:415px;height:80px;">${wxVote.content}</textarea> 
			<br>说明：每行一项，值=名称，每行结束后按“Enter”回车键结束。
			<br>例如：
			<br>    1=赞同
			<br>	2=不赞同
			<br>	0=不表态
		</td>
	</tr>
	<tr>
	    <td width="20%" align="left"></td>
		<td align="left" ><br>
            <input type="button" value=" 保存 " onclick="javascript:saveData();" class="btnGreen">
			<br><br>
		</td>
	</tr>
    </tbody>
  </table>
  </form>
</div>
</div>
</body>
</html>