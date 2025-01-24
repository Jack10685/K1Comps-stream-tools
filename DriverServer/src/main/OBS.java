package main;

import io.obswebsocket.community.client.OBSRemoteController;

public class OBS {
	
	static OBSRemoteController OBS;
	static boolean ready;
	static Number driver1leadid, driver2leadid, driver1chaseid, driver2chaseid, leftwinid, rightwinid, omt1id, omt2id;
	static int battleNum;
	static int readystate;
	static final String OVERLAY = "Lead-Chase overlay";
	
	public static void init(String password) {
		readystate = 0;
		battleNum = 1;
		ready = false;
		OBS = OBSRemoteController.builder()
				.host("localhost")
				.port(4455)
				.password(password)
				.connectionTimeout(10)
				.lifecycle()
				.onReady(new ConnectRunner())
				.and()
				.build();
		OBS.connect();
	}
	
	public static void setFirstBattle() {
		battleNum = 1;
		OBS.setSceneItemEnabled(OVERLAY, driver1leadid, true, 0);
		OBS.setSceneItemEnabled(OVERLAY, driver2chaseid, true, 0);
		
		OBS.setSceneItemEnabled(OVERLAY, driver1chaseid, false, 0);
		OBS.setSceneItemEnabled(OVERLAY, driver2leadid, false, 0);
		clearResult();
	}
	
	public static void setSecondBattle() {
		battleNum = 2;
		OBS.setSceneItemEnabled(OVERLAY, driver1leadid, false, 0);
		OBS.setSceneItemEnabled(OVERLAY, driver2chaseid, false, 0);
		
		OBS.setSceneItemEnabled(OVERLAY, driver1chaseid, true, 0);
		OBS.setSceneItemEnabled(OVERLAY, driver2leadid, true, 0);
		clearResult();
	}
	
	public static void clearResult() {
		OBS.setSceneItemEnabled(OVERLAY, omt1id, false, 0);
		OBS.setSceneItemEnabled(OVERLAY, omt2id, false, 0);
		OBS.setSceneItemEnabled(OVERLAY, rightwinid, false, 0);
		OBS.setSceneItemEnabled(OVERLAY, leftwinid, false, 0);
	}
	
	public static void giveDriver2Win() {
		clearResult();
		if (battleNum == 1) {
			OBS.setSceneItemEnabled(OVERLAY, rightwinid, true, 0);
		} else {
			OBS.setSceneItemEnabled(OVERLAY, leftwinid, true, 0);
		}
	}
	
	
	public static void giveDriver1Win() {
		clearResult();
		if (battleNum == 1) {
			OBS.setSceneItemEnabled(OVERLAY, leftwinid, true, 0);
		} else {
			OBS.setSceneItemEnabled(OVERLAY, rightwinid, true, 0);
		}
	}
	
	
	public static void setOMT() {
		clearResult();
		OBS.setSceneItemEnabled(OVERLAY, omt1id, true, 0);
		OBS.setSceneItemEnabled(OVERLAY, omt2id, true, 0);
	}
	
	public static void init2() {
		if (readystate == 8) {
			setFirstBattle();
			clearResult();
		}
	}
	
	
	private static class ConnectRunner implements Runnable {

		@Override
		public void run() {
			ready = true;
			OBS.getSceneItemId("Lead-Chase overlay", "Driver1-Lead", 0, info -> {driver1leadid = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId("Lead-Chase overlay", "Driver2-Lead", 0, info -> {driver2leadid = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId("Lead-Chase overlay", "Driver1-Chase", 0, info -> {driver1chaseid = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId("Lead-Chase overlay", "Driver2-Chase", 0, info -> {driver2chaseid = info.getSceneItemId(); ++readystate; init2();});
			
			OBS.getSceneItemId("Lead-Chase overlay", "left-win", 0, info -> {leftwinid = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId("Lead-Chase overlay", "right-win", 0, info -> {rightwinid = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId("Lead-Chase overlay", "omt-1", 0, info -> {omt1id = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId("Lead-Chase overlay", "omt-2", 0, info -> {omt2id = info.getSceneItemId(); ++readystate; init2();});
			
			
		}
		
	}
}
