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

public class CodeInq extends AbstractRepository {

	static Logger logger = Logger.getLogger(SqlTranInq.class);
	private final String namespace = "mm.repository.mapper.CodeMapper";

	public IDataSet s001(IDataSet requestData) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		try {
			String statement = namespace + ".S001";
			
			IRecordSet rs = null;
			
			RecordSetResultHandler resultHandler = new RecordSetResultHandler();
			resultHandler.setRecordSetId("RS_CODELIST");
			
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
	
	public IDataSet codeInq(IDataSet requestData){
		logger.debug("###########  START #########");
		logger.debug(getClass().getName());
		
		logger.debug(requestData.getField("INPUT"));
				
		/*************************************************************
		 *  Declare Var
		 *************************************************************/
		IDataSet 	responseData 	= new DataSet();
		IDataSet 	dsCode 			= null;
		IRecordSet 	rsCode 			= null;
//		IRecordSet 	rsRtn 			= new RecordSet("RS_LIST", new String[]{"WORK_NAME", "T_CODE_NAME", "T_CODE_CD", "T_CMT", "T_REMARK", "A_KCODE_NAME", "A_ECODE_NAME", "A_CODE_CD", "A_CMT", "A_REMARK", "업무담당자"});
		
		try
		{
			/*입력값 체크*/
			if(requestData.getField("INPUT").isEmpty())
			{
				responseData.setOkResultMessage("ERROR", new String[]{"입력값을 확인하세요"});
				return null;
			}
			
			dsCode	=	s001(requestData);
			
			rsCode = dsCode.getRecordSet("RS_CODELIST");
			
			logger.debug(rsCode);
			
			if(rsCode.getRecordCount() == 0)
			{
				responseData.setOkResultMessage("ERROR", new String[]{"조회된 데이터가 없습니다."});
			}
			
			responseData.putField("rsCnt", rsCode.getRecordCount());
			responseData.putRecordSet("rsCode", rsCode);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		responseData.setOkResultMessage("OK", new String[]{"조회완료되었습니다."});
		
		return responseData;	
	}
	
}
