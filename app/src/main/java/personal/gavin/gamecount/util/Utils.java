/**
 * 
 */
package personal.gavin.gamecount.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import personal.gavin.gamecount.R;
import personal.gavin.gamecount.data.Record;
import personal.gavin.gamecount.data.RecordComparator;

/**
 * @author Gavin
 * @version  2013-3-19 下午9:38:45
 */
public class Utils {
	public static final int NOT_NUMBER_KEY = -1;
	public static int keyToNumber(int id) {
		switch (id) {
			case R.id.one:
				return 1;
			case R.id.two:
				return 2;
			case R.id.three:
				return 3;
			case R.id.four:
				return 4;
			case R.id.five:
				return 5;
			case R.id.six:
				return 6;
			case R.id.seven:
				return 7;
			case R.id.eight:
				return 8;
			case R.id.nine:
				return 9;
			case R.id.zero:
				return 0;
		}
		return NOT_NUMBER_KEY;
	}
	
	public static double newRoundNumber(Record[] rounds) {
		if(rounds.length == 0) return 1;
		double result = Double.MIN_VALUE;
		for (Record r:rounds){
			result = Math.max(result, r.getRound());
		}
		return (result == (int)result) ? (result + 1) : Math.ceil(result);
	}
	
	public static JSONObject scoreMapToJSON (Map<Integer, Double> scores) throws JSONException {
		JSONObject score = new JSONObject();
		Iterator<Entry<Integer, Double>> iter = scores.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Double> entry = (Map.Entry<Integer, Double>) iter.next();
			score.put(String.valueOf(entry.getKey()), (double) entry.getValue());
		}
		return score;
	}
	
	public static void sortRecordList(List<Record> records) {
		 Collections.sort(records, new RecordComparator());
	}
	
	public static String doubleToString(double value) {
		String s = String.valueOf(value);
		if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }  
        return s;  
	}
	
	public static String inputNumberString(String original, String add) {
		if(add.equals("0") || add.equals("1") || add.equals("2") || add.equals("3") || add.equals("4") || add.equals("5") || 
				add.equals("6") || add.equals("7") || add.equals("8") || add.equals("9") || add.equals(".")){
			if(original.equals("0")) { //Empty
				if (add.equals(".")) return original.concat(add);
				else return add;
			}
			if(original.contains(".") && add.equals(".")) {
				return original;
			}
			return original.concat(add);
		}
		return original;		
	}
}
