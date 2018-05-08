package info.serxan.trackerprojectmanager.firestore;

import com.google.firebase.firestore.CollectionReference;

public class PositionFirestore extends Firestore {

    /**
     * Firestore collection name.
     */
    private final String collectionName = "positions";

    /**
     * Firestore collection reference.
     */
    private CollectionReference positionsCollection;

    /**
     * Initialize the collection reference.
     */
    public PositionFirestore() {
        super();
        positionsCollection = db.collection(collectionName);
    }

    /**
     * public getter @positionsCollection.
     * @return
     */
    public CollectionReference getPositionsCollection() {
        return positionsCollection;
    }

}
