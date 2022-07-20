package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

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
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

@EActivity(R.layout.activity_add_book_screen)
public class AddBookScreen extends AppCompatActivity {

    Realm realm;
    String shelfUUID;
    String fileName;

    @ViewById
    EditText bookTitleAddField;

    @ViewById
    EditText bookAuthorAddField;

    @ViewById
    EditText notesAddBookMultiLine;

    @ViewById
    RatingBar addBookRatingAddBook;

    @ViewById
    ImageView imageViewAddBook;


    @AfterViews
    public void init()
    {
        realm = Realm.getDefaultInstance();
        shelfUUID = getIntent().getStringExtra("shelfUUID");
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(!realm.isClosed())
        {
            realm.close();
        }
    }

    @Click(R.id.cancelAddBookButton)
    public void cancel()
    {
        finish();
    }

    @Click(R.id.saveAddBookButton)
    public void save() {
        String title = bookTitleAddField.getText().toString();
        String author = bookAuthorAddField.getText().toString();
        String notes = notesAddBookMultiLine.getText().toString();
        Float rating = addBookRatingAddBook.getRating();

        Book b = new Book();

        b.setUuid(UUID.randomUUID().toString());
        b.setShelfUUID(shelfUUID);
        b.setBookTitle(title);
        b.setAuthor(author);
        b.setNotes(notes);
        b.setRating(rating);
        b.setPath(fileName);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(b);
        realm.commitTransaction();

        int countResult = (int) realm.where(Book.class).equalTo("shelfUUID", shelfUUID).count();

        String message = "New Book saved.  Total: " + countResult;
        Toast toast = Toast.makeText(this, message , Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }

    public static int REQUEST_CODE_IMAGE_SCREEN_register = 0;

    @Click(R.id.imageViewAddBook)
    public void selectPic() {
        ImageActivity_.intent(this).startForResult(REQUEST_CODE_IMAGE_SCREEN_register);
    }

    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN_register)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                // receieve the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    fileName = System.currentTimeMillis()+".jpeg";
                    File f = saveFile(jpeg, fileName);
                    refreshImageView(f);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private File saveFile(byte[] jpeg, String path) throws IOException
    {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, path);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }

    private void refreshImageView(File savedImage) {
        // this will put the image saved to the file system to the imageview
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageViewAddBook);
    }








}