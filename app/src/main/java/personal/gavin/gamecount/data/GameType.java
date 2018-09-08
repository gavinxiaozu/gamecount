/**
 * 
 */
package personal.gavin.gamecount.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Gavin
 * @version  2013-3-22 下午4:57:57
 */
@DatabaseTable(tableName = "gametypes")
public class GameType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7023142932867711202L;

public static final String ID_FIELD_NAME = "gametype_id";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(unique = true)
	private String name;
	
	@DatabaseField(unique = true)
	private int type;
	
	GameType() {
		// needed by ormlite
	}
	
	public GameType (String name, int type){
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Type: " + id + " " + name + " " + type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
