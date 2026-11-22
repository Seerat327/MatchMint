
package Backend.srcc;

import Backend.ResumeAnalyzer;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MatchMintServer {

    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
            );

            // TEST API
            server.createContext("/api/test", exchange -> {
                sendResponse(exchange, 200,
                    "MatchMint Backend is Connected!");
            });

            // SIGNUP API
            server.createContext("/api/signup", exchange -> {

                if (handleOptions(exchange)) return;

                if (!exchange.getRequestMethod().equals("POST")) {
                    sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                    return;
                }

                try {
                    String body = readBody(exchange);

                    String name = getJsonValue(body, "fullName");
                    String email = getJsonValue(body, "email");
                    String password = getJsonValue(body, "password");

                    if (name == null || email == null || password == null
                            || name.isBlank() || email.isBlank()
                            || password.length() < 8) {
                        sendResponse(exchange, 400,
                            "{\"message\":\"Please provide valid details.\"}");
                        return;
                    }

                    User user = new User(name, email, password);

                    boolean registered =
                        new UserDAO().registerUser(user);

                    if (registered) {
                        sendResponse(exchange, 201,
                            "{\"message\":\"Account created successfully! Welcome to MatchMint!\"}");
                    } else {
                        sendResponse(exchange, 409,
                            "{\"message\":\"Registration failed. This email may already be registered.\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500,
                        "{\"message\":\"Something went wrong.\"}");
                }
            });

            // LOGIN API
            server.createContext("/api/login", exchange -> {

                if (handleOptions(exchange)) return;

                if (!exchange.getRequestMethod().equals("POST")) {
                    sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                    return;
                }

                try {
                    String body = readBody(exchange);

                    String email = getJsonValue(body, "email");
                    String password = getJsonValue(body, "password");

                    if (email == null || password == null
                            || email.isBlank() || password.isEmpty()) {
                        sendResponse(exchange, 400,
                            "{\"message\":\"Please enter email and password.\"}");
                        return;
                    }

                    LoginDAO dao = new LoginDAO();
                    User user = dao.loginUser(email, password);

                    if (user != null) {
                        String response =
                            "{\"message\":\"Login successful! Welcome back!\","
                            + "\"fullName\":\""
                            + escapeJson(user.getFullName()) + "\"}";

                        sendResponse(exchange, 200, response);

                    } else {
                        sendResponse(exchange, 401,
                            "{\"message\":\"Invalid email or password.\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500,
                        "{\"message\":\"Something went wrong.\"}");
                }
            });

            // RESUME ANALYSIS API
            server.createContext("/api/analyze", exchange -> {

                if (handleOptions(exchange)) return;

                if (!exchange.getRequestMethod().equals("POST")) {
                    sendResponse(exchange, 405,
                        "{\"message\":\"Method not allowed\"}");
                    return;
                }

                try {
                    String body = readBody(exchange);

                    String encodedFile =
                        getJsonValue(body, "file");

                    if (encodedFile == null || encodedFile.isBlank()) {
                        sendResponse(exchange, 400,
                            "{\"message\":\"Please upload a resume.\"}");
                        return;
                    }

                    // DECODE PDF
                    byte[] pdfData =
                        Base64.getDecoder().decode(encodedFile);

                    // CHECK FILE SIZE
                    if (pdfData.length > 5 * 1024 * 1024) {
                        sendResponse(exchange, 413,
                            "{\"message\":\"File size must be under 5 MB.\"}");
                        return;
                    }

                    if (pdfData.length < 5) {
                        sendResponse(exchange, 400,
                            "{\"message\":\"Invalid PDF file.\"}");
                        return;
                    }

                    // CHECK PDF SIGNATURE
                    String signature = new String(
                        pdfData, 0, 5,
                        StandardCharsets.US_ASCII
                    );

                    if (!signature.equals("%PDF-")) {
                        sendResponse(exchange, 400,
                            "{\"message\":\"Please upload a valid PDF file.\"}");
                        return;
                    }

                    // ANALYZE RESUME
                    String result =
                        ResumeAnalyzer.analyzeResume(pdfData);

                    sendResponse(exchange, 200, result);

                } catch (IllegalArgumentException e) {
                    sendResponse(exchange, 400,
                        "{\"message\":\"Invalid resume data.\"}");

                } catch (Exception e) {
                    e.printStackTrace();

                    sendResponse(exchange, 500,
                        "{\"message\":\"Resume analysis failed.\"}");
                }
            });

            // START SERVER
            server.start();

            System.out.println("MatchMint Server Started!");
            System.out.println("Signup API Ready!");
            System.out.println("Login API Ready!");
            System.out.println("Resume Analysis API Ready!");
            System.out.println("Open http://localhost:8080/api/test");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CORS PREFLIGHT
    private static boolean handleOptions(HttpExchange exchange)
            throws IOException {

        if (exchange.getRequestMethod().equals("OPTIONS")) {

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS");

            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type");

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return true;
        }

        return false;
    }

    // READ REQUEST BODY
    private static String readBody(HttpExchange exchange)
            throws IOException {

        try (InputStream input = exchange.getRequestBody()) {
            return new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8
            );
        }
    }

    // SEND RESPONSE
    private static void sendResponse(
            HttpExchange exchange, int status, String response)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8");

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*");

        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(status, data.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(data);
        }
    }

    // READ JSON VALUE
    private static String getJsonValue(String json, String key) {

        String search = "\"" + key + "\"";
        int start = json.indexOf(search);

        if (start == -1) return null;

        start = json.indexOf(":", start);
        if (start == -1) return null;

        start = json.indexOf("\"", start);
        if (start == -1) return null;

        int end = json.indexOf("\"", start + 1);
        if (end == -1) return null;

        return json.substring(start + 1, end);
    }

    // ESCAPE JSON RESPONSE
    private static String escapeJson(String value) {

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}

