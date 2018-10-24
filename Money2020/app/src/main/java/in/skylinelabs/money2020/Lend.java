package in.skylinelabs.money2020;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Lend extends AppCompatActivity implements  AsyncTaskComplete {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    private TextView mTextMessage;

    private List<LendDetails> parts = new ArrayList<LendDetails>();

    private ActionHandler actionHandler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    Intent intent = new Intent(Lend.this, Borrow.class);
                    startActivity(intent);
                    finish();
            }
            return false;
        }
    };



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int grantResult : grantResults) {
            // handle denied permissions
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        actionHandler = new ActionHandler( Lend.this);
        actionHandler.getAllLoans();

        mAdapter = new MyAdapter(parts, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String postId = parts.get(position).get_id();
                Intent intent = new Intent(Lend.this, ViewBorrowerDetails.class);
                intent.putExtra("id",postId );
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {

        switch (action) {


            case "Get Loan":

                JsonArray jsonArray = result.getAsJsonArray("data");

                try{
                    for(int i=0;i<jsonArray.size();i++) {
                        LendDetails temp = new LendDetails();
                        temp.set_name(jsonArray.get(i).getAsJsonObject().get("name").getAsString());
                        temp.set_amount(jsonArray.get(i).getAsJsonObject().get("amount").getAsString());
                        temp.set_rating(jsonArray.get(i).getAsJsonObject().get("creditScore").getAsString());
                        temp.set_photo(jsonArray.get(i).getAsJsonObject().get("photo").getAsString());
                        temp.set_endorsement(jsonArray.get(i).getAsJsonObject().get("endorsedBy").getAsString());
                        temp.set_id(jsonArray.get(i).getAsJsonObject().get("userId").getAsString());


                        parts.add(temp);
                    }
                } catch (Exception e) {
                    Log.d("Error parsing", e.toString());
                }

                mAdapter = new MyAdapter(parts, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        String postId = parts.get(position).get_id();
                        Intent intent = new Intent(Lend.this, ViewBorrowerDetails.class);
                        intent.putExtra("id",postId );
                        startActivity(intent);
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
                break;





        }
    }
}

