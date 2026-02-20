package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO {
    private HashMap<String, UserData> users = new HashMap<>();

    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("already taken");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }
}
