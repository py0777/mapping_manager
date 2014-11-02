<%@page import="nexcore.framework.core.util.StringUtils"%>
<%@ page import="nexcore.framework.core.data.DataSet, nexcore.framework.core.data.IDataSet, mm.service.SqlTranInq;" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<%
String result = null;

request.setCharacterEncoding("UTF-8");/* 한글깨짐 현상 없애기 위함*/
if(!StringUtils.isEmpty(request.getParameter("asisSql"))){
	
	IDataSet requestData = new DataSet();
	requestData.putField("QUERY", request.getParameter("asisSql"));
	SqlTranInq sti = new SqlTranInq();
	
	IDataSet responseData = sti.asSqlTrnInq(requestData);
	if( responseData != null){
		result = responseData.getField("RESULT");
	}	
}else{
	result = "";
}
%>
<form action="sqlTran.jsp" method="POST">
<input type="submit" value="변환" >
<br />
<TEXTAREA name="asisSql" rows="20" style="WIDTH: 40%">
<% if (StringUtils.isEmpty(request.getParameter("asisSql"))){ %><%= "" %>
<% }else{ %><%=request.getParameter("asisSql")%>
<% } %>
</textarea> 
<TEXTAREA name="tobeSql" rows="20" style="WIDTH: 40%">
<%=result%>
</textarea>
</form>
</body>
</html>