package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_shelf_recycler_view_screen)
public class ShelfRecyclerViewScreen extends AppCompatActivity {

    Realm realm;

    String ownerUUID;

    @ViewById
    RecyclerView shelfRecyclerView;

    @ViewById(R.id.addShelfButton)
    Button addShelfButton;

    @ViewById(R.id.clearShelvesButton)
    Button clearShelvesButton;

    @Click(R.id.addShelfButton)
    public void goToAddShelf(){
        AddShelfScreen_.intent(this).extra("ownerUUID", ownerUUID).start();
    }

    @Click(R.id.clearShelvesButton)
    public void clearShelves(){
        // https://stackoverflow.com/questions/14425826/variable-is-accessed-within-inner-class-needs-to-be-declared-final

        // https://www.geeksforgeeks.org/android-alert-dialog-box-and-how-to-create-it
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to DELETE ALL SHELVES?");
        builder.setTitle("Confirm Delete");
        builder.setCancelable(true);

        // final String testing = "";
        // testing = "";

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                // testing = "hello world";
                try {
                    realm.beginTransaction();
                    realm.deleteAll();  // save
                    realm.commitTransaction();

                    Toast toast = Toast.makeText(ShelfRecyclerViewScreen.this, "Deleted", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception e) {
                    Toast toast = Toast.makeText(ShelfRecyclerViewScreen.this, "Deletion Error", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();

    }

    @AfterViews
    public void init(){
        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        shelfRecyclerView.setLayoutManager(mLayoutManager);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        // query the things to display

        ownerUUID = getIntent().getStringExtra("uuid");
        RealmResults<Shelf> results = realm.where(Shelf.class).equalTo("ownerUUID", ownerUUID).findAll();

        // Adapter?
        ShelfAdapter adapter = new ShelfAdapter(this, results, true);
        shelfRecyclerView.setAdapter(adapter);
    }

    // NEVER FORGETTI
    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }

    public void deleteRow(Shelf u){
        if(u.isValid()){
            realm.beginTransaction();
            u.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    Shelf editedRow;

    public void editRow(Shelf u){
        if(u.isValid()){
            editedRow = u;
            EditShelfScreen_.intent(this).extra("UUIDToEdit", editedRow.getUuid()).start();
            // RegisterScreen_.intent(this).extra("UUIDToEdit", u.getUuid()).extra("allowEdit", true).start();
        }
    }

    public void openBookList(Shelf u) {
        BookRecyclerViewScreen_.intent(ShelfRecyclerViewScreen.this).extra("shelfUUID", u.getUuid()).start();
    }
}