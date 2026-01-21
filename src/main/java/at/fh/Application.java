package at.fh;

import at.fh.config.DatabaseConfig;
import at.fh.controller.LeaderboardController;
import at.fh.controller.MediaController;
import at.fh.controller.RatingController;
import at.fh.controller.UserController;
import at.fh.repository.*;
import at.fh.service.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;

public class Application {
    static void main() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        Connection dbConnection = DatabaseConfig.getConnection();

        JDBCUserRepository userRepository = new JDBCUserRepository(dbConnection);
        JDBCMediaEntryRepository mediaRepository = new JDBCMediaEntryRepository(dbConnection);
        JDBCGenreRepository genreRepository = new JDBCGenreRepository(dbConnection);
        JDBCRatingRepository  ratingRepository = new JDBCRatingRepository(dbConnection);
        JDBCRecommendationRepository recommendationRepository = new JDBCRecommendationRepository(dbConnection);
        JDBCLeaderboardRepository leaderboardRepository = new JDBCLeaderboardRepository(dbConnection);

        AuthService authService = new AuthService();
        UserService userService = new UserService(userRepository, leaderboardRepository, authService);
        MediaService mediaService = new MediaService(mediaRepository,  genreRepository, ratingRepository);
        RatingService ratingService = new RatingService(ratingRepository);
        RecommendationService recommendationService = new RecommendationService(recommendationRepository);

        UserController userController = new UserController(userService,authService, ratingService, mediaService, recommendationService);
        MediaController mediaController = new MediaController(mediaService, ratingService, authService);
        RatingController ratingController = new RatingController(ratingService, authService);
        LeaderboardController leaderboardController = new LeaderboardController(userService, authService);

        //Für Testzwecke
        server.createContext("/health", exchange -> {
            byte[] response = "OK".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        server.createContext("/api/users", userController::handle);
        server.createContext("/api/media", mediaController::handle);
        server.createContext("/api/ratings", ratingController::handle);
        server.createContext("/api/leaderboard", leaderboardController::handle);

        server.start();
        System.out.println("Server running on port 8080...");
    }
}
