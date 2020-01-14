package com.example.uietfest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HisEventsList extends AppCompatActivity {

    CustomSimpleAdapter csa;

    ListView listView;
    JSONObject jobj;
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> titles = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_his_events_list);

        final TextView tv = (TextView) findViewById(R.id.nothing);

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Loading....");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userEmail = mAuth.getCurrentUser().getEmail();

        listView = (ListView)findViewById(R.id.his_event);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_PARTICIPATION_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray;
                        int count = 0;
                        try {
                            jsonArray = new JSONArray(response);

                            if(jsonArray.length()==0) {
                                Toast.makeText(getApplicationContext(),"Your List is Empty", Toast.LENGTH_LONG).show();
                                tv.setText("NOTHING TO SHOW");
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    hashMap = new HashMap<>();
                                    jobj = jsonArray.getJSONObject(i);
                                    String s1 = jobj.getString("title");
                                    count++;
                                    hashMap.put("counter", "" + count);
                                    hashMap.put("title", s1);
                                    titles.add(s1);
                                    arrayList.add(hashMap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String[] from = {"counter", "title"};
                        int[] to = {R.id.serial, R.id.events_name};
                         csa = new CustomSimpleAdapter(getApplicationContext(),arrayList, R.layout.his_events_list, from, to);
                        listView.setAdapter(csa);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Something went Wrong. Try again!!", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                return params;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(stringRequest);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int w, long l) {
                final int index = w;
                new AlertDialog.Builder(HisEventsList.this)
                        .setTitle("REMOVE PARTICIPATION")
                        .setMessage("Do you want to remove your participation from " + titles.get(index) +" ?")
                        .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteDialog(userEmail, titles.get(index));
                            }
                        }).show();
            }
        });
    }


    private void deleteDialog(final String userEmail, final String title){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_DELETE_PARTICIPATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                params.put("title", title);
                return params;
            }
        };

        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
