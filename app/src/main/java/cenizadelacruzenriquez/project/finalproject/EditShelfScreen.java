package cenizadelacruzenriquez.project.finalproject;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_edit_user_screen)
public class EditUserScreen extends AppCompatActivity {

    /* Registration Function */

    SharedPreferences prefs;

    Realm realm;

    @ViewById
    EditText usernameRegisterField;

    @ViewById
    EditText passwordRegisterField;

    @ViewById
    EditText confirmPasswordRegisterField;

    @ViewById
    Button saveRegisterButton;

    @ViewById
    ImageView imageViewEdit;

    @Click(R.id.saveRegisterButton)
    public void saveRegister(){
        realm = Realm.getDefaultInstance();
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String uuidEditingValue = prefs.getString("uuidEdit", null);

        // Blank name
        if(usernameRegisterField.getText().toString().equals("")){
            Toast toast = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_SHORT);
            toast.show();
        }

        // Mismatched passwords
        else if(!passwordRegisterField.getText().toString().equals(confirmPasswordRegisterField.getText().toString())) {
            Toast toast = Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT);
            toast.show();
        }

        // Success, check for existing users
        else{
            // Check for existing users with same username
            RealmResults<User> matchUsername = realm.where(User.class)
                    .equalTo("name", usernameRegisterField.getText().toString(), Case.INSENSITIVE)
                    .notEqualTo("uuid", uuidEditingValue)
                    .findAll();

            for (User u : matchUsername)
            {
                Log.d("Realm results", u.toString());
            }

            // If a user matches searched
            if(matchUsername.isEmpty()){
                realm.beginTransaction();
                RealmResults<User> matchUuid = realm.where(User.class)
                        .equalTo("uuid", uuidEditingValue)
                        .findAll();

                User editedUser = matchUuid.first();

                editedUser.setName(usernameRegisterField.getText().toString());
                editedUser.setPassword(passwordRegisterField.getText().toString());

                realm.copyToRealmOrUpdate(editedUser);
                realm.commitTransaction();

                // Clear shared preferences containing uuid for account being edited
                prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("uuidEdit", null);
                edit.apply();

//                RealmResults<User> allResults = realm.where(User.class)
//                        .findAll();

                Toast toast = Toast.makeText(this, "User edited.", Toast.LENGTH_SHORT);
                toast.show();

//                Log.d("All users","-------------------------------------------");
//                for (User u : allResults)
//                {
//                    Log.d("Realm results", u.toString());
//                }

                openLoginScreen();
            }

            else{
                Toast toast = Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

    }

    @AfterViews
    public void init()
    {
        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String uuidEditingValue = prefs.getString("uuidEdit", null);

        if(uuidEditingValue == null){
            // Do nothing
        }
        else{

            RealmResults<User> matchAccount = realm.where(User.class)
                    .equalTo("uuid", uuidEditingValue, Case.INSENSITIVE)
                    .findAll();

            String usernameValue = matchAccount.first().getName();
            String passwordValue = matchAccount.first().getPassword();

            usernameRegisterField.setText(usernameValue);
            passwordRegisterField.setText(passwordValue);
            confirmPasswordRegisterField.setText(passwordValue);

            File getImageDir = getExternalCacheDir();
            // this will put the image saved to the file system to the imageview
            if(matchAccount.first().getPath()!=null) {
                // just a sample, normally you have a diff image name each time
                File file = new File(getImageDir, matchAccount.first().getPath());

                Picasso.get()
                        .load(file)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(imageViewEdit);
            }
            else{
                // Default string
            }
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }

    /* Cancel Button Navigation*/
    @ViewById
    Button cancelRegisterButton;

    @Click(R.id.cancelRegisterButton)
    public void cancelRegister(){
        openLoginScreen();
    }

    public void openLoginScreen(){
        finish();
    }

    // ----------------
    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    @Click(R.id.imageViewEdit)
    public void clickRegisterPicture() { setEditPicture(); }

    public void setEditPicture() {
        ImageActivity_.intent(this).startForResult(REQUEST_CODE_IMAGE_SCREEN);
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

                    prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    String uuidEditingValue = prefs.getString("uuidEdit", null);

                    String filename = System.currentTimeMillis()+".jpeg";
                    File savedImage = saveFile(jpeg, filename);

//                    User newUser = new User();
//                    newUser.setUuid(randomUUID);
//                    newUser.setPath(filename);

                    realm.beginTransaction();

                    User c = realm.where(User.class).equalTo("uuid",uuidEditingValue).findFirst();
                    c.setPath(filename);

                    realm.copyToRealmOrUpdate(c);
                    realm.commitTransaction();

                    File getImageDir = getExternalCacheDir();

                    if (filename != null) {
                        // just a sample, normally you have a diff image name each time
                        File file = new File(getImageDir, filename);

                        Picasso.get()
                                .load(file)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(imageViewEdit);
                    }

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