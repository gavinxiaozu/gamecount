/**
 * 
 */
package personal.gavin.gamecount.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Gavin
 * @version  2013-3-22 下午5:18:42
 */
@DatabaseTable(tableName = "records")
public class Record implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -869440800145096708L;

	public static final String ID_FIELD_NAME = "record_id";

	public static final String CREATED = "record_created";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnName = Game.ID_FIELD_NAME, uniqueCombo = true)
	private Game game;
	
	@DatabaseField(canBeNull = false, uniqueCombo = true)
	private double round;
	
	/**
	 *  JSON object {playerid:score}
	 */
	@DatabaseField
	private String score;
	
	Record() {
		// needed by ormlite
	}
	
	public Record(Game game, String score, double round){
		this.game = game;
		this.score = score;
		this.round = round;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Record: " + id + " " + game.getName() + "round:" + round + "score:" + score;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public double getRound() {
		return round;
	}

	public void setRound(double round) {
		this.round = round;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
	public JSONObject getScoreJSON() {
		try {
			return (JSONObject) (new JSONTokener(getScore())).nextValue();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void setScoreJSON(JSONObject score) {
		setScore(score.toString());
	}
}
