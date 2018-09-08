/**
 * 
 */
package personal.gavin.gamecount;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import personal.gavin.gamecount.app.SettingActivity;
import personal.gavin.gamecount.data.Game;
import personal.gavin.gamecount.data.Person;
import personal.gavin.gamecount.data.Player;
import personal.gavin.gamecount.data.Record;
import personal.gavin.gamecount.util.Utils;
import personal.gavin.gamecount.widget.DigitInputPanel;
import personal.gavin.gamecount.widget.NameAndNumber;
import personal.gavin.gamecount.widget.NameAndNumberAdapter;

/**
 * @author Gavin
 * @version  2013-4-16 下午4:51:52
 */
public class SettingRoundAddorEditActivity extends SettingActivity implements DigitInputPanel.OnOkListener{

	private boolean mEdit = false;
	
	protected ListFragment mListFragment;
	private DigitInputPanel mKeypad;
	
	private Game mGame;
	private Record mRecord;
	private List<Player> mPlayerList;
	private Map<Integer, Double> mScores;
	private final String SAVESTATE_RECORD= "savestate_record";
	
	private View mSelectedView;
	private Drawable mSelectedDrawable;
	private int mSelectedPosition = -1;
	
	private static final int SELECT_ITEM_SCROLL = 0x000001;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what){
			case SELECT_ITEM_SCROLL:
				mListFragment.getListView().setSelection(msg.arg1);
				break;
			}
			super.handleMessage(msg);
		} 
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_round_add_edit);
		
		mListFragment = (ListFragment) this.getFragmentManager().findFragmentById(R.id.list_fragment);
		mKeypad = (DigitInputPanel) findViewById(R.id.keypad);
		mKeypad.setOnOkListener(this);
		
		ListView mListView = mListFragment.getListView();
		
		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
				// TODO Auto-generated method stub
				onListItemClick(adapterView, v, position, id);
			}
			
		});
		
		Record saved = null;
		if (savedInstanceState != null) {
			saved = (Record) savedInstanceState.getSerializable(SAVESTATE_RECORD);
		}
		
		Intent i = getIntent();
		
		if (saved == null) {
			try {
				mRecord = getDb().getRecordDao().queryForId(i.getIntExtra(Record.ID_FIELD_NAME, 0));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			mRecord = saved;
		}
		
		if(i.getIntExtra(Record.ID_FIELD_NAME, -1) != -1) {
			mEdit = true;
			mGame = mRecord.getGame();
		} else {
			if(mRecord == null) {
				//New Player, need Game
				try {
					mGame = getDb().getGameDao().queryForId(i.getIntExtra(Game.ID_FIELD_NAME, -1));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mRecord = new Record(mGame, "", Utils.newRoundNumber(mGame.getRecords().toArray(new Record[0])));
			} else {
				try {
					mGame = getDb().getGameDao().queryForId(mRecord.getGame().getId());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		init();
	}
	
	private void init(){
		Log.w(TAG, "Edit ? -- " + mEdit);
		if(mEdit) {
			setTitle(R.string.edit);
		} else {
			setTitle(R.string.new_s);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);		
		try {
			mRecord.setScoreJSON(Utils.scoreMapToJSON(mScores));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		outState.putSerializable(SAVESTATE_RECORD, mRecord);
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.CommonListActivity#onListItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	public void onListItemClick(AdapterView<?> adapterView, View v,
			int position, long id) {
		// TODO Auto-generated method stub
		if (mSelectedView != null) {
			mSelectedView.setBackgroundDrawable(mSelectedDrawable);
		}
		if(mSelectedPosition >=0 && mSelectedPosition < adapterView.getCount()
				&& mKeypad.getModified()) { //If modified and select another one, set the score of the previous one.
			setPlayerScore(mSelectedPosition, mKeypad.getValue());
		}
		mSelectedView = v;
		mSelectedDrawable = v.getBackground();
		mSelectedPosition = position;
		
		int bgc = getResources().getColor(R.color.black_25);
		v.setBackgroundColor(bgc);
		mKeypad.show();
		mKeypad.setValue(mScores.get(mPlayerList.get(mSelectedPosition).getId()));
		
		Message msg = mHandler.obtainMessage(SELECT_ITEM_SCROLL, position, 0);
		mHandler.sendMessage(msg);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshData();
	}
	
	@SuppressLint("UseSparseArrays")
	private void refreshData() {
		Player[] players = mGame.getPlayers().toArray(new Player[0]);
		mPlayerList = new ArrayList<Player>();
		List<NameAndNumber> mm = new ArrayList<NameAndNumber>();
		if (mScores == null) mScores = new HashMap<Integer, Double>();
		
		JSONObject scoreJSON = mRecord.getScoreJSON();
		for (Player p:players){
			mPlayerList.add(p);
			double pScore = 0d;
			try {
				if(scoreJSON != null) pScore = scoreJSON.getDouble(String.valueOf(p.getId()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mScores.put(p.getId(),pScore);
			mm.add(new NameAndNumber(p.getPerson().getName(), Utils.doubleToString(pScore),p.getId(), p.getColor()));
		}
		mListFragment.setListAdapter(new NameAndNumberAdapter(this, R.layout.common_list_item_layout, mm));
	}
	
	private void setPlayerScore(int position, double value) {
		NameAndNumber m = (NameAndNumber) mListFragment.getListAdapter().getItem(position);
		if (m!= null){
			m.setNumber(Utils.doubleToString(value));
			((NameAndNumberAdapter)mListFragment.getListAdapter()).notifyDataSetChanged();
		}
		Player p = mPlayerList.get(position);
		mScores.put(p.getId(), value);
		setEdited();
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.SettingActivity#onSave()
	 */
	@Override
	protected boolean onSave() {
		// TODO Auto-generated method stub
		CreateOrUpdateStatus result = null;
		JSONObject scores = null;
		try {
			scores = Utils.scoreMapToJSON(mScores);
			Log.i(TAG, scores.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		mRecord.setScoreJSON(scores);
		try {
			result = getDb().getRecordDao().createOrUpdate(mRecord);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		if (result != null){
			Intent intent = new Intent();
			intent.putExtra(Record.CREATED, result.isCreated());
			setResult(RESULT_OK, intent);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.widget.DigitInputPanel.OnOkListener#onOk(double)
	 */
	@Override
	public void onOk(double value) {
		// TODO Auto-generated method stub
		Log.i("OK", "OnOK!! --- " + value);
		setPlayerScore(mSelectedPosition, value);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if(mKeypad.getVisibility() != View.GONE) {
					mKeypad.dismiss();
					return true;
				}
		}
		return super.onKeyDown(keyCode, event);
	}
}
