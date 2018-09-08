/**
 * 
 */
package personal.gavin.gamecount.widget;

/**
 * @author Gavin
 * @version  2013-4-16 下午8:08:57
 */
public class NameAndNumber {
	public String name;
	public String number;
	public int id;
	public int color;
	
	/**
	 * @param name
	 * @param number
	 * @param id
	 * @param color
	 */
	public NameAndNumber(String name, String number, int id, int color) {
		super();
		this.name = name;
		this.number = number;
		this.id = id;
		this.color = color;
	}

	/**
	 * @param name
	 * @param number
	 */
	public NameAndNumber(String name, String number) {
		super();
		this.name = name;
		this.number = number;
	}
	
	public NameAndNumber(String name, String number, int id) {
		super();
		this.name = name;
		this.number = number;
		this.id = id;
	}

	public NameAndNumber setName(String name) {
		this.name = name;
		return this;
	}
	
	public NameAndNumber setNumber(String number) {
		this.number = number;
		return this;
	}

	public NameAndNumber setId(int id) {
		this.id = id;
		return this;
	}

	public NameAndNumber setColor(int color) {
		this.color = color;
		return this;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name + " " + number;
	}
}
