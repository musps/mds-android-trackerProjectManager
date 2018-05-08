package info.serxan.trackerprojectmanager.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.firestore.PositionFirestore;
import info.serxan.trackerprojectmanager.models.PositionModel;
import info.serxan.trackerprojectmanager.tools.ApplicationTool;

public class MapCurrentSpyFragment extends SupportMapFragment implements OnMapReadyCallback {

    /**
     * Application context.
     */
    private ApplicationTool app;

    /**
     * Fragment listener.
     */
    private OnFragmentInteractionListener mListener;

    /**
     * Positions items.
     */
    private static ArrayList<PositionModel> itemList;

    /**
     * Google map view.
     */
    private GoogleMap mMap;

    /**
     * Constructor.
     */
    public MapCurrentSpyFragment() {
    }

    /**
     * onCreate
     * Initialize the application context & itemList.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ApplicationTool) getActivity().getApplicationContext();
        itemList = new ArrayList<PositionModel>();
    }

    /**
     * onActivityCreated
     * Initialize google map.
     *
     * @param bundle
     */
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getMapAsync(this);
    }

    /**
     * onButtonPressed
     *
     * @param uri
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * onAttach
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * onDetach
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interface listener.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * loadData
     *
     */
    public void loadData() {
        PositionFirestore positionFirestore = new PositionFirestore();

        positionFirestore.getPositionsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                        checkAndAddItem(doc.toObject(PositionModel.class));
                    }
                } else {
                    // --> Error Firebase.
                    app.showAlert(
                            getContext(),
                            getString(R.string.alert_error_firebase_title),
                            getString(R.string.alert_error_firebase_message) + " " + task.getException());
                }
            }
        });
    }

    /**
     * checkAndAddItem
     *
     * @param item
     */
    public void checkAndAddItem(PositionModel item) {
        boolean isDistinct = true;
        if (itemList.size() == 0) {
            itemList.add(item);
            addItemToMap(item);

        } else {
            for(int i = 0; i < itemList.size(); i = i + 1) {
                if (! new String(itemList.get(i).getPhoneId()).equals(new String(item.getPhoneId()))) {
                    itemList.add(item);
                    addItemToMap(item);
                }
            }
        }
    }

    /**
     * onMapReady
     *
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadData();
    }

    /**
     * addItemToMap
     *
     * @param item
     */
    public void addItemToMap(PositionModel item) {
        LatLng marker = new LatLng(
                item.getLatitude(),
                item.getLongitude()

        );

        String markerTitle = item.getPhoneBrand() + " " + item.getPhoneId();
        String markerSnippet = getContext().getString(R.string.va_itemCreatedAt) + " " + item.getCreatedAt();

        mMap.addMarker(new MarkerOptions()
                .position(marker)
                .title(markerTitle)
                .snippet(markerSnippet)
        );

        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
    }

}
