package dataaccess;
import model.AuthData;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> auths = new HashMap<>();

    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auths.containsKey(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        auths.remove(authToken);
    }

    public void clear() throws DataAccessException {
        auths.clear();
    }
}
