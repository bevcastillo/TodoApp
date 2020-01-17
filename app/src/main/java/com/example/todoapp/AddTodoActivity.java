package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddTodoActivity extends AppCompatActivity {

    EditText etCreateTodo;
    Button btnCreate;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        etCreateTodo = (EditText) findViewById(R.id.tv_new_todo);
        btnCreate = (Button) findViewById(R.id.btn_create_to_do);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoTitle = etCreateTodo.getText().toString();

                try {
                    createTodo(todoTitle);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createTodo(String title) throws JSONException {

        if (title.equals("")){
            Toast.makeText(this, "Fields cant be empty", Toast.LENGTH_SHORT).show();
        }else {
            TodoPayload todoPayload = new TodoPayload();
            todoPayload.setTodoTitle(title);

            doCreateTodo(todoPayload, Constant.TODO_API_URL);
        }
    }

    private void doCreateTodo(TodoPayload todoPayload, String url) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", todoPayload.getTodoTitle());

        Api api = new Api();
        String json = api.payload(jsonObject);

        SharedPreferences userTokenPref = getApplicationContext().getSharedPreferences("UserToken", MODE_PRIVATE);
        String userToken = (userTokenPref.getString("user_token", ""));

        api.postWithAuth(url, userToken, json, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.i("CREATE_TODO_RESP", responseStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApiResponse apiResponse = gson.fromJson(responseStr, ApiResponse.class);

                            Toast.makeText(AddTodoActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddTodoActivity.this, HomeActivity.class);
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
