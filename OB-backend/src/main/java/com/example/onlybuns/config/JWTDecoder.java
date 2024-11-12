package com.example.onlybuns.config;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.example.onlybuns.domain.JWTUser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class JWTDecoder {
    private static final String SECRET_KEY = "12345";
    private static final String ALGORITHM = "HmacSHA256";

    public static String createToken(JWTUser user, long expirationTimeMillis) throws Exception {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = String.format("{\"id\":%d,\"username\":\"%s\",\"role\":\"%s\",\"exp\":%d}",
                user.getId(), user.getUsername(), user.getRole(), System.currentTimeMillis() + expirationTimeMillis);

        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());

        String signature = sign(encodedHeader + "." + encodedPayload);

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public static JWTUser verifyToken(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return null;
        }

        String headerPayload = parts[0] + "." + parts[1];
        String signature = parts[2];

        String expectedSignature = sign(headerPayload);

        if (!expectedSignature.equals(signature)) {
            return null;
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        Map<String, Object> payload = jsonToMap(payloadJson);

        long exp = ((Number) payload.get("exp")).longValue();
        if (System.currentTimeMillis() > exp) {
            return null;
        }

        int id = (int) payload.get("id");
        String username = (String) payload.get("username");
        String role = (String) payload.get("role");

        return new JWTUser(id, username, role);
    }

    private static String sign(String data) throws Exception {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }

    private static Map<String, Object> jsonToMap(String json) {
        Map<String, Object> map = new HashMap<>();
        json = json.substring(1, json.length() - 1);
        String[] pairs = json.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            String key = keyValue[0].replaceAll("\"", "").trim();
            String value = keyValue[1].replaceAll("\"", "").trim();

            if (key.equals("id")) {
                map.put(key, Integer.parseInt(value));
            } else if (key.equals("exp")) {
                map.put(key, Long.parseLong(value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
}
