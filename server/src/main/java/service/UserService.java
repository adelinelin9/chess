package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(String username, String password, String email) throws DataAccessException {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        userDAO.createUser(new UserData(username, hashed, email));
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return auth;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDAO.getUser(username);
        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("unauthorized");
        }
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        authDAO.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
