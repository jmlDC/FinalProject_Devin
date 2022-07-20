package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_user_recycler_view_screen)
public class UserRecyclerViewScreen extends AppCompatActivity {

    @ViewById
    RecyclerView userRecyclerView;

    Realm realm;
    SharedPreferences prefs;

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public void edit(User c){
        if (c.isValid())
        {
            // Store UUID of User being edited to "pass" user to edit screen
            prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("uuidEdit", c.getUuid().toString());
            edit.apply();

            openEditScreen();
        }
    }

    public void openEditScreen(){
        EditUserScreen_.intent(this).start();
    }

    public void delete(User c)
    {
        // need to check if previously deleted
        if (c.isValid())
        {
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
                                    // Clears remember me stuff
                                    prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                                    prefs.edit().clear().commit();

                                    realm.beginTransaction();
                                    c.deleteFromRealm();
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

    @AfterViews
    public void init()
    {
        // initialize RecyclerView
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        userRecyclerView.setLayoutManager(mLayoutManager);

        // initialize Realm
        realm = Realm.getDefaultInstance();

        // query the things to display
        RealmResults<User> list = realm.where(User.class).findAll();

        // initialize Adapter
        UserAdapter adapter = new UserAdapter(this, list, true);
        userRecyclerView.setAdapter(adapter);
    }

    @ViewById
    Button clearShelfButton;
    @Click(R.id.clearShelfButton)
    public void clickClearButton(){
        clearAllAccounts();
    }

    public void clearAllAccounts(){

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
                                // Clears remember me stuff
                                prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                                prefs.edit().clear().commit();

                                // initialize Realm
                                realm = Realm.getDefaultInstance();

                                // From https://stackoverflow.com/questions/35040599/how-can-i-delete-all-items-in-a-realmresults-list-fastest-and-easiest
                                RealmResults<User> list = realm.where(User.class).findAll();

                                realm.beginTransaction();
                                list.deleteAllFromRealm();
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

    @ViewById
    Button addUserButton;

    @Click(R.id.addUserButton)
    public void clickAddButton(){
        openRegisterScreen();
    }

    public void openRegisterScreen(){
        RegisterScreen_.intent(this).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_recycler_view_screen);
    }

    // ------------------------------------------ PIC

    String editedUuidForPic;

    public void selectPic(String uuid)
    {
        ImageActivity_.intent(this).startForResult(REQUEST_CODE_IMAGE_SCREEN);
        editedUuidForPic = uuid;
    }

    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                // receieve the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {

                    String filename = System.currentTimeMillis()+".jpeg";
                    File savedImage = saveFile(jpeg, filename);

                    realm.beginTransaction();

                    User c = realm.where(User.class).equalTo("uuid",editedUuidForPic).findFirst();
                    c.setPath(filename);

//                    realm.copyToRealmOrUpdate(c);
                    realm.commitTransaction();

                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private File saveFile(byte[] jpeg, String name) throws IOException
    {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, name);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }


    @AfterViews
    public void checkPermissions()
    {

        // REQUEST PERMISSIONS for Android 6+
        // THESE PERMISSIONS SHOULD MATCH THE ONES IN THE MANIFEST
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA

                )

                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            // all permissions accepted proceed
                            init();
                        }
                        else
                        {
                            // notify about permissions
                            toastRequirePermissions();
                        }
                    }
                })
                .check();

    }


    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }




}