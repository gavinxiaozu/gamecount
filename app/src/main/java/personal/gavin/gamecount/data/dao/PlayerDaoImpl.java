/**
 * 
 */
package personal.gavin.gamecount.data.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import personal.gavin.gamecount.data.Person;
import personal.gavin.gamecount.data.Player;
import personal.gavin.gamecount.data.dao.base.MyBaseDaoImpl;
import personal.gavin.gamecount.data.dao.base.PlayerDao;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Gavin
 * @version  2013-3-14 下午1:26:51
 */
public class PlayerDaoImpl extends MyBaseDaoImpl<Player, Integer> implements PlayerDao {

	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public PlayerDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Player.class);
		// TODO Auto-generated constructor stub
	}
	
	private void preDelete(final Player player) throws SQLException {
		TransactionManager.callInTransaction (this.getConnectionSource(), new Callable<Void>(){

			@Override
			public Void call() throws Exception {
				// TODO Auto-generated method stub
				PlayerDaoImpl playerDao = PlayerDaoImpl.this;
				PersonDaoImpl personDao = getPersonDao();
				
				//Delete Person if it need
				QueryBuilder<Player, Integer> queB = playerDao.queryBuilder();
				queB.setWhere(queB.where().eq(Person.ID_FIELD_NAME, player.getPerson().getId()));
				if (queB.countOf() == 1){
					personDao.delete(player.getPerson());
				}
				return null;
			}
			
		});
	}

	@Override
	public int create(Player player) throws SQLException {
		// TODO Auto-generated method stub
		PersonDaoImpl personDao = getPersonDao();
		List<Person> persons = personDao.queryForEq("name", player.getPerson().getName());
		if(persons.size() == 1) {
			player.setPerson(persons.get(0));
		} else if(player.getPerson() != null){
			//Person should be a new one
			player.getPerson().setId(0);
		}
		return super.create(player);
	}

	@Override
	public int update(Player player) throws SQLException {
		// TODO Auto-generated method stub
		PersonDaoImpl personDao = getPersonDao();
		List<Person> persons = personDao.queryForEq("name", player.getPerson().getName());
		if(persons.size() == 1) {
			player.setPerson(persons.get(0));
		} else if(persons.size() == 0) {
			personDao.create(player.getPerson());
		}
		return super.update(player);
	}

	@Override
	public int delete(final Player player) throws SQLException {
		// TODO Auto-generated method stub
		preDelete(player);
		return super.delete(player);
	}

	@Override
	public int delete(Collection<Player> datas) throws SQLException {
		// TODO Auto-generated method stub
		for(Player p:datas){
			preDelete(p);
		}
		return super.delete(datas);
	}
	
}
