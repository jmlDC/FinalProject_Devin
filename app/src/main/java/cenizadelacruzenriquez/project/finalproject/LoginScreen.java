package cenizadelacruzenriquez.project.finalproject;

// Ralph Ceniza, Jose Mari Dela Cruz, Raphael Enriquez
// CSCI 281.04-F
// Final Project: Devin

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_main)
public class LoginScreen extends AppCompatActivity {

    /* Sign In Function from Login */
    SharedPreferences prefs;

    Realm realm;

    @ViewById
    EditText usernameLoginField;

    @ViewById
    EditText passwordLoginField;

    @ViewById
    Button signinLoginButton;

    @ViewById
    CheckBox rememberMeCheckBox;

    @Click(R.id.signinLoginButton)
    public void signinLogin() {
        prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        String uuidValue = prefs.getString("uuid", null);

        RealmResults<User> matchUsername = realm.where(User.class)
                .equalTo("name", usernameLoginField.getText().toString(), Case.INSENSITIVE)
                .findAll();

        if(matchUsername.isEmpty()){
            Toast toast = Toast.makeText(this, "No User found", Toast.LENGTH_SHORT);
            toast.show();
        }

        else{
            RealmResults<User> matchAccount = realm.where(User.class)
                    .equalTo("name", usernameLoginField.getText().toString(), Case.INSENSITIVE)
                    .equalTo("password", passwordLoginField.getText().toString())
                    .findAll();


            // No matching account found
            if(matchAccount.isEmpty()){
                Toast toast = Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT);
                toast.show();
            }

            else{
                SharedPreferences.Editor edit = prefs.edit();

                edit.putString("uuid", matchAccount.first().getUuid());
                edit.apply();

                // Remembered
                if (rememberMeCheckBox.isChecked()){
                    Toast toast = Toast.makeText(this, "User remembered", Toast.LENGTH_SHORT);
                    toast.show();
                    edit.putString("remembered", "yes");
                }

                else{
                    edit.putString("remembered", "no");
                }

                edit.apply();
                openWelcomeScreen(matchAccount.first().getUuid());
            }
        }

    }

    public void openWelcomeScreen(String uuid){
        ShelfRecyclerViewScreen_.intent(this).extra("uuid", uuid).start();
    }

    /* Remember Me Function in Login */
    @AfterViews
    public void init(){
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String rememberedValue = prefs.getString("remembered", null);
        String uuidRememberedValue = prefs.getString("uuid", null);

        if(rememberedValue == null){
            // Do nothing
        }
        else{
            if (rememberedValue.equals("yes") && !uuidRememberedValue.equals(null) && !realm.isEmpty()){
                RealmResults<User> matchAccount = realm.where(User.class)
                        .equalTo("uuid", uuidRememberedValue, Case.INSENSITIVE)
                        .findAll();

                String usernameValue = matchAccount.first().getName();
                String passwordValue = matchAccount.first().getPassword();

                rememberMeCheckBox.setChecked(true);
                usernameLoginField.setText(usernameValue);
                passwordLoginField.setText(passwordValue);
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

    /* Register Navigation from Login */
    @ViewById
    Button userManagementLoginButton;

    @Click(R.id.userManagementLoginButton)
    public void clickUserManagement(){
        openUserManagementScreen();
    }

    public void openUserManagementScreen(){
        UserRecyclerViewScreen_.intent(this).start();
    }

}