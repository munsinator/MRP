package at.fh.controller;

import at.fh.dto.MediaDetailsResponse;
import at.fh.dto.MediaInput;
import at.fh.dto.MediaSearchParams;
import at.fh.dto.RatingInput;
import at.fh.model.MediaEntry;
import at.fh.service.AuthService;
import at.fh.service.MediaService;
import at.fh.service.RatingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MediaController extends BaseController implements HttpHandler {
    private final MediaService mediaService;
    private final RatingService ratingService;

    public MediaController(MediaService mediaService, RatingService ratingService, AuthService authService) {
        super(authService);
        this.mediaService = mediaService;
        this.ratingService = ratingService;
    }

    // ROUTING
    @Override
    public void handle(HttpExchange ex) throws IOException {
        String fullPath = ex.getRequestURI().getPath();
        String context  = ex.getHttpContext().getPath();
        String path = fullPath.substring(context.length());
        String method = ex.getRequestMethod();

        try {
            if (path.equals("/") || path.isEmpty()) {
                if (method.equals("GET") && ex.getRequestURI().getQuery() != null) {
                    searchMedia(ex);
                    return;
                }

                switch (method) {
                    case "GET":
                        findAllMedia(ex);
                        return;
                    case "POST":
                        createMedia(ex);
                        return;
                    default:
                        sendText(ex, 405, "Not a valid method");
                        return;
                }
            } else if ((path.matches("^/[^/]+$"))) {
                switch (method) {
                    case "GET":
                        findMediaById(ex);
                        return;
                    case "PUT":
                        updateMedia(ex);
                        return;
                    case "DELETE":
                        deleteMedia(ex);
                        return;
                    default:
                        sendText(ex, 405, "Not a valid method");
                }
            } else if (path.matches("^/[^/]+/rate/?$")) {
                if (method.equals("POST")) {
                    rateMedia(ex);
                    return;
                }
                sendText(ex, 405, "Not a valid method");
                return;
            } else if (path.matches("^/[^/]+/favorite/?$")) {
                switch (method) {
                    case "POST":
                        markAsFavorite(ex);
                        return;
                    case "DELETE":
                        unmarkAsFavorite(ex);
                        return;
                    default:
                        sendText(ex, 405, "Not a valid method");
                }
            } else {
                sendText(ex, 404, "Endpoint not found");
                return;
            }
        } catch (Exception e) {
            sendText(ex, 500, "Internal Server Error");
            return;
        }

    }

    private void findAllMedia(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;

        List<MediaEntry> result = mediaService.findAll();

        if (result.isEmpty()){
            sendText(ex, 404, "No media found");
            return;
        }

        sendJson(ex, 200, result);
    }

    private void createMedia(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        //Liest die Daten, die der User über JSON mitgegeben hat!
        MediaInput req = readJson(ex, MediaInput.class);

        boolean success = mediaService.create(req, userId);
        if (!success) {
            sendText(ex, 404, "Invalid input");
            return;
        }

        sendText(ex, 201, "Media created");
    }

    private void findMediaById(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);

        Optional<MediaDetailsResponse> result = mediaService.getDetails(mediaId, userId);
        if (result.isEmpty()) {
            sendText(ex, 404, "Media not found");
            return;
        }
        sendJson(ex, 200, result.get());
    }

    private void updateMedia(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);

        //Liest die Daten, die der User über JSON mitgegeben hat!
        MediaInput req = readJson(ex, MediaInput.class);

        boolean success = mediaService.update(req, mediaId);
        if (!success) {
            sendText(ex, 404, "Media not found");
            return;
        }
        ex.sendResponseHeaders(200, -1);
        ex.close();
    }

    private void deleteMedia(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);

        boolean success = mediaService.delete(mediaId);

        if (!success) {
            sendText(ex, 404, "Media not Found");
            return;
        }

        ex.sendResponseHeaders(204, -1);
        ex.close();
    }

    private void rateMedia(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);
        RatingInput req = readJson(ex, RatingInput.class);
        boolean success = ratingService.createRating(req, mediaId, userId);

        if (!success) {
            sendText(ex, 404, "Rating failed");
            return;
        }

        sendText(ex, 201, "Rating submitted");
    }

    private void markAsFavorite(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);
        boolean success = mediaService.likeMedia(mediaId, userId);

        if (!success) {
            sendText(ex, 404, "Liking media failed");
            return;
        }

        sendText(ex, 200, "Marked as favorite");
    }

    private void unmarkAsFavorite(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);
        boolean success = mediaService.unlikeMedia(mediaId, userId);

        if (!success) {
            sendText(ex, 404, "Liking media failed");
            return;
        }

        ex.sendResponseHeaders(204, -1);
        ex.close();
    }

    private void searchMedia(HttpExchange ex) throws IOException {
        Map<String,String> q = queryParams(ex);

        MediaSearchParams p = new MediaSearchParams(
                q.get("title"),
                q.get("genre"),
                q.get("type"),
                q.containsKey("year") ? Integer.valueOf(q.get("year")) : null,
                q.containsKey("age") ? Integer.valueOf(q.get("age")) : null,
                q.containsKey("rating") ? Double.valueOf(q.get("rating")) : null,
                q.get("sort")
        );

        sendJson(ex, 200, mediaService.search(p));
    }
}