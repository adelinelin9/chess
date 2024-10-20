//package service;
//
//import dataaccess.*;
//
//import results.ClearResult;
//
//public class ClearService {
//
//    UserDAO userDAO;
//    GameDAO gameDAO;
//    AuthDAO authDAO;
//
//    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
//        this.userDAO = userDAO;
//        this.gameDAO = gameDAO;
//        this.authDAO = authDAO;
//    }
//
//    void clearUsers() throws DataAccessException {
//        userDAO.clearUsers();
//    }
//
//    void clearGames() throws DataAccessException {
//        gameDAO.clearGames();
//    }
//
//    void clearAuths() throws DataAccessException {
//        authDAO.clearAuths();
//    }
//
//    public ClearResult clearAll() throws DataAccessException {
//        clearGames();
//        clearUsers();
//        clearAuths();
//        return new ClearResult();
//    }
//}

package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

}