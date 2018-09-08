package personal.gavin.gamecount;

import java.sql.SQLException;
import java.util.List;
import personal.gavin.gamecount.app.CommonListActivity;
import personal.gavin.gamecount.data.Game;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

public class GameListActivity extends CommonListActivity {

	private final int mRequestCode =  (int) (Math.random() * 10000000);
	private boolean mUpdated = true;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game_list, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {                
            case R.id.menu_add_game:
            	Intent intent = new Intent(this, SettingGameAddorEditActivity.class);
            	startActivityForResult(intent, mRequestCode);
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!mUpdated) return;
		
		refreshData();
	}
    
    private void refreshData() {
    	Log.i(TAG, "refreshData");
    	List<Game> list = null;
		try {
			list = getDb().getGameDao().queryForAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showError(e);
		}
		
		getListFragment().setListAdapter(new ArrayAdapter<Game>(this, android.R.layout.simple_expandable_list_item_1, list));
		mUpdated = false;
    }
    
    @SuppressWarnings("unchecked")
	private void delGame(int position){
    	Game game = (Game) getListFragment().getListView().getItemAtPosition(position);
    	try {
			getDb().getGameDao().delete(game);
			ArrayAdapter<Game> adapter = (ArrayAdapter<Game>) getListFragment().getListAdapter();
			adapter.remove(game);
			adapter.notifyDataSetChanged();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Game game = (Game) getListFragment().getListView().getItemAtPosition(menuInfo.position);
		if (game != null) {
			switch (item.getItemId()) {                
		        case R.id.list_context_menu_edit:
		        	Log.d(TAG, "Edit: " + game.getName());
		        	Intent intent = new Intent(this, SettingGameAddorEditActivity.class);
		        	intent.putExtra(Game.ID_FIELD_NAME, game.getId());
	            	startActivityForResult(intent, mRequestCode);
		        	return true;
		        case R.id.list_context_menu_del:
		        	Log.d(TAG, "Delete: " + game.getName());
		        	showConfirmDialog(String.format(getResources().getString(R.string.dialog_comfirm_delete), game.toString()), new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							delGame(menuInfo.position);
						}
		        		
		        	});
		        	return true;
		        case R.id.lit_context_menu_detail:
		        	Log.d(TAG, "Detail: " + game.getName());
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
		Game game = (Game) getListFragment().getListAdapter().getItem(position);
		Intent intent = new Intent(this, GameStatusActivity.class);
    	intent.putExtra(Game.ID_FIELD_NAME, game.getId());
    	startActivity(intent);
		Log.d(TAG, "Click: " + game.getName());
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
