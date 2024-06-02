package com.iri5.medicine_management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iri5.medicine_management.Utils.Util;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    EditText et_email;
    EditText et_pwd;
    Button btn_login;
    Button btn_regist;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        et_email    = (EditText) findViewById(R.id.et_login_email);
        et_pwd      = (EditText) findViewById(R.id.et_login_pwd);
        btn_login   = (Button) findViewById(R.id.btn_login_login);
        btn_regist  = (Button) findViewById(R.id.btn_login_regist);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_email.getText().toString().equals("")){
                    if(!et_pwd.getText().toString().equals("")){
                        String email    = et_email.getText().toString().trim();
                        String pwd      = et_pwd.getText().toString().trim();
                        loginUser(email,pwd);
                    }else{
                        Util.showToast(getApplicationContext(), "패스워드를 입력해주세요");
                    }
                }else{
                    Util.showToast(getApplicationContext(), "아이디를 입력해주세요");
                }
            }
        });

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);
                startActivity(intent);
            }
        });

    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            getUserGrade(firebaseAuth.getUid());

                        } else {
                            // 로그인 실패
                            Util.showToast(getApplicationContext(),"아이디 또는 비밀번호가 일치하지 않습니다.");
                        }
                    }
                });
    }

    private void getUserGrade(String uid) {
        db.collection("User")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String grade = documentSnapshot.getString("grade");
                            Log.d(TAG, "Grade: " + grade);
                            if(grade.equals("고객")){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }else if(grade.equals("사업주")){
                                getShop(uid);
                            }
                            Util.showToast(getApplicationContext(),"로그인에 성공하였습니다.");
                            finish();
                        } else {
                            Log.d(TAG, "No such document");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                        Util.showToast(getApplicationContext(),"Error getting document");
                    }
                });
    }

    private void getShop(String uid) {
        db.collection("Shops")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String businessname = documentSnapshot.getString("businessname");
                            Log.d(TAG, "Business Name: " + businessname);

                            if(businessname.equals("")){
                                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }


                        } else {
                            Log.d(TAG, "No such document");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });
    }
}