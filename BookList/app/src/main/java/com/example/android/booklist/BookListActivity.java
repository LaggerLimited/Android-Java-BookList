package com.example.android.booklist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    // User input variable for the URL string
    String userInputString = "";

    /** Adapter for the list of books */
    private BookAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** View that is displayed when the list is loading */
    View loadingIndicator;

    /** Object that contains the network information of the device */
    NetworkInfo networkInfo;

    /** URL for book data from the Google Books data set */
    private String BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?q="+ userInputString + "&maxResults=10";

    public static final String LOG_TAG = BookListActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist_main);

        // Find a reference to the empty Text View
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Find a reference to the loading indicator
        loadingIndicator = findViewById(R.id.loading_indicator);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.book_list);

        // Create a new adapter for the list of books
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Click Listener for when the user clicks on a Book object
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getmUrl());
                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the entry button
        Button entryButton = (Button)findViewById(R.id.entry_button);
        // Get a reference to the search_entry EditText object
        final EditText userInput = (EditText)findViewById(R.id.search_entry);
        // Click Listener for the search button
        entryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Clear the old data
                mAdapter.clear();
                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                // Get details on the currently active default data network
                networkInfo = connMgr.getActiveNetworkInfo();
                // Remove the text from the empty_view
                mEmptyStateTextView.setText("");
                // Make the loading indicator visible
                loadingIndicator.setVisibility(View.VISIBLE);
                // Get the user data from the EditText object
                userInputString = userInput.getText().toString();
                // Change the BOOKS_REQUEST_URL
                BOOKS_REQUEST_URL =
                        "https://www.googleapis.com/books/v1/volumes?q="+ userInputString + "&maxResults=10";
                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Get the new book data
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(BOOKS_REQUEST_URL);
                }else{
                    // Otherwise, inform the user of an error
                    // Hide loading indicator so error message will be visible
                    loadingIndicator.setVisibility(View.GONE);
                    // Update empty state with no connection error message
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                }
            }
        });
    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Book> result = QueryUtils.fetchBookData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Book> data) {
            // Hide loading indicator because the data has been loaded
            loadingIndicator.setVisibility(View.GONE);
            // Clear the adapter of previous book data
            mAdapter.clear();
            // If there is a valid list of {@link Book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }else{
                // There is not data so inform the user there are no results
                mEmptyStateTextView.setText(R.string.no_results);
            }
        }
    }
}
