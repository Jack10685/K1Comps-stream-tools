package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.event.filters.SourceFilterEnableStateChangedEvent;
import io.obswebsocket.community.client.message.event.inputs.InputActiveStateChangedEvent;
import io.obswebsocket.community.client.message.event.inputs.InputMuteStateChangedEvent;
import io.obswebsocket.community.client.message.event.scenes.CurrentProgramSceneChangedEvent;

public class Start {
	static JFrame frame;
	static OBSRemoteController OBS;
	static boolean ready = false;
	static Number[] drone1, drone2, drone3, chase;
	static int readystate = 0;
	// arrays of 3, 0 = OVERLAY, 1 = NO_OVERLAY, 2 = REPLAY
	static final String OVERLAY = "Lead-Chase overlay", NO_OVERLAY = "No overlay", REPLAY = "Replay", STARTING_SOON = "Starting Soon", BRB = "Be Right Back";
	static JTextArea status;
	static boolean stopRecordClickedOnce, endStreamClickedOnce;
	
	
	public static void main(String[] args) {
		status = new JTextArea("Loaded");
		stopRecordClickedOnce = false;
		status.setEditable(false);
		DefaultCaret caret = (DefaultCaret)status.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		showLogin();
	}
	
	public static void showLogin() {
		frame = new JFrame("Stream Manager");
		frame.setSize(900,400);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		JLabel label = new JLabel("Enter ZeroTier IP (leave blank if own computer):");
		frame.add(label);
		
		JTextField text1 = new JTextField();
		
		frame.add(text1);
		
		JLabel label2 = new JLabel("Enter OBS Password:");
		frame.add(label2);
		
		JTextField text2 = new JTextField();
		
		frame.add(text2);
		
		JButton submit = new JButton("Log in");
		
		frame.add(submit);
		
		ActionListener login = new LoginListener(text1, text2);
		
		submit.addActionListener(login);
		text1.addActionListener(login);
		text2.addActionListener(login);
		
		frame.setVisible(true);
	}
	
	public static void showMainScreen() {
		frame.setVisible(false);
		frame.getContentPane().removeAll();
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel cams = new JPanel();
		cams.setLayout(new BoxLayout(cams, BoxLayout.X_AXIS));
		
		ButtonListener bl = new ButtonListener();
		
		JButton droneCam1 = new JButton("Drone Cam 1");
		droneCam1.addActionListener(bl);
		droneCam1.setActionCommand("dc1");
		cams.add(droneCam1);
		
		cams.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton droneCam2 = new JButton("Drone Cam 2");
		droneCam2.addActionListener(bl);
		droneCam2.setActionCommand("dc2");
		
		cams.add(droneCam2);
		
		cams.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton droneCam3 = new JButton("Drone Cam 3");
		droneCam3.addActionListener(bl);
		droneCam3.setActionCommand("dc3");
		
		cams.add(droneCam3);
		
		cams.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton chaseCam = new JButton("Chase Cam");
		chaseCam.addActionListener(bl);
		chaseCam.setActionCommand("chase");
		
		cams.add(chaseCam);
		frame.add(cams);
		
		JPanel scenes = new JPanel();
		scenes.setLayout(new BoxLayout(scenes, BoxLayout.X_AXIS));
		
		JButton overlay = new JButton("Show Overlay");
		overlay.addActionListener(bl);
		overlay.setActionCommand("overlay");
		
		scenes.add(overlay);
		
		scenes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton noverlay = new JButton("Hide Overlay");
		noverlay.addActionListener(bl);
		noverlay.setActionCommand("noverlay");
		
		scenes.add(noverlay);
		
		scenes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton replay = new JButton("Show Replay");
		replay.addActionListener(bl);
		replay.setActionCommand("replay");
		
		scenes.add(replay);
		
		scenes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton brb = new JButton("Show \"Be Right Back\" screen");
		brb.addActionListener(bl);
		brb.setActionCommand("brb");
		
		scenes.add(brb);
		
		scenes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton ss = new JButton("Show \"Starting Soon\" screen");
		ss.addActionListener(bl);
		ss.setActionCommand("ss");
		
		scenes.add(ss);
		frame.add(scenes);
		
		JPanel mutes = new JPanel();
		mutes.setLayout(new BoxLayout(mutes, BoxLayout.X_AXIS));
		
		JButton mute = new JButton("Mute Judges");
		mute.addActionListener(bl);
		mute.setActionCommand("mutejudges");
		
		mutes.add(mute);
		
		mutes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton unmute = new JButton("Unmute Judges");
		unmute.addActionListener(bl);
		unmute.setActionCommand("unmutejudges");
		
		mutes.add(unmute);
		
		mutes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton mute2 = new JButton("Mute Cars");
		mute2.addActionListener(bl);
		mute2.setActionCommand("mutecars");
		
		mutes.add(mute2);
		
		mutes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton unmute2 = new JButton("Unmute Cars");
		unmute2.addActionListener(bl);
		unmute2.setActionCommand("unmutecars");
		
		mutes.add(unmute2);
		
		mutes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton stoprecord = new JButton("Emergency Stop Record");
		stoprecord.addActionListener(bl);
		stoprecord.setActionCommand("stoprecord");
		
		mutes.add(stoprecord);
		
		mutes.add(Box.createRigidArea(new Dimension(20,50)));
		
		JButton endstream = new JButton("Stop Stream");
		endstream.addActionListener(bl);
		endstream.setActionCommand("endstream");
		
		mutes.add(endstream);
		
		frame.add(mutes);
		
		JScrollPane statusholder = new JScrollPane(status);
		
		frame.add(statusholder);
		
		frame.setVisible(true);
	}
	
	public static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("dc1")) {
				setCamEnabled(drone1, true);
				setCamEnabled(drone2, false);
				setCamEnabled(drone3, false);
				//setCamEnabled(chase, false);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("dc2")) {
				setCamEnabled(drone2, true);
				setCamEnabled(drone1, false);
				setCamEnabled(drone3, false);
				//setCamEnabled(chase, false);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("dc3")) {
				setCamEnabled(drone3, true);
				setCamEnabled(drone1, false);
				setCamEnabled(drone2, false);
				//setCamEnabled(chase, false);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("chase")) {
				//setCamEnabled(chase, true);
				setCamEnabled(drone1, false);
				setCamEnabled(drone2, false);
				setCamEnabled(drone3, false);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("mutejudges")) {
				OBS.setInputMute("SonoBus", true, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("unmutejudges")) {
				OBS.setInputMute("SonoBus", false, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("mutecars")) {
				OBS.setInputMute("Chase", true, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("unmutecars")) {
				OBS.setInputMute("Chase", false, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("overlay")) {
				OBS.setCurrentProgramScene(OVERLAY, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("noverlay")) {
				OBS.setCurrentProgramScene(NO_OVERLAY, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("replay")) {
				OBS.setCurrentProgramScene(REPLAY, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("brb")) {
				OBS.setCurrentProgramScene(BRB, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("ss")) {
				OBS.setCurrentProgramScene(STARTING_SOON, 0);
				stopRecordClickedOnce = false;
				endStreamClickedOnce = false;
			}
			else if (e.getActionCommand().equals("stoprecord")) {
				endStreamClickedOnce = false;
				if (stopRecordClickedOnce) {
					OBS.setSourceFilterEnabled("Chase", "Source Record", false, 0);
					log("Emergency stop record issued");
					stopRecordClickedOnce = false;
				} else {
					log("Click Emergency Stop Recoring again to stop recording");
					stopRecordClickedOnce = true;
				}
				
			}
			else if (e.getActionCommand().equals("endstream")) {
				stopRecordClickedOnce = false;
				if (endStreamClickedOnce) {
					OBS.stopStream(0);
					log("Stream stopped");
					endStreamClickedOnce = false;
				} else {
					log("Click End Stream again to stop end the stream");
					endStreamClickedOnce = true;
				}
				
			}
		}
		
	}
	
	public static void setCamEnabled(Number[] cam, boolean enabled) {
		OBS.setSceneItemEnabled(OVERLAY, cam[0], enabled, 0);
		OBS.setSceneItemEnabled(NO_OVERLAY, cam[1], enabled, 0);
		OBS.setSceneItemEnabled(REPLAY, cam[2], enabled, 0);
	}
	
	public static class LoginListener implements ActionListener {
		JTextField text1, text2;
		
		public LoginListener(JTextField text1, JTextField text2) {
			this.text1 = text1;
			this.text2 = text2;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String hostname = "localhost";
			if (!text1.getText().equals("")) {
				hostname = text1.getText();
			}
			OBS = OBSRemoteController.builder()
					.host(hostname)
					.port(4455)
					.password(text2.getText())
					.connectionTimeout(3)
					.lifecycle()
					.onReady(new OnConnect())
					.onIdentified((x) -> log("Authenticated"))
					.onDisconnect(new OnDisconnect())
					.and()
					.registerEventListener(SourceFilterEnableStateChangedEvent.class, (x) -> filterChanged(x))
					.registerEventListener(InputMuteStateChangedEvent.class, (x) -> muteChanged(x))
					.registerEventListener(CurrentProgramSceneChangedEvent.class, (x) -> sceneChanged(x))
					.registerEventListener(InputActiveStateChangedEvent.class, (x) -> inputActiveChange(x))
					.build();
			log("Connecting");
			OBS.connect();
			
		}
		
	}
	
	public static class OnDisconnect implements Runnable {

		@Override
		public void run() {
			log("Disconnected");
		}
		
	}
	
	public static class OnConnect implements Runnable {

		@Override
		public void run() {
			log("Initializing...");
			ready = true;
			drone1 = new Number[3];
			drone2 = new Number[3];
			drone3 = new Number[3];
			chase = new Number[3];
			
			OBS.getSceneItemId(OVERLAY, "Drone1", 0, info -> {drone1[0] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(OVERLAY, "Drone2", 0, info -> {drone2[0] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(OVERLAY, "Drone3", 0, info -> {drone3[0] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(OVERLAY, "Chase", 0, info -> {chase[0] = info.getSceneItemId(); ++readystate; init2();});
			
			OBS.getSceneItemId(NO_OVERLAY, "Drone1", 0, info -> {drone1[1] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(NO_OVERLAY, "Drone2", 0, info -> {drone2[1] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(NO_OVERLAY, "Drone3", 0, info -> {drone3[1] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(NO_OVERLAY, "Chase", 0, info -> {chase[1] = info.getSceneItemId(); ++readystate; init2();});
			
			OBS.getSceneItemId(REPLAY, "Drone1-replay", 0, info -> {drone1[2] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(REPLAY, "Drone2-replay", 0, info -> {drone2[2] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(REPLAY, "Drone3-replay", 0, info -> {drone3[2] = info.getSceneItemId(); ++readystate; init2();});
			OBS.getSceneItemId(REPLAY, "Chase-replay", 0, info -> {chase[2] = info.getSceneItemId(); ++readystate; init2();});
		}
		
		public static void init2() {
			if (readystate == 12) {
				showMainScreen();
				log("Initialized");
			}
		}
		
	}

	
	public static void filterChanged(SourceFilterEnableStateChangedEvent x) {
		if (x.getFilterName().equals("Source Record")) {
			if (x.getFilterEnabled()) {
				log("Replay Recording Started");
			} else {
				log("Replay Recording Stopped");
			}
		}
	}

	public static void muteChanged(InputMuteStateChangedEvent x) {
		if (x.getInputName().equals("SonoBus")) {
			if (x.getInputMuted()) {
				log("Judges Muted");
			} else {
				log("Judges Unmuted");
			}
		}
	}
	
	public static void sceneChanged(CurrentProgramSceneChangedEvent x) {
		log("Scene switched to: "+x.getSceneName());
	}
	
	public static void inputActiveChange(InputActiveStateChangedEvent x) {
		if (x.getVideoActive()) {
			if (x.getInputName().equals("Driver1-Lead") || x.getInputName().equals("Driver2-Lead")) {
				log("Lead and Chase switched (Or scene was just changed)");
			} else if (x.getInputName().equals("omt-1")) {
				log("OMT displayed on overlay");
			} else if (x.getInputName().equals("left-win")) {
				log("Left win displayed on overlay");
			} else if (x.getInputName().equals("right-win")) {
				log("Right win displayed on overlay");
			} else if (x.getInputName().equals("Drone1")) {
				log("Drone 1 Cam displayed");
			} else if (x.getInputName().equals("Drone2")) {
				log("Drone 2 Cam displayed");
			} else if (x.getInputName().equals("Drone3")) {
				log("Drone 3 Cam displayed");
			} else if (x.getInputName().equals("Chase")) {
				log("Chase Cam displayed");
			}
		} else {
			if (x.getInputName().equals("omt-1") || x.getInputName().equals("left-win") || x.getInputName().equals("right-win")) {
				log("Battle result cleared");
			}
		}
	}
	
	public static void log(String line) {
		status.setText(status.getText()+"\r\n"+line);
	}
}
