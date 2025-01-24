package replaymaker;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.event.filters.SourceFilterEnableStateChangedEvent;

public class Main {
	public static String ip;
	public static String password;
	public static OBSRemoteController obs;
	
	public static void main(String[] args) {
		ConnectScreen.run();
	}
	
	public static void filterStateChanged(SourceFilterEnableStateChangedEvent e) {
		if (e.getFilterName().equals("Source Record") && e.getSourceName().equals("Chase") && ClipScreen.started) {
			if (e.getFilterEnabled()) {
				if (ClipScreen.record == 1) {
					ClipScreen.state.setText("Recording run 1...");
				} else if (ClipScreen.record == 2) {
					ClipScreen.state.setText("Recording run 2...");
				}
			} else {
				if (ClipScreen.waitingToStop) {
					if (ClipScreen.record == 1) {
						ClipScreen.state.setText("Recording Saved for Run 1");
					} else if (ClipScreen.record == 2) {
						ClipScreen.state.setText("Recording Saved for Run 2");
					}
				} else {
					ClipScreen.state.setText("Emergency stop record issued by Host, recording not added to replay screen");
				}
				ClipScreen.waitingToStop = false;
				ClipScreen.recording = false;
			}
		}
	}
}
