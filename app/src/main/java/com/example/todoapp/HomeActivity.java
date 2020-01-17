package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.todoapp.Adapter.TodoAdapter;
import com.example.todoapp.Helper.Api;
import com.example.todoapp.Model.ApiResponse;
import com.example.todoapp.Model.Todo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.todoapp.Constant.TODO_API_URL;

public class HomeActivity extends AppCompatActivity {

    RecyclerView rvAllTodo;
    Button btnLogout;
//    Todo todo;

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        rvAllTodo = (RecyclerView) findViewById(R.id.rv_alltodo);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userTokenPref = getApplicationContext().getSharedPreferences("UserToken", MODE_PRIVATE);
                SharedPreferences.Editor editor = userTokenPref.edit();

                editor.remove("user_id");
                editor.remove("user_token");
                editor.clear();
                editor.commit();

                Toast.makeText(HomeActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences userTokenPref = getApplicationContext().getSharedPreferences("UserToken", MODE_PRIVATE);
        String userToken = (userTokenPref.getString("user_token", ""));
        String userId = (userTokenPref.getString("user_id", ""));

        Api api = new Api();
        api.getWithAuthorization(TODO_API_URL+"/"+userId, userToken, new Callback() {
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
                            Todo todo = gson.fromJson(responseStr, Todo.class);
                            TodoAdapter adapter = new TodoAdapter(getApplicationContext(), todo.getData());

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            rvAllTodo.setLayoutManager(layoutManager);
                            rvAllTodo.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                            rvAllTodo.setItemAnimator(new DefaultItemAnimator());
                            rvAllTodo.setAdapter(adapter);
                        }
                    });
                } else {
                    Log.e("REQ", response.body().string());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Intent intent = new Intent(HomeActivity.this, AddTodoActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
