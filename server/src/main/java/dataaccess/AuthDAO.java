package dataaccess;


import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private Map<String, AuthData> auths = new HashMap<>();

    public void clear() throws DataAccessException{
        auths.clear();
    }

    public void createAuth(AuthData auth) throws DataAccessException {
        if(auths.containsKey(auth.getAuthToken())){
            throw new DataAccessException("Auth token already exists.");
        }
        auths.put(auth.getAuthToken(), auth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if(!auths.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found.");
        }
        return auths.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if(!auths.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found.");
        }
        auths.remove(authToken);
    }
}