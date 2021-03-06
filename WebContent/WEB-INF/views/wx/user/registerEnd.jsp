<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://github.com/jior/glaf/tags" prefix="glaf"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="html"%>
<%@ page import="com.glaf.base.utils.*"%>
<%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type='text/javascript' src="<%= request.getContextPath() %>/scripts/main.js"></script>
<script type='text/javascript' src='<%= request.getContextPath() %>/scripts/close.js'></script>
<script language="javascript">
window.moveTo(10000, 10000);
window.resizeTo(0, 0);
</script>
<title>基础平台系统</title>
<body>
<div id="messageDiv" style="display:none">
<glaf:messages id="message" message="true"> 
   <glaf:write name="message"/> 
</glaf:messages>
</div>
 
</body>
<script language="javascript">
var messageDiv = document.getElementById("messageDiv");
var message = messageDiv.innerText;
 
if (message.length > 0) {
    alert(message);
}
 
<c:choose>
 <c:when test="${registerStatus==2}">
    location.href="<%= request.getContextPath() %>/mx/login";
 </c:when>
 <c:otherwise>
    history.back(0);
 </c:otherwise>
</c:choose>
</script>
</html>