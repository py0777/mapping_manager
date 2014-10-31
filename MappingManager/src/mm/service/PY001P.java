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


public class PY001P extends AbstractRepository {

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
			
			IRecordSet rs = null;
			
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

			responseData.putField("QUERY", "");
			responseData.putField("RESULT", sb.toString());
			responseData.putRecordSet(rsRtn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ???????. 
		responseData.setOkResultMessage("NCOM0000", null);
		return responseData;
	}
	
	public static void main(String[] args) throws Exception{
		
		logger.debug("########### start main #########");
		
		IDataSet requestData = new DataSet();
		IDataSet responseData = null;
		SqlTranInq sti= new SqlTranInq();
		String sql = null;
		sql = "SELECT A.ROWID AAT070_ROWID, B.ROWID AAT010_ROWID"
		                
						+ ", A.TNB_CD, A.AST_CD, A.ACCT_NO     "
		                + ", A.REGDT, A.BASE_IVST_AST          "
		                + ", A.BF_IVST_DT, A.BF_IVST_AST       "
		                + ", B.PRAC                            "
		                + ", B.CMA_TYPE_GBN, B.CMA_TYPE_GBN2   "
		                + ", B.AUTO_IVST_TNB_CD, B.AUTO_IVST_AST_CD, B.AUTO_IVST_ACCT_NO"
		           +"  FROM AA.AAT070 as A  "
		           +"     , AA.AAT010 as B"
		           +"     , AA.AAT010 as C"
		           +" WHERE B.TNB_CD = A.TNB_CD"
		           +"   AND B.AST_CD = A.AST_CD   "
		           +"   AND B.ACCT_NO = A.ACCT_NO "
		           +"   AND B.CMA_TYPE_GBN2 = '9' "
		           +"   AND A.REG_ABAN_GBN = '1' ";
		requestData.putField("QUERY", sql);
		responseData	=	sti.asSqlTrnInq(requestData);
		
		logger.debug(responseData);
		logger.debug("########### end main  #########");
		
	}

}
