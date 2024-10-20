//package dataaccess;
//
//import records.AuthData;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//public class MemoryAuthDAO implements AuthDAO {
//    ArrayList<AuthData> authTokens = new ArrayList<>();
//
//    public void clearAuths() {
//        authTokens.clear();
//    }
//
//    public void addAuth(AuthData authToken) {
//        authTokens.add(authToken);
//    }
//
//
//    public void deleteAuthorization(String authToken)  {
//        authTokens.removeIf(token -> Objects.equals(authToken, token.getAuthToken()));
//    }
//
//    public AuthData getAuth(String authToken) {
//        for (AuthData auth : authTokens) {
//            if (Objects.equals(auth.getAuthToken(), authToken)) {
//                return auth;
//            }
//        }
//        return null;
//    }
//
//    public boolean inAuths(String authToken) {
//        for (AuthData auth : authTokens) {
//            if (Objects.equals(auth.getAuthToken(), authToken)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//}

package dataaccess;

import records.AuthData;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private ConcurrentHashMap<String, String> auths = new ConcurrentHashMap<>();

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String username = auths.get(authToken);

        if (username == null) {
            throw new DataAccessException("Invalid authToken");
        }

        return new AuthData(username, authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.remove(authToken);
    }

    @Override
    public void clear() {
        auths.clear();
    }

    public int size() {
        return auths.size();
    }
}