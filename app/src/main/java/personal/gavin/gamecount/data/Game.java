/**
 * 
 */
package personal.gavin.gamecount.data;

import java.io.Serializable;

import personal.gavin.gamecount.data.dao.GameDaoImpl;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Gavin
 * @version  2013-3-13 下午10:00:23
 */

@DatabaseTable(tableName = "games", daoClass = GameDaoImpl.class)
public class Game implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6451104435268475453L;

	public static final String ID_FIELD_NAME = "game_id";
	
	public static final String CREATED = "game_created";

	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String name;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = GameType.ID_FIELD_NAME)
	private GameType type;
	
	@ForeignCollectionField
	private ForeignCollection<Player> players;
	
	@ForeignCollectionField
	private ForeignCollection<Record> records;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;		
	}

	public int getId() {
		return id;
	}

	public Game (String name ,GameType type) {
		this.name = name;
		this.type = type;
	}
	
	Game() {
		// needed by ormlite
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return id + " " + name;
	}

	public ForeignCollection<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ForeignCollection<Player> players) {
		this.players = players;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ForeignCollection<Record> getRecords() {
		return records;
	}

	public void setRecords(ForeignCollection<Record> records) {
		this.records = records;
	}
	
}
