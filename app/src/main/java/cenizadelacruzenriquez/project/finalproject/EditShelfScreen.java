package cenizadelacruzenriquez.project.finalproject;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Realm;

@EActivity(R.layout.activity_edit_shelf_screen)
public class EditShelfScreen extends AppCompatActivity {

    Realm realm;
    String ownerUUID;

    @ViewById(R.id.shelfNameFieldOnEdit)
    EditText shelfNameFieldOnEdit;

    @ViewById(R.id.shelfDescriptionFieldOnEdit)
    EditText shelfDescriptionFieldOnEdit;

    @ViewById(R.id.saveShelfButtonOnEdit)
    Button saveShelfButton;

    @ViewById(R.id.cancelShelfButtonOnEdit)
    Button cancelShelfButton;

    @Click(R.id.cancelShelfButtonOnEdit)
    public void cancel(){
        finish();
    }

    @Click(R.id.saveShelfButtonOnEdit)
    public void saveShelf(){
        String uuid = getIntent().getStringExtra("UUIDToEdit");
        String name = shelfNameFieldOnEdit.getText().toString();
        String desc = shelfDescriptionFieldOnEdit.getText().toString();

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

        String uuid = getIntent().getStringExtra("UUIDToEdit");
        ownerUUID = realm.where(Shelf.class).equalTo("uuid", uuid).findFirst().getOwnerUUID();
        String shelfName = realm.where(Shelf.class).equalTo("uuid", uuid).findFirst().getShelfName();
        String shelfDesc = realm.where(Shelf.class).equalTo("uuid", uuid).findFirst().getShelfDescription();

        shelfNameFieldOnEdit.setText(shelfName);
        shelfDescriptionFieldOnEdit.setText(shelfDesc);

        // ownerUUID = getIntent().getStringExtra("ownerUUID");
    }

}