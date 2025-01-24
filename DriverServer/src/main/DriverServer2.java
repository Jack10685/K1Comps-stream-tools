package main;

public class DriverServer2 {
	static Client client;
	
	public static void main(String[] args) {
		try {
			OBS.init(args[0]);
			Challonge.init();
			client = new Client();
			Thread thread = new Thread(client);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
