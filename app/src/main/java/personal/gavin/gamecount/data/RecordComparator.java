/**
 * 
 */
package personal.gavin.gamecount.data;

import java.util.Comparator;

/**
 * @author Gavin
 * @version  2013-4-23 下午2:45:06
 */
public class RecordComparator implements Comparator<Record> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Record lhs, Record rhs) {
		// TODO Auto-generated method stub
		if(lhs.getRound() < rhs.getRound()) return -1;
		else if (lhs.getRound() > rhs.getRound()) return 1;
		else return 0;
	}

}
