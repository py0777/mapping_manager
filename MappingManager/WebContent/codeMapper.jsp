<%@page import="nexcore.framework.core.data.IRecordSet"%>
<%@page import="nexcore.framework.core.util.StringUtils"%>
<%@page import= "nexcore.framework.core.data.RecordSet"%>
<%@ page import="nexcore.framework.core.data.DataSet, nexcore.framework.core.data.IDataSet, mm.service.CodeInq;" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CodeMapper</title>
</head>
<body>
  
<%
String result = null;

IRecordSet resultRs = new RecordSet("RS_LIST", new String[]{"WORK_NAME","T_ENG_COLUMN_NAME", "T_CODE_NAME", "T_CODE_CD", "T_CMT", "T_REMARK", "A_KCODE_NAME", "A_ECODE_NAME", "A_CODE_CD", "A_CMT", "A_REMARK", "업무담당자"});
int resultRsCnt = 0;
String resultMessage = "";
request.setCharacterEncoding("UTF-8");/* 한글깨짐 현상 없애기 위함*/
if(!StringUtils.isEmpty(request.getParameter("INPUT"))){
	
	IDataSet requestData = new DataSet();
	
	requestData.putField("INPUT", request.getParameter("INPUT").toUpperCase());
	requestData.putField("INQ_CON", request.getParameter("INQ_CON"));
	
	CodeInq cdi = new CodeInq();
	
	IDataSet responseData = cdi.codeInq(requestData);
	if( responseData != null){
		resultRs  = responseData.getRecordSet("rsCode");
		resultRsCnt = responseData.getIntField("rsCnt");
	}	
	}else{
		result = "";
	}


%>
<form action="codeMapper.jsp" method="POST">
<p>
	<lable for="INQ_CON"><strong>조회조건 : </strong></lable>
	<input type=radio name="INQ_CON" value="1" <%if("1".equals(request.getParameter("INQ_CON"))  || StringUtils.isEmpty(request.getParameter("INQ_CON"))){%>checked<%}%>/>TOBE영문명
	<input type=radio name="INQ_CON" value="2" <%if("2".equals(request.getParameter("INQ_CON"))){%>checked<%}%> />TOBE한글명
	<input type=radio name="INQ_CON" value="3" <%if("3".equals(request.getParameter("INQ_CON"))){%>checked<%}%> />ASIS영문명
	<input type=radio name="INQ_CON" value="4" <%if("4".equals(request.getParameter("INQ_CON"))){%>checked<%}%> />ASIS한글명
</p>
<label ><strong>INPUT  : </strong></lable>
<input name ="INPUT" type ="text" value =<%=request.getParameter("INPUT")%>>
<input type="submit" value="조회" >
<br />
<label >※ TOBE영문코드명,TOBE한글코드명, ASIS코드영문명, ASIS코드한글명 들로 LIKE 조회됨.</lable>
<br />
<br />
		<table width="80%" border="1" cellpadding="4" cellspacing ="0" style="border-collapse:collapse;">
		<%
		  if(resultRsCnt > 0){
		%>
		<tr>
			<td bgcolor="yellow"><strong>담당파트</strong></td>
			<td bgcolor="yellow"><strong>TOBE코드영문명</strong></td>
			<td bgcolor="yellow"><strong>TOBE코드한글명</strong></td> 
			<td bgcolor="yellow"><strong>TOBE유효값</strong></td>     	
			<td bgcolor="yellow"><strong>TOBE유효값명</strong></td>   
			<td bgcolor="yellow"><strong>ASIS코드한글</strong></td>  	
			<td bgcolor="yellow"><strong>ASIS코드명</strong></td> 
			<td bgcolor="yellow"><strong>ASIS유효값</strong></td>   	
			<td bgcolor="yellow"><strong>ASIS유효명</strong></td>
			<td bgcolor="yellow"><strong>TOBE비고</strong></td>
			<td bgcolor="yellow"><strong>ASIS비고</strong></td>
			<td bgcolor="yellow"><strong>업무담당자</strong></td>
		</tr>	   
		<% 	
		   }
		%>
<%
	for(int i = 0; i < resultRsCnt ; i++){		
%>
			<tr>
				<td><%=resultRs.get(i, "업무영역")%></td>     <!- 파트      ->
				<td><%=resultRs.get(i, "TOBE영문코드명")%></td>  <!- tobe코드영문명  ->
				<td><%=resultRs.get(i, "TOBE코드명")%></td>  <!- tobe코드명  ->
				<td><%=resultRs.get(i, "TOBE코드값")%></td>     <!- tobe유효값        ->
				<td><%=resultRs.get(i, "TOBE설명")%></td>   <!- tobe유효값명    ->				
				<td><%=resultRs.get(i, "ASIS한글컬럼명")%></td>    <!- asis코드한글  ->
				<td><%=resultRs.get(i, "ASIS영문컬럼명")%></td>   <!- asis코드명       ->
				<td><%=resultRs.get(i, "ASIS코드값")%></td>    <!- asis유효값    ->
				<td><%=resultRs.get(i, "ASIS설명")%></td>    <!- asis유효값명    ->
				<td><%=resultRs.get(i, "TOBE비고")%></td>  <!- TOBE비고     ->
				<td><%=resultRs.get(i, "ASIS비고")%></td>    <!-ASIS 비고   ->
				<td><%=resultRs.get(i, "업무담당자")%></td>    <!-업무담당자->
			</tr>
			<%	
	}

%>
		</table>
		<%=resultMessage %>
	</form>
</body>
</html>