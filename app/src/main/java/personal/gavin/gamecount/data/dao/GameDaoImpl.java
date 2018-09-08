/**
 * 
 */
package personal.gavin.gamecount.data.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import personal.gavin.gamecount.data.Game;
import personal.gavin.gamecount.data.Person;
import personal.gavin.gamecount.data.Player;
import personal.gavin.gamecount.data.Record;
import personal.gavin.gamecount.data.dao.base.GameDao;
import personal.gavin.gamecount.data.dao.base.MyBaseDaoImpl;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Gavin
 * @version  2013-3-14 下午1:26:51
 */
public class GameDaoImpl extends MyBaseDaoImpl<Game, Integer> implements GameDao {
	/**
	 * @param connectionSource
	 * @throws SQLException
	 */
	public GameDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Game.class);
		// TODO Auto-generated constructor stub
		
	}
	
	private void preDelete(final Game game) throws SQLException {
		TransactionManager.callInTransaction (this.getConnectionSource(), new Callable<Void>(){

			@Override
			public Void call() throws Exception {
				// TODO Auto-generated method stub
				PlayerDaoImpl playerDao = getPlayerDao();
				PersonDaoImpl personDao = getPersonDao();
				RecordDaoImpl recordDao = getRecordDao();
						
				//Delete assicate Record
				DeleteBuilder<Record, Integer> delB = recordDao.deleteBuilder();
				delB.setWhere(delB.where().eq(Game.ID_FIELD_NAME, game.getId()));
				delB.delete();
				
				//Delete assicate Player
				Player [] dPlayers = game.getPlayers().toArray(new Player[0]);
				DeleteBuilder<Player, Integer> delB2 = playerDao.deleteBuilder();
				delB2.setWhere(delB2.where().eq(Game.ID_FIELD_NAME, game.getId()));
				delB2.delete();
				
				//Delete Person if it need
				QueryBuilder<Player, Integer> queB = playerDao.queryBuilder();
				Collection<Person> dPersons = new ArrayList<Person>();
				for (Player player : dPlayers) {
					queB.setWhere(queB.where().eq(Person.ID_FIELD_NAME, player.getPerson().getId()));
					if (queB.countOf() == 0){
						dPersons.add(player.getPerson());
					}
					//queB.clear();
				}
				personDao.delete(dPersons);
				return null;
			}
		});
	}
	
	@Override
	public int delete(final Game game) throws SQLException {
		// TODO Auto-generated method stub
		preDelete(game);
		return super.delete(game);
	}

	@Override
	public int delete(Collection<Game> datas) throws SQLException {
		// TODO Auto-generated method stub
		for (Game g:datas){
			preDelete(g);
		}
		return super.delete(datas);
	}	
}
