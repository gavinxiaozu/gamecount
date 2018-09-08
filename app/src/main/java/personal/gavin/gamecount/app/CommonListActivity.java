/**
 * 
 */
package personal.gavin.gamecount.app;

import personal.gavin.gamecount.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * @author Gavin
 * @version  2013-3-13 下午8:01:45
 */
public abstract class CommonListActivity extends DatabaseActivity {
	
	protected ListFragment mListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_list_layout);
		
		mListFragment = (ListFragment) this.getFragmentManager().findFragmentById(R.id.list_fragment);
		
		ListView mListView = mListFragment.getListView();
		
		mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				onCreateListContextMenu(menu, v,menuInfo);
			}
			
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				// TODO Auto-generated method stub
				onListItemClick(adapterView, v, position, id);
			}
			
		});
	}	

	public ListFragment getListFragment() {
		return mListFragment;	
	}
	
	public abstract void onCreateListContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo);
	
	public abstract void onListItemClick(AdapterView<?> adapterView, View v, int position, long id);
	
	protected void showConfirmDialog(final String title, final Runnable callback) {
		new DialogFragment() {
			@Override  
		    public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(title);
				if(callback == null) {
					builder.setNeutralButton(R.string.comfirm, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
						
					});
				} else {
					builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							callback.run();
						}
					})
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
				}
				return builder.create();
			}
		}.show(getFragmentManager(), "Confirm");
	}
}
