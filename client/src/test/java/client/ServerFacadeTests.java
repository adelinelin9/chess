package client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearData() throws Exception {
        facade.clear();
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        ServerFacade.AuthResult result = facade.register("alice", "password", "alice@email.com");
        assertNotNull(result);
        assertEquals("alice", result.username);
        assertNotNull(result.authToken);
    }

    @Test
    public void testRegisterFail() throws Exception {
        facade.register("alice", "password", "alice@email.com");
        try {
            facade.register("alice", "password", "alice@email.com");
            fail("Should have thrown an exception for duplicate user");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testLoginSuccess() throws Exception {
        facade.register("bob", "1234", "bob@email.com");
        ServerFacade.AuthResult result = facade.login("bob", "1234");
        assertNotNull(result);
        assertEquals("bob", result.username);
    }

    @Test
    public void testLoginFail() throws Exception {
        facade.register("bob", "1234", "bob@email.com");
        try {
            facade.login("bob", "wrongpassword");
            fail("Should have thrown an exception for wrong password");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        ServerFacade.AuthResult auth = facade.register("carol", "pass", "carol@email.com");
        facade.logout(auth.authToken);
        assertTrue(true);
    }

    @Test
    public void testLogoutFail() throws Exception {
        try {
            facade.logout("badtoken");
            fail("Should have thrown an exception for bad token");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        ServerFacade.AuthResult auth = facade.register("dave", "pass", "dave@email.com");
        int gameID = facade.createGame(auth.authToken, "mygame");
        assertTrue(gameID > 0);
    }

    @Test
    public void testCreateGameFail() throws Exception {
        try {
            facade.createGame("badtoken", "mygame");
            fail("Should have thrown an exception for bad token");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testListGamesSuccess() throws Exception {
        ServerFacade.AuthResult auth = facade.register("eve", "pass", "eve@email.com");
        facade.createGame(auth.authToken, "game1");
        facade.createGame(auth.authToken, "game2");
        List<ServerFacade.GameEntry> games = facade.listGames(auth.authToken);
        assertEquals(2, games.size());
    }

    @Test
    public void testListGamesFail() throws Exception {
        try {
            facade.listGames("badtoken");
            fail("Should have thrown an exception for bad token");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void testJoinGameSuccess() throws Exception {
        ServerFacade.AuthResult auth = facade.register("frank", "pass", "frank@email.com");
        int gameID = facade.createGame(auth.authToken, "testgame");
        facade.joinGame(auth.authToken, "WHITE", gameID);
        List<ServerFacade.GameEntry> games = facade.listGames(auth.authToken);
        assertEquals("frank", games.get(0).whiteUsername);
    }

    @Test
    public void testJoinGameFail() throws Exception {
        ServerFacade.AuthResult auth1 = facade.register("grace", "pass", "grace@email.com");
        ServerFacade.AuthResult auth2 = facade.register("henry", "pass", "henry@email.com");
        int gameID = facade.createGame(auth1.authToken, "testgame");
        facade.joinGame(auth1.authToken, "WHITE", gameID);
        try {
            facade.joinGame(auth2.authToken, "WHITE", gameID);
            fail("Should have thrown an exception for already taken");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }
}
