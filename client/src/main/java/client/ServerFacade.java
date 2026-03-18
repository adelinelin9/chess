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

    
}
