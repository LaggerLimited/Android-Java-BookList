package com.example.android.booklist;
/**
 * This is a book object that represents one book entry returned from a search done
 * on the Google Books API.
 */
public class Book {
    /** Title */
    private String mTitle;
    /** Author */
    private String mAuthor;
    /** Price Type */
    private String mPriceType;
    /** Price */
    private double mPrice;
    /** Currency Code*/
    private String mCurrencyCode;
    /** Information Url */
    private String mUrl;
    /** Image Url */
    private String mImage;
    /**
     * Create a new Book object.
     *
     * @param title the name of the book
     * @param author the author(s) of the book
     * @param priceType the type of price(retail/list/NFS)
     * @param price the price of the book
     * @param currencyCode the denomination of the currency listed
     * @param infoURL the URL used to give information about the book
     * @param imgURL the URL used to display a thumbnail image of the book
     */
    public Book(String title, String author, String priceType, double price, String currencyCode, String infoURL, String imgURL) {
        mTitle = title;
        mAuthor = author;
        mPriceType = priceType;
        mPrice = price;
        mCurrencyCode = currencyCode;
        mUrl = infoURL;
        mImage = imgURL;
    }

    public String getmTitle() {
        return mTitle;
    }
    public String getmAuthor() {
        return mAuthor;
    }
    public String getmPriceType() {
        return mPriceType;
    }
    public double getmPrice() {
        return mPrice;
    }
    public String getmUrl() {
        return mUrl;
    }
    public String getmImage() {
        return mImage;
    }
    public String getmCurrencyCode() {
        return mCurrencyCode;
    }
    public boolean getmPriceToList () {
        if(mPrice == 0.00){
            return false;
        }else{
            return true;
        }
    }
}
