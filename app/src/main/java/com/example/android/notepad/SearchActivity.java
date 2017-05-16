package com.example.android.notepad;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SearchActivity extends ListActivity {

    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE, //2
            NotePad.Notes.COLUMN_NAME_GROUPID
    };

    private static final String selection = NotePad.Notes.COLUMN_NAME_TITLE+" like ? ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
           // Toast.makeText(this,query,Toast.LENGTH_SHORT).show();


           String [] selectionArgs = {query+"%"};

            Cursor cursor =  getContentResolver().query(
                    getIntent().getData(),            // Use the default content URI for the provider.
                    PROJECTION,                      // Return the note ID and title for each note.
                    selection,
                    selectionArgs,
                    NotePad.Notes.DEFAULT_SORT_ORDER  // Use the default sort order.
            );

            // The names of the cursor columns to display in the view, initialized to the title column
            String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE ,NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE} ;

            // The view IDs that will display the cursor columns, initialized to the TextView in
            // noteslist_item.xml
            int[] viewIDs = { android.R.id.text1 ,R.id.dateText };

            // Creates the backing adapter for the ListView.
            SimpleCursorAdapter adapter
                    = new SimpleCursorAdapter(
                    this,                             // The Context for the ListView
                    R.layout.noteslist_item,          // Points to the XML for a list item
                    cursor,                           // The cursor to get items from
                    dataColumns,
                    viewIDs
            );

            // Sets the ListView's adapter to be the cursor adapter that was just created.
            setListAdapter(adapter);

        }

    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        // Constructs a new URI from the incoming URI and the row ID
        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

        // Gets the action from the incoming Intent
        String action = getIntent().getAction();

        // Handles requests for note data
        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {

            // Sets the result to return to the component that called this Activity. The
            // result contains the new URI
            setResult(RESULT_OK, new Intent().setData(uri));
        } else {

            // Sends out an Intent to start an Activity that can handle ACTION_EDIT. The
            // Intent's data is the note ID URI. The effect is to call NoteEdit.
            startActivity(new Intent(Intent.ACTION_EDIT, uri));
        }
    }
}
