package com.example.refresh.Authentication;
/*
Description:
    The purpose of this class is to register new accounts into the local SQLite database.

Specific Features:
    Inserts new user into "user_table"

Documentation and Code Written By:
    Steven Yen
    Staples Intern Summer 2019
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.example.refresh.DatabaseHelper.DatabaseHelper;
import com.example.refresh.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class AccountCreation extends AppCompatActivity {

    /*
    Methods that are called when the activity launches.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);
        setSubmitButton();
    }

    /*
    Setup of the submit button.
     */
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
            createNewUser(userInput,passInput,fNameInput,lNameInput);
        });
    }

    /*
    Creates a new user if all arguments are valid.
     */
    private void createNewUser(String userInput, String passInput, String fNameInput, String lNameInput){
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
    }

    /*
    Checks if a string is not empty.
     */
    private boolean validInput(String input){
        if(!input.equals("")){
            return true;
        }
        return false;
    }
}
