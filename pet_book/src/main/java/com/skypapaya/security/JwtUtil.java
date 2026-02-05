package com.skypapaya.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 轻量级 JWT 工具类（HS256），不依赖额外库
 */
public class JwtUtil {

    // 简单示例用的密钥，实际可以放到配置文件
    private static final String SECRET = "pet_book_demo_secret_change_me";
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 生成 token，默认有效期 7 天
    public static String generateToken(Long userId) {
        try {
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            payload.put("uid", userId);
            payload.put("exp", Instant.now().getEpochSecond() + 7 * 24 * 3600);

            String headerJson = MAPPER.writeValueAsString(header);
            String payloadJson = MAPPER.writeValueAsString(payload);

            String headerBase64 = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadBase64 = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
            String content = headerBase64 + "." + payloadBase64;

            String signature = sign(content);
            return content + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("生成 token 失败", e);
        }
    }

    // 解析 token，返回 userId；失败或过期返回 null
    public static Long parseUserId(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;

            String content = parts[0] + "." + parts[1];
            String expectedSig = sign(content);
            if (!expectedSig.equals(parts[2])) {
                return null;
            }

            String payloadJson = new String(base64UrlDecode(parts[1]), StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = MAPPER.readValue(payloadJson, Map.class);

            Object expObj = payload.get("exp");
            long now = Instant.now().getEpochSecond();
            if (expObj instanceof Number && now > ((Number) expObj).longValue()) {
                return null;
            }

            Object uidObj = payload.get("uid");
            if (uidObj instanceof Number) {
                return ((Number) uidObj).longValue();
            }
            if (uidObj instanceof String) {
                return Long.parseLong((String) uidObj);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String sign(String content) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGO);
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
        byte[] sig = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(sig);
    }

    private static String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static byte[] base64UrlDecode(String str) {
        return Base64.getUrlDecoder().decode(str);
    }
}

