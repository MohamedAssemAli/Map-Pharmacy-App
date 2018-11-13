package app.pharmacy.map.com.mappharmacyapp.Utils;

import android.support.design.widget.TextInputEditText;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Validation {

    public boolean isEditTextEmpty(TextInputEditText textInputEditText) {
        return textInputEditText.getText().toString().isEmpty();
    }

    private boolean validateName(String name) {
        return name.length() >= 2;
    }

    private boolean validateEmail(String email) {
        return email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    private boolean validatePhone(String phone) {
        return phone.matches("^[+\\d{3}]?\\d{11,20}$");
    }

    private boolean validatePassword(String password) {
        return password.length() >= 6;
    }

    public boolean validateCheckBox(CheckBox checkBox) {
        return checkBox.isChecked();
    }

    public boolean validateRadioGroup(RadioGroup radioGroup) {
        return radioGroup.getCheckedRadioButtonId() != -1;
    }

    public boolean validate(int funNum, String word) {
        switch (funNum) {
            case 0:
                return validateName(word);
            case 1:
                return validateName(word);
            case 2:
                return validateEmail(word);
            case 3:
                return validatePhone(word);
            case 4:
                return validatePassword(word);
            default:
                return false;
        }
    }


    public static final int VALIDATE_NAME = 0;
    public static final int VALIDATE_USERNAME = 1;
    public static final int VALIDATE_EMAIL = 2;
    public static final int VALIDATE_PHONE = 3;
    public static final int VALIDATE_PASSWORD = 4;

}

