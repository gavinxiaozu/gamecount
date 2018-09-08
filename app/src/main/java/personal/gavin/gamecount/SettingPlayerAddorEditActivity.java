/**
 * 
 */
package personal.gavin.gamecount;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import personal.gavin.gamecount.app.SettingActivity;
import personal.gavin.gamecount.data.Game;
import personal.gavin.gamecount.data.Person;
import personal.gavin.gamecount.data.Player;
import personal.gavin.gamecount.widget.ColorPicker.OnColorChangedListener;
import personal.gavin.gamecount.widget.ColorPickerDialog;
import personal.gavin.gamecount.widget.NameAndNumber;
import personal.gavin.gamecount.widget.NameAndNumberAdapter;

/**
 * @author Gavin
 * @version  2013-3-28 下午8:44:50
 */
public class SettingPlayerAddorEditActivity extends SettingActivity implements TextWatcher{

	private final String SAVESTATE_PLAYER = "savestate_player";
	private Player mPlayer;
	private boolean mEdit = false;
	
	private EditText mNameEditText = null;
	private Spinner mColorSelector = null; 
	private ImageView mColorButton = null; 
	private View mColorSample = null;
	private ColorPickerDialog mColorPickerDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Player savedPlayer = null;
		if (savedInstanceState != null) {
			savedPlayer = (Player) savedInstanceState.getSerializable(SAVESTATE_PLAYER);
		}
		
		Intent i = getIntent();
		
		if (savedPlayer == null) {
			try {
				mPlayer = getDb().getPlayerDao().queryForId(i.getIntExtra(Player.ID_FIELD_NAME, 0));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mPlayer = savedPlayer;
		}
		
		if(i.getIntExtra(Player.ID_FIELD_NAME, -1) != -1) {
			mEdit = true;
		} else {
			if(mPlayer == null) {
				//New Player, need Game
				Game game = (Game) i.getSerializableExtra(Game.ID_FIELD_NAME);
				mPlayer = new Player(new Person(""), game);
			}
		}
		
		this.setContentView(R.layout.setting_player_add_edit);
		init();
	}
	
	private void init(){
		Log.w(TAG, "Edit ? -- " + mEdit);
		if(mEdit) {
			setTitle(R.string.title_edit_player);
		} else {
			setTitle(R.string.title_new_player);
		}
		mNameEditText = (EditText) findViewById(R.id.name_edit);
		mColorSelector = (Spinner) findViewById(R.id.setting_player_type_spn);
		mColorButton= (ImageView) findViewById(R.id.setting_player_type_detail_image);
		mColorSample = findViewById(R.id.setting_player_type_color_sample);
		mColorPickerDialog = new ColorPickerDialog(mPlayer.getColor());
		
		//Fill data
		mNameEditText.setText(mPlayer.getPerson().getName());
		mColorSample.setBackgroundColor(mPlayer.getColor());
		
		//Get Color list
		int[] colorList = getResources().getIntArray(R.array.player_selectable_color_list);
		String[] colorNameList = getResources().getStringArray(R.array.player_selectable_color_name_list);
		
		List<NameAndNumber> mm = new ArrayList<NameAndNumber>();
		for (int i = 0; i < colorList.length; i++) {
			mm.add(new NameAndNumber(colorNameList[i], "", i, colorList[i]));
		}
		
		mColorSelector.setAdapter(new NameAndNumberAdapter(this, R.layout.common_list_item_layout, mm));
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Set Changed listener
		mNameEditText.addTextChangedListener(this);
		
		mColorPickerDialog.setOnColorChangedListener(new OnColorChangedListener(){

			@Override
			public void colorChanged(int color) {
				// TODO Auto-generated method stub
				mColorSample.setBackgroundColor(color);
				mPlayer.setColor(color);
				setEdited();
			}
			
		});
		
		mColorSelector.setOnItemSelectedListener(new OnItemSelectedListener(){
			private boolean mFirst = true;

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(mFirst) {
					mFirst = false;
					return;
				}
				NameAndNumber item = (NameAndNumber) arg0.getItemAtPosition(arg2);
				mColorSample.setBackgroundColor(item.color);
				mPlayer.setColor(item.color);
				setEdited();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		mColorButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mColorPickerDialog.setColor(mPlayer.getColor());
				mColorPickerDialog.show(getFragmentManager(), "ColorPick");
			}
			
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(SAVESTATE_PLAYER, mPlayer);
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.SettingActivity#onSave()
	 */
	@Override
	protected boolean onSave() {
		// TODO Auto-generated method stub
		CreateOrUpdateStatus result = null;
		try {
			result = getDb().getPlayerDao().createOrUpdate(mPlayer);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		if (result != null){
			Intent intent = new Intent();
			intent.putExtra(Player.CREATED, result.isCreated());
			setResult(RESULT_OK, intent);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		setEdited();
		mPlayer.getPerson().setName(mNameEditText.getText().toString());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mNameEditText.removeTextChangedListener(this);
		mColorButton.setOnClickListener(null);
		mColorSelector.setOnItemSelectedListener(null);
	}
}
