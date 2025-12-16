package at.fh;

import at.fh.config.DatabaseConfig;
import at.fh.controller.MediaController;
import at.fh.controller.UserController;
import at.fh.repository.MediaEntryRepository;
import at.fh.repository.UserRepository;
import at.fh.service.AuthService;
import at.fh.service.MediaService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;

public class Application {
    static void main() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        MediaController mediaController = getMediaController();


        //Für Testzwecke
        server.createContext("/health", exchange -> {
            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });


        //server.createContext("/api/users", userController::handle);
        server.createContext("/api/media", mediaController::handle);


        server.start();
        System.out.println("Server running on port 8080...");
    }

    private static MediaController getMediaController() {
        Connection dbConnection = DatabaseConfig.getConnection();

        //UserRepository userRepository = new UserRepository();
        MediaEntryRepository mediaRepository = new MediaEntryRepository(dbConnection);

        //AuthService authService = new AuthService();
        //UserService userService = new UserService();
        MediaService mediaService = new MediaService(mediaRepository);

        //UserController userController = new UserController(userService,authService);
        MediaController mediaController = new MediaController(mediaService);
        return mediaController;
    }
}
