package artifact.Backend.Services.Impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import artifact.Backend.Models.GoogleUser;
import artifact.Backend.Services.Interfaces.IOAuthService;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Service implementation for handling the Google OAuth 2.0 Authorization Code Flow.
 * Includes PKCE (Proof Key for Code Exchange) support for enhanced security.
 */
public class GoogleOAuthService implements IOAuthService {

    private static final String CLIENT_ID = "747969748852-v49k8l6lloskvhe7iueaujo30pbsof80.apps.googleusercontent.com";
    
    private static String CLIENT_SECRET;
    
    private static final String REDIRECT_URI = "http://localhost:8888/callback";

    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email"
    );

    static {
        Dotenv dotenv = Dotenv.load();
        CLIENT_SECRET = dotenv.get("GOOGLE_CLIENT_SECRET");
    }

    /**
     * Generates the Google Login URL including scopes and PKCE challenges.
     * @return The formatted URL string to redirect the user's browser to.
     */
    public String getAuthUrl() {
        try {
            String scope = URLEncoder.encode(String.join(" ", SCOPES), StandardCharsets.UTF_8);
            return "https://accounts.google.com/o/oauth2/auth?"
                    + "client_id=" + CLIENT_ID
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                    + "&response_type=code"
                    + "&scope=" + scope
                    + "&access_type=offline"
                    + "&prompt=consent"
                    // Attach PKCE Challenge derived from the Verifier
                    + "&code_challenge=" + GooglePKCE.codeChallenge
                    + "&code_challenge_method=S256";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exchanges the authorization code returned by Google for an Access Token,
     * then uses that token to fetch the User's Profile (Email/Name).
     *
     * @param authCode The code received on the callback URL.
     * @return GoogleUser DTO containing name and email.
     * @throws Exception If network request or JSON parsing fails.
     */
    public GoogleUser fetchUserFromAuthCode(String authCode) throws Exception {

        OkHttpClient client = new OkHttpClient();

        // 1. Build POST request to exchange Code for Token
        RequestBody body = new FormBody.Builder()
                .add("code", authCode)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("redirect_uri", REDIRECT_URI)
                .add("grant_type", "authorization_code")
                .add("code_verifier", GooglePKCE.codeVerifier) // Send the original Verifier to prove identity
                .build();

        Request request = new Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Token exchange failed: " + response.body().string());
            }

            String json = response.body().string();
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            String accessToken = obj.get("access_token").getAsString();

            // 2. Use Access Token to fetch User Info
            Request userReq = new Request.Builder()
                    .url("https://www.googleapis.com/oauth2/v2/userinfo")
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .build();

            try (Response userRes = client.newCall(userReq).execute()) {
                String userJson = userRes.body().string();
                JsonObject userObj = JsonParser.parseString(userJson).getAsJsonObject();

                return new GoogleUser(
                        userObj.get("name").getAsString(),
                        userObj.get("email").getAsString()
                );
            }
        }
    }
}

/**
 * Helper class to generate PKCE (Proof Key for Code Exchange) values.
 * Ensures that the entity requesting the code is the same one exchanging it.
 */
class GooglePKCE {
    public static String codeVerifier;
    public static String codeChallenge;

    static {
        codeVerifier = generateCodeVerifier();
        try {
        codeChallenge = generateCodeChallenge(codeVerifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Creates a random high-entropy string
    private static String generateCodeVerifier() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // SHA-256 Hashes the verifier to create the challenge
    private static String generateCodeChallenge(String verifier) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(verifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}