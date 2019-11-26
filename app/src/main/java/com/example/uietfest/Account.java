package com.example.uietfest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uietfest.EventsPackage.CustomSimpleAdapter;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Account extends Fragment {
    TextView tv, email, name, rollno, department, branch, year;
    Button bt, part;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    String global_roll_no = "";

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null)
        {
            getFragmentManager().beginTransaction().replace(R.id.layoutID, new LoginPage()).commit();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        bt = (Button)view.findViewById(R.id.logout);
        part = (Button)view.findViewById(R.id.participate);
        tv = (TextView)view.findViewById(R.id.text);
        name = (TextView)view.findViewById(R.id.name);
        email = (TextView)view.findViewById(R.id.email);
        rollno = (TextView)view.findViewById(R.id.rollno);
        department = (TextView)view.findViewById(R.id.department);
        branch = (TextView)view.findViewById(R.id.branch);
        year = (TextView)view.findViewById(R.id.year);

        progressDialog = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();

        String userEmail = mAuth.getCurrentUser().getEmail();
        email.setText("      USER EMAIL = " + userEmail);

        getUser(userEmail);

        part.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HisEventsList.class);
                startActivity(intent);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    mAuth.signOut();
                    Toast.makeText(getContext(), "LOG OUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                    getFragmentManager().beginTransaction().replace(R.id.layoutID, new LoginPage()).commit();
                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "COULD NOT LOG OUT", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    private void getUser(final String userEmail) {

        progressDialog.setMessage("Loading.....");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_INFO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String sRoll = jsonObject.getString("rollno");
                            String sDepart = jsonObject.getString("department");
                            String sBranch = jsonObject.getString("branch");
                            String sYear = jsonObject.getString("year");
                            String sName = jsonObject.getString("name");
                            name.setText("       FULL NAME = " + sName);
                            department.setText("   DEPARTMENT = " + sDepart);
                            rollno.setText("  ROLL NUMBER = " + sRoll);
                            branch.setText("             BRANCH = " + sBranch);
                            year.setText("CURRENT YEAR = " + sYear);
                            global_roll_no = sRoll;
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Taking too long to get the response. Please try again!!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

}
