/**
 * 
 */
package personal.gavin.gamecount.app;

import personal.gavin.gamecount.R;
import personal.gavin.gamecount.data.DatabaseHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author Gavin
 * @version  2013-3-19 下午7:48:00
 */
public class DatabaseActivity extends Activity {
	
	protected final String TAG = getClass().getSimpleName();
	
	private DialogFragment mErrorDialog = null;

	//DB
	private DatabaseHelper mDb = null;
	
	public DatabaseHelper getDb() {
		if(mDb == null) {
			mDb = DatabaseHelper.getHelper(this);
		}
		return mDb;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mDb != null) {
			mDb.close();
			mDb = null;
		}
	}

	public void showError(final Exception e){
		if (mErrorDialog == null) {
			mErrorDialog = new DialogFragment() {
				@Override  
			    public Dialog onCreateDialog(Bundle savedInstanceState) {
					return new AlertDialog.Builder(this.getActivity())
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.dialog_comfirm_error)
						.setMessage(e.toString())
						.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						})
						.create();
				}
			};	
		}
		mErrorDialog.show(getFragmentManager(), "Error");
	}
}
