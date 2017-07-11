package mm.service;
import mm.common.RecordSetResultHandler;
import mm.repository.AbstractRepository;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.util.StringUtils;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;


	public class ColInq extends AbstractRepository {

	static Logger logger = Logger.getLogger(SqlTranInq.class);
	private final String namespace = "mm.repository.mapper.ColMapper";

	public IDataSet s001(IDataSet requestData) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		try {
			String statement = namespace + ".S001";
			
			RecordSetResultHandler resultHandler = new RecordSetResultHandler();
			resultHandler.setRecordSetId("RS_COLLIST");
			
			logger.debug("strat!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			sqlSession.select(statement, requestData.getFieldMap(), resultHandler);
			
			IDataSet ds = new DataSet();
			ds.putRecordSet(resultHandler.getRecordSet());
			
			logger.debug(ds);
			
			return ds;
		} finally {
			sqlSession.close();
			/*test*/
		}
	}
	
	public IDataSet colInq(IDataSet requestData){
		logger.debug("###########  START #########");
		logger.debug(getClass().getName());
		
		logger.debug(requestData.getField("INPUT"));
				
		/*************************************************************
		 *  Declare Var
		 *************************************************************/
		IDataSet 	responseData 	= new DataSet();
		IDataSet 	dsCol 			= null;
		IRecordSet 	rsCol 			= null;
		IRecordSet 	rsTbl 			= new RecordSet("AA");
		String      rtnMsg			= "";
		try
		{
			/*�Է°� üũ*/
			if(		(	requestData.getField("IN_TBL").isEmpty()
					 &&	requestData.getField("IN_COL").isEmpty())
				||	(	"NULL".equals(requestData.getField("IN_TBL"))
					 &&	"NULL".equals(requestData.getField("IN_COL"))))
			{
				rtnMsg = "�Է°��� Ȯ���ϼ���";
				responseData.setOkResultMessage("ERROR", new String[]{"�Է°��� Ȯ���ϼ���"});
				responseData.putField("rtnMsg", rtnMsg);
				return responseData;
			}
			
			if( requestData.getField("IN_TBL").isEmpty() || "NULL".equals(requestData.getField("IN_TBL")))
			{
				requestData.putField("IN_TBL", "%");
			}
			
			if( requestData.getField("IN_COL").isEmpty() || "NULL".equals(requestData.getField("IN_COL")))
			{
				requestData.putField("IN_COL", "%");
			}
			dsCol	=	s001(requestData);
			
			rsCol = dsCol.getRecordSet("RS_COLLIST");
			
			logger.debug(rsCol);
			
			responseData.putField("rsCnt", rsCol.getRecordCount());
			
			if(rsCol.getRecordCount() == 0)
			{
				rtnMsg = "��ȸ�� �����Ͱ� �����ϴ�.";
				responseData.setOkResultMessage("ERROR", new String[]{"��ȸ�� �����Ͱ� �����ϴ�."});
				responseData.putField("rtnMsg", rtnMsg);				
				return responseData;
			}
			
			/*���̺���� �ش� ����*/
			for(int i = 0 ; i < rsCol.getHeaderCount(); i ++ )
			{
				rsTbl.addHeader(i, rsCol.getHeader(i));
			}
			
			/*���̺���� ���ڵ�� ����*/
			String l_T_ENG_TABLE_NAME = "";
			String l_A_ENG_TABLE_NAME = "";
			String l_MAP_ID = "";
			
			int j = 0;
			for(int i=0; i < rsCol.getRecordCount(); i++)
			{	
				
				if( 	StringUtils.isEmpty(rsCol.getRecord(i).get("T_ENG_TABLE_NAME"))
					||	StringUtils.isEmpty(rsCol.getRecord(i).get("A_ENG_TABLE_NAME"))	)
				{
					continue;
				}
				
				if(l_MAP_ID.equals(rsCol.getRecord(i).get("MAP_ID")))
				{
					continue;
				}
				
				if( 	!l_T_ENG_TABLE_NAME.equals(rsCol.getRecord(i).get("T_ENG_TABLE_NAME"))
					||	!l_A_ENG_TABLE_NAME.equals(rsCol.getRecord(i).get("A_ENG_TABLE_NAME"))	)
				{
					logger.debug( rsCol.getRecord(i).get("T_ENG_TABLE_NAME"));
					logger.debug(rsCol.getRecord(i).get("A_ENG_TABLE_NAME"));
					rsTbl.addRecord(j, rsCol.getRecord(i));
					j++;
				}
				
				l_T_ENG_TABLE_NAME = rsCol.getRecord(i).get("T_ENG_TABLE_NAME");
				l_A_ENG_TABLE_NAME = rsCol.getRecord(i).get("A_ENG_TABLE_NAME");
				l_MAP_ID = rsCol.getRecord(i).get("MAP_ID");
			}
			
			logger.debug(rsTbl);
			responseData.putRecordSet("rsTbl", rsTbl);
			responseData.putRecordSet("rsCol", rsCol);
			
			responseData.putField("rsTblCnt", rsTbl.getRecordCount());
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		rtnMsg = "��ȸ�Ϸ�Ǿ����ϴ�.";
		responseData.putField("rtnMsg", rtnMsg);
		responseData.setOkResultMessage("OK", new String[]{"��ȸ�Ϸ�Ǿ����ϴ�."});
		
		return responseData;	
	}
}
