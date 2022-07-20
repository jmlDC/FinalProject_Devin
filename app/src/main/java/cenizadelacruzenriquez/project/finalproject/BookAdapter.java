package cenizadelacruzenriquez.project.finalproject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


// the parameterization <type of the RealmObject, ViewHolder type)
public class BookAdapter extends RealmRecyclerViewAdapter<Book, BookAdapter.ViewHolder> {

    // THIS DEFINES WHAT VIEWS YOU ARE FILLING IN
    public class ViewHolder extends RecyclerView.ViewHolder {

        // have a field for each one
        TextView title;
        TextView author;
        RatingBar rating;
        ImageView pic;
        ImageButton edit;
        ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize them from the itemView using standard style
            title = itemView.findViewById(R.id.bookTitleBookLayoutTextView);
            author = itemView.findViewById(R.id.bookAuthorBookLayoutTextView);
            rating = itemView.findViewById(R.id.bookLayoutRatingBar);
            edit = itemView.findViewById(R.id.editBookLayoutImageButton);
            delete = itemView.findViewById(R.id.deleteBookLayoutImageButton);
            pic = itemView.findViewById(R.id.bookImageViewBookLayout);

        }
    }


    // IMPORTANT
    // THE CONTAINING ACTIVITY NEEDS TO BE PASSED SO YOU CAN GET THE LayoutInflator(see below)
    BookRecyclerViewScreen activity;

    public BookAdapter(BookRecyclerViewScreen activity, @Nullable OrderedRealmCollection<Book> data, boolean autoUpdate) {
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // create the raw view for this ViewHolder
        View v = activity.getLayoutInflater().inflate(R.layout.book_row_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE

        // assign view to the viewholder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gives you the data object at the given position
        Book b = getItem(position);

        // copy all the values needed to the appropriate views
        holder.title.setText(b.getBookTitle());
        holder.author.setText(b.getAuthor());
        holder.rating.setRating(b.getRating());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.edit(b);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.delete(b);

            }
        });

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openBookInfo(b);
            }
        });


        File getImageDir = activity.getExternalCacheDir();


        // this will put the image saved to the file system to the imageview
        if(b.getPath()!=null) {
            // just a sample, normally you have a diff image name each time
            File file = new File(getImageDir, b.getPath());

            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.pic);
        }
        else{
            holder.pic.setImageResource(R.mipmap.ic_launcher);
        }


    }

}
