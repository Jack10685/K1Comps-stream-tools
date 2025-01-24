package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Client implements Runnable{
	BufferedWriter out;
	BufferedReader in;
	ServerSocket serv;
	Socket client;
	
	// called by Challonge.java
	public void sendNames() {
		try {
			out.write("driver1 "+Challonge.getDriverName(Challonge.driver1)+"\r\n");
			out.write("driver2 "+Challonge.getDriverName(Challonge.driver2)+"\r\n");
			out.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			serv = new ServerSocket(4555);
			
			System.out.println("Waiting for client...");
			client = serv.accept();
			
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			char[] pass = new char[6];
			for (int i = 0; i < 6; i++) {
				pass[i] = (char) ('0' + (char) (Math.random()*10));
			}
			System.out.println("Connection received from: "+client.getInetAddress());
			System.out.println("Security code: "+(new String(pass)));
			
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			
			String line = in.readLine();
			if (Arrays.equals(pass, line.toCharArray())) {
				out.write("ACCEPTED\r\n");
				out.flush();
				while (client.isConnected()) {
					line = in.readLine();
					System.out.println("Received from controller: "+line);
					if (line.equals("D1W")) {
						OBS.giveDriver1Win();
						Challonge.driver1Win();
					} else if (line.equals("D2W")) {
						OBS.giveDriver2Win();
						Challonge.driver2Win();
					} else if (line.equals("OMT")) {
						OBS.setOMT();
					} else if (line.equals("SFR")) {
						OBS.setFirstBattle();
					} else if (line.equals("SSR")) {
						OBS.setSecondBattle();
					} else if (line.equals("NB")) {
						Challonge.getNextMatch();
						OBS.setFirstBattle();
					} else if (line.equals("PB")) {
						Challonge.getPreviousMatch();
						OBS.setFirstBattle();
					} else if (line.startsWith("SB")) {
						Challonge.getMatchNumber(Integer.parseInt(line.split(" ")[1]));
					} else if (line.equals("REFRESH")) {
						Challonge.loadParticipants(true);
					}
				}
			} else {
				out.write("DENIED\r\n");
				out.flush();
				out.close();
				client.close();
				serv.close();
				run();
			}
		} catch(IOException e) {
			System.exit(0);
			e.printStackTrace();
		}
		
	}
}
