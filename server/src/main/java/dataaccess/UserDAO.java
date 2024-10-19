//package dataaccess;
//
//import model.UserData;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UserDAO {
//
//    //keep track of users
//    private Map<String, UserData> users = new HashMap<>();
//
//    public void clear() throws DataAccessException{
//        users.clear();
//    }
//
//    public void createUser(UserData user) throws DataAccessException{
//        if(users.containsKey(user.getUsername())) {
//            throw new DataAccessException("User already exists.");
//        }
//        users.put(user.getUsername(), user);
//    }
//
//    public UserData getUser(String username) throws DataAccessException{
//        if(!users.containsKey(username)) {
//            throw new DataAccessException("User not found.");
//        }
//        return users.get(username);
//    }
//}


package dataaccess;

import records.*;

public interface UserDAO {
    void clearUsers() throws DataAccessException;
    void addUser(UserData user) throws DataAccessException;
    UserData getUser(String username, String password) throws DataAccessException;

}