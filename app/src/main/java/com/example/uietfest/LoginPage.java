package com.example.uietfest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends Fragment {
    EditText et1, et2;
    Button bt;
    FirebaseAuth mAuth;
    TextView tv;
    private ProgressDialog progressDialog;

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!= null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.layoutID, new BottomNavigation());
            fragmentTransaction.commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);
        et1 = (EditText)view.findViewById(R.id.email);
        et2 = (EditText)view.findViewById(R.id.password);
        tv = (TextView)view.findViewById(R.id.register);
        bt = (Button)view.findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getContext());

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NewAccountLogin.class);
                startActivity(intent);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    progressDialog.setMessage("Logging in.....");
                    progressDialog.show();
                    String email = et1.getText().toString();
                    String password = et2.getText().toString();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful())
                            {
                                Toast.makeText(getContext(), "AUTHENTICATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.layoutID, new BottomNavigation());
                                fragmentTransaction.commit();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "AUTHENTICATION FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "ENTER EMAIL AND PASSWORD", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
