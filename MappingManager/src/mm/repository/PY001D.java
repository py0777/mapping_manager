package mm.repository;

import mm.common.RecordSetResultHandler;
import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

public class PY001D extends AbstractRepository {

	static Logger logger = Logger.getLogger(PY001D.class);
	private final String namespace = "mm.repository.mapper.SqlTranInqMapper";
	
	
	public IDataSet s001(IDataSet requestData) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		IDataSet ds = new DataSet();
		try {
			String statement = namespace + ".S001";
			
			RecordSetResultHandler resultHandler = new RecordSetResultHandler();
			resultHandler.setRecordSetId("RS_TBLLIST");
			sqlSession.select(statement, requestData.getFieldMap(), resultHandler);
			
			ds.putRecordSet(resultHandler.getRecordSet());
			
			logger.debug(ds);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		} finally {
			sqlSession.close();
		}
		return ds;
	}
	
	public IDataSet s002(IDataSet requestData) {
		SqlSession sqlSession = getSqlSessionFactory().openSession();
		IDataSet ds = new DataSet();
		try {
			
			String statement = namespace + ".S002";
			
			RecordSetResultHandler resultHandler = new RecordSetResultHandler();
			resultHandler.setRecordSetId("RS_COLLIST");
			sqlSession.select(statement, requestData.getFieldMap(), resultHandler);
			ds.putRecordSet(resultHandler.getRecordSet());			
			logger.debug(ds);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		} finally {
			sqlSession.close();
		}
		
		return ds;
		
	}
}
