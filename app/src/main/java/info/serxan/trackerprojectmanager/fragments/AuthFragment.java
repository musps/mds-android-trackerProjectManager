package info.serxan.trackerprojectmanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import info.serxan.trackerprojectmanager.R;
import info.serxan.trackerprojectmanager.firestore.UserFirestore;
import info.serxan.trackerprojectmanager.models.UserModel;
import info.serxan.trackerprojectmanager.tools.ApplicationTool;

public class AuthFragment extends Fragment  {

    /**
     * Application context.
     */
    private ApplicationTool app;

    /**
     * View item.
     */
    private AutoCompleteTextView mEmailView;

    /**
     * View item.
     */
    private EditText  mPasswordView;

    /**
     * Fragment listener.
     */
    private OnFragmentInteractionListener mListener;

    /**
     * Constructor
     */
    public AuthFragment() {
    }

    /**
     * Instantiate the fragment.
     *
     * @param param1
     * @param param2
     * @return
     */
    public static AuthFragment newInstance(String param1, String param2) {
        AuthFragment fragment = new AuthFragment();
        return fragment;
    }

    /**
     * onCreate
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ApplicationTool) getActivity().getApplicationContext();
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

        mEmailView = (AutoCompleteTextView) getView().findViewById(R.id.email);
        mPasswordView = (EditText) getView().findViewById(R.id.password);

        setButtonsActions();
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
        return inflater.inflate(R.layout.fragment_auth, container, false);
    }

    /**
     * onAttach
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        void onUserLogged();
    }

    /**
     * Check is the email is a valid one.
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    /**
     * Check if the password respect the rules.
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 2;
    }

    /**
     * Set buttons click actions.
     */
    public void setButtonsActions() {

        // --> Register.
        Button btnRegister = (Button) getView().findViewById(R.id.button_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields("register");
            }
        });

        // --> Login.
        Button btnLogin = (Button) getView().findViewById(R.id.button_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFields("login");
            }
        });

    }

    /**
     * Check form fields.
     *
     * @param target
     */
    private void checkFields(String target) {
        // --> Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // --> Input values.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // --> Check is empty value.
        if (! isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
        }
        if (! isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }

        // --> Send API request.
        if (isEmailValid(email) && isPasswordValid(password)) {
            if (TextUtils.equals(target, "register")) {
                System.out.println("btn cick : register");
                onRegister(email, password);
            } else if (TextUtils.equals(target, "login")) {
                System.out.println("btn cick : login");
                onLogin(email, password);
            }
        }

    }

    /**
     * onLogin
     * Check if an user exist in the firestore database with the credentials combination.
     * If there is an error an alert is shown.
     *
     * @param email
     * @param password
     */
    public void onLogin(String email, String password) {
        UserFirestore userFirestore = new UserFirestore();
        Task findUserByEmailAndPassword = userFirestore.findUserByEmailAndPassword(email, password);
        findUserByEmailAndPassword.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // --> No match. Throw error.
                    if (task.getResult().size() == 0) {
                        // --> Login error.
                        app.showAlert(
                                getContext(),
                                getString(R.string.alert_error),
                                getString(R.string.alert_login_message));
                    } else {
                        // --> User logged.
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        userModel.setId(documentSnapshot.getId());
                        app.loggedUser = userModel;
                        // --> Callback Activity.
                        mListener.onUserLogged();
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
     * onRegister
     * Check if an user exist in the firestore database with the
     * credentials combination or the account is created.
     * If there is an error an alert is shown.
     *
     * @param email
     * @param password
     */
    public void onRegister(String email, String password) {
        final UserModel userModel = new UserModel(email, password, "");
        final UserFirestore userFirestore = new UserFirestore();

        // --> Check is email is available.
        Task checkIfEmailIsTaken = userFirestore.checkIfEmailIsTaken(email);
        checkIfEmailIsTaken.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // --> Match. Throw error.
                    if (task.getResult().size() == 1) {
                        // --> Error.
                        app.showAlert(
                                getContext(),
                                getString(R.string.alert_error),
                                getString(R.string.alert_register_message));
                    } else {
                        // --> Success.
                        userFirestore.getUsersCollection().add(userModel)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                                        userModel.setId(documentReference.getId());
                                        app.loggedUser = userModel;
                                        // --> Callback Activity.
                                        mListener.onUserLogged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Error adding document");
                                    }
                                });
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

}
