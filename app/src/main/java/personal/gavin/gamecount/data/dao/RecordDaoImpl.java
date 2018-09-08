/**
 * 
 */
package personal.gavin.gamecount.data.dao;

import java.sql.SQLException;

import com.j256.ormlite.support.ConnectionSource;

import personal.gavin.gamecount.data.Record;
import personal.gavin.gamecount.data.dao.base.MyBaseDaoImpl;
import personal.gavin.gamecount.data.dao.base.RecordDao;

/**
 * @author Gavin
 * @version  2013-3-22 下午7:08:09
 */
public class RecordDaoImpl extends MyBaseDaoImpl<Record, Integer> implements RecordDao {

	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public RecordDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Record.class);
		// TODO Auto-generated constructor stub
	}

}
