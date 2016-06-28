package com.udacity.garyshem.newsapp;

public class NewsArticle {
    private String mSection;
    private String mTitle;
    private String mWebUrl;

    public NewsArticle(String section, String title, String WebUrl) {
        mSection = section;
        mTitle = title;
        mWebUrl = WebUrl;
    }

    @Override
    public String toString() {
        return "NewsArticle{" +
                "mSection='" + mSection + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mWebUrl='" + mWebUrl + '\'' +
                '}';
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getWebUrl() {
        return mWebUrl;
    }
}
