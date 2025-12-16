package at.fh.controller;

import at.fh.service.AuthService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class BaseController {
    protected ObjectMapper mapper = new ObjectMapper();
    protected AuthService authService;

    protected BaseController(AuthService authService) {
        this.authService = authService;
    }

    protected UUID authorizeUser(HttpExchange ex) throws IOException {
        String authHeader = ex.getRequestHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendText(ex, 401, "Missing or invalid Authorization header");
            return null;
        }

        String token = authHeader.substring("Bearer ".length());
        UUID userId = authService.authenticate(token);

        if (userId == null) {
            sendText(ex, 401, "Invalid token");
            return null;
        }

        return userId;
    }


    //JSON einlesen
    protected <T> T readJson(HttpExchange ex, Class<T> clazz) throws IOException {
        try (var in = ex.getRequestBody()) {
            return mapper.readValue(in, clazz);
        }
    }

    //Server Response as Plain Text
    protected void sendText(HttpExchange ex, int code, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    //Server Response as JSON
    private void sendJson(HttpExchange ex, int code, Object message) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(message);
        ex.getResponseHeaders().set("Content-Type", "application/son; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }
}
