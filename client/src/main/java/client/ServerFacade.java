package client;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFacade {

    private String serverUrl;
    private Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public static class AuthResult {
        public String username;
        public String authToken;
    }

    public static class GameEntry {
        public int gameID;
        public String gameName;
        public String whiteUsername;
        public String blackUsername;
    }

    public AuthResult register(String username, String password, String email) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("email", email);
        String response = makeRequest("POST", "/user", gson.toJson(body), null);
        return gson.fromJson(response, AuthResult.class);
    }

    public AuthResult login(String username, String password) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        String response = makeRequest("POST", "/session", gson.toJson(body), null);
        return gson.fromJson(response, AuthResult.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, authToken);
    }

    public List<GameEntry> listGames(String authToken) throws Exception {
        String response = makeRequest("GET", "/game", null, authToken);
        Map<String, Object> result = gson.fromJson(response, Map.class);
        List<Map<String, Object>> games = (List<Map<String, Object>>) result.get("games");
        List<GameEntry> gameList = new ArrayList<>();
        for (Map<String, Object> game : games) {
            GameEntry entry = new GameEntry();
            entry.gameID = (int) (double) game.get("gameID");
            entry.gameName = (String) game.get("gameName");
            entry.whiteUsername = (String) game.get("whiteUsername");
            entry.blackUsername = (String) game.get("blackUsername");
            gameList.add(entry);
        }
        return gameList;
    }

    public int createGame(String authToken, String gameName) throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("gameName", gameName);
        String response = makeRequest("POST", "/game", gson.toJson(body), authToken);
        Map<String, Object> result = gson.fromJson(response, Map.class);
        return (int) (double) result.get("gameID");
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("playerColor", playerColor);
        body.put("gameID", gameID);
        makeRequest("PUT", "/game", gson.toJson(body), authToken);
    }

    private String makeRequest(String method, String path, String body, String authToken) throws Exception {
        URL url = URI.create(serverUrl + path).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            conn.setRequestProperty("Authorization", authToken);
        }
        if (body != null) {
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.close();
        }
        int status = conn.getResponseCode();
        InputStream stream;
        if (status >= 400) {
            stream = conn.getErrorStream();
        } else {
            stream = conn.getInputStream();
        }
        String response = new String(stream.readAllBytes());
        stream.close();
        if (status >= 400) {
            Map<String, String> errorBody = gson.fromJson(response, Map.class);
            throw new Exception(errorBody.get("message"));
        }
        return response;
    }
}
