package backend.nourishnet.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseAuth firebaseAuth() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream in = getClass().getResourceAsStream("/nourishnetfiapproject-firebase-adminsdk-fbsvc-91e2e151b9.json")) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }
        return FirebaseAuth.getInstance();
    }
}
