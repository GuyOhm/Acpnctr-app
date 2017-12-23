package com.acpnctr.acpnctr.utils;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Util class for firebase
 */

public class FirebaseUtils {

    /**
     * This method allow to get a firestore document's id from a ViewHolder displayed
     * in a RecyclerView by an Adapter
     *
     * @param adapter which is displaying data in the RecyclerView
     * @param position of the ViewHolder
     * @return the id of the firestore document
     */
    public static String getIdFromSnapshot(FirestoreRecyclerAdapter adapter, int position) {
        ObservableSnapshotArray observableSnapshotArray = adapter.getSnapshots();
        DocumentSnapshot snapshot = (DocumentSnapshot) observableSnapshotArray.getSnapshot(position);
        return snapshot.getId();
    }

}
