package com.eudhari.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class Firebaseinitialization {

    static {
        getFirebaseConfig();
    }
    
    public static synchronized void getFirebaseConfig(){
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            InputStream serviceAccount = Firebaseinitialization.class.getClassLoader().getResourceAsStream("java2026.json");
            if (serviceAccount == null) {
                serviceAccount = Thread.currentThread().getContextClassLoader().getResourceAsStream("java2026.json");
            }
            if (serviceAccount == null) {
                File file = new File("ui/src/main/resources/java2026.json");
                if (!file.exists()) {
                    file = new File("src/main/resources/java2026.json");
                }
                if (!file.exists()) {
                    file = new File("demo/src/main/resources/java2026.json");
                }
                if (file.exists()) {
                    serviceAccount = new FileInputStream(file);
                }
            }

            if (serviceAccount == null) {
                System.err.println("[Firebaseinitialization] Could not locate java2026.json credential file.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            FirebaseApp.initializeApp(options);
            System.out.println("[Firebaseinitialization] Firebase initialized successfully!");

        } catch(Exception e){
            System.err.println("[Firebaseinitialization] Exception during Firebase setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Firestore getFireStore(){
        if (FirebaseApp.getApps().isEmpty()) {
            getFirebaseConfig();
        }
        return FirestoreClient.getFirestore();
    }
}

