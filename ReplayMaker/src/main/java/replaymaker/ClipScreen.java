package replaymaker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.gson.JsonObject;

public class ClipScreen {
	public static JLabel state;
	public static int videonum = 0;
	public static JsonObject settingsrec;
	public static JsonObject settingssave;
	public static boolean waitingToStop = false;
	public static int record = 0;
	public static boolean recording = false;
	public static boolean started = false;
	
	public static void run() {
		System.out.println("test");
		JFrame frame = ConnectScreen.frame;
		
		frame.setVisible(false);
		frame.getContentPane().removeAll();
		frame.setSize(800,200);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 10;
		c.fill = GridBagConstraints.BOTH;
		
		JButton start1 = new JButton("Start First Battle Clip");
		start1.addActionListener(new StartB1Listener());
		frame.add(start1, c);
		
		
		c.gridx = 1;
		JButton end1 = new JButton("End First Battle Clip");
		end1.addActionListener(new EndB1Listener());
		frame.add(end1);
		
		c.gridx = 3;
		JButton start2 = new JButton("Start Second Battle Clip");
		start2.addActionListener(new StartB2Listener());
		frame.add(start2, c);
		
		
		c.gridx = 4;
		JButton end2 = new JButton("End Second Battle Clip");
		end2.addActionListener(new EndB2Listener());
		frame.add(end2, c);
		
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		state = new JLabel("Idle");
		frame.add(state, c);

		frame.setVisible(true);
		frame.repaint();
		
		Main.obs.setSourceFilterEnabled("Chase", "Source Record", false, 1000);
		settingsrec = new JsonObject();
		settingssave = new JsonObject();
		
	}
	
	private static class StartB1Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			started = true;
			if (!recording) {
				record = 1;
				state.setText("Sending record request for run 1...");
				recording = true;
				settingsrec.addProperty("filename_formatting", "battle"+videonum);
				Main.obs.setSourceFilterSettings("Chase", "Source Record", settingsrec, true, 100);
				Main.obs.setSourceFilterEnabled("Chase", "Source Record", true, 100);
			}
		}
	}
	
	private static class EndB1Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			waitingToStop = true;
			state.setText("Added first battle replay");
			settingssave.addProperty("local_file", "C:/K1Comps/video/battle"+videonum+".mp4");
			Main.obs.setSourceFilterEnabled("Chase", "Source Record", false, 100);
			Main.obs.setInputSettings("replay1", settingssave, true, 100);
			videonum++;
			
		}
	}
	
	private static class StartB2Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			started = true;
			if (!recording) {
				record = 2;
				state.setText("Sending record request for run 2...");
				recording = true;
				settingsrec.addProperty("filename_formatting", "battle"+videonum);
				Main.obs.setSourceFilterSettings("Chase", "Source Record", settingsrec, true, 100);
				Main.obs.setSourceFilterEnabled("Chase", "Source Record", true, 100);
			}
		}
	}
	
	private static class EndB2Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			waitingToStop = true;
			state.setText("Added second battle replay");
			settingssave.addProperty("local_file", "C:/K1Comps/video/battle"+videonum+".mp4");
			Main.obs.setSourceFilterEnabled("Chase", "Source Record", false, 100);
			Main.obs.setInputSettings("replay2", settingssave, true, 100);
			videonum++;
		}
	}
	
	
}
