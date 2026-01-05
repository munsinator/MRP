package at.fh.controller;

import at.fh.dto.UserCredentials;
import at.fh.service.AuthService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Optional;

public class UserController extends BaseController implements HttpHandler {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        super(authService);
        this.userService = userService;
        this.authService = authService;
    }

    // ROUTING
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();

        try {
            if (path.equals("/api/users/register/") || path.equals("/api/users/register")) {
                if (method.equals("POST")) {
                    registerUser(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else if (path.equals("/api/users/login/") || path.equals("/api/users/login")) {
                if (method.equals("POST")) {
                    loginUser(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else {
                sendText(ex, 404, "Not found");
                return;
            }

        } catch (Exception e) {
            sendText(ex, 500, "Internal Server Error");
            return;
        }
    }

    public void registerUser(HttpExchange ex) throws IOException {
        UserCredentials req = readJson(ex, UserCredentials.class);
        userService.register(req);

        ex.sendResponseHeaders(201, -1);
        ex.close();
    }

    public void loginUser(HttpExchange ex) throws IOException {
        UserCredentials req = readJson(ex, UserCredentials.class);
        Optional<String> token = userService.login(req);

        if (token.isEmpty()) {
            sendText(ex, 401, "Invalid credentials");
            return;
        }

        ex.getResponseHeaders().set("X-Auth-Token", token.get());
        ex.sendResponseHeaders(200, -1);
        ex.close();
    }
}
