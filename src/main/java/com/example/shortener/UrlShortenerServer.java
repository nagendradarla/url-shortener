package com.example.shortener;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/** Minimal HTTP layer: POST /shorten, GET /stats/{code}, GET /{code}. */
public class UrlShortenerServer {

    private final UrlShortenerService service;

    public UrlShortenerServer() {
        this(new UrlShortenerService());
    }

    public UrlShortenerServer(UrlShortenerService service) {
        this.service = service;
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        new UrlShortenerServer().start(port);
        System.out.println("URL shortener listening on http://localhost:" + port);
    }

    public HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/shorten", this::handleShorten);
        server.createContext("/stats", this::handleStats);
        server.createContext("/", this::handleResolve);
        server.start();
        return server;
    }

    private void handleShorten(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Method Not Allowed");
            return;
        }
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            String code = service.shorten(body.trim());
            respond(exchange, 200, code);
        } catch (IllegalArgumentException e) {
            respond(exchange, 400, "Invalid URL");
        }
    }

    private void handleStats(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Method Not Allowed");
            return;
        }
        String path = exchange.getRequestURI().getPath();
        String code = path.startsWith("/stats/") ? path.substring("/stats/".length()) : "";
        if (code.isBlank()) {
            respond(exchange, 404, "Not found");
            return;
        }
        Optional<Long> count = service.clickCount(code);
        if (count.isPresent()) {
            respond(exchange, 200, Long.toString(count.get()));
        } else {
            respond(exchange, 404, "Not found");
        }
    }

    private void handleResolve(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            respond(exchange, 405, "Method Not Allowed");
            return;
        }
        String code = exchange.getRequestURI().getPath().replaceFirst("^/", "");
        if (code.isBlank() || "shorten".equals(code) || "stats".equals(code)) {
            respond(exchange, 400, "Missing short code");
            return;
        }
        Optional<String> longUrl = service.resolve(code);
        if (longUrl.isPresent()) {
            exchange.getResponseHeaders().add("Location", longUrl.get());
            respond(exchange, 302, "");
        } else {
            respond(exchange, 404, "Not found");
        }
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
