package com.udacity.garyshem.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager mConnectivityManager;
    private EditText mQueryField;
    private ListView mNewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueryField = (EditText)findViewById(R.id.edit_query);
        mNewsList = (ListView)findViewById(R.id.list_news);

        // Set OnItemClickListener to open the related URL
        // Since we want to do it in user's browser, but don't know which one
        // just make a general web page intent so the user can decide
        mNewsList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsArticle item = (NewsArticle) mNewsList.getItemAtPosition(position);
                String url = item.getWebUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    public void startSearch(View view) {
        final String queryText = mQueryField.getText().toString();
        // If the query field is empty, do not start the search
        // Since this function is of return type void, we don't lose anything
        // by returning prematurely
        if (queryText.trim().equals("")) {
            printError("Empty query");
            return;
        }
        // Check internet connection
        // If there's no connection, don't run the search, again
        if (isNetworkConnected() == false) {
            printError("No internet connection");
            return;
        }
        // Otherwise, start the search
        // Reform the query to match TheGuardian API
        // For this, we've made a nested AsyncTask, so it has direct access to activity
        TheGuardianRequest request = new TheGuardianRequest();
        request.execute(queryText);
    }

    private void printError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        Log.i(getClass().getName(), errorMessage);
    }

    protected boolean isNetworkConnected() {

        // Instantiate mConnectivityManager if necessary
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    class TheGuardianRequest extends AsyncTask<String, Object, ArrayList<NewsArticle>> {

        @Override
        protected ArrayList<NewsArticle> doInBackground(String... strings) {
//            android.os.Debug.waitForDebugger();
            // Stop if cancelled
            if (isCancelled()) {
                return null;
            }
            ArrayList<NewsArticle> news = null;
            String apiUrlString = "http://content.guardianapis.com/search?q="
                    + strings[0] + "&format=json&api-key=test";
            HttpURLConnection connection = null;
            // Build Connection.
            try {
                URL url = new URL(apiUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.setReadTimeout(5000); // 5 seconds
                connection.setConnectTimeout(5000); // 5 seconds

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    Log.w(getClass().getName(),
                            "GoogleBooksAPI request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }
                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                Log.d(getClass().getName(), "Response String: " + responseString);
                // Close connection and return response code.
                connection.disconnect();

                JSONObject responseJson = new JSONObject(responseString);
                Log.i(getClass().getName(), responseJson.toString());
                JSONObject response = responseJson.getJSONObject("response");
                JSONArray articles = response.getJSONArray("results");

                news = new ArrayList<>();

                for (int i = 0; i < articles.length(); i += 1) {
                    // Get an article from the array
                    JSONObject currentArticle = articles.getJSONObject(i);
                    // We want section, title, and web link of the articles, so let's read them
                    String title = currentArticle.getString("webTitle");
                    String section = currentArticle.getString("sectionName");
                    String webUrl = currentArticle.getString("webUrl");

                    news.add(new NewsArticle(section, title, webUrl));
                }
            } catch (MalformedURLException e) {
                Log.i(getClass().getName(), e.getMessage());
            } catch (ProtocolException e) {
                Log.i(getClass().getName(), e.getMessage());
            } catch (IOException e) {
                Log.i(getClass().getName(), e.getMessage());
            } catch (JSONException e) {
                Log.i(getClass().getName(), e.getMessage());
            }
            return news;
        }

        // After the query finishes, populate the view
        @Override
        protected void onPostExecute(ArrayList<NewsArticle> news) {
            TextView helpTextView = (TextView) findViewById(R.id.help_text_view);
            // in case we haven't received any books
            if (news == null || news.size() == 0) {
                mNewsList.setVisibility(View.GONE);
                helpTextView.setVisibility(View.VISIBLE);
                helpTextView.setText("Your query didn't return any results");
                Log.i(getClass().getName(), "News is null");
                return;
            } else {
                helpTextView.setVisibility(View.GONE);
                mNewsList.setVisibility(View.VISIBLE);
                mNewsList.setAdapter(new NewsAdapter(MainActivity.this, news));
                mNewsList.invalidateViews();
            }
        }
    }
}

