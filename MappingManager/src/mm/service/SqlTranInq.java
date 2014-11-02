package mm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;



import mm.common.RecordSetResultHandler;
import mm.repository.AbstractRepository;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
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
			sqlSession.select(statement, requestData.getFieldMap(), resultHandler);
			
			IDataSet ds = new DataSet();
			ds.putRecordSet(resultHandler.getRecordSet());
			
			logger.debug(ds);
			
			return ds;
		} finally {
			sqlSession.close();
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
			
			logger.debug(ds);
			return ds;
		} finally {
			sqlSession.close();
		}
	}
	
	public IDataSet asSqlTrnInq(IDataSet requestData){
		
		logger.debug("###########  START #########");
		logger.debug(getClass().getName());
		
		logger.debug(requestData.getField("QUERY"));
		IDataSet responseData = new DataSet();

		IDataSet dsTbl = null;
		IDataSet dsCol = null;
		IRecordSet rsTbl = null;
		IRecordSet rsCol = null;
		IRecordSet rsRtn = new RecordSet("RS_LIST", new String[]{"TBL","TBLNM","COL","COLNM","ATBL","ATBLNM","ACOL","ACOLNM"});
		
		String query = "";
		String key = "";
		String[] tmp;
		String[] tmp2;
		StringBuffer sb = new StringBuffer();
		

		String parseReg[] = {"\\.", "\\;", "\\(", "\\)", "\\,", "\\|", "=", " ", "\r\n", "\r", "\n"};
		String parseReg2[] = {"~~\\.~~", "~~\\;~~", "~~\\(~~", "~~\\)~~", "~~\\,~~", "~~|~~","~~=~~", "~~ ~~", "~~\r\n~~", "~~\r~~", "~~\n~~"};
		try {
			query = requestData.getField("QUERY");
			
			dsTbl = s001(requestData);
			rsTbl = dsTbl.getRecordSet("RS_TBLLIST");
			
			query = query.toUpperCase();
			query = query.replaceAll("\t", "    ");
			
			for(int i=0; i<parseReg.length; i++){
				query = query.replaceAll(parseReg[i], parseReg2[i]);
			}
			
			logger.debug(query);	
			ArrayList<String> al = new ArrayList<String>();
			HashMap<String, String> tblMap = new HashMap<String, String>();
			tmp = query.split("~~");
			int kk = 0;
			for(int i = 0; i < tmp.length ; i++){
				if(!StringUtils.isEmpty(tmp[i].replace(" ", ""))){
					al.add(tmp[i]);
				}	
			}
			logger.debug(al);
			
			for(int j=0; j<rsTbl.getRecordCount(); j++){
				key = rsTbl.getRecord(j).get("TBL").trim();
				for(int k=0; k<tmp.length; k++){
					if(!StringUtils.equals(" ", tmp[k])){
						if(StringUtils.equals(tmp[k], key)){
							tmp[k] = tmp[k].replaceAll(key, rsTbl.getRecord(j).get("ATBL").trim());
							tblMap.put(key, rsTbl.getRecord(j).get("ATBL").trim());
						}
					}
				}
			}
			
			logger.debug(tblMap);
			
			StringBuffer keySB = new StringBuffer();
			Set<String> tblSet = tblMap.keySet();
			Iterator<String> it = tblSet.iterator();
			
			while(it.hasNext()){
				String k = it.next();
				keySB.append("'");
				keySB.append(k);
				keySB.append("'");
				keySB.append(",");
			}
			
			if(keySB.length() >0){
				requestData.putField("KEY_LIST", keySB.delete(keySB.length()-1, keySB.length()).toString());
			}else{
				requestData.putField("KEY_LIST", "''");
			}
			dsCol = s002(requestData);
			rsCol = dsCol.getRecordSet("RS_COLLIST");
			
			logger.debug("=========================");
			logger.debug(rsCol);
			for(int j=0; j<rsCol.getRecordCount(); j++){
				if(tblMap.containsKey(rsCol.getRecord(j).get("TBL"))){
					key = rsCol.getRecord(j).get("COL").trim();
					for(int k=0; k<tmp.length; k++){
						if(!StringUtils.equals(" ", tmp[k])){
							if(StringUtils.equals(tmp[k], key)){
								tmp[k] = tmp[k].replaceAll(key, rsCol.getRecord(j).get("ACOL").trim());
								rsRtn.addRecord(rsCol.getRecord(j));
							}else if(StringUtils.equals(tmp[k], ":"+key)){
								tmp[k] = "#"+tmp[k].replaceAll(key, rsCol.getRecord(j).get("ACOL").trim()).substring(1)+"#";
							}
						}
					}
				}
			}
			
			for(int u=0; u<tmp.length; u++){
				sb.append(tmp[u]);
			}
			SQLBeautifier sbf = new SQLBeautifier();
			
			responseData.putField("QUERY", "");
			responseData.putField("RESULT", sbf.beautify(sb.toString()));
			responseData.putRecordSet(rsRtn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ???????. 
		responseData.setOkResultMessage("NCOM0000", null);
		return responseData;
	}
	
//	public static void main(String[] args) throws Exception{
//		
//		logger.debug("########### start main #########");
//		
//		IDataSet requestData = new DataSet();
//		IDataSet responseData = null;
//		SqlTranInq sti= new SqlTranInq();
//		String sql = null;
//		sql = "UPDATE AC.TRMPAYM, DD.TCMBTRT   SET CLCUSR_NUM  = T_AUTO_IVST_TNB_CD,      CLCBRC_COD  = T_AUTO_IVST_AST_CD,"
//				+ "       CLCBRC_COD1  = T_AUTO_IVST_AST_CD,"
//				+ "       MRVBRC_COD = T_AUTO_IVST_ACCT_NO,"
//				+ "       PATMT_MTHCOD      = T_CMA_TYPE_GBN,"
//				+ "       PAYMY_CNT    = SYSDATE,"
//				+ "       DLMN_ENO      = '원카드',"
//				+ "       PAYMT_CNT     = BLNG_TNB_CD,"
//				+ "       PAYMT_DTE    = T_DL_TERM_NO"
//				+ " WHERE TRANS_COD    = C1.TNB_CD"
//				+ "   AND OPTBRC_COD   = C1.AST_CD"
//				+ "   AND WOORI_COD    = C1.ACCT_NO;"
//				;
//		requestData.putField("QUERY", sql);
//		responseData	=	sti.asSqlTrnInq(requestData);
//		
//		logger.debug(responseData);
//		logger.debug("########### end main  #########");
//		
//	}
}
