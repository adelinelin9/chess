package server;

import dataaccess.DataAccessException;
import service.UserService;
import model.UserData;
import model.AuthData;
import utils.JSONUtil;

import spark.Request;
import spark.Response;
import spark.Route;


public class UserHandler {
    private UserService userService;
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Route register = (Request req, Response res) -> {
        try {
            UserData user = JSONUtil.fromJson(req.body(), UserData.class);
            validateUserData(user);

            AuthData auth = userService.register(user);
            res.status(200);
            return JSONUtil.toJson(auth);
        } catch (InvalidUserDataException e) {
            res.status(400);
            return "{\"message\" : \"Error: bad request\"}";
        } catch (DataAccessException e) {
            res.status(403);
            return "{\"message\" : \"Error: already taken\"}";
        } catch (Exception e) {
            res.status(500);
            return "{\"message\" : \"Error: " + e.getMessage() + "\"}";
        }
    };





}
