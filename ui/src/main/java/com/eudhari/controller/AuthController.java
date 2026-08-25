package com.eudhari.controller;

import com.eudhari.dao.FirestoreUserDAO;
import com.eudhari.dao.UserDAO;
import com.eudhari.model.UserModel;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;

public class AuthController {
    private static final String API_KEY = "AIzaSyDOuoq7S2xD0q1FKCXjBiauejh8s2PfHIo";
    private static final String SIGNUP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
    private static final String SIGNIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    private final HttpClient httpClient;
    private final UserDAO userDAO;

    public AuthController() {
        this.httpClient = HttpClient.newHttpClient();
        this.userDAO = new FirestoreUserDAO();
    }

    public AuthController(UserDAO userDAO) {
        this.httpClient = HttpClient.newHttpClient();
        this.userDAO = userDAO;
    }

    public AuthResult registerUser(String email, String password, String name, String phone, String role, Map<String, Object> extraFields) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Email and password are required.");
        }
        if (name == null || name.trim().isEmpty()) {
            return new AuthResult(false, "Full name is required.");
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("email", email.trim());
            payload.put("password", password.trim());
            payload.put("returnSecureToken", true);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SIGNUP_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (response.statusCode() == 200 && jsonResponse.has("localId")) {
                String uid = jsonResponse.getString("localId");
                String normalizedRole = role != null ? role.trim().toLowerCase() : "customer";
                String createdAt = Instant.now().toString();

                String userCode = null;
                if (extraFields != null && extraFields.containsKey("userCode") && extraFields.get("userCode") != null) {
                    userCode = extraFields.get("userCode").toString();
                }
                if (userCode == null || userCode.trim().isEmpty()) {
                    String prefix = "U";
                    if ("shopkeeper".equalsIgnoreCase(normalizedRole)) {
                        prefix = "SH";
                    } else if ("admin".equalsIgnoreCase(normalizedRole)) {
                        prefix = "AD";
                    }
                    int count = userDAO.getUserCountByRole(normalizedRole);
                    userCode = String.format("%s%02d", prefix, count + 1);
                }

                UserModel user = new UserModel(uid, userCode, name.trim(), email.trim(), phone != null ? phone.trim() : "", normalizedRole, createdAt);

                if (extraFields != null) {
                    if (extraFields.containsKey("ownerName") && extraFields.get("ownerName") != null)
                        user.setOwnerName(extraFields.get("ownerName").toString());
                    if (extraFields.containsKey("shopName") && extraFields.get("shopName") != null)
                        user.setShopName(extraFields.get("shopName").toString());
                    if (extraFields.containsKey("shopAddress") && extraFields.get("shopAddress") != null)
                        user.setShopAddress(extraFields.get("shopAddress").toString());
                    if (extraFields.containsKey("gpayId") && extraFields.get("gpayId") != null)
                        user.setGpayId(extraFields.get("gpayId").toString());
                    if (extraFields.containsKey("businessCategory") && extraFields.get("businessCategory") != null)
                        user.setBusinessCategory(extraFields.get("businessCategory").toString());
                    if (extraFields.containsKey("storeImagePath") && extraFields.get("storeImagePath") != null)
                        user.setStoreImagePath(extraFields.get("storeImagePath").toString());
                }

                userDAO.saveUser(user);
                com.eudhari.config.UserSession.getInstance().setCurrentUser(user);

                if ("shopkeeper".equalsIgnoreCase(normalizedRole)) {
                    String sName = user.getShopName();
                    if (sName == null || sName.trim().isEmpty()) {
                        sName = user.getName() + " Store";
                    }
                    String sAddr = user.getAddress();
                    String sCat = user.getBusinessCategory();
                    String gpay = user.getGpayId();
                    com.eudhari.controller.shopkeppercontroller.ShopController.getInstance().createShop(
                            sName, uid, user.getName(), sAddr, sCat, gpay
                    );
                }

                return new AuthResult(true, "Registration successful!", user);
            } else {
                String errorMessage = parseFirebaseError(jsonResponse);
                return new AuthResult(false, errorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResult(false, "Network or server error during registration: " + e.getMessage());
        }
    }

    public AuthResult loginUser(String email, String password, String expectedRole) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new AuthResult(false, "Invalid email or password.");
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("email", email.trim());
            payload.put("password", password.trim());
            payload.put("returnSecureToken", true);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SIGNIN_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());

            if (response.statusCode() == 200 && jsonResponse.has("localId")) {
                String uid = jsonResponse.getString("localId");
                UserModel user = userDAO.getUserById(uid);

                if (user == null) {
                    return new AuthResult(false, "Invalid email or password.");
                }

                if ("INACTIVE".equalsIgnoreCase(user.getStatus())) {
                    return new AuthResult(false, "Access Denied: Your account is INACTIVE. Please contact Support / Admin.");
                }

                if (expectedRole != null && !expectedRole.trim().isEmpty()) {
                    String reqRole = expectedRole.trim().toLowerCase();
                    String userRole = user.getRole() != null ? user.getRole().trim().toLowerCase() : "";
                    if (!userRole.equals(reqRole)) {
                        return new AuthResult(false, "Invalid email or password.");
                    }
                }

                if ("shopkeeper".equalsIgnoreCase(user.getRole())) {
                    com.eudhari.model.ShopModel shop = com.eudhari.controller.shopkeppercontroller.ShopController.getInstance().getShopByOwnerId(user.getUid());
                    if (shop == null) {
                        String sName = user.getShopName();
                        if (sName == null || sName.trim().isEmpty()) {
                            sName = user.getName() + " Store";
                        }
                        shop = com.eudhari.controller.shopkeppercontroller.ShopController.getInstance().createShop(
                                sName, user.getUid(), user.getName(), user.getAddress(), user.getBusinessCategory(), user.getGpayId()
                        );
                    }
                    if (shop != null && "INACTIVE".equalsIgnoreCase(shop.getStatus())) {
                        return new AuthResult(false, "Access Denied: Your shop is INACTIVE. Please contact Support / Admin.");
                    }
                }

                com.eudhari.config.UserSession.getInstance().setCurrentUser(user);
                return new AuthResult(true, "Login successful!", user);
            } else {
                return new AuthResult(false, "Invalid email or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResult(false, "Invalid email or password.");
        }
    }

    private String parseFirebaseError(JSONObject jsonResponse) {
        if (jsonResponse.has("error")) {
            JSONObject errorObj = jsonResponse.getJSONObject("error");
            String message = errorObj.optString("message", "UNKNOWN_ERROR");

            if (message.contains("EMAIL_EXISTS")) {
                return "The email address is already in use by another account.";
            } else if (message.contains("INVALID_EMAIL")) {
                return "The email address is invalid.";
            } else if (message.contains("WEAK_PASSWORD") || message.contains("PASSWORD_TOO_SHORT")) {
                return "Password should be at least 6 characters.";
            } else if (message.contains("EMAIL_NOT_FOUND") || message.contains("INVALID_PASSWORD") || message.contains("INVALID_LOGIN_CREDENTIALS")) {
                return "Invalid email or password.";
            } else if (message.contains("USER_DISABLED")) {
                return "This user account has been disabled.";
            } else if (message.contains("TOO_MANY_ATTEMPTS")) {
                return "Too many failed attempts. Please try again later.";
            }
            return "Firebase Auth Error: " + message;
        }
        return "Authentication failed due to an unknown error.";
    }
}
