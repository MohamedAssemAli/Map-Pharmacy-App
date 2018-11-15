package app.pharmacy.map.com.mappharmacyapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.Activities.PharmacyMapActivity;
import app.pharmacy.map.com.mappharmacyapp.Activities.UserMapActivity;
import app.pharmacy.map.com.mappharmacyapp.Activities.UserTypeActivity;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.User;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {

    private final String TAG = SignUpFragment.class.getSimpleName();
    // Vars
    private String email, username, password;
    boolean isValid = false;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    // Views
    @BindView(R.id.fragment_sign_up_email_input_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.fragment_sign_up_email_edit_text)
    TextInputEditText emailTxt;
    @BindView(R.id.fragment_sign_up_username_input_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.fragment_sign_up_username_edit_text)
    TextInputEditText usernameTxt;
    @BindView(R.id.fragment_sign_up_password_input_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.fragment_sign_up_password_edit_text)
    TextInputEditText passwordTxt;
    @BindView(R.id.fragment_sign_up_button)
    Button signUpBtn;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    @OnClick(R.id.fragment_sign_up_button)
    void goToMain() {
        CheckValidation();
        if (isValid)
            signUp();
    }

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        if (UserTypeActivity.typeId == 0) {
            signUpBtn.setText(getString(R.string.next));
        }
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    private void signUp() {
        final User user = new User(username, email, password);
        if (UserTypeActivity.typeId == 0) {
            Intent intent = new Intent(getActivity(), PharmacyMapActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(AppConfig.INTENT_KEY, user);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            toggleLayout(false);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        user.setUid(uid);
                        mRef.child(AppConfig.USERS).child(uid).setValue(user);
                        Intent intent = new Intent(getActivity(), UserMapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Objects.requireNonNull(getActivity()).finish();
                        hideKeyboard();
                        toggleLayout(true);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    toggleLayout(true);
                }
            });
        }
    }

    private void CheckValidation() {
        boolean emailFlag, usernameFlag, passwordFlag;
        emailFlag = validateInput(emailTxt, emailLayout, Validation.VALIDATE_EMAIL, getString(R.string.empty_email), getString(R.string.invalid_email));
        usernameFlag = validateInput(usernameTxt, usernameLayout, Validation.VALIDATE_NAME, getString(R.string.empty_username), getString(R.string.invalid_username));
        passwordFlag = validateInput(passwordTxt, passwordLayout, Validation.VALIDATE_PASSWORD, getString(R.string.empty_password), getString(R.string.invalid_password));

        if (emailFlag && usernameFlag && passwordFlag) {
            email = getInput(emailTxt);
            username = getInput(usernameTxt);
            password = getInput(passwordTxt);
            isValid = true;
        } else {
            isValid = false;
        }
    }

    private boolean validateInput(TextInputEditText textInputEditText, TextInputLayout textInputLayout, int funNum, String emptyFieldMsg, String errorMsg) {
        String text = getInput(textInputEditText);
        Validation validation = new Validation();
        if (text.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(emptyFieldMsg);
            return false;
        } else {
            if (validation.validate(funNum, text)) {
                textInputLayout.setErrorEnabled(false);
                return true;
            } else {
                textInputLayout.setErrorEnabled(true);
                textInputEditText.setError(errorMsg);
                return false;
            }
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

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputManager).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
