package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

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

    public AuthData register(UserData user) throws DataAccessException {
        try {
            userDAO.getUser(user.getUsername());
            throw new DataAccessException("User already exists.");
        } catch (DataAccessException e) {
            // hash password here
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            userDAO.createUser(user);
            AuthData auth = new AuthData(generateAuthToken(), user.getUsername());
            authDAO.createAuth(auth);
            return auth;
        }
    }

    public AuthData login(UserData user) throws DataAccessException {
        //retrieve user from database here
        UserData existingUser = userDAO.getUser(user.getUsername());
        if (existingUser == null || !BCrypt.checkpw(user.getPassword(), existingUser.getPassword())) {
            //if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            throw new DataAccessException("Invalid credentials.");
        }
        AuthData auth = new AuthData(generateAuthToken(), user.getUsername());
        authDAO.createAuth(auth);
        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if(auth == null) {
            throw new DataAccessException("Auth token not found");
        }
        authDAO.deleteAuth(authToken);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}