package com.udacity.garyshem.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<NewsArticle> {

    public NewsAdapter(Context context, ArrayList<NewsArticle> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Book} object located at this position in the list
        NewsArticle currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID author.
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.section);
        // Then set the author name to that field
        authorTextView.setText(currentBook.getSection());

        // Find the TextView in the list_item.xml layout with the ID title.
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        // Then set the title to that field
        titleTextView.setText(currentBook.getTitle());

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
