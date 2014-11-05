package com.example.patrick.tasktracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shwaat on 11/3/2014.
 */
public class Activity_LoginMain extends Activity {
    Date checkpoint;
    EditText username;
    EditText password;
    TextView alert;
    public final static String EXTRA_USERNAME = "com.example.patrick.tasktracker.USERNAME";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Parse.initialize(this, "6yEsCcvYy5ym7rmRKWleVy5A9jc2wHFz6aEL3Czs", "t3h3S0090VVBwdw0zasj5J0b28dLe9xebL5nIfKw");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        SyncData();
    }


    public void LogIn(View view){
        final Intent intent = new Intent(this, Activity_AdminMain.class);
        username = (EditText)findViewById(R.id.login_main_username_field);
        password = (EditText)findViewById(R.id.login_main_password_field);
        alert = (TextView)findViewById(R.id.login_main_greeting);
        DatabaseHandler db = new DatabaseHandler(this);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Employee");
        query.whereEqualTo("First_name", username.getText().toString());
        query.whereEqualTo("Password", password.getText().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null && parseObjects.size() > 0){

                    Log.d("Login", parseObjects.get(0).get("First_name").toString());// + " " + cursor.getString(1));

                    // if(username.getText().toString() == cursor.getString(0)){// && password.getText().toString() == cursor.getString(1)){
                        intent.putExtra(EXTRA_USERNAME,parseObjects.get(0).get("First_name").toString());

                        startActivity(intent);

                        alert.setText("Login Successful!");

                }else{
                    Log.d("Login", username.getText().toString() + " " + password.getText().toString());
                    Log.d("Login", "Incorrect username or password");
                }

            }
        });
        //Cursor cursor = db.getData("Select User_name, Password FROM Employee WHERE User_name LIKE " + "'%" + username.getText().toString() + "%'");//+ " AND Password LIKE " + "'%" + password.getText().toString() + "%'");


    }
    public void SyncData(){
        //Instantiate database handler
        final DatabaseHandler db = new DatabaseHandler(this);

        //create temporary list for manipulation
        final List<Employee> empList = new ArrayList<Employee>();
        Log.d("Notice", "running SyncData()");


        if(db.getEmployeesCount() > 0) {

            Cursor cursor = db.getData("SELECT Sync_id, Sync_timestmap FROM Employee ORDER BY Sync_timestmap DESC");

            if(cursor.moveToFirst()) {
                Employee em = db.getEmployee(cursor.getString(0));

                Log.d("Notice", "Local db has employee rows");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Employee");
                Log.d("Notice", "Name " + em.getFirst_name() + " Checkpoint " + checkpoint);

                query.whereGreaterThan("updatedAt", checkpoint);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> objectList, ParseException e) {
                        Log.d("Notice", "Query returned " + objectList.size() + " entries");
                        if (e == null) {
                            for (ParseObject Employee : objectList) {

                                Employee emp = new Employee((String) Employee.get("User_name"),
                                        (String) Employee.get("Password"),
                                        (String) Employee.get("First_name"),
                                        (String) Employee.get("Last_name"),
                                        (String) Employee.get("Admin"),
                                        new Date(System.currentTimeMillis()));
                                emp.setSync_id(Employee.getObjectId());
                                if (Employee.get("DeleteFlag") == "1") {
                                    db.deleteEmployee(emp);
                                } else {
                                    empList.add(emp);
                                }
                            }
                            for (int i = 0; i < empList.size(); i++) {
                                Log.d("Notice", "Adding employee to local db");
                                db.addEmployee(empList.get(i), false);
                            }
                            checkpoint = new Date(System.currentTimeMillis());
                            db.close();
                        } else {
                            // something went wrong
                            Log.d("Sync error", "Cannot sync employees table");
                            db.close();
                        }
                    }
                });
            }
        }else{
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Employee");
            query.whereLessThan("updatedAt", checkpoint);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objectList, ParseException e) {
                    Log.d("Empty database", "Query returned " + objectList.size() + " entries");
                    if (e == null) {
                        for (ParseObject Employee : objectList) {
                            Employee emp = new Employee(
                                    (String) Employee.get("User_name"),
                                    (String) Employee.get("Password"),
                                    (String) Employee.get("First_name"),
                                    (String) Employee.get("Last_name"),
                                    (String) Employee.get("Admin"),
                                    (Date) Employee.get("updatedAt"));
                            emp.setSync_id(Employee.getObjectId());
                            empList.add(emp);
                        }
                        for (int i = 0; i < empList.size(); i++) {
                            Log.d("Notice", "Adding employee to local db");
                            db.addEmployee(empList.get(i), false);
                        }
                        checkpoint = new Date(System.currentTimeMillis());
                        db.close();
                    } else {
                        // something went wrong
                        Log.d("Sync error", "Cannot sync employees table");
                        db.close();
                    }

                }
            });
        }
    }

}