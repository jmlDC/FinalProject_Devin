package cenizadelacruzenriquez.project.finalproject;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {

    // THIS DEFINES WHAT VIEWS YOU ARE FILLING IN
    public class ViewHolder extends RecyclerView.ViewHolder {

        // have a field for each one
        // TextView uuidTextView;
        TextView nameTextView;
        TextView passwordTextView;

        ImageButton deleteImageButton;
        ImageButton editImageButton;

        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // initialize them from the itemView using standard style
            nameTextView = itemView.findViewById(R.id.bookTitleBookLayoutTextView);
            passwordTextView = itemView.findViewById(R.id.bookAuthorBookLayoutTextView);

            // initialize the buttons in the layout
            deleteImageButton = itemView.findViewById(R.id.deleteShelfLayoutImageButton);
            editImageButton = itemView.findViewById(R.id.editImageButton);

            // initialize the pic in the layout
            pic = itemView.findViewById(R.id.imageViewRow);
        }
    }



    // IMPORTANT
    // THE CONTAINING ACTIVITY NEEDS TO BE PASSED SO YOU CAN GET THE LayoutInflator(see below)
    UserRecyclerViewScreen activity;

    public UserAdapter(UserRecyclerViewScreen activity, @Nullable OrderedRealmCollection<User> data, boolean autoUpdate) {
        super(data, autoUpdate);

        // THIS IS TYPICALLY THE ACTIVITY YOUR RECYCLERVIEW IS IN
        this.activity = activity;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // create the raw view for this ViewHolder
        View v = activity.getLayoutInflater().inflate(R.layout.user_row_layout, parent, false);  // VERY IMPORTANT TO USE THIS STYLE

        // assign view to the viewholder
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gives you the data object at the given position
        User u = getItem(position);

        // copy all the values needed to the appropriate views
        holder.nameTextView.setText(u.getName());
        holder.passwordTextView.setText(u.getPassword());


        // From https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        activity.delete(u);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        holder.editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.edit(u);
            }
        });


        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.selectPic(u.getUuid());
            }
        });


        File getImageDir = activity.getExternalCacheDir();


        // this will put the image saved to the file system to the imageview
        if(u.getPath()!=null) {
            // just a sample, normally you have a diff image name each time
            File file = new File(getImageDir, u.getPath());

            Picasso.get()
                    .load(file)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.pic);
        }
        else{
            // Default string
        }

    }



}

