package com.example.patrick.tasktracker;


import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shwaat on 11/2/2014.
 */
public class Activity_AdminDepartments extends ActionBarActivity {

    ListView departmentListView;

    ParseQueryAdapter<ParseObject> mainAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Parse.initialize(this, "6yEsCcvYy5ym7rmRKWleVy5A9jc2wHFz6aEL3Czs", "t3h3S0090VVBwdw0zasj5J0b28dLe9xebL5nIfKw");
        super.onCreate(savedInstanceState);

        Log.d("Department", "main");
        setContentView(R.layout.admin_department_main);


        mainAdapter = new ParseQueryAdapter<ParseObject>(this, "Department");
        mainAdapter.setTextKey("Department_id");


        departmentListView = (ListView)findViewById(R.id.departments_list_view);
        departmentListView.setPadding(0,5,0,5);
        departmentListView.setAdapter(mainAdapter);
        mainAdapter.loadObjects();

        departmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParseObject po = (ParseObject)parent.getItemAtPosition(position);
                Department dep = new Department(po.getObjectId(), po.get("Department_id").toString(), po.get("Charged").toString());
                Log.d("AdminDepartments", "Charged status is : " + po.getString("Charged"));
                Intent intent = new Intent(view.getContext(), Activity_DepartmentLocInfo.class);
                intent.putExtra("extra", dep);
                Log.d("AdminDepartments", " " +po.getObjectId());
                startActivity(intent);
            }
        });
    }

    public void addDepartment(View view){
        Intent intent = new Intent(this, Activity_AdminNewDepartment.class);
        startActivity(intent);
    }

    public void remDepartment(View view){
        Intent intent = new Intent(this, Activity_DepartmentRemLoc.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.admin_employee_refresh_item:
                finish();
                startActivity(getIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
