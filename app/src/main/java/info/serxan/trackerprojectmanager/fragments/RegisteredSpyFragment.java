package info.serxan.trackerprojectmanager.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.activities.PhonePositionsActivity;
import info.serxan.trackerprojectmanager.adapter.RegisteredSpyAdapter;
import info.serxan.trackerprojectmanager.firestore.PositionFirestore;
import info.serxan.trackerprojectmanager.models.PositionModel;
import info.serxan.trackerprojectmanager.tools.ApplicationTool;

import java.util.ArrayList;
import java.util.List;

public class RegisteredSpyFragment extends Fragment {

    /**
     * Application context.
     */
    private ApplicationTool app;

    /**
     * Fragment listener.
     */
    private OnListFragmentInteractionListener mListener;

    /**
     * Position items.
     */
    private static ArrayList<PositionModel> itemList;

    /**
     * List adapter.
     */
    private static RegisteredSpyAdapter adapter;

    /**
     * List view.
     */
    private ListView mListView;

    /**
     * Constructor.
     */
    public RegisteredSpyFragment() {
    }

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ApplicationTool) getActivity().getApplicationContext();
        itemList = new ArrayList<PositionModel>();
        adapter = new RegisteredSpyAdapter(getContext(), itemList);
    }

    /**
     * onViewCreated
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    /**
     * onCreateView
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registered_spy, container, false);
        mListView = view.findViewById(R.id.lv_positions_list);

        Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData(true);
            }
        });
        return view;
    }

    /**
     * onAttach
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
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
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PositionModel item);
    }

    /**
     * loadData
     *
     * @param isUpdate
     */
    public void loadData(Boolean isUpdate) {
        PositionFirestore positionFirestore = new PositionFirestore();

        positionFirestore.getPositionsCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (isUpdate) {
                        checkAndUpdateItem(task.getResult().getDocuments());
                    } else {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            checkAndAddItem(doc.toObject(PositionModel.class));
                        }
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
     * checkAndUpdateItem
     *
     * @param items
     */
    public void checkAndUpdateItem(List<DocumentSnapshot> items) {
        int nbNew = 0;

        for (int i = 0; i < items.size(); i = i + 1) {
            PositionModel item = items.get(i).toObject(PositionModel.class);
            // --> Empty list
            if (itemList.size() == 0) {
                itemList.add(item);
                nbNew = nbNew + 1;
            } else {
                boolean _isDistinct = true;
                for (int y = 0; y < itemList.size(); y = y + 1) {
                    PositionModel _oldItem = itemList.get(y);

                    if (new String(_oldItem.getPhoneId()).equals(new String(item.getPhoneId()))) {
                        _isDistinct = false;
                    } else if ((y + 1) == itemList.size() && _isDistinct) {
                        itemList.add(item);
                        nbNew = nbNew + 1;
                    }
                }
            }

            if ((i + 1) == items.size()) {
                app.showAlert(
                        getContext(),
                        getString(R.string.alert_info_update_title),
                        (nbNew + " " + getString(R.string.alert_info_update_message))
                );
            }
        }
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
            adapter.notifyDataSetChanged();
        } else {
            for(int i = 0; i < itemList.size(); i = i + 1) {
                if (new String(itemList.get(i).getPhoneId()).equals(new String(item.getPhoneId()))) {
                    isDistinct = false;
                } else if ((i + 1) == itemList.size() && isDistinct) {
                    itemList.add(item);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * init
     *
     */
    public void init() {
        loadData(false);

        mListView.setAdapter(adapter);

        // -- On List View -> ITEM CLICK
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PositionModel item = itemList.get(i);
                showItemActivity(item);
            }
        });

    }

    /**
     * showItemActivity
     *
     * @param item
     */
    public void showItemActivity(PositionModel item) {
        Intent intent = new Intent(getActivity(), PhonePositionsActivity.class);
        // -- Put extra data.
        intent.putExtra("item", item);
        // -- Launch Activity.
        startActivityForResult(intent, 1049);
    }

}
