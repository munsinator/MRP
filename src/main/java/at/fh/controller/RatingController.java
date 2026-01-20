package at.fh.controller;

import at.fh.service.AuthService;
import at.fh.service.RatingService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class RatingController extends BaseController implements HttpHandler {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService, AuthService authService) {
        super(authService);
        this.ratingService = ratingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
