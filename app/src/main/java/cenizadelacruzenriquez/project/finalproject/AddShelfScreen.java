package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.UUID;

import io.realm.Realm;

@EActivity(R.layout.activity_add_shelf_screen)
public class AddShelfScreen extends AppCompatActivity {

    Realm realm;
    String ownerUUID;

    @ViewById(R.id.shelfNameField)
    EditText shelfNameField;

    @ViewById(R.id.shelfDescriptionField)
    EditText shelfDescriptionField;

    @ViewById(R.id.saveShelfButton)
    Button saveShelfButton;

    @ViewById(R.id.cancelShelfButton)
    Button cancelShelfButton;

    @Click(R.id.cancelShelfButton)
    public void cancel(){
        finish();
    }

    @Click(R.id.saveShelfButton)
    public void saveShelf(){
        String uuid = UUID.randomUUID().toString();
        String name = shelfNameField.getText().toString();
        String desc = shelfDescriptionField.getText().toString();
        // ownerUUID = getIntent().getStringExtra("ownerUUID");

        Shelf shelf = new Shelf();

        shelf.setUuid(uuid);
        shelf.setShelfName(name);
        shelf.setShelfDescription(desc);
        shelf.setOwnerUUID(ownerUUID);

        long count = 0;

        try {
            realm.beginTransaction();
            // Log.i("Reg Screen", "I reached after begin.");
            realm.copyToRealmOrUpdate(shelf);  // save
            // Log.i("Reg Screen", "I reached after save.");
            realm.commitTransaction();
            // Log.i("Reg Screen", "I reached after commit.");

            count = realm.where(Shelf.class).equalTo("ownerUUID", ownerUUID).count();
            Toast toast = Toast.makeText(this, "Saved: "+count, Toast.LENGTH_LONG);
            toast.show();

        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "Error saving SHELF", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }

        finish();
    }

    @AfterViews
    public void init(){
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        ownerUUID = getIntent().getStringExtra("ownerUUID");
    }




}