/**
 * 
 */
package personal.gavin.gamecount.data.dao.base;

import java.sql.SQLException;

import personal.gavin.gamecount.data.dao.GameDaoImpl;
import personal.gavin.gamecount.data.dao.GameTypeDaoImpl;
import personal.gavin.gamecount.data.dao.PersonDaoImpl;
import personal.gavin.gamecount.data.dao.PlayerDaoImpl;
import personal.gavin.gamecount.data.dao.RecordDaoImpl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Gavin
 * @version  2013-3-22 下午6:48:44
 */
public class MyBaseDaoImpl<T, ID> extends BaseDaoImpl<T, ID> {

	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	protected MyBaseDaoImpl(ConnectionSource connectionSource,
			Class<T> dataClass) throws SQLException {
		super(connectionSource, dataClass);
		// TODO Auto-generated constructor stub
	}
	
	private static GameDaoImpl mGameDao;
	private static PlayerDaoImpl mPlayerDao;
	private static PersonDaoImpl mPersonDao;
	private static RecordDaoImpl mRecordDao;
	private static GameTypeDaoImpl mGameTypeDao;

	protected GameDaoImpl getGameDao() throws SQLException {
		if(mGameDao == null) {
			mGameDao = new GameDaoImpl(getConnectionSource());
		}
		return mGameDao;
	}
	
	protected PlayerDaoImpl getPlayerDao() throws SQLException {
		if(mPlayerDao == null) {
			mPlayerDao = new PlayerDaoImpl(getConnectionSource());
		}
		return mPlayerDao;
	}
	
	protected PersonDaoImpl getPersonDao() throws SQLException {
		if(mPersonDao == null) {
			mPersonDao = new PersonDaoImpl(getConnectionSource());
		}
		return mPersonDao;
	}
	
	protected RecordDaoImpl getRecordDao() throws SQLException {
		if(mRecordDao == null) {
			mRecordDao = new RecordDaoImpl(getConnectionSource());
		}
		return mRecordDao;
	}
	
	protected GameTypeDaoImpl getGameTypeDao() throws SQLException {
		if(mGameTypeDao == null) {
			mGameTypeDao = new GameTypeDaoImpl(getConnectionSource());
		}
		return mGameTypeDao;
	}
}
