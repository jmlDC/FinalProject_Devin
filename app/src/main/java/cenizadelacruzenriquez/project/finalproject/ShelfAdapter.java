package cenizadelacruzenriquez.project.finalproject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class ShelfAdapter extends RealmRecyclerViewAdapter<Shelf, ShelfAdapter.ViewHolder> {

    // THIS DEFINES WHAT VIEWS YOU ARE FILLING IN
    public class ViewHolder extends RecyclerView.ViewHolder {

        // have a field for each one
        TextView shelfName;
        TextView shelfDescription;

        ImageButton edit;
        ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize them from the itemView using standard style
            shelfName = itemView.findViewById(R.id.shelfNameInRowTextView);
            shelfDescription = itemView.findViewById(R.id.shelfDescriptionInRowTextView);

            edit = itemView.findViewById(R.id.editImageButton);
            delete = itemView.findViewById(R.id.deleteImageButton);

        }
    }


    // IMPORTANT
    // THE CONTAINING ACTIVITY NEEDS TO BE PASSED SO YOU CAN GET THE LayoutInflator(see below)
    ShelfRecyclerViewScreen activity;

    public ShelfAdapter(ShelfRecyclerViewScreen activity, @Nullable OrderedRealmCollection<Shelf> data, boolean autoUpdate) {
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // create the raw view for this ViewHolder
        View v = activity.getLayoutInflater().inflate(R.layout.shelf_row_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE

        // assign view to the viewholder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gives you the data object at the given position
        Shelf u = getItem(position);


        // copy all the values needed to the appropriate views
        holder.shelfName.setText(u.getShelfName());
        holder.shelfDescription.setText(u.getShelfDescription());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.editRow(u);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.deleteRow(u);

            }
        });

        holder.shelfName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openBookList(u);
            }
        });


    }

}
