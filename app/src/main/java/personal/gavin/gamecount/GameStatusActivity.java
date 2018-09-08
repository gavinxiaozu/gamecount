/**
 * 
 */
package personal.gavin.gamecount;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.FrameLayout;
import personal.gavin.gamecount.app.CommonListActivity;
import personal.gavin.gamecount.chart.ChartManager;
import personal.gavin.gamecount.chart.ChartManagerSaveObject;
import personal.gavin.gamecount.chart.ChartSerie;
import personal.gavin.gamecount.data.Game;
import personal.gavin.gamecount.data.Player;
import personal.gavin.gamecount.data.Record;
import personal.gavin.gamecount.data.RecordComparator;
import personal.gavin.gamecount.util.Utils;
import personal.gavin.gamecount.widget.NameAndNumber;
import personal.gavin.gamecount.widget.NameAndNumberAdapter;
import personal.gavin.gamecount.widget.NameAndNumberComparator;

/**
 * @author Gavin
 * @version  2013-3-27 下午11:01:12
 */
public class GameStatusActivity extends CommonListActivity {

	private final int mRequestCode =  (int) (Math.random() * 10000000);
	private final String SAVESTATE_GAME = "savestate_game";
	private final String SAVESTATE_CHART = "savestate_chart";
	
	private boolean mUpdated = true;
	private boolean mSaveChart = false;
	
	private Game mGame;
	
	private ChartManager mChartManager;
	private View mLastHeaderView;
	
	private ChartSerie[] mChartDataAllRound;
	private ChartSerie[] mChartDataSummery;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        int gameId = -1;
		if (savedInstanceState != null) {
			gameId = savedInstanceState.getInt(SAVESTATE_GAME);
			mChartManager = new ChartManager(this);
			mChartManager.setSaveObject((ChartManagerSaveObject) savedInstanceState.getSerializable(SAVESTATE_CHART));
			mSaveChart = true;
		}
		
		Intent i = getIntent();
		
		if (gameId == -1) gameId = i.getIntExtra(Game.ID_FIELD_NAME, -1);
		
		try {
			mGame = getDb().getGameDao().queryForId(gameId);
			setTitle(mGame.getName());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(SAVESTATE_GAME, mGame.getId());
		outState.putSerializable(SAVESTATE_CHART, mChartManager.getSaveObject());
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_player_list, menu);
        return true;
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem addRound = menu.findItem(R.id.menu_add_round);
		try {
			mGame.getPlayers().refreshCollection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			showError(e);
			e.printStackTrace();
		}
		addRound.setEnabled(mGame.getPlayers().size() != 0);
		return super.onPrepareOptionsMenu(menu);
	}

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {                
            case R.id.menu_add_round:
            	Intent round_intent = new Intent(this, SettingRoundAddorEditActivity.class);
            	round_intent.putExtra(Game.ID_FIELD_NAME, mGame.getId());
            	startActivityForResult(round_intent, mRequestCode);
            	return true;
            	
            case R.id.menu_add_player:
            	Intent player_intent = new Intent(this, SettingPlayerAddorEditActivity.class);
            	player_intent.putExtra(Game.ID_FIELD_NAME, mGame);
            	startActivityForResult(player_intent, mRequestCode);
            	return true;
            	
            case R.id.menu_settings:
            	startActivity(mChartManager.newActivityIntent(this, "Test"));
            	return true;
            	
            case android.R.id.home:
            	finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!mUpdated) return;
				
		try {
			refreshData();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
	}
    
    private void refreshData() throws JSONException,SQLException {
    	Log.i(TAG, "refreshData");
		Player[] players = mGame.getPlayers().toArray(new Player[0]);
		SparseArray<ArrayList<Double>> playerScoreList = new SparseArray<ArrayList<Double>>(); 
		for (Player p:players){
			playerScoreList.put(p.getId(), new ArrayList<Double>());
		}
		Record[] recordArray = mGame.getRecords().toArray(new Record[0]);
		ArrayList<Record> records = new ArrayList<Record>();		
		for (Record rArray:recordArray){
			records.add(rArray);
		}
		Utils.sortRecordList(records);  //*********************Must Sort BEFORE!!!!!!!!!
		
		Collection<Record> rmRecords = new ArrayList<Record>();
		for (Record r:records){
			boolean noCurrentPlayerInRound = true;
			JSONObject scoreJSON = r.getScoreJSON();
			SparseArray<Double> scores = new SparseArray<Double>();
			@SuppressWarnings("unchecked")
			Iterator<String> keys = scoreJSON.keys();
			while (keys.hasNext()){
				String key = keys.next();
				double thisRoundScore = scoreJSON.getDouble(key);
				int id = Integer.valueOf(key);
				scores.put(id, thisRoundScore);
				
				if(noCurrentPlayerInRound && playerScoreList.get(id,null) != null) noCurrentPlayerInRound = false;
			}
			
			if(noCurrentPlayerInRound) {
				rmRecords.add(r);
			} else {
				for (Player p:players){
					playerScoreList.get(p.getId()).add(
								scores.get(p.getId(), 0d)
							);
				}
			}
		}		
		getDb().getRecordDao().delete(rmRecords);    //*********************remove no use record
		
		List<NameAndNumber> mm = new ArrayList<NameAndNumber>();
		mChartDataAllRound = new ChartSerie[players.length];
		mChartDataSummery = new ChartSerie[players.length];
		int playerCount = 0;
		for (Player p:players){
			double totalScore = 0d;
			ArrayList<Double> al = playerScoreList.get(p.getId());
			double[] scores = new double[al.size()];
			double[] scoresSummery = new double[al.size()];
			for(int i=0;i<al.size();i++) {
				double dd = al.get(i);
				totalScore += dd;
				scores[i] = dd;
				if(i == 0){
					scoresSummery[i] = dd; 
				} else {
					scoresSummery[i] = scoresSummery[i-1] + dd;
				}
			}
			mChartDataAllRound[playerCount] = new ChartSerie(p.getPerson().getName(),p.getId(), p.getColor(), scores);
			mChartDataSummery[playerCount] = new ChartSerie(p.getPerson().getName(), p.getId(), p.getColor(), scoresSummery);
			playerCount++;
			mm.add(new NameAndNumber(p.getPerson().getName(), Utils.doubleToString(totalScore), p.getId(), p.getColor()));
		}
		Collections.sort(mm, new NameAndNumberComparator());
		
		//Draw chart
		boolean isNewChart;		
		if(mSaveChart) { //saved data no need to set
			isNewChart = true;
			mSaveChart = false;
		} else {
			isNewChart = mChartManager == null;
			if(isNewChart) mChartManager = new ChartManager(this);
			
			//set data for chart
			mChartManager.setSeries(mChartDataSummery);
		}
		
		mChartManager.setChartSettings(mGame.getName(), 
				getResources().getString(R.string.round), 
				getResources().getString(R.string.score),
				Color.LTGRAY, Color.LTGRAY);
		
		//if(isNewChart){
		View chart = mChartManager.inflence(this);
		chart.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.dimen_200_dip)));
		getListFragment().getListView().removeHeaderView(mLastHeaderView);
		mLastHeaderView = chart;
		getListFragment().getListView().addHeaderView(mLastHeaderView);
		//}
		
		getListFragment().setListAdapter(new NameAndNumberAdapter(this, R.layout.common_list_item_layout, mm));
		
		this.invalidateOptionsMenu();
		
		mUpdated = false;
    }
    
	private void delPlayer(int position){
    	NameAndNumber player = (NameAndNumber) getListFragment().getListView().getItemAtPosition(position);
    	try {
			getDb().getPlayerDao().deleteById(player.id);
//			NameAndNumberAdapter adapter = (NameAndNumberAdapter) getListFragment().getListAdapter();
//			adapter.remove(player);
//			adapter.notifyDataSetChanged();
			refreshData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
    }
    
	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.CommonListActivity#onCreateListContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateListContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.list_common_context_menu, menu);

	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo(); 
		NameAndNumber player = (NameAndNumber) getListFragment().getListView().getItemAtPosition(menuInfo.position);
		if (player != null) {
			switch (item.getItemId()) {                
		        case R.id.list_context_menu_edit:
		        	Log.d(TAG, "Edit: " + player);
		        	Intent intent = new Intent(this, SettingPlayerAddorEditActivity.class);
		        	intent.putExtra(Player.ID_FIELD_NAME, player.id);
	            	startActivityForResult(intent, mRequestCode);
		        	return true;
		        case R.id.list_context_menu_del:
		        	Log.d(TAG, "Delete: " + player);
		        	showConfirmDialog(String.format(getResources().getString(R.string.dialog_comfirm_delete), player.toString()), new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							delPlayer(menuInfo.position);
						}
		        		
		        	});
		        	return true;
		        case R.id.lit_context_menu_detail:
		        	Log.d(TAG, "Detail: " + player);
		        	Intent intent2 = new Intent(this, SettingPlayerStatusActivity.class);
		        	intent2.putExtra(Player.ID_FIELD_NAME, player.id);
		        	startActivityForResult(intent2, mRequestCode);
		        	return true;
		    }
		}
		return super.onContextItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see personal.gavin.gamecount.app.CommonListActivity#onListItemClick(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(AdapterView<?> adapterView, View v,
			int position, long id) {
		// TODO Auto-generated method stub
		NameAndNumber player = (NameAndNumber) adapterView.getItemAtPosition(position);
		Log.d(TAG, "Click: " + player);
		Intent intent = new Intent(this, SettingPlayerStatusActivity.class);
    	intent.putExtra(Player.ID_FIELD_NAME, player.id);
    	startActivityForResult(intent, mRequestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != mRequestCode) return;
		Log.i(TAG, "onResult: " + resultCode);
		switch (resultCode){
			case RESULT_OK:
				mUpdated = true;
				break;
			case RESULT_CANCELED:
			default:
				break;
		}
	}
}
