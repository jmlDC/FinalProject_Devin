package cenizadelacruzenriquez.project.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_shelf_recycler_view_screen)
public class ShelfRecyclerViewScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf_recycler_view_screen);
    }
}