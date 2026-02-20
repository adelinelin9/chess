package dataaccess;
import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO {
    private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        auths.remove(authToken);
    }

    public void clear() {
        auths.clear();
    }
}
