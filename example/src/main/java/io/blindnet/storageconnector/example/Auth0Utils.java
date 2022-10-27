package io.blindnet.storageconnector.example;

import com.auth0.client.auth.AuthAPI;
import com.auth0.json.auth.UserInfo;

import java.util.Map;

public class Auth0Utils {
    private static final AuthAPI auth0 = new AuthAPI(
            System.getenv("AUTH0_DOMAIN"), System.getenv("AUTH0_ID"), System.getenv("AUTH0_SECRET")
    );

    public static String verifyTokenFromHeader(String header) throws IllegalArgumentException {
        if(header == null || !header.startsWith("Bearer "))
            throw new IllegalArgumentException("Invalid header");

        UserInfo userInfo;
        try {
            userInfo = auth0.userInfo(header.substring(7)).execute();
        } catch(Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }

        Map<String, Object> infoMap = userInfo.getValues();
        if(!infoMap.containsKey("email_verified") || !Boolean.TRUE.equals(infoMap.get("email_verified")))
            throw new IllegalArgumentException("Email not verified");

        return (String) infoMap.get("email");
    }
}
