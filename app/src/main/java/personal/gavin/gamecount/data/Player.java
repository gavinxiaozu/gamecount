/**
 * 
 */
package personal.gavin.gamecount.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Gavin
 * @version  2013-3-14 下午12:46:57
 */
@DatabaseTable(tableName = "players")
public class Player implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7933923552130849513L;

	public static final String ID_FIELD_NAME = "player_id";
	
	public static final String CREATED = "player_created";
	
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnName = Person.ID_FIELD_NAME, foreignAutoCreate = true, uniqueCombo = true)
	private Person person;
	
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnName = Game.ID_FIELD_NAME, uniqueCombo = true)
	private Game game;
	
	@DatabaseField
	private int color = 0xFF0066EA;

	Player() {
		// needed by ormlite
	}
	
	public Player(Person person, Game game){
		this.person = person;
		this.game = game;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Player: " + id + " " + person.getName() + "  " + game.getName();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	

	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
