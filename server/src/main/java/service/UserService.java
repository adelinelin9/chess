//package service;
//
//import records.*;
//import dataaccess.*;
//import requests.LoginRequest;
//import requests.RegisterRequest;
//import requests.LogoutRequest;
//import results.LoginResult;
//import results.LogoutResult;
//import results.RegisterResult;
//import java.util.Objects;
//
//public class UserService {
//
//    UserDAO userDAO;
//    GameDAO gameDAO;
//    AuthDAO authDAO;
//
//
//    public UserService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
//        this.userDAO = userDAO;
//        this.authDAO = authDAO;
//        this.gameDAO = gameDAO;
//    }
//
//    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
//        if (!Objects.equals(registerRequest.getUsername(), null) && !Objects.equals(registerRequest.getPassword(), null) && !Objects.equals(registerRequest.getEmail(), null)) {
//            UserData user = userDAO.getUser(registerRequest.getUsername(), registerRequest.getPassword());
//            if (user == null) {
//                UserData newUser = new UserData(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
//                userDAO.addUser(newUser);
//                AuthData authToken = new AuthData(registerRequest.getUsername());
//                authDAO.addAuth(authToken);
//                return new RegisterResult(newUser, authToken);
//            }
//            throw new DataAccessException("Error: already taken");
//        }
//        throw new DataAccessException("Error: bad request");
//    }
//
//
//    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
//        UserData user = userDAO.getUser(loginRequest.getUsername(), loginRequest.getPassword());
//        if (!Objects.equals(loginRequest.getUsername(), "") && !Objects.equals(loginRequest.getPassword(), "")) {
//            if (user != null && Objects.equals(user.getPassword(), loginRequest.getPassword())) {
//                AuthData authToken = new AuthData(loginRequest.getUsername());
//                authDAO.addAuth(authToken);
//                return new LoginResult(user, authToken);
//            }
//            throw new DataAccessException("Error: unauthorized");
//        }
//        throw new DataAccessException("Error: bad request");
//    }
//
//    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
//        if (!Objects.equals(logoutRequest.getAuth(), "")) {
//            if (authDAO.inAuths(logoutRequest.getAuth())) {
//                authDAO.deleteAuthorization(logoutRequest.getAuth());
//                return new LogoutResult();
//            }
//            throw new DataAccessException("Error: unauthorized");
//        }
//        throw new DataAccessException("Error: bad request");
//    }
//}

package service;

import records.AuthData;
import records.UserData;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import dataaccess.DataAccessException;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws AlreadyTakenException, BadRequestException {
        // Check if required fields are present
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("Missing required fields");
        }

        // If the user already exists, throw an AlreadyTakenException
        try {
            userDAO.createUser(userData);

            // Create the AuthData to insert into the AuthDAO
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(userData.username(), authToken);
            authDAO.createAuth(authData);

            return authData;

        } catch (DataAccessException e) {
            throw new AlreadyTakenException("User already exists");
        }
    };

    public AuthData login(String username, String password) throws UnauthorizedException, BadRequestException {
        // Get the user from the UserDAO, throw a BadRequestException if the user is not found
        try {
            UserData user = userDAO.getUser(username);

            // Check if the password is incorrect, throw an UnauthorizedException if it is not
            if (user.password().equals(password)) {

                // Create an AuthData and insert it into the AuthDAO
                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(username, authToken);
                authDAO.createAuth(authData);

                return authData;

            } else {
                throw new UnauthorizedException("Invalid password");
            }

        } catch (DataAccessException e) {
            throw new BadRequestException("User not found");
        }
    };

    public void logout(String authToken) throws UnauthorizedException {
        // Verify the authToken, throw UnauthorizedException if invalid
        try {
            authDAO.getAuth(authToken);

            authDAO.deleteAuth(authToken);

        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid authToken");
        }

    };

}