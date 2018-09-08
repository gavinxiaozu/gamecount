/**
 * 
 */
package personal.gavin.gamecount.data;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Gavin
 * @version  2013-3-14 下午12:48:10
 */
@DatabaseTable(tableName = "persons")
public class Person implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3151560511165484405L;

	public static final String ID_FIELD_NAME = "person_id";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(unique = true)
	private String name;
	
	Person() {
		// needed by ormlite
	}
	
	public Person (String name){
		this.name = name;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Person: " + id + " " + name;
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
}
