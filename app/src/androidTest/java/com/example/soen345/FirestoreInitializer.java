package com.example.soen345;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreInitializer {
    private static FirebaseFirestore instance;
    static volatile boolean isInitialized = false;

    public synchronized static FirebaseFirestore getInstance(){
        if (!isInitialized){
            instance = FirebaseFirestore.getInstance();
            instance.useEmulator("10.0.2.2", 8080);
            isInitialized = true;
        }
        return instance;
    }

}
