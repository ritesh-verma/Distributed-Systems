package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

class Response {

    private String request;
    Response(String requestIn) {
        request = requestIn;
    }

    String generateResponse(File requestFile) throws IOException {
        StringBuilder response = new StringBuilder();

        String[] requestLines = request.split("\n");
        String[] tokens = requestLines[0].split(" ");

        Path source = Paths.get(requestFile.getPath());

        if (tokens[0].equals("GET") && requestFile.exists()) {
            response.append(tokens[2]).append(" ");
            response.append("200 OK").append("\r\n");
            response.append("Date: ").append(getCurrentTime()).append("\r\n");
            response.append("Server: ").append("My HTTP Server").append("\r\n");
            response.append("Last-Modified: ").append(getLastModifiedTime(requestFile)).append("\r\n");
            response.append("Content-Type: ").append(Files.probeContentType(source)).append("\r\n");
            response.append("Content-Length: ").append(requestFile.length()).append("\r\n\r\n");

        } else if (!requestFile.exists()) {
            response.append(tokens[2]).append(" ");
            response.append("404 Not Found\r\n");
            response.append("Date: ").append(getCurrentTime()).append("\r\n");
            response.append("Server: ").append("My HTTP Server").append("\r\n\r\n");
        } else {
            response.append("Incorrect request format.\r\n");
        }

        return response.toString();
    }

    String getRequestFile() {
        String requestFile;

        String[] requestLines = request.split("\n");
        String[] tokens = requestLines[0].split(" ");

        requestFile = tokens[1].substring(1, tokens[1].length());

        return requestFile;
    }

    private String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private String getLastModifiedTime(File file) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(file.lastModified());
    }
}
