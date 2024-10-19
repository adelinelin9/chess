//package service;
//
//import dataaccess.UserDAO;
//import dataaccess.AuthDAO;
//import dataaccess.DataAccessException;
//
//import model.AuthData;
//import model.UserData;
//
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.UUID;
//
//public class UserService {
//    private UserDAO userDAO;
//    private AuthDAO authDAO;
//
//    public UserService(UserDAO userDAO, AuthDAO authDAO) {
//        this.userDAO = userDAO;
//        this.authDAO = authDAO;
//    }
//
//    public AuthData register(UserData user) throws DataAccessException {
//        try {
//            userDAO.getUser(user.getUsername());
//            throw new DataAccessException("User already exists.");
//        } catch (DataAccessException e) {
//            // Hash password using SHA-256
//            user.setPassword(hashPassword(user.getPassword()));
//            userDAO.createUser(user);
//            AuthData auth = new AuthData(generateAuthToken(), user.getUsername());
//            authDAO.createAuth(auth);
//            return auth;
//        }
//    }
//
//    public AuthData login(UserData user) throws DataAccessException {
//        // Retrieve user from database
//        UserData existingUser = userDAO.getUser(user.getUsername());
//        if (existingUser == null || !hashPassword(user.getPassword()).equals(existingUser.getPassword())) {
//            throw new DataAccessException("Invalid credentials.");
//        }
//        AuthData auth = new AuthData(generateAuthToken(), user.getUsername());
//        authDAO.createAuth(auth);
//        return auth;
//    }
//
//    public void logout(String authToken) throws DataAccessException {
//        AuthData auth = authDAO.getAuth(authToken);
//        if (auth == null) {
//            throw new DataAccessException("Auth token not found");
//        }
//        authDAO.deleteAuth(authToken);
//    }
//
//    private String generateAuthToken() {
//        return UUID.randomUUID().toString();
//    }
//
//    // Hash the password using SHA-256
//    private String hashPassword(String password) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(password.getBytes());
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Error hashing password", e);
//        }
//    }
//}


package service;

import records.*;
import dataaccess.*;
import requests.LoginRequest;
import requests.RegisterRequest;
import requests.LogoutRequest;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;
import java.util.Objects;

public class UserService {

    UserDAO userDAO;
    GameDAO gameDAO;
    AuthDAO authDAO;


    public UserService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (!Objects.equals(registerRequest.getUsername(), null) && !Objects.equals(registerRequest.getPassword(), null) && !Objects.equals(registerRequest.getEmail(), null)) {
            UserData user = userDAO.getUser(registerRequest.getUsername(), registerRequest.getPassword());
            if (user == null) {
                UserData newUser = new UserData(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
                userDAO.addUser(newUser);
                AuthData authToken = new AuthData(registerRequest.getUsername());
                authDAO.addAuth(authToken);
                return new RegisterResult(newUser, authToken);
            }
            throw new DataAccessException("Error: already taken");
        }
        throw new DataAccessException("Error: bad request");
    }


    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData user = userDAO.getUser(loginRequest.getUsername(), loginRequest.getPassword());
        if (!Objects.equals(loginRequest.getUsername(), "") && !Objects.equals(loginRequest.getPassword(), "")) {
            if (user != null && Objects.equals(user.getPassword(), loginRequest.getPassword())) {
                AuthData authToken = new AuthData(loginRequest.getUsername());
                authDAO.addAuth(authToken);
                return new LoginResult(user, authToken);
            }
            throw new DataAccessException("Error: unauthorized");
        }
        throw new DataAccessException("Error: bad request");
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        if (!Objects.equals(logoutRequest.getAuth(), "")) {
            if (authDAO.inAuths(logoutRequest.getAuth())) {
                authDAO.deleteAuthorization(logoutRequest.getAuth());
                return new LogoutResult();
            }
            throw new DataAccessException("Error: unauthorized");
        }
        throw new DataAccessException("Error: bad request");
    }
}