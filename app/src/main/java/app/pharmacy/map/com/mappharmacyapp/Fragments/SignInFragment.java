package app.pharmacy.map.com.mappharmacyapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.Activities.MainActivity;
import app.pharmacy.map.com.mappharmacyapp.Activities.PharmacyOrdersActivity;
import app.pharmacy.map.com.mappharmacyapp.Activities.StartActivity;
import app.pharmacy.map.com.mappharmacyapp.Activities.UserMapActivity;
import app.pharmacy.map.com.mappharmacyapp.Activities.UserTypeActivity;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.Imageutility;
import app.pharmacy.map.com.mappharmacyapp.Utils.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInFragment extends Fragment {

    private final String TAG = SignInFragment.class.getSimpleName();
    // Vars
    String email, password;
    boolean isValid = false;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    // Views
    @BindView(R.id.fragment_sign_in_email_input_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.fragment_sign_in_email_edit_text)
    TextInputEditText emailTxt;
    @BindView(R.id.fragment_sign_in_password_input_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.fragment_sign_in_password_edit_text)
    TextInputEditText passwordTxt;
    @BindView(R.id.fragment_sign_in_button)
    Button signInBtn;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    @OnClick(R.id.fragment_sign_in_button)
    void goToMain() {
        CheckValidation();
        if (isValid)
            signIn();
    }

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }


    private void signIn() {
        toggleLayout(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    getUserdata();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toggleLayout(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("login", e.toString());
                toggleLayout(true);
            }
        });
    }

    private void CheckValidation() {
        boolean emailFlag, passwordFlag;
        emailFlag = validateInput(emailTxt, emailLayout, Validation.VALIDATE_EMAIL, getString(R.string.empty_email), getString(R.string.invalid_email));
        passwordFlag = validateInput(passwordTxt, passwordLayout, Validation.VALIDATE_PASSWORD, getString(R.string.empty_password), getString(R.string.invalid_password));

        if (emailFlag && passwordFlag) {
            email = getInput(emailTxt);
            password = getInput(passwordTxt);
            isValid = true;
        } else {
            isValid = false;
        }
    }

    private void getUserdata() {
        String uid = null;
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null)
        uid = currentUser.getUid();
        // typeId = 0 for pharmacy
        // typeId = 1 for customer
        if (UserTypeActivity.typeId == 0) {
            mRef.child(AppConfig.PHARMACY).child(Objects.requireNonNull(uid)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Intent intent = new Intent(getActivity(), PharmacyOrdersActivity.class);
                        startActivity(intent);
                        Objects.requireNonNull(getActivity()).finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        toggleLayout(true);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.invalid_type), Toast.LENGTH_LONG).show();
                        toggleLayout(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "load user data:onCancelled", databaseError.toException());
                }
            });
        } else {
            mRef.child(AppConfig.USERS).child(Objects.requireNonNull(uid)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Intent intent = new Intent(getActivity(), UserMapActivity.class);
                        startActivity(intent);
                        Objects.requireNonNull(getActivity()).finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        toggleLayout(true);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.invalid_type), Toast.LENGTH_LONG).show();
                        toggleLayout(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "load user data:onCancelled", databaseError.toException());
                }
            });
        }
    }

    private boolean validateInput(TextInputEditText textInputEditText, TextInputLayout textInputLayout, int funNum, String emptyFieldMsg, String errorMsg) {
        String text = getInput(textInputEditText);
        if (text.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(emptyFieldMsg);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private String getInput(TextInputEditText textInputEditText) {
        return Objects.requireNonNull(textInputEditText.getText()).toString();
    }

    private void toggleLayout(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
        }
    }

}
