// package com.example.Loadbalancer.controller;

// import com.example.Loadbalancer.service.LoadBalancerService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.*;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.client.RestTemplate;

// import jakarta.servlet.http.HttpServletRequest;
// import java.util.List;
// @RestController
// public class ForwardController {

//     @Autowired
//     private LoadBalancerService loadBalancerService;

//     @Autowired
//     private RestTemplate restTemplate;

//     @RequestMapping("/api/**")
//     public ResponseEntity<?> forwardRequest(HttpServletRequest request, HttpEntity<String> httpEntity) {
        
//         System.out.println("USLOOOOOOOOOO");
//         String backendUrl = loadBalancerService.getNextServer() + request.getRequestURI();
//         try {
//             // Prosledi zahtev backendu
//             System.out.println("Forwarding request to: " + backendUrl);
//             ResponseEntity<String> response = restTemplate.exchange(
//                 backendUrl,
//                 HttpMethod.valueOf(request.getMethod()),
//                 httpEntity,
//                 String.class
//             );
//             return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//         } catch (Exception ex) {
//             // Retry policy: pokušaj na drugom serveru
//             String retryUrl = loadBalancerService.getNextServer() + request.getRequestURI();
//             try {
//                 ResponseEntity<String> retryResponse = restTemplate.exchange(
//                     retryUrl,
//                     HttpMethod.valueOf(request.getMethod()),
//                     httpEntity,
//                     String.class
//                 );
//                 return ResponseEntity.status(retryResponse.getStatusCode()).body(retryResponse.getBody());
//             } catch (Exception retryEx) {
//                 return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All backend servers are unavailable.");
//             }
//         }
//     }
// }

package com.example.Loadbalancer.controller;

import com.example.Loadbalancer.service.LoadBalancerService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import java.net.http.HttpHeaders;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ForwardController {

    @Autowired
    private LoadBalancerService loadBalancerService;

    @Autowired
    private RestTemplate restTemplate;

    // @RequestMapping(value = "/api/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    // public ResponseEntity<?> forwardRequest(HttpServletRequest request, @RequestBody(required = false) byte[] body, @RequestHeader HttpHeaders headers) {
    //     String backendUrl = loadBalancerService.getNextServer() + request.getRequestURI();

    //     HttpMethod method = HttpMethod.valueOf(request.getMethod());
    //     //System.out.println("Method: " + method);
    //     HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);
    //     //System.out.println("httpEntity: " + httpEntity);

    //     try {
    //         ResponseEntity<byte[]> response = restTemplate.exchange(
    //             backendUrl,
    //             method,
    //             httpEntity,
    //             byte[].class
    //         );
    //         return ResponseEntity.status(response.getStatusCode())
    //                 .headers(response.getHeaders())
    //                 .body(response.getBody());
    //     } catch (Exception ex) {
    //         // Retry policy: pokušaj na drugom serveru
    //         String retryUrl = loadBalancerService.getNextServer() + request.getRequestURI();
    //         try {
    //             ResponseEntity<byte[]> retryResponse = restTemplate.exchange(
    //                 retryUrl,
    //                 method,
    //                 httpEntity,
    //                 byte[].class
    //             );
    //             return ResponseEntity.status(retryResponse.getStatusCode())
    //                     .headers(retryResponse.getHeaders())
    //                     .body(retryResponse.getBody());
    //         } catch (Exception retryEx) {
    //             return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All backend servers are unavailable.");
    //         }
    //     }
    // }



    //OVAJ RADI OKEJ, PRIKAZUJE SLIKE NA POCETNOJ ALI NE MOZE DA ODRADI POST OBJAVE
    // @RequestMapping(value = {"/api/**", "/images/**", "/compressedImages/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    // public ResponseEntity<?> forwardRequest(HttpServletRequest request) throws IOException {
    //     String backendUrl = loadBalancerService.getNextServer() + request.getRequestURI();

    //     HttpHeaders headers = new HttpHeaders();
    //     Collections.list(request.getHeaderNames()).forEach(headerName ->
    //         headers.add(headerName, request.getHeader(headerName))
    //     );

    //     byte[] body = request.getInputStream().readAllBytes();

    //     HttpMethod method = HttpMethod.valueOf(request.getMethod());
    //     HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);

    //     try {
    //         ResponseEntity<byte[]> response = restTemplate.exchange(
    //             backendUrl,
    //             method,
    //             httpEntity,
    //             byte[].class
    //         );
    //         HttpHeaders responseHeaders = new HttpHeaders();
    //         responseHeaders.putAll(response.getHeaders());
    //         return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    //     } catch (Exception ex) {
    //         return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All backend servers are unavailable.");
    //     }
    // }

    // PRIKAZUJE SLIKE ALI NE POSTUJE OBJAVE
    // NEMA RETRY POLICY
    // @RequestMapping(value = {"/api/**", "/images/**", "/compressedImages/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    // public ResponseEntity<?> forwardRequest(HttpServletRequest request) throws IOException {
    //     String backendUrl = loadBalancerService.getNextServer() + request.getRequestURI();

    //     java.net.URL url = new java.net.URL(backendUrl);
    //     System.out.println("Forwarding request to: " + backendUrl);
    //     System.out.println("URL: " + url);
    //     java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
    //     conn.setRequestMethod(request.getMethod());

    //     // Kopiraj zaglavlja (Authorization, Content-Type, itd.)
    //     Collections.list(request.getHeaderNames()).forEach(headerName -> {
    //         conn.setRequestProperty(headerName, request.getHeader(headerName));
    //     });

    //     //input i output rezim (omogucava citanje i pisanje podataka kroz konekciju)
    //     conn.setDoInput(true);
    //     conn.setDoOutput(true);
        
    //     System.out.println("CONN: " + conn);

    //     // Kopiraj telo zahteva (osim get zahteva)
    //     if (!request.getMethod().equalsIgnoreCase("GET")) {
    //         try (var out = conn.getOutputStream(); var in = request.getInputStream()) {
    //             in.transferTo(out);
    //         }
    //     }

    //     int status = conn.getResponseCode();
    //     byte[] responseBody = conn.getInputStream().readAllBytes();

    //     //Kopira sva zaglavlja iz odgovora backend servera (npr. Content-Type, Set-Cookie, itd.)
    //     HttpHeaders responseHeaders = new HttpHeaders();
    //     conn.getHeaderFields().forEach((key, value) -> {
    //         if (key != null && value != null) responseHeaders.put(key, value);
    //     });

    //     //Vraća klijentu (frontend aplikaciji) odgovor backend servera, uključujući telo, zaglavlja i status kod.
    //     return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.valueOf(status));
    // }


    @RequestMapping(value = {"/api/**", "/images/**", "/compressedImages/**"}, method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
    public ResponseEntity<?> forwardRequest(HttpServletRequest request) throws IOException {
        // Učitaj listu servera iz LoadBalancerService
        List<String> servers = Arrays.asList(
            "http://localhost:8086",
            "http://localhost:8087"
        );
        //int startIndex = new java.util.Random().nextInt(servers.size()); // ili koristi counter iz LoadBalancerService
        
        String firstServer = loadBalancerService.getNextServer();
        int firstServerIndex = servers.indexOf(firstServer);
        IOException lastException = null;

        for (int i = 0; i < servers.size(); i++) {
            //int serverIndex = (startIndex + i) % servers.size();
            int serverIndex = (firstServerIndex + i) % servers.size();
            String backendUrl = servers.get(serverIndex) + request.getRequestURI();

            java.net.URL url = new java.net.URL(backendUrl);
            System.out.println("Forwarding request to: " + backendUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod(request.getMethod());

            // Kopiraj zaglavlja
            Collections.list(request.getHeaderNames()).forEach(headerName -> {
                conn.setRequestProperty(headerName, request.getHeader(headerName));
            });

            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Kopiraj telo zahteva (osim GET)
            if (!request.getMethod().equalsIgnoreCase("GET")) {
                try (var out = conn.getOutputStream(); var in = request.getInputStream()) {
                    in.transferTo(out);
                }
            }

            try {
                int status = conn.getResponseCode();
                byte[] responseBody = conn.getInputStream().readAllBytes();

                HttpHeaders responseHeaders = new HttpHeaders();
                conn.getHeaderFields().forEach((key, value) -> {
                    if (key != null && value != null) responseHeaders.put(key, value);
                });

                if (status < 400) {
                    return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.valueOf(status));
                }
            } catch (IOException ex) {
                lastException = ex;
            }
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("All backend servers are unavailable.");
    }
}