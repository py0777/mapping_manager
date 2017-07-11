package mm.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mm.common.RecordSetResultHandler;
import mm.repository.AbstractRepository;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IRecord;
import nexcore.framework.core.data.IRecordHeader;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.util.StringUtils;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.orasql.SQLBeautifier;

public class SqlTranInq extends AbstractRepository {
	static Logger logger = Logger.getLogger(SqlTranInq.class);
	private final String namespace = "mm.repository.mapper.SqlTranInqMapper";

	public IDataSet s001(IDataSet requestData) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		try {
			String statement = namespace + ".S001";

			IRecordSet rs = null;

			RecordSetResultHandler resultHandler = new RecordSetResultHandler();
			resultHandler.setRecordSetId("RS_TBLLIST");

			logger.debug("strat!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			sqlSession.select(statement, requestData.getFieldMap(), resultHandler);

			IDataSet ds = new DataSet();
			ds.putRecordSet(resultHandler.getRecordSet());

			return ds;
		} finally {
			sqlSession.close();
			/* test */
		}
	}

	public IDataSet s002(IDataSet requestData) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		try {

			String statement = namespace + ".S002";

			RecordSetResultHandler resultHandler = new RecordSetResultHandler();
			resultHandler.setRecordSetId("RS_COLLIST");
			sqlSession.select(statement, requestData.getFieldMap(), resultHandler);

			IDataSet ds = new DataSet();
			ds.putRecordSet(resultHandler.getRecordSet());

			return ds;
		} finally {
			sqlSession.close();
		}
	}

	public IDataSet asSqlTrnInq(IDataSet requestData) {

		logger.debug("###########  START #########");
		logger.debug(getClass().getName());
		
		logger.debug(requestData);
		logger.debug(requestData.getFieldValues("TOBE_TBL_LIST"));
		/*************************************************************
		 * Declare Var
		 *************************************************************/
		IDataSet responseData = new DataSet();

		IDataSet dsTbl = null;
		IDataSet dsCol = null;
		
		IRecordSet rsTbl = null;
		IRecordSet rsCol = null;
		IRecordSet rsRtn = new RecordSet("RS_LIST");
		IRecordSet rsTblRtn = new RecordSet("RETURN_TBL");		
		IRecordSet rsTblColRtn = new RecordSet("RETURN_TBL_COL");
		
		String rtnMsg = "";

		String query = "";
		String s_asisTbl = "";
		String s_tobeTbl ="";
		String s_asisCol = "";
		String s_tobeCol = "";
		String s_tobeColNm = "";
		String[] tmp;
		String[] inputTobeTbl;
		
		
		
		StringBuffer sb = new StringBuffer();

		String parseReg[] = { "\\.", "\\;", "\\(", "\\)", "\\,", "\\|", "=", " ", "\r\n", "\r", "\n" , "@"};
		String parseReg2[] = { "~~\\.~~", "~~\\;~~", "~~\\(~~", "~~\\)~~", "~~\\,~~", "~~|~~", "~~=~~", "~~ ~~",
				"~~\r\n~~", "~~\r~~", "~~\n~~" ,"~~@~~"};
		String today = "";
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss초 E(a)");

		try {
			Date d = gc.getTime();
			/*************************************************************
			 * 1. 모든 테이블 조회
			 *************************************************************/
			today = sdformat.format(d);
			query = requestData.getField("QUERY");

			dsTbl = s001(requestData);
			rsTbl = dsTbl.getRecordSet("RS_TBLLIST");

			query = query.toUpperCase();
			query = query.replaceAll("\t", "    ");

			for (int i = 0; i < parseReg.length; i++) {
				query = query.replaceAll(parseReg[i], parseReg2[i]);
			}

			logger.debug(query);

			tmp = query.split("~~");
			
			logger.debug(tmp);
			
			inputTobeTbl = requestData.getFieldValues("TOBE_TBL_LIST");
			
			/*리턴테이블 헤더 값설정*/
			for(int i = 0 ; i < rsTbl.getHeaderCount(); i ++ )
			{
				rsTblRtn.addHeader(i, rsTbl.getHeader(i));
			}
			
			String skipYn = "N";
			for (int i = 0; i < rsTbl.getRecordCount(); i++) {
				s_asisTbl = rsTbl.getRecord(i).get("TBL").trim();

				for (int j = 0; j < tmp.length; j++) {
					
					if (StringUtils.equals(tmp[j], s_asisTbl) && !StringUtils.equals(tmp[j], "SY")) {
						
						if(StringUtils.isEmpty(inputTobeTbl[0]))
						{
							rsTblRtn.addRecord(rsTbl.getRecord(i));
						}
						else
						{
							for(int k = 0 ; k < inputTobeTbl.length; k++ )
							{
								if(StringUtils.isEmpty(inputTobeTbl[k]))
								{
									continue;
								}
								if(inputTobeTbl[k].equals(rsTbl.getRecord(i).get("ATBL").trim()))
								{
									rsTblRtn.addRecord(rsTbl.getRecord(i));								
								}
							}
						}						
					}
				}
			}

			
			logger.debug("rsTblRtn : " + rsTblRtn);
			StringBuffer asisTblSB = new StringBuffer();
			StringBuffer tobeTblSB = new StringBuffer();
			
			for(int i= 0 ; i < rsTblRtn.getRecordCount(); i++)
			{
				String asisTbl = rsTblRtn.get(i, "TBL"); /*ASIS*/
				asisTblSB.append("'");
				asisTblSB.append(asisTbl);
				asisTblSB.append("'");
				asisTblSB.append(",");
				
				String tobeTbl = rsTblRtn.get(i, "ATBL"); /*TOBE*/
				tobeTblSB.append("'");
				tobeTblSB.append(tobeTbl);
				tobeTblSB.append("'");
				tobeTblSB.append(",");
			}
			

			if (asisTblSB.length() > 0) {
				requestData.putField("ASIS_TBL_LIST", asisTblSB.delete(asisTblSB.length() - 1, asisTblSB.length()).toString());
				requestData.putField("TOBE_TBL_LIST", tobeTblSB.delete(tobeTblSB.length() - 1, tobeTblSB.length()).toString());
			} else {
				requestData.putField("ASIS_TBL_LIST", "''");
				requestData.putField("TOBE_TBL_LIST", "''");
			}
			dsCol = s002(requestData);
			rsCol = dsCol.getRecordSet("RS_COLLIST");

			/*테이블맵핑 해더 설정*/
			for(int i = 0 ; i < rsCol.getHeaderCount(); i ++ )
			{
				rsRtn.addHeader(i, rsCol.getHeader(i));
				rsTblColRtn.addHeader(i, rsCol.getHeader(i));
			}
			logger.debug("=========================");
			logger.debug(rsCol);
			
			/*컬럼 변환*/
			for (int i = 0; i < rsCol.getRecordCount(); i++) {
				s_asisCol = rsCol.getRecord(i).get("COL").trim();
				s_tobeCol = rsCol.getRecord(i).get("ACOL");
				s_tobeColNm = rsCol.getRecord(i).get("ACOLNM");
				for (int k = 0; k < tmp.length; k++) {

					if (StringUtils.equals(tmp[k], s_asisCol)) {
						tmp[k] = tmp[k].replaceAll(s_asisCol, s_tobeCol);
						if ("Y".equals(requestData.getField("COMMENT_YN"))) {
							tmp[k] = tmp[k].concat("/*").concat(s_tobeColNm)
									       .concat("*/"); /* 컬럼 뒤에 주석달기 */
						}
												
						rsRtn.addRecord(rsCol.getRecord(i));
						
						if( !s_tobeTbl.equals(rsCol.getRecord(i).get("ATBL")))
						{
							rsTblColRtn.addRecord(rsCol.getRecord(i));
							s_tobeTbl = rsCol.getRecord(i).get("ATBL");
						}						
						
					} else if (tmp[k].contains(":")) {
						tmp[k] = tmp[k].toLowerCase();
					}
				}
			}
			
			logger.debug(rsTblColRtn);
			
			/*테이블 변환*/
			for (int i = 0; i < rsTblColRtn.getRecordCount(); i++) {
				s_asisTbl = rsTblColRtn.getRecord(i).get("TBL").trim();

				for (int j = 0; j < tmp.length; j++) {
					if (StringUtils.equals(tmp[j], s_asisTbl) && !StringUtils.equals(tmp[j], "SY")) {
						
						tmp[j] = tmp[j].replaceAll(s_asisTbl, rsTblColRtn.getRecord(i).get("ATBL").concat("  /*")
								.concat(rsTblColRtn.getRecord(i).get("ATBLNM")).concat("*/"));
						if (".".equals(tmp[j - 1])) {
							tmp[j - 2] = "ACADM";
						} else {
							tmp[j] = "ACADM.".concat(tmp[j]);
						}
						
						
					}
				}
			}
			
			for (int u = 0; u < tmp.length; u++) {
				sb.append(tmp[u]);
			}
			SQLBeautifier sbf = new SQLBeautifier();
			
			responseData.putField("QUERY", "");
			if("Y".equals(requestData.getField("SQLFORMAT_YN"))){
				responseData.putField("RESULT", sbf.beautify(String.valueOf(sb)));	
			}
			else
			{
				responseData.putField("RESULT", String.valueOf(sb));
			}
			
			responseData.putField("rsCnt", rsRtn.getRecordCount());
			responseData.putField("rsTblCnt", rsTblColRtn.getRecordCount());
			responseData.putRecordSet("rsTblRtn", rsTblColRtn);			
			responseData.putRecordSet("rsRtn", rsRtn);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (rsTblRtn.getRecordCount() == 0) {
			rtnMsg = "입력한 테이블이 존재하지 않습니다.";
		} else {
			rtnMsg = "조회완료되었습니다.";
		}

		responseData.putField("rtnMsg", today + " " + rtnMsg);
		responseData.setOkResultMessage("NCOM0000", null);
		return responseData;
	}
}
