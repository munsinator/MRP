package at.fh.controller;

import at.fh.service.AuthService;
import at.fh.service.MediaService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public UserController( UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        if ("POST".equals(method) && path.equals("/api/users/register")) {
            register(ex);
        }
        if ("POST".equals(method) && path.equals("/api/users/login")) {
            login(ex);
        }
    }

    private void register(HttpExchange ex) throws IOException {

    }

    private void login(HttpExchange ex) throws IOException {

    }

}
