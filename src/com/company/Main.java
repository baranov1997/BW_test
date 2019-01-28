package com.company;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(
                    "jdbc:sqlite:test.db3");

            Statement st = connection.createStatement();

            String date = "2002-06-01";
//            ResultSet rs = st.executeQuery("SELECT * FROM Site WHERE DATE(date)>'"+date+"'");
            ResultSet rs = st.executeQuery("SELECT * FROM Site");


            Map<String, String> temp = new HashMap<>();

            while (rs.next()){
                String id = rs.getString("id");
                String url = rs.getString("url");

                temp.put(id, url);
            }

//            temp.forEach((k,v)-> {
//                try {
//                    setStatus(k, v, connection);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });

            ForkJoinPool pool = new ForkJoinPool(1000);
            pool.submit(()->temp.entrySet().parallelStream().forEach(entry-> {
                    try {
                        setStatus(entry.getKey(), entry.getValue(), connection);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
            ).get();



            rs.close();
            connection.close();

//            urls.forEach(System.out::println);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

//    Get site status
    public static void setStatus(String id, String s_url, Connection con) throws IOException {
//        URL url = new URL(s_url);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//        connection.connect();

//        int code = connection.getResponseCode();

        int code = 200;

        try {
            Statement st = con.createStatement();
            st.execute(
                    "UPDATE Site " +
                    "SET status = '"+String.valueOf(code)+"' " +
                    "WHERE id = "+id+";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
