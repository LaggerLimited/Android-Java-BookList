# BookList Application

BookList is an Android application that uses JSON to query the Google Books API and create a list of books. This operation is ran asynchronously in the background. After the user enters their search terms a ListView works along with a BookAdapter to populate the list. The list is inflated using view recycling to minimize load on the device. 

## Installation

Debug package for download:

[BookList App](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList-debug.zip)

## Usage

After installing the .APK file use the search bar at the top of the list to query the Google Books API. The list features the books’ picture, title, author, and price. More information can be displayed by selecting a book from the list.

## Java Code

* [Book Object](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList/app/src/main/java/com/example/android/booklist/Book.java): Represents one book entry returned from a search done on the Google Books API.

* [Book List Activity](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList/app/src/main/java/com/example/android/booklist/BookListActivity.java): Main Activity that initializes values, instantiates Views, and defines the asynchronous task.

* [Book Adapter](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList/app/src/main/java/com/example/android/booklist/BookAdapter.java): Inflates the array with book objects and handles the downloading of book images using asynchronous task.

* [QueryUtils](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList/app/src/main/java/com/example/android/booklist/QueryUtils.java): Helps request and receive book data by parsing JSON and creating book objects. 

## XML Code

* [Screen Layout](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList/app/src/main/res/layout/booklist_main.xml): Defines the ListView and describes how each list item will be displayed.

* [List Item](https://github.com/LaggerLimited/Android-Java-BookList/blob/master/BookList/app/src/main/res/layout/list_item.xml): Defines how the data will be displayed for each individual book.

## Contributing
Pull requests are welcome.

For major changes, please open an issue first to discuss what you would like to change.
