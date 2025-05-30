package com.example.backofficeproject.Helpers;



import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    private static final String PASSWORD_PATTERN =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            System.out.println("Password is null or empty");
            return false;
        }

        boolean isValid = Pattern.compile(PASSWORD_PATTERN)
                .matcher(password)
                .matches();

        System.out.println("Validating password: " + password + " -> " + isValid);
        return isValid;
    }
}
