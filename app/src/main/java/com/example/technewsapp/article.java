package com.example.technewsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class article extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        WebView view = findViewById(R.id.article_View);

        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient());

//        if(getIntent().getExtras() != null) {
//            String htmlcontent = (String) getIntent().getSerializableExtra("HTML");
//            view.loadData("HTML", "text/html", "UTF-8" + "");
//        }
        Intent intent = getIntent();
        view.loadData(intent.getStringExtra("article_txt"), "text/html", "UTF-8");


    }
}