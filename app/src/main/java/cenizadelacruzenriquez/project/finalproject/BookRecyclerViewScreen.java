package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_book_recycler_view_screen)
public class BookRecyclerViewScreen extends AppCompatActivity {

    private Realm realm;
    private String bookUUID;
    private String shelfUUID;
    private Shelf shelf;

    @ViewById
    RecyclerView bookRecyclerView;

    @ViewById
    TextView shelfNameTextView;

    @AfterViews
    public void init(){
        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        bookRecyclerView.setLayoutManager(mLayoutManager);

        // initialize Realm
        realm = Realm.getDefaultInstance();

        // query the things to display
        shelfUUID = getIntent().getStringExtra("shelfUUID");
        RealmResults<Book> results = realm.where(Book.class).equalTo("shelfUUID", shelfUUID).findAll();

        // initialize Adapter
        BookAdapter adapter = new BookAdapter(this, results, true);
        bookRecyclerView.setAdapter(adapter);

        shelf = realm.where(Shelf.class).equalTo("uuid", shelfUUID).findFirst();
        shelfNameTextView.setText(shelf.getShelfName());

    }

    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Click(R.id.addBooksButton)
    public void addBook(){
        AddBookScreen_.intent(this).extra("shelfUUID", shelfUUID).start();
    }

    @Click(R.id.clearBooksButton)
    public void clearBooks(){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.deleteprompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                realm.beginTransaction();
//                                realm.deleteAll();
                                RealmResults<Book> results = realm.where(Book.class).equalTo("shelfUUID", shelfUUID ).findAll();
                                for ( Book b: results) {
                                    b.deleteFromRealm();
                                }
                                realm.commitTransaction();
                            }
                        })
                .setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void edit(Book b){
        if(b.isValid()){
            bookUUID = b.getUuid();
            EditBookScreen_.intent(this).extra("bookUUID", bookUUID).start();
        }
    }

    public void delete(Book b){
        if (b.isValid()){
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.deleteprompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    realm.beginTransaction();
                                    b.deleteFromRealm();
                                    realm.commitTransaction();
                                }
                            })
                    .setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

    public void openBookInfo(Book b){
        bookUUID = b.getUuid();
        ViewBookScreen_.intent(this).extra("bookUUID", bookUUID).start();
    }







}