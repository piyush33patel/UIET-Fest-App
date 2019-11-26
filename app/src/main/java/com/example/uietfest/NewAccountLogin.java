package com.example.uietfest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewAccountLogin extends AppCompatActivity {

    ProgressDialog progressDialog;
    EditText etname,etrollno,etemail,etpassword;
    Button bt;
    Spinner spdepartment,spbranch,spyear;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_login);

        progressDialog = new ProgressDialog(NewAccountLogin.this);
        etname = (EditText) findViewById(R.id.name);
        etrollno = (EditText) findViewById(R.id.rollno);
        spdepartment = (Spinner)findViewById(R.id.department);
        spbranch = (Spinner) findViewById(R.id.branch);
        spyear = (Spinner) findViewById(R.id.year);
        etemail = (EditText) findViewById(R.id.email);
        etpassword = (EditText) findViewById(R.id.password);
        bt = (Button) findViewById(R.id.submit);

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(NewAccountLogin.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.branches));
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spbranch.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(NewAccountLogin.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.departments));
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spdepartment.setAdapter(arrayAdapter2);

        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<>(NewAccountLogin.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.year));
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spyear.setAdapter(arrayAdapter3);

        mAuth = FirebaseAuth.getInstance();

    bt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           progressDialog.setMessage("Registering User.....");
            progressDialog.show();
            try {
                final String name = etname.getText().toString().trim();
                final String rollno = etrollno.getText().toString().trim();
                final String department = spdepartment.getSelectedItem().toString().trim();
                final String branch = spbranch.getSelectedItem().toString().trim();
                final String year = spyear.getSelectedItem().toString().trim();
                final String email = etemail.getText().toString().trim();
                String password = etpassword.getText().toString().trim();


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(NewAccountLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       progressDialog.dismiss();
                        if (!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Authentication Successful", Toast.LENGTH_SHORT).show();
                            registerUser(name, rollno, department, branch, year, email);
                        }
                    }
                });
            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Enter Email and Password", Toast.LENGTH_SHORT).show();
            }
        }
    });
    }


    private void registerUser(final String name, final String rollno, final String department, final String branch, final String year, final String email) {

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            Toast.makeText(getApplicationContext(), "Happening", Toast.LENGTH_LONG).show();
                            JSONObject jsonObject = new JSONObject(response);

                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();


                            //changed
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.layoutID, new BottomNavigation());
                            fragmentTransaction.commit();
                            //changed

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rollno", rollno);
                params.put("name", name);
                params.put("department", department);
                params.put("branch", branch);
                params.put("year", year);
                params.put("email", email);
                return params;
            }
        };
        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
