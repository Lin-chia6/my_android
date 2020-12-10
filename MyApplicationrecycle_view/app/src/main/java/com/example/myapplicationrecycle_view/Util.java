package com.example.myapplicationrecycle_view;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Util {
    public static Connection openConnection(String url, String user,
                                            String password) {
        Connection conn = null;
        try {
            final String DRIVER_NAME = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            Log.e("============", "報錯 ClassNotFoundException");
            e.printStackTrace();
            conn = null;
        } catch (SQLException e) {
            Log.e("============", "報錯 SQLException");
            e.printStackTrace();
            conn = null;
        }

        return conn;
    }

    public static ResultSet query(Connection conn, String sql) {

        if (conn == null) {
            Log.e("=====連接前判斷=======", "conn == null");
            return null;
        }

        Statement statement = null;
        ResultSet result = null;

        try {
            statement = conn.createStatement();
            result = statement.executeQuery(sql);
            if (result != null && result.first()) {
                int idColumnIndex = result.findColumn("id");
                int nameColumnIndex = result.findColumn("name");
                Log.e("======結果======", "結果");
                while (!result.isAfterLast()) {
                    Log.e("======id======", result.getString(idColumnIndex) + "\t\t");
                    Log.e("======name======", result.getString(nameColumnIndex));

//					System.out.print(result.getString(idColumnIndex) + "\t\t");
//					System.out.println(result.getString(nameColumnIndex));
                    result.next();
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                    result = null;
                }
                if (statement != null) {
                    statement.close();
                    statement = null;
                }

            } catch (SQLException sqle) {

            }
        }
        return null;
    }

    public static boolean execSQL(Connection conn, String sql) {
        boolean execResult = false;
        if (conn == null) {
            return execResult;
        }

        Statement statement = null;

        try {
            statement = conn.createStatement();
            if (statement != null) {
                execResult = statement.execute(sql);
            }
        } catch (SQLException e) {
            execResult = false;
        }

        return execResult;
    }
}