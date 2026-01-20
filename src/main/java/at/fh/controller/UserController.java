package at.fh.controller;

import at.fh.dto.UserCredentials;
import at.fh.dto.UserProfileUpdate;
import at.fh.model.MediaEntry;
import at.fh.model.Rating;
import at.fh.model.User;
import at.fh.model.UserProfile;
import at.fh.service.AuthService;
import at.fh.service.MediaService;
import at.fh.service.RatingService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UserController extends BaseController implements HttpHandler {
    private final UserService userService;
    private final RatingService ratingService;
    private final MediaService mediaService;

    public UserController(UserService userService, AuthService authService, RatingService ratingService, MediaService mediaService) {
        super(authService);
        this.userService = userService;
        this.ratingService = ratingService;
        this.mediaService = mediaService;
    }

    // ROUTING
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String fullPath = ex.getRequestURI().getPath();
        String context  = ex.getHttpContext().getPath();
        String path = fullPath.substring(context.length());
        String method = ex.getRequestMethod();

        try {
            if (path.equals("/register/") || path.equals("/register")) {
                if (method.equals("POST")) {
                    registerUser(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else if (path.equals("/login/") || path.equals("/login")) {
                if (method.equals("POST")) {
                    loginUser(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else if (path.matches("^/[^/]+/profile/?$")) {
                if (method.equals("GET")) {
                    getProfile(ex);
                    return;
                }

                if (method.equals("PUT")) {
                    updateProfile(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else if (path.matches("^/[^/]+/ratings/?$")) {
                if (method.equals("GET")) {
                    getRatingHistory(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else if (path.matches("^/[^/]+/favorites/?$")) {
                if (method.equals("GET")) {
                    getFavoriteMedia(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else {
                sendText(ex, 404, "Endpoint not found");
                return;
            }

        } catch (Exception e) {
            sendText(ex, 500, "Internal Server Error");
            return;
        }
    }

    private void registerUser(HttpExchange ex) throws IOException {
        UserCredentials req = readJson(ex, UserCredentials.class);
        userService.register(req);

        ex.sendResponseHeaders(201, -1);
        ex.close();
    }

    private void loginUser(HttpExchange ex) throws IOException {
        UserCredentials req = readJson(ex, UserCredentials.class);
        Optional<String> token = userService.login(req);

        if (token.isEmpty()) {
            sendText(ex, 401, "Invalid credentials");
            return;
        }

        sendJson(ex, 200, Map.of("token", token.get()));
    }

    private void getProfile(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;

        UUID requestedUserId = extractUuid(ex);
        if (requestedUserId == null) return;

        if (!requestedUserId.equals(loggedInUserId)) {
            sendText(ex, 403, "Forbidden Access");
            return;
        }

        Optional<User> profile = userService.getUserById(requestedUserId);
        sendJson(ex, 200, profile);
    }

    private void updateProfile(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;

        UserProfileUpdate req = readJson(ex, UserProfileUpdate.class);

        boolean result = userService.updateUser(req, loggedInUserId);

        if (!result) {
            sendText(ex, 404, "User not found");
            return;
        }

        sendText(ex, 200, "Profile updated");
    }

    private void getRatingHistory(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;

        UUID requestedUserId = extractUuid(ex);
        if (requestedUserId == null) return;

        if (!requestedUserId.equals(loggedInUserId)) {
            sendText(ex, 403, "Forbidden Access");
            return;
        }

        List<Rating> ratingHistory = ratingService.getRatingHistoryOfUser(requestedUserId);
        sendJson(ex, 200, ratingHistory);
    }

    private void getFavoriteMedia(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;

        UUID requestedUserId = extractUuid(ex);
        if (requestedUserId == null) return;

        if (!requestedUserId.equals(loggedInUserId)) {
            sendText(ex, 403, "Forbidden Access");
            return;
        }

        List<MediaEntry> favorites = mediaService.getFavoriteMediaFrom(requestedUserId);
        sendJson(ex, 200, favorites);
    }
}