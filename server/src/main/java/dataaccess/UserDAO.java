//package dataaccess;
//
//import records.*;
//
//public interface UserDAO {
//    void clearUsers() throws DataAccessException;
//    void addUser(UserData user) throws DataAccessException;
//    UserData getUser(String username, String password) throws DataAccessException;
//
//}

package dataaccess;

import records.UserData;

public interface UserDAO {

    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    void clear();



}