package com.example.uietfest.EventsPackage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uietfest.Constants;
import com.example.uietfest.R;
import com.example.uietfest.RequestHandler;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Technical extends Fragment {

    private ProgressDialog progressDialog;
    ListView listView;
    JSONObject jobj;
    JsonArrayRequest jar;
    HashMap<String,String> hashMap;
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> titles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical, container, false);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String userEmail = mAuth.getCurrentUser().getEmail();

        progressDialog = new ProgressDialog(getContext());
        listView = (ListView)view.findViewById(R.id.list_view_technical);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        jar = new JsonArrayRequest(Constants.URL_FETCH_TECHNICAL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                for(int i = 0 ; i<response.length() ; i++)
                {
                    try
                    {
                        hashMap = new HashMap<>();
                        jobj = response.getJSONObject(i);
                        String s1 = jobj.getString("title");
                        String s2 = jobj.getString("about");
                        String s3 = jobj.getString("date");
                        String s4 = jobj.getString("venue");
                        String s5 = jobj.getString("begin") + " to " + jobj.getString("finish");
                        hashMap.put("title", s1);
                        hashMap.put("about", s2);
                        hashMap.put("date",s3);
                        hashMap.put("venue", s4);
                        hashMap.put("time", s5);
                        titles.add(s1);
                        arrayList.add(hashMap);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                String[] from = {"title", "about", "date", "venue", "time"};
                int[] to = {R.id.events_title, R.id.events_description, R.id.events_date, R.id.events_venue, R.id.events_duration};
                CustomSimpleAdapter csa = new CustomSimpleAdapter(getContext(),arrayList, R.layout.list_items, from, to);
                listView.setAdapter(csa);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue rq = Volley.newRequestQueue(getContext());
        rq.add(jar);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showStatus(i, userEmail);
            }
            });
        return view;
    }
    
    private void showStatus(final int index, final String userEmail){
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_SHOW_DIALOG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int counter = jsonObject.getInt("count");
                            if(counter == 0)
                                showDoRegistration(index,userEmail);
                            else
                                showAlreadyRegistered(index);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", userEmail);
                params.put("title", titles.get(index));
                return params;
            }
        };

        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void showDoRegistration(final int w, final String userEmail) {
        new AlertDialog.Builder(getContext())
                .setTitle("PARTICIPATION STATUS")
                .setMessage("Do you want participate in " + titles.get(w) + " ?")
                .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doParticipation(userEmail, titles.get(w));
                    }
                }).show();
    }

    private void showAlreadyRegistered(final int w) {
        new AlertDialog.Builder(getContext())
                .setTitle("PARTICIPATION STATUS")
                .setMessage("You have already participated in " + titles.get(w) + ".")
                .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void doParticipation(final String userEmail, final String title){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_PARTICIPTE_STUDENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }
}

