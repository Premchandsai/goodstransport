package com.p2p.transport.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        String firebaseCredentialsPath = System.getenv("FIREBASE_CREDENTIALS_FILE");
        if (firebaseCredentialsPath == null) {
            throw new IOException("Firebase credentials file path not set in FIREBASE_CREDENTIALS_FILE environment variable");
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(firebaseCredentialsPath));
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(options);
        return FirebaseMessaging.getInstance(app);
    }
}