/**
 * 
 */
package personal.gavin.gamecount;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import personal.gavin.gamecount.app.SettingActivity;
import personal.gavin.gamecount.data.Game;

/**
 * @author Gavin
 * @version  2013-3-19 下午9:09:34
 */
public class SettingGameAddorEditActivity extends SettingActivity implements TextWatcher{
	
	private final String SAVESTATE_GAME = "savestate_game";
	private Game mGame;
	private boolean mEdit = false;
	
	private EditText mNameEditText = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Game savedGame = null;
		if (savedInstanceState != null) {
			savedGame = (Game) savedInstanceState.getSerializable(SAVESTATE_GAME);
		}
		
		Intent i = getIntent();
		
		if (savedGame == null) {
			try {
				mGame = getDb().getGameDao().queryForId(i.getIntExtra(Game.ID_FIELD_NAME, 0));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mGame = savedGame;
		}
		
		if(i.getIntExtra(Game.ID_FIELD_NAME, -1) != -1) {
			mEdit = true;
		} else {
			if(mGame == null) mGame = new Game("",null);
		}
		
		this.setContentView(R.layout.setting_game_add_edit);
		init();
	}
	
	private void init(){
		Log.w(TAG, "Edit ? -- " + mEdit);
		if(mEdit) {
			setTitle(R.string.title_edit_game);
		} else {
			setTitle(R.string.title_new_game);
		}
		mNameEditText = (EditText) findViewById(R.id.name_edit);
		
		//Fill data
		mNameEditText.setText(mGame.getName());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Set Changed listener
		mNameEditText.addTextChangedListener(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(SAVESTATE_GAME, mGame);
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.SettingActivity#onSave()
	 */
	@Override
	protected boolean onSave() {
		// TODO Auto-generated method stub
		// Get data
		CreateOrUpdateStatus result = null;
		try {
			result = getDb().getGameDao().createOrUpdate(mGame);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		if (result != null){
			Intent intent = new Intent();
			intent.putExtra(Game.CREATED, result.isCreated());
			setResult(RESULT_OK, intent);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		setEdited();
		mGame.setName(mNameEditText.getText().toString());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mNameEditText.removeTextChangedListener(this);
	}
}
