package com.example.technewsapp;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    //initializing the variables
    ArrayList<String> articleText = new ArrayList<String>();
    ArrayAdapter adapter;
    ArrayList<String> subject = new ArrayList<String>();
    SQLiteDatabase articles;
    SharedPreferences shared;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        articles = this.openOrCreateDatabase("List Articles", MODE_PRIVATE, null);
        articles.execSQL("CREATE TABLE IF NOT EXISTS article_tbl (id INTEGER PRIMARY KEY, artID, INTEGER, title VARCHAR, article_txt VARCHAR)");
        //articles.execSQL("DROP TABLE IF EXISTS article_tbl");
        listView = (ListView) findViewById(R.id.articles);

        DownloadTask downloadTask = new DownloadTask();

        shared = this.getSharedPreferences("com.example.technewsapp", Context.MODE_PRIVATE);
        try {
            //shared.edit().remove("saving").commit();
            Boolean check = shared.contains("saving");
            Log.i("Check", check.toString());

            if( check == false){
                downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json");
                listView.setAdapter(adapter);
            }else{
                updateListView();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


//    public void initDB(SQLiteDatabase db){
//        db = this.openOrCreateDatabase("List Articles", MODE_PRIVATE, null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS article_tbl (id INTEGER PRIMARY KEY, artID, INTEGER, title VARCHAR, article_txt VARCHAR)");
//
//    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            String res = "";
            HttpURLConnection urlConn = null;

            try{
                url = new URL(strings[0]);
                urlConn = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //read the first bit of data then repeat until it exists, meaning it is != -1
                int content = inputStreamReader.read();

                while(content != -1){
                    char current = (char) content;
                    res += current;

                    content = inputStreamReader.read();
                }
                Log.i("Result", res);

                //for fetching 20 articles maximum from API
                int maxArticles = 20;
                JSONArray array = new JSONArray(res);

                if (array.length()<20){
                    maxArticles = array.length();
                }
                articles.execSQL("DELETE FROM article_tbl");
                //loop over all the articles to execute the API on each one
                for (int i = 0; i<maxArticles;i++){
                    String articleID = array.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleID + ".json?print=pretty");
                    urlConn = (HttpURLConnection) url.openConnection();

                    inputStream = urlConn.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);

                    content = inputStreamReader.read();
                    //getting the article details
                    String aDetails = "";

                    while (content != -1){
                        char current = (char) content;
                        aDetails += current;
                        content = inputStreamReader.read();
                    }

                    JSONObject object = new JSONObject(aDetails);
                    //checking and getting the detailed info
                    if(!object.isNull("url") && !object.isNull("title")){
                        String title = object.getString("title");
                        Log.i("Article is", title);
                        String aUrl = object.getString("url");
                        url = new URL(aUrl);
                        urlConn = (HttpURLConnection) url.openConnection();
                        inputStream = urlConn.getInputStream();
                        BufferedReader read  = new BufferedReader(new InputStreamReader(inputStream));
                        String content2 = read.readLine();
                        String aContent = "";

                        while (content2 != null){
                            aContent+=content2;
                            content2 = read.readLine();
                        }
                        Log.i("HTML", aContent);

                        String sql = "INSERT INTO article_tbl (artID, title, article_txt) VALUES (?, ?, ?)";
                        SQLiteStatement statement = articles.compileStatement(sql);
                        statement.bindString(1, articleID);
                        statement.bindString(2, title);
                        statement.bindString(3, aContent);

                        statement.execute();

                    }
                }

                return res;

            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Boolean check = shared.edit().putBoolean("saving", true).commit();
            Log.i("Check", check.toString());
            updateListView();

        }
    }

    public void updateListView() {
        Log.i("TAG", "updateListView: ");
        Cursor cursor = articles.rawQuery("SELECT * FROM article_tbl", null);

        int contentIndex = cursor.getColumnIndex("article_txt");
        int titleIndex = cursor.getColumnIndex("title");

        if (cursor.moveToFirst()) {
            subject.clear();
            articleText.clear();

            do {
                subject.add(cursor.getString(titleIndex));
                articleText.add(cursor.getString(contentIndex));

            } while (cursor.moveToNext());

            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, subject);
            listView.setAdapter(adapter);

           listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                   Intent intent = new Intent(getApplicationContext(), article.class);
                   intent.putExtra("article_txt", articleText.get(position));
                   startActivity(intent);
               }
           });
        }
    }

}