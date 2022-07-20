package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import io.realm.Realm;

@EActivity(R.layout.activity_view_book_screen)
public class ViewBookScreen extends AppCompatActivity {

    @ViewById
    ImageView bookCoverImageView;

    @ViewById
    TextView bookTitleTextView;

    @ViewById
    TextView authorTextView;

    @ViewById
    RatingBar ratingBar;

    @ViewById
    TextView notesTextView;

    private Book book;
    private String bookUUID;
    private Realm realm;

    @AfterViews
    public void init(){
        realm = Realm.getDefaultInstance();
        bookUUID = getIntent().getStringExtra("bookUUID");

        book = realm.where(Book.class).equalTo("uuid", bookUUID).findFirst();
        Log.d("Debug", book.getBookTitle()+"=====");
        bookTitleTextView.setText(book.getBookTitle());
        authorTextView.setText(book.getAuthor());
        ratingBar.setRating(book.getRating());
        notesTextView.setText(book.getNotes());

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

    @Click(R.id.editBookButton)
    public void editBook(){
        EditBookScreen_.intent(this).extra("bookUUID", bookUUID).start();
        finish();
    }

    private void refreshImageView(File savedImage) {
        // this will put the image saved to the file system to the imageview
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(bookCoverImageView);
    }
}