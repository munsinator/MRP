package at.fh.controller;

import at.fh.dto.MediaInput;
import at.fh.dto.RatingInput;
import at.fh.model.MediaEntry;
import at.fh.service.AuthService;
import at.fh.service.RatingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class RatingController extends BaseController implements HttpHandler {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService, AuthService authService) {
        super(authService);
        this.ratingService = ratingService;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String fullPath = ex.getRequestURI().getPath();
        String context  = ex.getHttpContext().getPath();
        String path = fullPath.substring(context.length());
        String method = ex.getRequestMethod();

        try {
             if ((path.matches("^/[^/]+$"))) { //TODO: Regex bearbeiten!
                switch (method) {
                    case "PUT":
                        updateRating(ex);
                        return;
                    case "DELETE":
                        deleteRating(ex);
                        return;
                    default:
                        sendText(ex, 405, "Not a valid method");
                }
            } else if (path.matches("^/[^/]+/like/?$")) {
                if (method.equals("POST")) {
                    likeRating(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;

            } else if (path.matches("^/[^/]+/confirm/?$")) {
                if (method.equals("POST")) {
                    confirmComment(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;
            } else {
                sendText(ex, 404, "Endpoint not found");
                return;
            }

        } catch (Exception e){
            sendText(ex, 500, "Internal Server Error");
            return;
        }
    }

    private void confirmComment(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;
        UUID ratingId = extractUuid(ex);
        boolean success = ratingService.confirmRating(ratingId);

        if (!success) {
            sendText(ex, 404, "Rating not found");
            return;
        }

        sendText(ex, 200, "Comment confirmed successfully");
    }

    private void likeRating(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;
        UUID ratingId = extractUuid(ex);
        boolean success = ratingService.likeRating(ratingId);

        if (!success) {
            sendText(ex, 404, "Rating not found");
            return;
        }

        sendText(ex, 200, "Rating liked successfully");
    }

    private void deleteRating(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;
        UUID ratingId = extractUuid(ex);

        boolean success = ratingService.deleteRating(ratingId);

        if (!success) {
            sendText(ex, 404, "Rating not found");
            return;
        }

        sendText(ex, 204, "Rating deleted");
    }

    private void updateRating(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;
        UUID ratingId = extractUuid(ex);
        RatingInput req = readJson(ex, RatingInput.class);

        boolean result = ratingService.updateRating(req, ratingId, loggedInUserId);

        if (!result) {
            sendText(ex, 404, "Rating not found");
            return;
        }

        sendText(ex, 200, "Rating updated");
    }
}
