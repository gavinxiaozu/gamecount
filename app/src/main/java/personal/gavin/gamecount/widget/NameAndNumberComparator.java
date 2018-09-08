/**
 * 
 */
package personal.gavin.gamecount.widget;

import java.util.Comparator;

/**
 * @author Gavin
 * @version  2013-4-23 下午4:49:35
 */
public class NameAndNumberComparator implements Comparator<NameAndNumber> {

	private boolean mSec = true;
	/**
	 * 
	 */
	public NameAndNumberComparator() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param order is sec or asec
	 */
	public NameAndNumberComparator(boolean order) {
		// TODO Auto-generated constructor stub
		mSec = order;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(NameAndNumber lhs, NameAndNumber rhs) {
		// TODO Auto-generated method stub
		double l = Double.valueOf(lhs.number);
		double r = Double.valueOf(rhs.number);
		int result;
		if(l < r) result = -1;
		else if(l > r) result = 1;
		else result = 0;
		return mSec ? result : -result;
	}

}
