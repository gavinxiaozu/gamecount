/**
 * 
 */
package personal.gavin.gamecount.data.dao;

import java.sql.SQLException;

import personal.gavin.gamecount.data.GameType;
import personal.gavin.gamecount.data.dao.base.GameTypeDao;
import personal.gavin.gamecount.data.dao.base.MyBaseDaoImpl;

import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Gavin
 * @version  2013-3-22 下午7:06:49
 */
public class GameTypeDaoImpl extends MyBaseDaoImpl<GameType, Integer> implements GameTypeDao {

	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public GameTypeDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, GameType.class);
		// TODO Auto-generated constructor stub
	}

}
