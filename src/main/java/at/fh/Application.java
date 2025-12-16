package at.fh;

import at.fh.controller.MediaController;
import at.fh.controller.UserController;
import at.fh.service.AuthService;
import at.fh.service.MediaService;
import at.fh.service.UserService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Application {
    static void main() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        AuthService authService = new AuthService();
        UserService userService = new UserService();
        MediaService mediaService = new MediaService();

        UserController userController = new UserController(userService,authService);
        MediaController mediaController = new MediaController(mediaService,authService);


        //Für Testzwecke
        server.createContext("/health", exchange -> {
            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });


        server.createContext("/api/users", userController::handle);
        server.createContext("/api/media", mediaController::handle);


        server.start();
        System.out.println("Server running on port 8080...");
    }
}
