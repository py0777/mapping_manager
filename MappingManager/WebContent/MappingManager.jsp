<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>MappingManager</title>
<% String pageNum = "1"; %>
</head>
<body>

<form action="MappingManager.jsp" method="POST">
<input type="submit" name = "sqlTran"  value ="SQL_TRAN" >
<input type="submit" name = "sqlTran"  value ="CODE_MAPPING" >
<input type="submit" name = "sqlTran"  value ="COL_MAPPING" >
</br>
<table width="80%" border="1"></table>

<%
log("value  :" +request.getParameter("sqlTran"));
if("SQL_TRAN".equals(request.getParameter("sqlTran")))
{%>
<iframe src ="sqlTran.jsp" frameborder="0" bordercolor ="" marginwidth ="0" width ="100%" height ="700" scolling="no" align="" name="ifr" hspace="0" vspace="0"></iframe>
<%}
else if("CODE_MAPPING".equals(request.getParameter("sqlTran")))
{%>
<iframe src ="codeMapper.jsp" frameborder="0" bordercolor ="" marginwidth ="0" width ="100%" height ="700" scolling="no" align="" name="ifr" hspace="0" vspace="0"></iframe>
<%}
else if("COL_MAPPING".equals(request.getParameter("sqlTran")))
{%>
<iframe src ="colMapper.jsp" frameborder="0" bordercolor ="" marginwidth ="0" width ="100%" height ="700" scolling="no" align="" name="ifr" hspace="0" vspace="0"></iframe>
<%}else{%>
<iframe src ="sqlTran.jsp" frameborder="0" bordercolor ="" marginwidth ="0" width ="100%" height ="700" scolling="no" align="" name="ifr" hspace="0" vspace="0"></iframe>
<%}%>
</form>
</body>
</html>