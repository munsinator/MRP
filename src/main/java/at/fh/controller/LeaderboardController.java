package at.fh.controller;

import at.fh.dto.UserStatistics;
import at.fh.model.User;
import at.fh.service.AuthService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class LeaderboardController extends BaseController implements HttpHandler {

    private final UserService userService;

    public LeaderboardController(UserService userService, AuthService authService) {
        super(authService);
        this.userService = userService;
    }

    // Routing
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String fullPath = ex.getRequestURI().getPath();
        String context  = ex.getHttpContext().getPath();
        String path = fullPath.substring(context.length());
        String method = ex.getRequestMethod();

        try {
            if (path.equals("/") || path.isEmpty()) {
                if (method.equals("GET")) {
                    getLeaderboard(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;
            }

            sendText(ex, 404, "Endpoint not found");
        } catch (Exception e) {
            sendText(ex, 500, "Internal Server Error");
        }
    }

    private void getLeaderboard(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        List<UserStatistics> result = userService.getLeaderboard();
        sendJson(ex, 200, result);
    }
}