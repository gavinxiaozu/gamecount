/**
 * 
 */
package personal.gavin.gamecount;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
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
public class SettingPlayerStatusActivity extends SettingActivity implements DigitInputPanel.OnOkListener{

	protected ListFragment mListFragment;
	private DigitInputPanel mKeypad;
	
	private Game mGame;
	private Player mPlayer;
	private List<Record> mRecords;
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
	
	@SuppressWarnings("unchecked")
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
		
		Intent i = getIntent();
		
		try {
			mPlayer = getDb().getPlayerDao().queryForId(i.getIntExtra(Player.ID_FIELD_NAME, 0));
			mGame = mPlayer.getGame();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			showError(e);
			e.printStackTrace();
		}
		
		Object saved = null;
		if (savedInstanceState != null) {
			saved = savedInstanceState.getSerializable(SAVESTATE_RECORD);
		}
		
		if (saved == null) {
			//read original mGame records data
			mRecords = new ArrayList<Record>();
			Record[] rs = mGame.getRecords().toArray(new Record[0]);
			for (int i1=0;i1<rs.length;i1++){
				mRecords.add(rs[i1]);
			}			
		} else {
			//read saved records data
			mRecords = (List<Record>) saved;
		}
		Utils.sortRecordList(mRecords);
		
		init();
	}
	
	private void init(){
		setTitle(mPlayer.getPerson().getName() + " - " + mGame.getName());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putSerializable(SAVESTATE_RECORD, (Serializable) mRecords);
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
			setRoundScore(mSelectedPosition, mKeypad.getValue());
		}
		
		mSelectedView = v;
		mSelectedDrawable = v.getBackground();
		mSelectedPosition = position;
		
		int bgc = getResources().getColor(R.color.black_25);
		v.setBackgroundColor(bgc);
		mKeypad.show();
		
		NameAndNumber m = (NameAndNumber) adapterView.getAdapter().getItem(mSelectedPosition);
		mKeypad.setValue(Double.valueOf(m.number));
		
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
		List<NameAndNumber> mm = new ArrayList<NameAndNumber>();
		
		for (int i=0;i<mRecords.size();i++){
			Record r = mRecords.get(i);
			JSONObject scoreJSON = r.getScoreJSON();
			double pScore = 0d;
			try {
				if(scoreJSON != null) pScore = scoreJSON.getDouble(String.valueOf(mPlayer.getId()));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mm.add(new NameAndNumber(String.valueOf(i+1), Utils.doubleToString(pScore), r.getId()));
		}
		mListFragment.setListAdapter(new NameAndNumberAdapter(this, R.layout.common_list_item_layout, mm));
	}
	
	private void setRoundScore(int position, double value) {
		NameAndNumber m = (NameAndNumber) mListFragment.getListAdapter().getItem(position);
		if (m!= null){
			m.setNumber(Utils.doubleToString(value));
			((NameAndNumberAdapter)mListFragment.getListAdapter()).notifyDataSetChanged();
		}
		Record r = mRecords.get(position);
		JSONObject scoreJSON = r.getScoreJSON();
		try {
			scoreJSON.put(String.valueOf(mPlayer.getId()), value);
			r.setScoreJSON(scoreJSON);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		setEdited();
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.SettingActivity#onSave()
	 */
	@Override
	protected boolean onSave() {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			for (Record r:mRecords){
				getDb().getRecordDao().update(r);
			}
			result = true;
			setResult(RESULT_OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.widget.DigitInputPanel.OnOkListener#onOk(double)
	 */
	@Override
	public void onOk(double value) {
		// TODO Auto-generated method stub
		Log.i("OK", "OnOK!! --- " + value);
		setRoundScore(mSelectedPosition, value);
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
