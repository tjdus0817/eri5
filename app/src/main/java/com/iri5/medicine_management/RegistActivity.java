package com.iri5.medicine_management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.iri5.medicine_management.Utils.Util;

import java.util.HashMap;
import java.util.Map;

public class RegistActivity extends AppCompatActivity {
    private static final String TAG = "RegistActivity";
    EditText et_email;
    EditText et_pwd;
    EditText et_pwd_ck;
    RadioGroup rg_grade;
    RadioButton rb_customer;
    RadioButton rb_owner;
    Button btn_submit;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        et_email    = (EditText) findViewById(R.id.et_regist_email);
        et_pwd      = (EditText) findViewById(R.id.et_regist_pwd);
        et_pwd_ck   = (EditText) findViewById(R.id.et_regist_pwd_ck);
        rg_grade    = (RadioGroup) findViewById(R.id.rg_regist_grade);
        rb_customer = (RadioButton) findViewById(R.id.rb_regist_customer);
        rb_owner    = (RadioButton) findViewById(R.id.rb_regist_owner);
        btn_submit  = (Button) findViewById(R.id.btn_regist_submit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_email.getText().toString().equals("")){
                    if(!et_pwd.getText().toString().equals("")){
                        if(!et_pwd_ck.getText().toString().equals("")){
                            if(et_pwd.getText().toString().equals(et_pwd_ck.getText().toString())){
                                
                                String email = et_email.getText().toString().trim();
                                String pwd = et_pwd.getText().toString().trim();
                                String grade = "";
                                
                                if(rg_grade.getCheckedRadioButtonId() == R.id.rb_regist_customer){
                                    grade = "고객";
                                }else if(rg_grade.getCheckedRadioButtonId() == R.id.rb_regist_owner){
                                    grade = "사업주";
                                }
                                createUser(email,pwd,grade);

                            }else{
                                Util.showToast(getApplicationContext(), "비밀번호와 비밀번호확인이 \n 일치하지 않습니다.");
                            }
                        }else{
                            Util.showToast(getApplicationContext(), "비밀번호 확인을 입력해주세요");
                        }
                    }else{
                        Util.showToast(getApplicationContext(), "비밀번호를 입력해주세요");
                    }
                }else{
                    Util.showToast(getApplicationContext(), "아이디를 입력해주세요");
                }
            }
        });

    }

    private void createUser(String email, String password, String grade){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUser(firebaseAuth.getUid(), grade);
                        } else {
                            // 계정이 중복된 경우
                            Util.showToast(getApplicationContext(),"이미 존재하는 계정입니다.");
                        }
                    }
                });
    }

    private void addUser(String uid, String grade) {
        // Firestore에 삽입할 데이터
        Map<String, Object> user = new HashMap<>();
        user.put("uid", uid);
        user.put("grade", grade);

        // Firestore에 데이터 삽입
        db.collection("User")
                .document(uid) // Document ID를 uid로 설정
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User successfully written!");
                        if(grade.equals("고객")){
                            Util.showToast(getApplicationContext(),"회원가입에 성공했습니다.");
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (grade.equals("사업주")) {
                            addShop(uid);
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error user writing document", e);
                    }
                });
    }

    private void addShop(String uid) {
        // Firestore에 삽입할 데이터
        Map<String, Object> shop = new HashMap<>();
        shop.put("uid", uid);
        shop.put("businessname", "");
        shop.put("lat", "");
        shop.put("lon", "");
        shop.put("description", "");
        shop.put("inventory", "");
        shop.put("operatingTime", "");

        db.collection("Shops")
                .document(uid)
                .set(shop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Shops successfully written!");
                        Util.showToast(getApplicationContext(),"회원가입에 성공했습니다.");
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error shop writing document", e);
                    }
                });
    }
}