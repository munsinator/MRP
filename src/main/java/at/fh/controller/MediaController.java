package at.fh.controller;

import at.fh.dto.MediaInput;
import at.fh.model.MediaEntry;
import at.fh.service.AuthService;
import at.fh.service.MediaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaController extends BaseController implements HttpHandler {
    private final MediaService mediaService;
    private final AuthService authService;

    public MediaController(MediaService mediaService, AuthService authService) {
        super(authService);
        this.mediaService = mediaService;
        this.authService = authService;
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
                    //TODO: FERTIG MACHEN
            } else {
                sendText(ex, 404, "Endpoint not found");
                return;
            }
        } catch (Exception e) {
            sendText(ex, 500, "Internal Server Error");
            return;
        }

    } //TODO: MEDIARATING FERTIG MACHEN

    private void findAllMedia(HttpExchange ex) throws IOException {
        UUID loggedInUserId = authorizeUser(ex);
        if (loggedInUserId == null) return;

        List<MediaEntry> result = mediaService.findAll();

        if (result.isEmpty()){
            sendText(ex, 404, "No media found");
            return;
        }

        ex.sendResponseHeaders(200, -1);
        ex.close();
    }

    private void createMedia(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        //Liest die Daten, die der User über JSON mitgegeben hat!
        MediaInput req = readJson(ex, MediaInput.class);

        boolean success = mediaService.create(req, userId);
        if (!success) {
            sendText(ex, 404, "Media not found");
            return;
        }

        sendText(ex, 201, "Media created");
    }

    private void findMediaById(HttpExchange ex) throws IOException {
        UUID userId = authorizeUser(ex);
        if (userId == null) return;

        UUID mediaId = extractUuid(ex);

        Optional<MediaEntry> result = mediaService.findById(mediaId);
        if (result.isEmpty()) {
            sendText(ex, 404, "Media not found");
            return;
        }
        sendJson(ex, 200, result);
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
}