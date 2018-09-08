/**
 * 
 */
package personal.gavin.gamecount.data.dao;

import java.sql.SQLException;

import personal.gavin.gamecount.data.Person;
import personal.gavin.gamecount.data.dao.base.MyBaseDaoImpl;
import personal.gavin.gamecount.data.dao.base.PersonDao;

import com.j256.ormlite.support.ConnectionSource;

/**
 * @author Gavin
 * @version  2013-3-22 下午7:03:25
 */
public class PersonDaoImpl extends MyBaseDaoImpl<Person, Integer> implements PersonDao {

	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public PersonDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Person.class);
		// TODO Auto-generated constructor stub
	}

}
