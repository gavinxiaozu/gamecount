/**
 * 
 */
package personal.gavin.gamecount.widget;

import java.util.List;

import personal.gavin.gamecount.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Gavin
 * @version  2013-4-16 下午8:02:19
 */
public class NameAndNumberAdapter extends ArrayAdapter<NameAndNumber> {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mResource;
	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public NameAndNumberAdapter(Context context, int resource,List<NameAndNumber> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View result;
		TextView t1,t2;
		View color;
		if(convertView == null) {
			result = mInflater.inflate(mResource, parent, false);
		} else {
			result = convertView;
		}
		try {
			t1 = (TextView) result.findViewById(R.id.list_item_text_state);
			t2 = (TextView) result.findViewById(R.id.list_item_text_content);
			color = result.findViewById(R.id.list_itme_view_state);
		} catch (ClassCastException e){
			Log.e("NameAndNumberAdapter", "You resource ID not match 2 TextView");
			throw new IllegalStateException ("NameAndNumberAdapter , You resource ID not match 2 TextView", e);
		}
		
		NameAndNumber item = getItem(position);
		if (item != null){
			t1.setText(item.name);
			t2.setText(item.number);
			color.setBackgroundColor(item.color);
		}
		
		return result;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getView(position, convertView, parent);
	}
}
