package com.example.mountaineer.DocSpace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ArticleActivity extends AppCompatActivity {

    private ImageView mDisplayImage;
    private TextView mContent;
    private TextView mTitle;
    private TextView mAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        String content = getIntent().getStringExtra("content");
        String title = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("ArticleImage");
        String author = getIntent().getStringExtra("author");


        mDisplayImage = (ImageView) findViewById(R.id.ArticleImage);
        mContent = (TextView) findViewById(R.id.Articlecontent);
        mContent.setText(content);

        mTitle = (TextView) findViewById(R.id.ArticleHeaderTitle);
        mTitle.setText(title);

        mAuthor = (TextView) findViewById(R.id.ArticleHeaderAuthor);
        mAuthor.setText(author);

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .into(mDisplayImage);

    }
}
