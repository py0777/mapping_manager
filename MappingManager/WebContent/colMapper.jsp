<%@page import="nexcore.framework.core.data.IRecordSet"%>
<%@page import="nexcore.framework.core.util.StringUtils"%>
<%@page import= "nexcore.framework.core.data.RecordSet"%>
<%@ page import="nexcore.framework.core.data.DataSet, nexcore.framework.core.data.IDataSet, mm.service.ColInq;" language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ColMapper</title>
</head>
<body>
  
<%
String result = null;

IRecordSet colRs = null;
IRecordSet tblRs = null;
int resultRsCnt = 0;
int rsTblCnt = 0;
String resultMessage = "";
request.setCharacterEncoding("UTF-8");/* 한글깨짐 현상 없애기 위함*/
if(!StringUtils.isEmpty(request.getParameter("INQ_CON"))){
	
	IDataSet requestData = new DataSet();
	
	requestData.putField("IN_TBL", request.getParameter("IN_TBL").toUpperCase());
	requestData.putField("IN_COL", request.getParameter("IN_COL").toUpperCase());
	requestData.putField("INQ_CON", request.getParameter("INQ_CON"));
	
	ColInq coli = new ColInq();
	
	IDataSet responseData = coli.colInq(requestData);
	if( responseData != null){
		colRs  = responseData.getRecordSet("rsCol");
		resultRsCnt = responseData.getIntField("rsCnt");	//컬럼맵핑정의서 count
		rsTblCnt = responseData.getIntField("rsTblCnt");	//테이블 count
		tblRs  = responseData.getRecordSet("rsTbl");
		resultMessage = responseData.getField("rtnMsg");
	}
	
}else{
		result = "";
}


%>
<form action="colMapper.jsp" method="POST">
<p>
	<lable for="INQ_CON"><strong>조회조건 : </strong></lable>
	<input type=radio name="INQ_CON" value="1" <%if("1".equals(request.getParameter("INQ_CON"))){%>checked<%}%> />TOBE기준
	<input type=radio name="INQ_CON" value="2" <%if("2".equals(request.getParameter("INQ_CON"))|| StringUtils.isEmpty(request.getParameter("INQ_CON"))){%>checked<%}%> />ASIS기준	
</p>
<label ><strong>테이블명  : </strong></lable>
<input name ="IN_TBL" type ="text" value =<%=request.getParameter("IN_TBL")%>>
<label ><strong>컬럼명  : </strong></lable>
<input name ="IN_COL" type ="text" value =<%=request.getParameter("IN_COL")%>>
<input type="submit" value="조회" >
<br />
<label >※ 테이블또는 컬럼 중 한개 이상 입력하셔야 합니다.</lable>

<br />
		<table width="80%" border="1" cellpadding="4" cellspacing ="0" style="border-collapse:collapse;">
		<%
		  if(rsTblCnt > 0){
		%>
		<tr> 
			<td bgcolor="green" colspan="5"><strong>테이블맵핑</strong></td>
		</tr>		
		<tr>
			<td bgcolor="yellow"><strong>TOBE 테이블</strong></td> 
			<td bgcolor="yellow"><strong>TOBE 테이블한글명</strong></td>
			<td bgcolor="yellow"><strong>ASIS 테이블</strong></td> 
			<td bgcolor="yellow"><strong>ASIS 테이블한글명</strong></td>
			<td bgcolor="yellow"><strong>업무담당자</strong></td>
		</tr>	   
		<% 	
		   }
		%>
<%
	for(int i = 0; i < rsTblCnt ; i++){		
%>
			<tr>				  
				<td><%=tblRs.get(i, "T_ENG_TABLE_NAME")%></td> 
				<td><%=tblRs.get(i, "T_KOR_TABLE_NAME")%></td>
				<td><%=tblRs.get(i, "A_ENG_TABLE_NAME")%></td>
				<td><%=tblRs.get(i, "A_KOR_TABLE_NAME")%></td>
				<td><%=tblRs.get(i, "업무담당자")%></td>
			</tr>
<%	
}
%>
<br />
<br />
		<table width="80%" border="1" cellpadding="4" cellspacing ="0" style="border-collapse:collapse;">
		<%
		  if(resultRsCnt > 0){
		%>
		
		<tr> 
			<td bgcolor="green" colspan="19"><strong>컬럼맵핑정의서</strong></td>
		</tr>
		
		<tr>
			<td bgcolor="yellow"><strong>MAP_ID</strong></td>
			<!--<td bgcolor="yellow"><strong>T_OWNER</strong></td> -->
			<td bgcolor="yellow"><strong>TOBE테이블명</strong></td> 
			<td bgcolor="yellow"><strong>TOBE한글테이블명</strong></td>
			<td bgcolor="yellow"><strong>TOBE컬럼명</strong></td>
			<td bgcolor="yellow"><strong>TOBE영문컬럼명</strong></td>
			<td bgcolor="yellow"><strong>TOBE TYPE</strong></td>
			<td bgcolor="yellow"><strong>TOBE길이1</strong></td>
			<td bgcolor="yellow"><strong>TOBE길이2</strong></td>
			<td bgcolor="yellow"><strong>TOBE_PK</strong></td>
			<!--<td bgcolor="yellow"><strong>A_SYSTEM_NAME</strong></td>-->			
			<!--<td bgcolor="yellow"><strong>A_OWNER</strong></td>-->
			<td bgcolor="yellow"><strong>ASIS테이블명</strong></td> 
			<td bgcolor="yellow"><strong>ASIS한글테이블명</strong></td>
			<td bgcolor="yellow"><strong>ASIS컬럼명</strong></td>
			<td bgcolor="yellow"><strong>ASIS영문컬럼명</strong></td>
			<td bgcolor="yellow"><strong>ASIS TYPE</strong></td>
			<td bgcolor="yellow"><strong>ASIS길이1</strong></td>			
			<td bgcolor="yellow"><strong>ASIS_PK</strong></td>
			<td bgcolor="yellow"><strong>MOVE_RULE</strong></td>	
			<td bgcolor="yellow"><strong>MOVE_SQL</strong></td>	
			<td bgcolor="yellow"><strong>업무담당자</strong></td>
			<!--<td bgcolor="yellow"><strong>UPDATE_DT</strong></td>-->	
		</tr>	   
		<% 	
		   }
		%>
<%
	for(int i = 0; i < resultRsCnt ; i++){		
%>
			<tr>
				<td><%=colRs.get(i, "MAP_ID")%></td>     
				<!--  <td><%=colRs.get(i, "T_OWNER")%></td>-->  
				<td><%=colRs.get(i, "T_ENG_TABLE_NAME")%></td> 
				<td><%=colRs.get(i, "T_KOR_TABLE_NAME")%></td>
				<td><%=colRs.get(i, "T_ENG_COLUMN_NAME")%></td>
				<td><%=colRs.get(i, "T_KOR_COLUMN_NAME")%></td>
				<td><%=colRs.get(i, "T_DATA_TYPE")%></td>
				<td><%=colRs.get(i, "T_LENGTH1")%></td>
				<td><%=colRs.get(i, "T_LENGTH2")%></td>
				<td><%=colRs.get(i, "T_PK")%></td>
				<!--<td><%=colRs.get(i, "A_SYSTEM_NAME")%></td>-->
				<!--<td><%=colRs.get(i, "A_OWNER")%></td>-->
				<td><%=colRs.get(i, "A_ENG_TABLE_NAME")%></td>
				<td><%=colRs.get(i, "A_KOR_TABLE_NAME")%></td>
				<td><%=colRs.get(i, "A_ENG_COLUMN_NAME")%></td>
				<td><%=colRs.get(i, "A_KOR_COLUMN_NAME")%></td>
				<td><%=colRs.get(i, "A_DATA_TYPE")%></td>
				<td><%=colRs.get(i, "A_LENGTH1")%></td>
				<td><%=colRs.get(i, "A_PK")%></td>
				<td><%=colRs.get(i, "MOVE_RULE")%></td>
				<td><%=colRs.get(i, "MOVE_SQL")%></td>
				<td><%=colRs.get(i, "업무담당자")%></td>
				<!--<td><%=colRs.get(i, "UPDATE_DT")%></td>-->
			</tr>
<%	
}
%>
		</table>
		<%=resultMessage %>
	</form>
</body>
</html>