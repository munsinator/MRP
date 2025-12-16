package at.fh.controller;

import at.fh.model.MediaEntry;
import at.fh.service.AuthService;
import at.fh.service.MediaDTO;
import at.fh.service.MediaService;
import com.sun.net.httpserver.HttpExchange;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class MediaController {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MediaService mediaService;
    //private final AuthService authService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        if (path.equals("/api/media/")) {
            switch (method) {
                case "GET" -> sendJson(ex, 200, mediaService.findAll());
                case "POST" -> createMedia(ex);
                default -> sendText(ex, 405, "Method Not Allowed");
            }
        }

        if (path.startsWith("/api/media/")) {
            UUID mediaId = UUID.fromString(path.substring("/api/media/".length()));

            switch (method) {
                case "DELETE" -> deleteMedia(ex, mediaId); //204
                case "GET" -> getMedia(ex, mediaId); //200
                case "PUT" -> updateMedia(ex, mediaId); //200
                default -> sendText(ex, 405, "Method Not Allowed");
            }
        }
    }

    private void getMedia(HttpExchange ex, UUID mediaId) throws IOException {
        Optional<MediaEntry> opt = mediaService.findById(mediaId);

        if (opt.isEmpty()) {
            sendText(ex, 404, "Not Found");
            return;
        }

        sendJson(ex, 200, opt.get());
    }


    private void deleteMedia(HttpExchange ex, UUID mediaId) throws IOException {

        try {
            boolean ok = mediaService.delete(mediaId);
            if (!ok) {
                sendText(ex, 404, "Not Found");
                return;
            }
            ex.sendResponseHeaders(204, -1);
            ex.close();
        } catch (SecurityException se) {
            sendText(ex, 403, "Forbidden");
        } catch (Exception e) {
            sendText(ex, 500, "Server error");
        }
    }

    private void sendText(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    private void sendJson(HttpExchange ex, int status, Object body) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(body);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

}