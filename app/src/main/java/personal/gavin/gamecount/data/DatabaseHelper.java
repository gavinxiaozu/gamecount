/**
 * 
 */
package personal.gavin.gamecount.data;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import personal.gavin.gamecount.data.dao.GameDaoImpl;
import personal.gavin.gamecount.data.dao.GameTypeDaoImpl;
import personal.gavin.gamecount.data.dao.PersonDaoImpl;
import personal.gavin.gamecount.data.dao.PlayerDaoImpl;
import personal.gavin.gamecount.data.dao.RecordDaoImpl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author Gavin
 * @version  2013-3-13 下午9:51:03
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private final String TAG = getClass().getSimpleName();
	
	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "gameCount.db";
	
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;
	
	// we do this so there is only one helper
	private static DatabaseHelper helper = null;
	private static final AtomicInteger usageCounter = new AtomicInteger(0);
	
	//DAO
	private GameDaoImpl mGameDao;
	private PlayerDaoImpl mPlayerDao;
	private PersonDaoImpl mPersonDao;
	private RecordDaoImpl mRecordDao;
	private GameTypeDaoImpl mGameTypeDao;

	/**
	 * @param context
	 * @param databaseName
	 * @param factory
	 * @param databaseVersion
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Get the helper, possibly constructing it if necessary. For each call to this method, there should be 1 and only 1
	 * call to {@link #close()}.
	 */
	public static synchronized DatabaseHelper getHelper(Context context) {
		if (helper == null) {
			helper = new DatabaseHelper(context);
		}
		usageCounter.incrementAndGet();
		return helper;
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource)
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		// TODO Auto-generated method stub
		try {
			Log.i(TAG, "onCreate");
			TableUtils.createTable(connectionSource, Game.class);
			TableUtils.createTable(connectionSource, Player.class);
			TableUtils.createTable(connectionSource, Person.class);
			TableUtils.createTable(connectionSource, Record.class);
			TableUtils.createTable(connectionSource, GameType.class);
			initGameTypes();

			// here we try inserting data in the on-create as a test
			Log.i(TAG, "created Game Table");
		} catch (SQLException e) {
			Log.e(TAG, "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		// TODO Auto-generated method stub
		try {
			Log.i(TAG, "onUpgrade");
			TableUtils.dropTable(connectionSource, Game.class, true);
			TableUtils.dropTable(connectionSource, Player.class, true);
			TableUtils.dropTable(connectionSource, Person.class, true);
			TableUtils.dropTable(connectionSource, Record.class, true);
			TableUtils.dropTable(connectionSource, GameType.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Can't drop databases", e);
			throw new RuntimeException(e);
		}

	}
	
	private void initGameTypes() {
		
	}
	
	public void destoryAllData() {
		try {
			Log.i(TAG, "Destory");
			TableUtils.clearTable(connectionSource, Game.class);
			TableUtils.clearTable(connectionSource, Player.class);
			TableUtils.clearTable(connectionSource, Person.class);
			TableUtils.clearTable(connectionSource, Record.class);
			TableUtils.clearTable(connectionSource, GameType.class);
		} catch (SQLException e) {
			Log.e(TAG, "Can't clear databases", e);
			throw new RuntimeException(e);
		}
	}
	
	public GameDaoImpl getGameDao() throws SQLException {
		if(mGameDao == null) {
			mGameDao = new GameDaoImpl(getConnectionSource());
		}
		return mGameDao;
	}
	
	public PlayerDaoImpl getPlayerDao() throws SQLException {
		if(mPlayerDao == null) {
			mPlayerDao = new PlayerDaoImpl(getConnectionSource());
		}
		return mPlayerDao;
	}

	public PersonDaoImpl getPersonDao() throws SQLException {
		if(mPersonDao == null) {
			mPersonDao = new PersonDaoImpl(getConnectionSource());
		}
		return mPersonDao;
	}
	
	public RecordDaoImpl getRecordDao() throws SQLException {
		if(mRecordDao == null) {
			mRecordDao = new RecordDaoImpl(getConnectionSource());
		}
		return mRecordDao;
	}
	
	public GameTypeDaoImpl getGameTypeDao() throws SQLException {
		if(mGameTypeDao == null) {
			mGameTypeDao = new GameTypeDaoImpl(getConnectionSource());
		}
		return mGameTypeDao;
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		if (usageCounter.decrementAndGet() == 0) {
			super.close();
			mGameDao = null;
			mPlayerDao = null;
			mPersonDao = null;
			mRecordDao = null;
			mGameTypeDao = null;
			helper = null;
		}
	}

	
}
