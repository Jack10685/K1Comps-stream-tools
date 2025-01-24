package replaymaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.event.filters.SourceFilterEnableStateChangedEvent;

public class Connect implements ActionListener, Runnable {

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(ConnectScreen.pwenter.getPassword());
		Main.obs = OBSRemoteController.builder()
				.host(ConnectScreen.ipenter.getText())
				.port(4455)
				.password(new String(ConnectScreen.pwenter.getPassword()))
				.connectionTimeout(3)
				.lifecycle()
				.onReady(this)
				.and()
				.registerEventListener(SourceFilterEnableStateChangedEvent.class, (event) -> Main.filterStateChanged(event))
				.build();
		Main.obs.connect();
	}

	@Override
	public void run() {
		ClipScreen.run();
	}

}
