<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="html"%>
<%@ page import="java.util.*"%>
<%@ page import="com.glaf.base.modules.sys.*"%>
<%@ page import="com.glaf.base.modules.sys.model.*"%>
<%@ page import="com.glaf.base.utils.*"%>
<%
String context = request.getContextPath();
Group bean=(Group)request.getAttribute("bean");
List  list = (List)request.getAttribute("parent");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<link href="<%=context%>/css/site.css" type="text/css" rel="stylesheet">
<script language="javascript" src='<%=context%>/scripts/main.js'></script>
<script language="javascript" src='<%=context%>/scripts/verify.js'></script></head>

<body>
<html:form action="${contextPath}/base/group.do?method=saveModify" method="post"  onsubmit="return verifyAll(this);">
<input type="hidden" name="id" value="<%=bean.getGroupId()%>">
<input type="hidden" name="groupId" value="<%=bean.getGroupId()%>">
<div class="nav-title"><span class="Title">群组管理</span>&gt;&gt;修改群组</div>
<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0" class="box">
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr class="box">
        <td class="box-lt">&nbsp;</td>
        <td class="box-mt">&nbsp;</td>
        <td class="box-rt">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td class="box-mm"><table width="95%" align="center" border="0" cellspacing="0" cellpadding="5">
      <tr>
        <td class="input-box">群组名称*</td>
        <td>
		<input name="name" type="text" size="35" class="input" datatype="string" nullable="no" maxsize="20" chname="群组名称" value="<%=bean.getName()%>">
		</td>
      </tr>
      <tr>
        <td class="input-box2" valign="top">描　　述</td>
        <td>
		<textarea name="desc" cols="35" rows="3" class="input-multi" datatype="string" nullable="yes" maxsize="100" 
		chname="描述"><%=bean.getDesc()%></textarea>       
		</td>
      </tr>
      <tr>
        <td colspan="2" align="center" valign="bottom" height="30">&nbsp;
              <input name="btn_save" type="submit" value="保存" class="button">
	    </td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr class="box">
        <td class="box-lb">&nbsp;</td>
        <td class="box-mb">&nbsp;</td>
        <td class="box-rb">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
</table>
</html:form>
</body>
</html>
