package info.serxan.trackerprojectmanager.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class UserFirestore extends Firestore {

    /**
     * Firestore collection name.
     */
    private final String collectionName = "users";
    private CollectionReference usersCollection;

    /**
     * Firestore collection reference.
     */
    public UserFirestore() {
        super();
        usersCollection = db.collection(collectionName);
    }

    /**
     * public getter @usersCollection.
     * @return
     */
    public CollectionReference getUsersCollection() {
        return usersCollection;
    }

    /**
     * Login query. Find an user by email and password.
     * @param email
     * @param password
     * @return
     */
    public Task findUserByEmailAndPassword(String email, String password)  {
        // --> Query.
        Query query = usersCollection
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .limit(1);

        // --> Fetch.
        return query.get();
    }

    /**
     * Check if the given email is already in use.
     * @param email
     * @return
     */
    public Task checkIfEmailIsTaken(String email)  {
        // --> Query.
        Query query = usersCollection
                .whereEqualTo("email", email)
                .limit(1);

        // --> Fetch.
        return query.get();
    }

}
