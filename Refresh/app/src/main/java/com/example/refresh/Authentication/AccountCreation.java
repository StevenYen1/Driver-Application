package com.example.refresh.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class AccountCreation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        setSubmitButton();
    }

    private void setSubmitButton(){
        EditText username = findViewById(R.id.account_user);
        EditText password = findViewById(R.id.account_password);
        EditText firstName = findViewById(R.id.account_firstname);
        EditText lastName = findViewById(R.id.account_lastname);
        FancyButton submit = findViewById(R.id.account_submit);
        submit.setOnClickListener(v -> {
            String userInput = username.getText().toString();
            String passInput = password.getText().toString();
            String fNameInput = firstName.getText().toString();
            String lNameInput = lastName.getText().toString();

            if(validInput(userInput)
                    && validInput(passInput)
                    && validInput(fNameInput)
                    && validInput(lNameInput)){
                DatabaseHelper databaseHelper = new DatabaseHelper(AccountCreation.this);
                if(!databaseHelper.userExists(userInput)){
                    databaseHelper.createUser(userInput, passInput, fNameInput, lNameInput);
                    Intent intent = new Intent(AccountCreation.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(AccountCreation.this, "Username unavailable.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(AccountCreation.this, "Please enter all information.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validInput(String input){
        if(!input.equals("")){
            return true;
        }
        return false;
    }
}
