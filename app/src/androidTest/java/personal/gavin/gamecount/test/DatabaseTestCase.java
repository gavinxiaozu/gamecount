/**
 * 
 */
package personal.gavin.gamecount.test;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;
import org.junit.Ignore;

import personal.gavin.gamecount.data.DatabaseHelper;
import personal.gavin.gamecount.data.Game;
import personal.gavin.gamecount.data.Person;
import personal.gavin.gamecount.data.Player;
import personal.gavin.gamecount.data.Record;
import personal.gavin.gamecount.data.dao.GameDaoImpl;
import personal.gavin.gamecount.data.dao.PersonDaoImpl;
import personal.gavin.gamecount.data.dao.PlayerDaoImpl;
import personal.gavin.gamecount.data.dao.RecordDaoImpl;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

/**
 * @author Gavin
 * @version  2013-3-25 下午12:46:15
 */
public class DatabaseTestCase extends AndroidTestCase {

	DatabaseHelper mDb;
	
	private final String SP_PERSON = "Special Person";
	private final String SP_PERSON_2 = "Special Person 2";
	/**
	 * @param name
	 */
	public DatabaseTestCase(String name) {
		super();
		setName(name);
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		mDb = DatabaseHelper.getHelper(getContext());
		initData();
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		mDb.destoryAllData();
	}
	
	private int random() {
		return random(10000);
	}
	
	private int random(double range) {
		return (int) (Math.random() * range);
	}
	
	
	private void initData() throws Exception {
		mDb.destoryAllData();
		insertGames();
		insertPlayers();
		for (int i=0;i<10;i++) insertRounds();
	}
	
	private void insertGames() throws SQLException {
		GameDaoImpl gameDao = mDb.getGameDao();
		if (gameDao.countOf() == 0) {
			for (int i=0;i<10;i++){
				Game g = new Game("Game " + random(), null);
				gameDao.create(g);
			}
		}
		assertEquals(gameDao.countOf(),10);
	}
	
	private void insertPlayer(Game game) throws Exception {
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		PersonDaoImpl personDao = mDb.getPersonDao();
		Player data = new Player(new Person("Person " + random()), game);
		data.setColor(random());
		
		List<Person> ps = personDao.queryForEq("name", data.getPerson().getName());
		int personCount = ps.size();
		assertTrue(personCount == 0 || personCount == 1);
		int playerCount = (int) playerDao.countOf();
		SQLException hasE = null;
		try {
			playerDao.create(data);
		} catch (SQLException e){
			hasE = e;
		}
		
		ps = personDao.queryForEq("name", data.getPerson().getName());
		assertEquals(1, ps.size());
		if(hasE == null) {
			assertEquals(playerCount + 1, playerDao.countOf());
		} else {
			Iterator<Player> players = game.getPlayers().iterator();
			Player sp = null;
			while(players.hasNext()) {
				Player next = players.next();
				if(next.getPerson().getName().equals(data.getPerson().getName())){
					sp = next;
					Log.w("insertPlayer", "Meet same random in a Game~~ Let it go~~");
					break;
				}
			}
			assertNotNull(sp);
		}
	}
	
	private void insertPlayers() throws Exception {
		GameDaoImpl gameDao = mDb.getGameDao();
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		PersonDaoImpl personDao = mDb.getPersonDao();
		List<Game> games = gameDao.queryForAll();
		boolean firstSp = true;
		for (Game g:games){
			Log.e(getName(), g.toString());
			for (int i=0;i<10;i++){
				insertPlayer(g);
			}
			long counts = personDao.countOf();
			//Test same person
			Player data = new Player(new Person(SP_PERSON), g);
			playerDao.create(data);
						
			if(firstSp){
				assertEquals(personDao.countOf(), counts + 1);
				firstSp = false;
			} else {
				assertEquals(personDao.countOf(), counts);
			}
		}
	}
	
	private void insertRound(Game g) throws Exception {
		Player[] players = g.getPlayers().toArray(new Player[0]);
		Record[] records = g.getRecords().toArray(new Record[0]);
		
		double maxRound = 0;
		for (Record r:records){
			if(r.getRound() > maxRound) maxRound = r.getRound();
		}
		
		maxRound = (maxRound == (int)maxRound) ? (maxRound + 1) : Math.ceil(maxRound);
		
		JSONObject score = new JSONObject();
		for (Player p:players){
			score.put(String.valueOf(p.getId()), random());
		}
		Record newR = new Record(g, score.toString(), maxRound);
		g.getRecords().add(newR);
	}
	
	private void insertRounds() throws Exception {
		GameDaoImpl gameDao = mDb.getGameDao();
		RecordDaoImpl recordDao = mDb.getRecordDao();
		List<Game> games = gameDao.queryForAll();
		
		int originalRecord = 0;
		int addRecord = 0;
		for (Game g:games){
			Record[] records = g.getRecords().toArray(new Record[0]);
			
			originalRecord += records.length;
			
			insertRound(g);
			
			addRecord++;
		}
		
		List<Record> allRec = recordDao.queryForAll();
		for (Record r:allRec){
			Log.i("insertRound", r.toString());
		}
		assertEquals(allRec.size(), originalRecord + addRecord);
	}
	
	private void deleteRandomRound(Game game) throws SQLException {
		RecordDaoImpl recordDao = mDb.getRecordDao();
		Record[] records = game.getRecords().toArray(new Record[0]);
		Record delRec = records[random(records.length)];
		recordDao.delete(delRec);
		game.getRecords().refreshCollection();
		assertEquals(records.length - 1, game.getRecords().size());
	}
	
	private void deleteRandomPlayer(Game game) throws SQLException {
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		PersonDaoImpl personDao = mDb.getPersonDao();
		
		Player[] players = game.getPlayers().toArray(new Player[0]);
		Player delPlayer = players[random(players.length)];
		
		List<Player> ps = playerDao.queryForEq(Person.ID_FIELD_NAME, delPlayer.getPerson().getId());
		int personInOtherGameCount = ps.size();
		int allPersonCount = (int)personDao.countOf();
		
		playerDao.delete(delPlayer);
		game.getPlayers().refreshCollection();
		assertEquals(players.length - 1, game.getPlayers().size());
		
		if(personInOtherGameCount == 1) {
			Log.w("deleteRandomPlayer", "----Only one!");
			assertEquals(allPersonCount - 1, personDao.countOf());
		} else if (personInOtherGameCount > 1){
			Log.w("deleteRandomPlayer", "||||In Many!");
			assertEquals(allPersonCount, personDao.countOf());
		}
	}
	
	private void updatePlayerInSameName (Player source, Player target) throws Exception {
		assertEquals(source.getGame().getId(), target.getGame().getId());
		assertFalse(source.getPerson().getId() == target.getPerson().getId());
		assertFalse(source.getPerson().getName() == target.getPerson().getName());
		
		Exception expectException = null;
		String oName = target.getPerson().getName();
		int oId = target.getPerson().getId();
		target.getGame().getPlayers().refreshCollection();
		int oSize = target.getGame().getPlayers().size();
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		try {
			target.getPerson().setName(source.getPerson().getName());
			playerDao.update(target);
		} catch (Exception e) {
			Log.e("updatePlayerInSameName","updatePlayerInSameName !!");
			expectException = e;
		}
		
		target = playerDao.queryForId(target.getId());
		target.getGame().getPlayers().refreshCollection();
		assertTrue(expectException instanceof SQLException);
		assertEquals( oName, target.getPerson().getName());
		assertEquals( oId, target.getPerson().getId());
		assertEquals( oSize, target.getGame().getPlayers().size());
	}
	
	@SmallTest
	public void testDeleteAndInsert() throws Exception {		
		GameDaoImpl gameDao = mDb.getGameDao();
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		PersonDaoImpl personDao = mDb.getPersonDao();
		
		List<Game> games = gameDao.queryForAll();
		int gameIndex = (int) random(games.size());
		Game testGame = games.get(gameIndex);
		
		//4.1. insert a exist person in the same game
		Exception expectException = null;
		int oSize = testGame.getPlayers().size();
		try {
			Player wrong = new Player(new Person(SP_PERSON), testGame);
			playerDao.create(wrong);
		} catch (Exception e) {
			expectException = e;
			Log.e("testDeleteAndInsert", "insert a exist person in the same game");
		}
		testGame.getPlayers().refreshCollection();
		assertTrue(expectException instanceof SQLException);
		assertEquals(oSize, testGame.getPlayers().size());
		
		//4.2 delete player in many game
		Iterator<Player> players = testGame.getPlayers().iterator();
		Player sp = null;
		while(players.hasNext() && (sp == null ? true: !sp.getPerson().getName().equals(SP_PERSON))) {
			sp = players.next();
		}		
		List<Player> ps = playerDao.queryForEq(Person.ID_FIELD_NAME, sp.getPerson().getId());
		int personInOtherGameCount = ps.size();
		int allPersonCount = (int)personDao.countOf();
		
		playerDao.delete(sp);
		
		if(personInOtherGameCount == 1) {
			Log.w("deleteRandomPlayer", "----Only one! SP");
			assertEquals(allPersonCount - 1, personDao.countOf());
		} else if (personInOtherGameCount > 1){
			Log.w("deleteRandomPlayer", "||||In Many! SP");
			assertEquals(allPersonCount, personDao.countOf());
		}
		
		//4. delete a round 
		deleteRandomRound(testGame);
		//5. delete a player
		deleteRandomPlayer(testGame);
		//6. delete another round
		deleteRandomRound(testGame);
		//7. insert a round
		insertRound(testGame);
		//8. insert a player
		insertPlayer(testGame);
		//9. repeat
		for (int i=0;i<5;i++){
			//7. insert a round
			insertRound(testGame);
			//8. insert a player
			insertPlayer(testGame);
		}
		for (int i=0;i<5;i++){
			//7. insert a round
			insertRound(testGame);
		}
		for (int i=0;i<5;i++){
			//4. delete a round 
			deleteRandomRound(testGame);
			//5. delete a player
			deleteRandomPlayer(testGame);
			//6. delete another round
			deleteRandomRound(testGame);
		}
	}
	
	@MediumTest
	public void testUpdate() throws Exception {
		GameDaoImpl gameDao = mDb.getGameDao();
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		PersonDaoImpl personDao = mDb.getPersonDao();
		
		int gameIndex = (int) random(gameDao.countOf());
		List<Game> games = gameDao.queryForAll();
		Game testGame = games.get(gameIndex);
		Player[] players = testGame.getPlayers().toArray(new Player[0]);
		
		//13. update same player name in the same game
		Player spPlayer = null, sourcePlayer, targetPlayer;
		for (Player p: players){
			if(p.getPerson().getName().equals(SP_PERSON)) {
				spPlayer = p;
				break;
			}
		}
		do {
			sourcePlayer = players[random(players.length)];
		} while (sourcePlayer == spPlayer);
		
		do {
			targetPlayer = players[random(players.length)];
		} while (targetPlayer == spPlayer || targetPlayer == sourcePlayer);
		
		playerDao.delete(spPlayer);
		//13.1. update a person in another game SP_PERSON
		int personCount = (int) personDao.countOf();
		sourcePlayer.getPerson().setName(SP_PERSON);
		playerDao.update(sourcePlayer);
		sourcePlayer = playerDao.queryForId(sourcePlayer.getId());
		assertEquals(spPlayer.getPerson().getId(), sourcePlayer.getPerson().getId());
		assertEquals(personCount, personDao.countOf());
		
		//13.2. update a person who already in this game SP_PERSON (expect error)
		updatePlayerInSameName(sourcePlayer, targetPlayer);
		
		//11. update player name & color
		players = testGame.getPlayers().toArray(new Player[0]);
		personCount = (int) personDao.countOf();
		String[] oNames = new String[players.length];
		int[] oColor = new int[players.length];
		int i = 0;
		for (Player p:players){
			oNames[i] = p.getPerson().getName();
			oColor[i] = p.getColor();
			
			p.getPerson().setName(p.getPerson().getName() + "__Chnaged");
			p.setColor(p.getColor() + 1024);
			
			playerDao.update(p);
			i++;
		}
		// refresh
		players = testGame.getPlayers().toArray(new Player[0]);
		i = 0;
		for (Player p:players){
			assertEquals(oNames[i] + "__Chnaged", p.getPerson().getName());
			assertEquals(oColor[i] + 1024, p.getColor());
			i++;
		}
		assertEquals(personCount + players.length, personDao.countOf());
		
		//12. update game name
		oNames = new String[games.size()];
		i = 0;
		for (Game g:games){
			String oname = g.getName();
			oNames[i++] = oname;
			g.setName(oname + "__Chnaged");
			gameDao.update(g);
		}
		// refresh
		games = gameDao.queryForAll();
		i = 0;
		for (Game g:games){
			assertEquals(oNames[i++] + "__Chnaged", g.getName());
		}
		
	}
	
	@LargeTest
	public void testDeleteGame () throws Exception {
		GameDaoImpl gameDao = mDb.getGameDao();
		PlayerDaoImpl playerDao = mDb.getPlayerDao();
		RecordDaoImpl recordDao = mDb.getRecordDao();
		PersonDaoImpl personDao = mDb.getPersonDao();
		
		int gameIndex = (int) random(gameDao.countOf());
		List<Game> games = gameDao.queryForAll();
		Game testGame = games.get(gameIndex);
		
		assertTrue(testGame.getPlayers().size() > 0);
		playerDao.delete(testGame.getPlayers());
		testGame.getPlayers().refreshCollection();
		assertEquals(0, testGame.getPlayers().size());
		
		gameDao.delete(games);
		
		assertEquals(0, gameDao.countOf());
		assertEquals(0, playerDao.countOf());
		assertEquals(0, recordDao.countOf());
		assertEquals(0, personDao.countOf());
	}
	
	
	public void testDestoryAllData() throws Exception {
		mDb.destoryAllData();
	}

}
