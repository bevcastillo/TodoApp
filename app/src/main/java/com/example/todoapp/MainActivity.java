package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.Helper.Api;
import com.example.todoapp.Model.ApiResponse;
import com.example.todoapp.Model.Payload;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtSignUp;
    EditText etName, etPassw;
    Button btnSignIn;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSignUp = (TextView) findViewById(R.id.tv_signup);
        etName = (EditText) findViewById(R.id.et_name);
        etPassw = (EditText) findViewById(R.id.et_passw);
        btnSignIn = (Button) findViewById(R.id.btn_signin);

        btnSignIn.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.tv_signup:
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_signin:
                String name = etName.getText().toString().trim();
                String passw = etPassw.getText().toString().trim();
                try {
                    userLogin(name, passw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void userLogin(String name, String passw) throws JSONException {
        if (name.equals("") && passw.equals("")){
            Toast.makeText(this, "Fields cant be empty", Toast.LENGTH_SHORT).show();
        }else {
            Payload payload = new Payload();
            payload.setName(name);
            payload.setPassword(passw);

            doLogin(payload, Constant.USER_LOGIN_API_URL);
        }
    }

    private void doLogin(Payload payload, String userLoginApi) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", payload.getName());
        jsonObject.put("password", payload.getPassword());

        Api api = new Api();
        String json = api.payload(jsonObject);

        api.post(userLoginApi, json, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.i("LOGIN_RESP", responseStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApiResponse apiResponse = gson.fromJson(responseStr, ApiResponse.class);

//                            Toast.makeText(MainActivity.this, apiResponse.getMessage()+" token: "+apiResponse.getToken(), Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            //
                            SharedPreferences userTokenPref = getApplicationContext().getSharedPreferences("UserToken", MODE_PRIVATE); // 0 - for private mode
                            SharedPreferences.Editor editor = userTokenPref.edit();

                            editor.putString("user_token", apiResponse.getToken());
                            editor.commit();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
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
