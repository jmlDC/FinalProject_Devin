package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

@EActivity(R.layout.activity_edit_book_screen)
public class EditBookScreen extends AppCompatActivity {

    private Realm realm;
    private String bookUUID;
    private String fileName;
    private Book book;

    @ViewById
    EditText bookTitleEditField;

    @ViewById
    EditText bookAuthorEditField;

    @ViewById
    EditText notesEditBookMultiLine;

    @ViewById
    RatingBar addBookRatingEditBook;

    @ViewById
    ImageView imageViewEditBook;


    @AfterViews
    public void init()
    {
        realm = Realm.getDefaultInstance();
        bookUUID = getIntent().getStringExtra("bookUUID");

        book = realm.where(Book.class).equalTo("uuid", bookUUID).findFirst();
        bookTitleEditField.setText(book.getBookTitle());
        bookAuthorEditField.setText(book.getAuthor());
        notesEditBookMultiLine.setText(book.getNotes());
        addBookRatingEditBook.setRating(book.getRating());

        File getImageDir = getExternalCacheDir();

        if (book.getPath()!=null){
            File savedImage = new File(getImageDir, book.getPath());
            if (savedImage.exists()) {
                refreshImageView(savedImage);
            }
        }

    }

    public void onDestroy()
    {
        super.onDestroy();
        if(!realm.isClosed())
        {
            realm.close();
        }
    }

    @Click(R.id.cancelEditBookButton)
    public void cancel()
    {
        finish();
    }

    @Click(R.id.saveEditBookButton)
    public void save() {
        String title = bookTitleEditField.getText().toString();
        String author = bookAuthorEditField.getText().toString();
        String notes = notesEditBookMultiLine.getText().toString();
        Float rating = addBookRatingEditBook.getRating();

        realm.beginTransaction();

        if (book.isValid()){
            book.setBookTitle(title);
            book.setAuthor(author);
            book.setNotes(notes);
            book.setRating(rating);
            if (fileName!=null){
                book.setPath(fileName);
            }

            realm.copyToRealmOrUpdate(book);
        }

        realm.commitTransaction();


        String message = book.getBookTitle() + " has been updated";
        Toast toast = Toast.makeText(this, message , Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }

    public static int REQUEST_CODE_IMAGE_SCREEN_register = 1;

    @Click(R.id.imageViewEditBook)
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
                .into(imageViewEditBook);
    }







}