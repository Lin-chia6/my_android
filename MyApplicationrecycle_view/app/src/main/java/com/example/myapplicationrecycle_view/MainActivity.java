package com.example.myapplicationrecycle_view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnclickListener {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    List<String> moviesList;

    private static final String REMOTE_IP = "193.42.40.110";//服務器地址
    private static final String URL = "jdbc:mysql://" + REMOTE_IP + "/MedBigDataAI?autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
    private static final String USER = "medical";//數據庫賬戶
    private static final String PASSWORD = "ggininder";//數據庫密碼

    private Connection conn;

    public void onConn(View view) {

        new Thread() {
            public void run() {
                Log.e("============", "預備連接數據庫");
                conn = Util.openConnection(URL, USER, PASSWORD);
            }
        }.start();
    }

    public void onInsert(View view) {
        new Thread() {
            public void run() {
                Log.e("============", "預備插入");
                String sql = "insert into users values(3, 'yinhongbo', 'yinhongbo')";
                Util.execSQL(conn, sql);
            }
        }.start();
    }

    public void onDelete(View view) {
        String sql = "delete from mytable where name='mark'";
        Util.execSQL(conn, sql);
    }

    public void onUpdate(View view) {
        String sql = "update mytable set name='lilei' where name='hanmeimei'";
        Util.execSQL(conn, sql);
    }

    public ResultSet onQuery(View view, String sql) {
        final ResultSet[] result = {null};
        new Thread() {
            public void run() {
                Log.e("============", "預備查詢");
                result[0] = Util.query(conn,  sql);
            }
        }.start();
        return result[0];
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesList = new ArrayList<>();

        this.onConn(null);

        while(conn == null);
        ResultSet result = this.onQuery(null, "SELECT * FROM patient_info;");

        try {
            if (result != null && result.first()) {
                int nameColumnIndex = result.findColumn("name");
                while (!result.isAfterLast()) {
                    moviesList.add(result.getString(nameColumnIndex));
                    result.next();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(moviesList, this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                conn = null;
            } finally {
                conn = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onclick(int position) {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}