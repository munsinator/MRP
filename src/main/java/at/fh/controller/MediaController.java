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
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();

        try {
            if (path.equals("/api/media/") || path.equals("/api/media")) {
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
            }

            if (path.startsWith("/api/media/")) {
                String id = path.substring("/api/media/".length());
                UUID mediaId = UUID.fromString(id);

                switch (method) {
                    case "GET":
                        findMediaById(ex,mediaId);
                        return;
                    case "PUT":
                        updateMedia(ex,mediaId);
                        return;
                    case "DELETE":
                        deleteMedia(ex,mediaId);
                        return;
                    default:
                        sendText(ex, 405, "Not a valid method");
                }
            }
        } catch (Exception e) {
            sendText(ex, 500, "Internal Server Error");
            return;
        }

    }

    private void findAllMedia(HttpExchange ex) throws IOException {
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
        //Liest die Daten, die der User über JSON mitgegeben hat!
        MediaInput req = readJson(ex, MediaInput.class);

        boolean success = mediaService.create(req, userId);
        if (!success) {
            sendText(ex, 404, "Media not found");
            return;
        }

        ex.sendResponseHeaders(201, -1);
        ex.close();
    }

    private void findMediaById(HttpExchange ex, UUID mediaId) throws IOException {
        Optional<MediaEntry> result = mediaService.findById(mediaId);
        if (result.isEmpty()) {
            sendText(ex, 404, "Media not found");
            return;
        }
        ex.sendResponseHeaders(200, -1);
        ex.close();
    }

    private void updateMedia(HttpExchange ex, UUID mediaId) throws IOException {

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

    private void deleteMedia(HttpExchange ex, UUID mediaId) throws IOException {
        boolean success = mediaService.delete(mediaId);

        if (!success) {
            sendText(ex, 404, "Media not Found");
            return;
        }

        ex.sendResponseHeaders(204, -1);
        ex.close();
    }
}