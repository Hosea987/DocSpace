package com.example.mountaineer.DocSpace;

public class ArticlePost {

    public String author, content, date, title;

    public  ArticlePost() {}

    public ArticlePost(String author, String content, String date, String title) {
        this.author = author;
        this.content = content;
        this.date = date;
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
