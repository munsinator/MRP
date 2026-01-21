package at.fh.controller;

import at.fh.service.AuthService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
    protected <T> T readJson(HttpExchange ex, Class<T> obj) throws IOException {
        try (var in = ex.getRequestBody()) {
            return mapper.readValue(in, obj);
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
    protected void sendJson(HttpExchange ex, int code, Object message) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(message);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    //Extract UUID such as userId, mediaId etc. HELPER
    protected UUID extractUuid(HttpExchange ex) throws IOException {
        String[] parts = ex.getRequestURI().getPath().split("/");

        try {
            return UUID.fromString(parts[3]);
        } catch (IllegalArgumentException e) {
            sendText(ex, 400, "Error while accessing endpoint: Invalid UUID");
            return null;
        }
    }

    //Helps extracting the paramaters of a query
    protected Map<String, String> queryParams(HttpExchange ex) {
        Map<String, String> parameters = new HashMap<>();
        String query = ex.getRequestURI().getRawQuery();

        if (query == null || query.isEmpty()) return parameters;

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1
                    ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8)
                    : "";
            parameters.put(key, value);
        }
        return parameters;
    }


}
