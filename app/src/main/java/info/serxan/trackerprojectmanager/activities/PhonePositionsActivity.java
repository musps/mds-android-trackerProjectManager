package info.serxan.trackerprojectmanager.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.adapter.RegisteredSpyAdapter;
import info.serxan.trackerprojectmanager.firestore.PositionFirestore;
import info.serxan.trackerprojectmanager.models.PositionModel;
import info.serxan.trackerprojectmanager.tools.ApplicationTool;

public class PhonePositionsActivity extends AppCompatActivity {

    /**
     * Application context.
     */
    private ApplicationTool app;

    /**
     * Extras parameters.
     */
    private Bundle extras;

    /**
     * Selected phone id.
     */
    private String phoneId;

    /**
     * Positions items.
     */
    private ArrayList<PositionModel> itemList;

    /**
     * List adapter.
     */
    private RegisteredSpyAdapter adapter;

    /**
     * onCreate
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_positions);

        app = (ApplicationTool) getApplicationContext();
        itemList = new ArrayList<PositionModel>();
        adapter = new RegisteredSpyAdapter(this, itemList);

        // --> Get the argument item.
        extras = getIntent().getExtras();
        PositionModel item = (PositionModel) extras.getSerializable("item");
        phoneId = item.getPhoneId();

        Button btnUpdate = (Button) findViewById(R.id.btn_update_act);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData(true);
            }
        });

        init();
    }

    /**
     * Load positions in the firestore database.
     *
     * @param isUpdate - handle 2 actions :
     *                 - true : will trigger the update method
     *                 - false : will load default data.
     */
    public void loadData(boolean isUpdate) {
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
                            getApplicationContext(),
                            getString(R.string.alert_error_firebase_title),
                            getString(R.string.alert_error_firebase_message) + " " + task.getException());
                }
            }
        });
    }

    /**
     * Compare 2 PositionModel object and add one item in the itemList array
     * if the phone id match is found.
     *
     * @param item
     */
    public void checkAndAddItem(PositionModel item) {
        if (new String(item.getPhoneId()).equals(new String(item.getPhoneId()))) {
            itemList.add(item);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Initialize default.
     */
    public void init() {
        ListView container = (ListView) findViewById(R.id.lv_phone_positions_list);
        container.setAdapter(adapter);
        loadData(false);
    }

    /**
     * We check if there is new item in the database and show an alert
     * with the new item added.
     *
     * @param items
     */
    public void checkAndUpdateItem(List<DocumentSnapshot> items) {
        int nbNew = 0;

        for (int i = 0; i < items.size(); i = i + 1) {
            PositionModel item = items.get(i).toObject(PositionModel.class);

            if (new String(item.getPhoneId()).equals(phoneId)) {
                // --> Empty list
                if (itemList.size() == 0) {
                    itemList.add(item);
                    nbNew = nbNew + 1;
                } else {
                    boolean _isDistinct = true;
                    for (int y = 0; y < itemList.size(); y = y + 1) {
                        PositionModel _oldItem = itemList.get(y);

                        if (new String(_oldItem.getCreatedAt()).equals(new String(item.getCreatedAt()))) {
                            _isDistinct = false;
                        } else if ((y + 1) == itemList.size() && _isDistinct) {
                            itemList.add(item);
                            nbNew = nbNew + 1;
                        }
                    }
                }
            }

            if ((i + 1) == items.size()) {
                app.showAlert(
                        this,
                        getString(R.string.alert_info_update_title),
                        (nbNew + " " + getString(R.string.alert_info_update_message))
                );
            }
        }
    }

}
