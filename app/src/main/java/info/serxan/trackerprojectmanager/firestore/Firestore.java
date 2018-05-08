package info.serxan.trackerprojectmanager.firestore;

import com.google.firebase.firestore.FirebaseFirestore;

public class Firestore {

    /**
     * Firestore instance.
     */
    public static FirebaseFirestore db;

    /**
     * Initialize the firestore instance.
     */
    public Firestore() {
        db = FirebaseFirestore.getInstance();
    }

}
