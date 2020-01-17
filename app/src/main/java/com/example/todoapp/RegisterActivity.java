package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Helper.Api;
import com.example.todoapp.Model.ApiResponse;
import com.example.todoapp.Model.Payload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etRegName, etRegAge, etRegPassw, etRegConfPassw;
    Button btnSignUp;
    TextView txtSignin;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegName = (EditText) findViewById(R.id.et_reg_name);
        etRegAge = (EditText) findViewById(R.id.et_reg_age);
        etRegPassw = (EditText) findViewById(R.id.et_reg_passw);
        etRegConfPassw = (EditText) findViewById(R.id.et_reg_conf_passw);
        btnSignUp = (Button) findViewById(R.id.btn_signup);
        txtSignin = (TextView) findViewById(R.id.tv_signin);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        btnSignUp.setOnClickListener(this);
        txtSignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.btn_signup:
                String name = etRegName.getText().toString().trim();
                int age = Integer.parseInt(etRegAge.getText().toString().trim());
                String passw = etRegPassw.getText().toString().trim();
                String confPassw = etRegConfPassw.getText().toString().trim();

                try {
                    registerUser(name, age, passw, confPassw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_signin:
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void registerUser(String name, int age, String passw, String confPassw) throws JSONException {
        //
        if(name.equals("") && passw.equals("")){
            Toast.makeText(this, "Fields cant be empty", Toast.LENGTH_SHORT).show();
        }else {
            Payload payload = new Payload();
            payload.setName(name);
            payload.setAge(age);
            payload.setPassword(passw);
            payload.setConfirmPassword(confPassw);

            doRegister(payload, Constant.USER_REGISTER_API_URL);
        }
    }

    public void doRegister(Payload payload, String url) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", payload.getName());
        jsonObject.put("age", payload.getAge());
        jsonObject.put("password", payload.getPassword());
        jsonObject.put("confirm_password", payload.getConfirmPassword());

        Api api = new Api();
        String json = api.payload(jsonObject);
        api.post(url, json, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.i("REGISTER_RESP", responseStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApiResponse apiResponse = gson.fromJson(responseStr, ApiResponse.class);

                            Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    });

                } else {
                    Log.e("REQ", response.body().string());
                }
            }
        });
    }
}
