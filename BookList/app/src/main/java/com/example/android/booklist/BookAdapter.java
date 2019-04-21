package com.example.android.booklist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import static com.example.android.booklist.R.id.price;

/**
 * This class uses a list of Book objects to populate an array that is then inflated using view
 * recycling. This class also handles the downloading of the images from a URL using an AsyncTask.
 */

public class BookAdapter extends ArrayAdapter<Book>{
    /**
     * Create a new {@link BookAdapter} object.
     *
     * @param context    is the current context (i.e. Activity) that the adapter is being created in.
     * @param book is the list of {@link  Book}s to be displayed.
     */
    public BookAdapter(Context context, List<Book> book) {
        super(context, 0, book);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);
        // Find the TextView with the ID book_title
        TextView bookTitleView = (TextView) listItemView.findViewById(R.id.book_title);
        // Display the title of the book in that TextView
        bookTitleView.setText(currentBook.getmTitle());

        // Find the book_author TextView and display the author of the book in the TextView
        TextView bookAuthorView = (TextView) listItemView.findViewById(R.id.book_author);
        bookAuthorView.setText(currentBook.getmAuthor());

        // Find the price_type TextView and display the price type of the book in the TextView
        TextView priceTypeView = (TextView) listItemView.findViewById(R.id.price_type);
        priceTypeView.setText(currentBook.getmPriceType());

        // Find the price TextView and display the price of the book in the TextView
        TextView priceView = (TextView) listItemView.findViewById(price);
        // Remove the price TextView if the book has no price to list
        if (!currentBook.getmPriceToList()){
            priceView.setVisibility(View.GONE);
        }else{
            priceView.setVisibility(View.VISIBLE);
            priceView.setText(formatPrice(currentBook));
        }

        // Find the image_thumb Image View and display the thumbnail image of the book
        ImageView thumbImageView = (ImageView) listItemView.findViewById(R.id.image_thumb);
        if(!currentBook.getmImage().equals("NONE")){
            // Run an AsyncTask to download the image for the book object
            new DownloadImageTask(thumbImageView)
                    .execute(currentBook.getmImage());
        }else{
            // There is no image thumbnail available so set the "no image" image
            thumbImageView.setImageResource(R.drawable.no_image_available);
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted price string (i.e. "$9999.99") from a NumberFormat object
     * This string will change depending on the currency code of the {@link  Book} object
     */
    private String formatPrice(Book currentBook) {
        // Get the current book's price
        double price = currentBook.getmPrice();
        // Create a currency instance of a Number Format object
        NumberFormat formatPrice = NumberFormat.getCurrencyInstance();
        // Get the currency code associated with the Book
        Currency currency = Currency.getInstance(currentBook.getmCurrencyCode());
        formatPrice.setCurrency(currency);
        // Format the price as currency in the correct denomination
        String result = formatPrice.format(price);
        return result;
    }

    /**
     * AsyncTask used to download each image associated with the {@link  Book} object
     * This is needed because each image takes time to download so a place holder image is used
     * in place of the image until it is downloaded.
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
