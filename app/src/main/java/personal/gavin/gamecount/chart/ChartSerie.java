/**
 * 
 */
package personal.gavin.gamecount.chart;

import personal.gavin.gamecount.widget.NameAndNumber;

/**
 * @author Gavin
 * @version  2013-4-25 上午12:41:40
 */
public class ChartSerie extends NameAndNumber {
	public double[] data;
	
	/**
	 * @param name
	 * @param number
	 */
	public ChartSerie(String name,int id, int color, double[] data) {
		super(name, "", id, color);
		// TODO Auto-generated constructor stub
		this.data = data;
	}
}
