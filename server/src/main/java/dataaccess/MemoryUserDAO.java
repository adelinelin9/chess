//package dataaccess;
//
//import records.UserData;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//public class MemoryUserDAO implements UserDAO {
//    ArrayList<UserData> users = new ArrayList<>();
//
//    public void clearUsers() {
//        users.clear();
//    }
//
//    public void addUser(UserData user) throws DataAccessException {
//        for (UserData youser : users) {
//            if (Objects.equals(user.getUsername(), youser.getUsername())) {
//                throw new DataAccessException("already taken");
//            }
//        }
//        users.add(user);
//    }
//
//    public UserData getUser(String username, String password) {
//        for (UserData user : users) {
//            if (Objects.equals(user.getUsername(), username) && Objects.equals(password, user.getPassword())) {
//                return user;
//            }
//        }
//        return null;
//    }
//}

package dataaccess;

import records.UserData;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryUserDAO implements UserDAO {
    final private ConcurrentHashMap<String, UserData> users = new ConcurrentHashMap<>();


    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData = users.get(username);

        if (userData == null) {
            throw new DataAccessException("User not found");
        }

        return userData;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        // Check if user already exists, if so throw exception
        try {
            getUser(user.username());

        } catch (DataAccessException e) {
            // User does not exist, add user
            users.put(user.username(), user);
            return;
        }

        throw new DataAccessException("User already exists");
    }

    @Override
    public void clear() {
        users.clear();
    }

    public int size() {
        return users.size();
    }
}