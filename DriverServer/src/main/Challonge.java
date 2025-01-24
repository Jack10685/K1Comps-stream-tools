package main;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class Challonge {
	static CloseableHttpClient http;
	static int tournamentid;
	static int battleNum;
	static HashMap<Integer, String> participants; // id, name
	static int currentMatchID, driver1, driver2;
	static boolean participantsLoaded;
	
	public static void init() {
		battleNum = 1;
		participantsLoaded = false;
		http = HttpClients.createDefault();
		ClassicHttpRequest req = ClassicRequestBuilder.get()
				.setUri("https://api.challonge.com/v1/tournaments.json")
				.addParameter("api_key", "api key here")
				.addParameter("state", "in_progress")
				.build();
		
		try {
			http.execute(req, (HttpClientResponseHandler<?>) res -> {
				JSONArray json = new JSONArray(new String(IOUtils.toByteArray(res.getEntity().getContent())));
				tournamentid = json.getJSONObject(json.length()-1).getJSONObject("tournament").getInt("id");
				
				System.out.println("Tournament loaded");
				
				loadParticipants(false);
				
				System.out.println("Tournament "+json.getJSONObject(json.length()-1).getJSONObject("tournament").getString("name")+" loaded.");
				return null;
			});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void loadParticipants(boolean reload) {
		participantsLoaded = false;
		participants = new HashMap<>();
		ClassicHttpRequest req = ClassicRequestBuilder.get()
				.setUri("https://api.challonge.com/v1/tournaments/"+tournamentid+"/participants.json")
				.addParameter("api_key", "k0MiZVR1yenalXSnPLeqtUX25LhcR6yDmcsxyiYp")
				.build();
		
		try {
			http.execute(req, (HttpClientResponseHandler<?>) res -> {
				JSONArray json = new JSONArray(new String(IOUtils.toByteArray(res.getEntity().getContent())));
				json.forEach((obj) -> {
					JSONObject jobj = (JSONObject) obj;
					JSONObject p = jobj.getJSONObject("participant");
					participants.put(p.getInt("id"), p.getString("name"));
				});
				
				participantsLoaded = true;
				
				if (reload) {
					System.out.println("Participants reloaded");
					getMatchNumber(battleNum);
				} else {
					System.out.println("Participants loaded");
				}
				
				return null;
			});
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void getMatchNumber(int match) {
		if (match >= 1 && match <= 31) {
			battleNum = match;
			ClassicHttpRequest req = ClassicRequestBuilder.get()
					.setUri("https://api.challonge.com/v1/tournaments/"+tournamentid+"/matches.json")
					.addParameter("api_key", "k0MiZVR1yenalXSnPLeqtUX25LhcR6yDmcsxyiYp")
					.build();
			
			try {
				http.execute(req, (HttpClientResponseHandler<?>) res -> {
					JSONArray json = new JSONArray(new String(IOUtils.toByteArray(res.getEntity().getContent())));
					currentMatchID = json.getJSONObject(battleNum-1).getJSONObject("match").getInt("id");
					
					if (participantsLoaded) {
						loadCurrentParticipants();
					}
						
					return null;
				});
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void getNextMatch() {
		getMatchNumber(battleNum+1);
	}

	public static void getPreviousMatch() {
		getMatchNumber(battleNum-1);
	}

	public static void loadCurrentParticipants() {
		ClassicHttpRequest req = ClassicRequestBuilder.get()
				.setUri("https://api.challonge.com/v1/tournaments/"+tournamentid+"/matches/"+currentMatchID+".json")
				.addParameter("api_key", "k0MiZVR1yenalXSnPLeqtUX25LhcR6yDmcsxyiYp")
				.build();
		
		try {
			http.execute(req, (HttpClientResponseHandler<?>) res -> {
				JSONObject json = new JSONObject(new String(IOUtils.toByteArray(res.getEntity().getContent())));
				driver1 = json.getJSONObject("match").getInt("player1_id");
				driver2 = json.getJSONObject("match").getInt("player2_id");
				
				System.out.println("Current match partitipants: "+driver1+", "+driver2);
				
				LocalStorage.updateNames();
				DriverServer2.client.sendNames();
				
				return null;
			});
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void driver1Win() {
		ClassicHttpRequest req = ClassicRequestBuilder.put()
				.setUri("https://api.challonge.com/v1/tournaments/"+tournamentid+"/matches/"+currentMatchID+".json")
				.addParameter("api_key", "k0MiZVR1yenalXSnPLeqtUX25LhcR6yDmcsxyiYp")
				.addParameter("match[winner_id]", Integer.toString(driver1))
				.addParameter("match[scores_csv]", "1-0")
				.build();
		
		try {
			http.execute(req, (HttpClientResponseHandler<?>) res -> {
				return null;
			});
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void driver2Win() {
		ClassicHttpRequest req = ClassicRequestBuilder.put()
				.setUri("https://api.challonge.com/v1/tournaments/"+tournamentid+"/matches/"+currentMatchID+".json")
				.addParameter("api_key", "k0MiZVR1yenalXSnPLeqtUX25LhcR6yDmcsxyiYp")
				.addParameter("match[winner_id]", Integer.toString(driver2))
				.addParameter("match[scores_csv]", "0-1")
				.build();
		
		try {
			http.execute(req, (HttpClientResponseHandler<?>) res -> {
				return null;
			});
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static String getDriverName(int id) {
		return participants.get(id);
	}
	
}
