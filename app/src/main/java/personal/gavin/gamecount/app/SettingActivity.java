/**
 * 
 */
package personal.gavin.gamecount.app;

import personal.gavin.gamecount.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Gavin
 * @version  2013-3-19 下午10:12:35
 */
public abstract class SettingActivity extends DatabaseActivity {
	private DialogFragment mExitDialog = null;
	private final String SAVESTATE_EDITED = "savestate_edited";
	private boolean mEdited = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if(savedInstanceState != null){
			mEdited = savedInstanceState.getBoolean(SAVESTATE_EDITED, false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean(SAVESTATE_EDITED, mEdited);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				showQuitOrNotDialog();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_common_menu, menu);
        return true;
    }

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	showQuitOrNotDialog();
                return true;
                
            case R.id.menu_save:
            	if(onSave()) {
            		onExit();
            	}
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void showQuitOrNotDialog(){
		if(!mEdited){
			onExit(); //没有修改过直接退出
			return;
		}
		if (mExitDialog == null) {
			mExitDialog = new DialogFragment() {
				@Override  
			    public Dialog onCreateDialog(Bundle savedInstanceState) {
					return new AlertDialog.Builder(this.getActivity())
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.dialog_comfirm_discard)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								onExit();
							}
						})
						.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
							
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
		mExitDialog.show(getFragmentManager(), "QuitOrNot");
	}
	
	protected void onExit() {
		finish();
	}
	
	abstract protected boolean onSave();
	
	protected void setEdited() {
		mEdited = true;
	}
}
