package com.example.android.booklist;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import static com.example.android.booklist.BookListActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving book data from Google Books.
 */
public final class QueryUtils {
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed)
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response
     */
    public static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE
        try {
            // Build up a list of Book objects with the corresponding data
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            JSONArray bookArray;
            // Try to parse the baseJsonResponse and get the JSONArray with the key "items"
            if (baseJsonResponse.has("items")) {
                // Parse the items field
                bookArray = baseJsonResponse.getJSONArray("items");
            } else {
                // Unable to get the array with the key "items" return a null List<Book> object
                return null;
            }

            // Get Volume Info
            for (int i = 0; i < bookArray.length(); i++){
                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                // Extract the value(s) for the key called "authors"
                // Clear the authors string object
                String authors = "";
                // Try to parse the data for authors if none is present then return "No Author"
                if (volumeInfo.has("authors")) {
                    // parse the authors field
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    // Build the authors string object
                    for (int j =0; j < authorsArray.length(); j++){
                        // Add comma if more than one author is present
                        if ( j > 0){
                            authors = authors + ", ";
                        }
                        authors = authors + authorsArray.getString(j);
                    }
                } else {
                    // No authors are present
                    authors = "No Author";
                }

                /*
                 * Extract the value for the price type that will be displayed (Retail/List)
                 * and the price amount that corresponds to the correct price type (if it applies)
                 */
                // Clear the variables
                double amount = 0.00;
                String currencyCode = "";
                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
                // Check to make sure the book is for sale
                String priceType = saleInfo.getString("saleability");
                if (priceType.equals("NOT_FOR_SALE")) {
                    //This book cannot be sold
                    priceType = "Not For Sale";
                } else if (priceType.equals("FOR_SALE")) {
                    // This book can be sold
                    // Find the correct price listing
                    if (saleInfo.getJSONObject("retailPrice") != null) {
                        // Set the price type
                        priceType = "Retail:";
                        // Extract the value for the key called "amount"
                        JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                        amount = retailPrice.getDouble("amount");
                        // Extract the value for the key called "currencyCode"
                        currencyCode = retailPrice.getString("currencyCode");
                    } else if (saleInfo.getJSONObject("listPrice") != null) {
                        // Set the price type
                        priceType = "List:";
                        // Extract the value for the key called "amount"
                        JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                        amount = listPrice.getDouble("amount");
                        // Extract the value for the key called "currencyCode"
                        currencyCode = listPrice.getString("currencyCode");
                    } else {
                        // Something has gone wrong return a price error
                        priceType = "Pricing Error";
                    }
                } else if (priceType.equals("FREE")) {
                    // This book has no charge
                    priceType = "FREE";
                } else if (priceType.equals("FOR_PREORDER")) {
                    // This book is not available yet but can be pre-ordered
                    priceType = "Pre-Order Only";
                }else if (priceType.equals("FOR_SALE_AND_RENTAL")) {
                    // This book is available to purchase and rent
                    priceType = "Sale and Rental Available";
                }else {
                    // Something has gone wrong so return a string that communicates this
                    priceType = "Pricing Error";
                }

                // Extract the value for the key called "infoLink"
                String infoLinkURL = volumeInfo.getString("infoLink");

                // Extract the value for the key called "thumbnail" if it exists
                String imageURL = "";
                if (volumeInfo.has("imageLinks")) {
                    // Parse the imageLinks field
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    imageURL = imageLinks.getString("thumbnail");
                } else {
                    // No imageLinks object could be found
                    imageURL = "NONE";
                }

                /**
                 * Create a new Book object and then add the object to the ArrayList
                 */
                Book book = new Book(title, authors, priceType, amount, currencyCode, infoLinkURL, imageURL);
                books.add(book);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // Return the list of books
        return books;
    }

    /**
     * Query the Google Books data set and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link Book}s
        return books;
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200)
            // Then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
