package org.example.utils;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CurlParser {

    public static HttpRequest parse(String curlCommand) throws Exception {
        if (curlCommand == null || !curlCommand.trim().toLowerCase().startsWith("curl")) {
            throw new IllegalArgumentException("Not a valid cURL command");
        }

        List<String> args = tokenize(curlCommand);
        
        String method = "GET";
        String urlStr = null;
        List<String> headers = new ArrayList<>();
        StringBuilder bodyBuilder = new StringBuilder();
        boolean hasBody = false;
        
        for (int i = 1; i < args.size(); i++) {
            String arg = args.get(i);
            
            if (arg.startsWith("-")) {
                switch (arg) {
                    case "-X":
                    case "--request":
                        if (i + 1 < args.size()) {
                            method = args.get(++i).toUpperCase();
                        }
                        break;
                    case "-I":
                    case "--head":
                        method = "HEAD";
                        break;
                    case "-H":
                    case "--header":
                        if (i + 1 < args.size()) {
                            headers.add(cleanHeader(args.get(++i)));
                        }
                        break;
                    case "-d":
                    case "--data":
                    case "--data-raw":
                    case "--data-binary":
                    case "--data-ascii":
                        if (i + 1 < args.size()) {
                            if (hasBody) bodyBuilder.append("&");
                            bodyBuilder.append(args.get(++i));
                            hasBody = true;
                        }
                        break;
                    case "--data-urlencode":
                        if (i + 1 < args.size()) {
                            if (hasBody) bodyBuilder.append("&");
                            String data = args.get(++i);
                            // cURL syntax: name=content (encode content), =content (encode content), or just content (encode all)
                            // Simplified logic: just URL encode the value if it looks like key=value
                            if (data.contains("=")) {
                                String[] parts = data.split("=", 2);
                                bodyBuilder.append(parts[0]).append("=").append(URLEncoder.encode(parts[1], StandardCharsets.UTF_8.toString()));
                            } else {
                                bodyBuilder.append(URLEncoder.encode(data, StandardCharsets.UTF_8.toString()));
                            }
                            hasBody = true;
                        }
                        break;
                    case "-A":
                    case "--user-agent":
                        if (i + 1 < args.size()) {
                            headers.add("User-Agent: " + args.get(++i));
                        }
                        break;
                    case "-b":
                    case "--cookie":
                        if (i + 1 < args.size()) {
                            headers.add("Cookie: " + args.get(++i));
                        }
                        break;
                    case "-u":
                    case "--user":
                        if (i + 1 < args.size()) {
                            String auth = args.get(++i);
                            String encoded = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
                            headers.add("Authorization: Basic " + encoded);
                        }
                        break;
                    case "--compressed":
                        headers.add("Accept-Encoding: gzip, deflate, br");
                        break;
                    case "-k":
                    case "--insecure":
                        // Burp handles SSL, maybe ignore or log
                        break;
                    default:
                        // Skip unknown flags with arguments? Difficult to guess.
                        // cURL flags are consistent, but short ones can be bundled (-vL).
                        // Tokenizer splits spaces, not bundled flags. Assuming standard expanded cURL.
                        break;
                }
            } else {
                if (urlStr == null && (arg.startsWith("http") || arg.startsWith("www") || !arg.startsWith("-"))) {
                    urlStr = arg;
                }
            }
        }

        if (urlStr == null) {
            throw new IllegalArgumentException("No URL found in cURL command");
        }
        
        // Auto-detect POST if not explicit
        if (hasBody && "GET".equals(method)) {
            method = "POST";
        }
        
        // Clean URL quotes if tokenizer missed them (sanity check)
        if (urlStr.startsWith("'") && urlStr.endsWith("'")) urlStr = urlStr.substring(1, urlStr.length()-1);
        if (urlStr.startsWith("\"") && urlStr.endsWith("\"")) urlStr = urlStr.substring(1, urlStr.length()-1);

        URI uri = new URI(urlStr);
        boolean secure = "https".equalsIgnoreCase(uri.getScheme());
        if (uri.getScheme() == null) {
            // Default to http if missing
            secure = false;
            uri = new URI("http://" + urlStr);
        }
        
        int port = uri.getPort();
        if (port == -1) {
            port = secure ? 443 : 80;
        }
        
        HttpService service = HttpService.httpService(uri.getHost(), port, secure);
        
        StringBuilder reqBuilder = new StringBuilder();
        String path = uri.getRawPath();
        if (path == null || path.isEmpty()) path = "/";
        if (uri.getRawQuery() != null) {
            path += "?" + uri.getRawQuery();
        }
        
        reqBuilder.append(method).append(" ").append(path).append(" HTTP/1.1\r\n");
        reqBuilder.append("Host: ").append(uri.getHost());
        if ((secure && port != 443) || (!secure && port != 80)) {
            reqBuilder.append(":").append(port);
        }
        reqBuilder.append("\r\n");
        
        boolean hasContentType = false;
        for (String header : headers) {
            reqBuilder.append(header).append("\r\n");
            if (header.toLowerCase().startsWith("content-type:")) {
                hasContentType = true;
            }
        }
        
        if (hasBody && !hasContentType) {
            reqBuilder.append("Content-Type: application/x-www-form-urlencoded\r\n");
        }
        
        String body = hasBody ? bodyBuilder.toString() : null;
        if (body != null) {
            reqBuilder.append("Content-Length: ").append(body.getBytes().length).append("\r\n");
        }
        
        reqBuilder.append("\r\n");
        if (body != null) {
            reqBuilder.append(body);
        }
        
        return HttpRequest.httpRequest(service, reqBuilder.toString());
    }
    
    private static String cleanHeader(String header) {
        // Remove surrounding quotes if present
        if (header.startsWith("'") && header.endsWith("'")) return header.substring(1, header.length()-1);
        if (header.startsWith("\"") && header.endsWith("\"")) return header.substring(1, header.length()-1);
        return header;
    }

    private static List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inDoubleQuote = false;
        boolean inSingleQuote = false;
        boolean escapeNext = false;

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if (escapeNext) {
                currentToken.append(c);
                escapeNext = false;
            } else if (c == '\\') {
                // If in single quotes, backslash is literal usually in shell, but cURL copies usually escape correctly.
                // Standard shell: single quotes preserve EVERYTHING except single quote (which can't be inside).
                // Let's keep escape logic generic for now.
                escapeNext = true; 
            } else if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
            } else if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (Character.isWhitespace(c) && !inDoubleQuote && !inSingleQuote) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        return tokens;
    }
}