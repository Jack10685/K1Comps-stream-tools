package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BracketScreen {
	static JFrame frame;
	static JTextField benter;
	static JButton d1w, d2w;
	
	public static void open() {
		frame = new JFrame("Bracket Screen");
		frame.setSize(700,250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		
		ButtonListener bl = new ButtonListener();
		
		JPanel decs = new JPanel();
		
		Dimension decsize = new Dimension(200, 50);
		
		d1w = new JButton();
		d1w.setActionCommand("D1W");
		d1w.addActionListener(bl);
		d1w.setPreferredSize(decsize);
		decs.add(d1w);
		
		JButton omt = new JButton("OMT");
		omt.setActionCommand("OMT");
		omt.addActionListener(bl);
		omt.setPreferredSize(decsize);
		decs.add(omt);
		
		d2w = new JButton();
		d2w.setActionCommand("D2W");
		d2w.addActionListener(bl);
		d2w.setPreferredSize(decsize);
		decs.add(d2w);
		
		frame.add(decs);
		
		JPanel runs = new JPanel();
		
		JButton sfr = new JButton("Set First Run");
		sfr.setActionCommand("SFR");
		sfr.addActionListener(bl);
		runs.add(sfr);
		
		JButton ssr = new JButton("Set Second Run");
		ssr.setActionCommand("SSR");
		ssr.addActionListener(bl);
		runs.add(ssr);
		
		frame.add(runs);
		
		JPanel battles = new JPanel();
		
		JButton pb = new JButton("Previous Battle");
		pb.setActionCommand("PB");
		pb.addActionListener(bl);
		battles.add(pb);
		
		JButton nb = new JButton("Next Battle");
		nb.setActionCommand("NB");
		nb.addActionListener(bl);
		battles.add(nb);
		
		frame.add(battles);
		
		JPanel misc = new JPanel();
		
		benter = new JTextField();
		benter.setPreferredSize(new Dimension(100,25));
		misc.add(benter, "span 2");
		
		JButton enter = new JButton("Go To");
		enter.setActionCommand("SB");
		enter.addActionListener(bl);
		misc.add(enter);
		
		JButton refresh = new JButton("Refresh");
		refresh.setActionCommand("REFRESH");
		refresh.addActionListener(bl);
		misc.add(refresh);
		
		frame.add(misc);
		
		Thread listener = new Thread(new InListener());
		listener.start();
		
		try {
			Start.out.write("SB 1\r\n");
			Start.out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		frame.setVisible(true);
	}
	
	public static class InListener implements Runnable {

		@Override
		public void run() {
			try {
				String line;
				while (true) {
					line = Start.in.readLine();
					if (line.startsWith("driver1")) {
						d1w.setText(line.substring(8));
					} else if (line.startsWith("driver2")) {
						d2w.setText(line.substring(8));
					}
					frame.repaint();
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getActionCommand().equals("SB")) {
					if (isNumeric(benter.getText())) {
						Start.out.write("SB "+benter.getText()+"\r\n");
						Start.out.flush();
					}
				} else {
					Start.out.write(e.getActionCommand()+"\r\n");
					Start.out.flush();
				}
			} catch(IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
		public static boolean isNumeric(String str) {
			  ParsePosition pos = new ParsePosition(0);
			  NumberFormat.getInstance().parse(str, pos);
			  return str.length() == pos.getIndex();
			}
		
	}
}
