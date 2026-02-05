package org.example.utils;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.HttpService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TemplateUtils {

    public static String processCommand(String commandTemplate, HttpRequest request, File tempFileDir) {
        if (commandTemplate == null || commandTemplate.isEmpty()) {
            return "";
        }

        String processed = commandTemplate;
        HttpService service = request.httpService();
        String url = request.url();
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            // Fallback for malformed URLs if necessary, or just rely on basic string parsing
            // For now, assume valid URL from Burp
            uri = null;
        }

        // %H: Host
        processed = processed.replace("%H", service.host());

        // %P: Port
        processed = processed.replace("%P", String.valueOf(service.port()));

        // %S: Protocol
        boolean isHttps = service.secure();
        processed = processed.replace("%S", isHttps ? "https" : "http");

        // %M: Method
        processed = processed.replace("%M", request.method());

        // %U: Full URL
        processed = processed.replace("%U", url);

        if (uri != null) {
            // %E: Endpoint (No Query)
            String endpoint = (isHttps ? "https://" : "http://") + service.host() + ":" + service.port() + uri.getPath();
            // Or simpler construction if standard port:
            // String endpoint = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() != -1 ? ":" + uri.getPort() : "") + uri.getPath();
            // Let's stick to Burp's HttpService data + path
            processed = processed.replace("%E", endpoint);

            // %B: Base URL
            String baseUrl = (isHttps ? "https://" : "http://") + service.host() + (service.port() != 80 && service.port() != 443 ? ":" + service.port() : "");
            processed = processed.replace("%B", baseUrl);

            // %L: Path
            processed = processed.replace("%L", uri.getPath());

            // %Q: Query
            String query = uri.getQuery();
            processed = processed.replace("%Q", query != null ? query : "");
        } else {
            // Fallback replacements if URI parsing fails
            processed = processed.replace("%E", "");
            processed = processed.replace("%B", "");
            processed = processed.replace("%L", "");
            processed = processed.replace("%Q", "");
        }

        // %C: Cookie
        // Naive cookie extraction from header
        String cookie = "";
        if (request.hasHeader("Cookie")) {
            cookie = request.headerValue("Cookie");
        }
        processed = processed.replace("%C", cookie);

        // %A: User-Agent
        String ua = "";
        if (request.hasHeader("User-Agent")) {
            ua = request.headerValue("User-Agent");
        }
        processed = processed.replace("%A", ua);

        // %R: Request File
        if (processed.contains("%R") || processed.contains("%F")) {
            // Generate a filename: burp_host_port_date.req
            String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String simpleName = String.format("burp_%s_%s_%s.req", service.host(), service.port(), date);

            // Get full path using Utils
            String fullPath = Utils.getTempReqName(simpleName);
            File reqFile = new File(fullPath);

            try {
                // Write request bytes to file
                try (FileWriter writer = new FileWriter(reqFile)) {
                    writer.write(request.toString());
                }
                String filePath = reqFile.getAbsolutePath();
                processed = processed.replace("%R", filePath);
                processed = processed.replace("%F", filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return processed;
    }
}
