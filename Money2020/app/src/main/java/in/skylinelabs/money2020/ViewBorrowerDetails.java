package in.skylinelabs.money2020;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

public class ViewBorrowerDetails extends AppCompatActivity implements  AsyncTaskComplete {

    private ActionHandler actionHandler;

    TextView nameTxt;
    ImageView imgView;
    TextView endorsedBy;

    TextView locationTxt;
    TextView workTxt;

    TextView socialScoreText;
    TextView creditScoreText;

    Button pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_borrower_details);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        actionHandler = new ActionHandler( ViewBorrowerDetails.this);
        actionHandler.getUserData(id);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String email = preferences.getString("email", "");

        pay =  (Button)findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://money2020-2018.appspot.com/pay?email=" + email + "&amount=1000";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        nameTxt = (TextView) findViewById(R.id.name);
        imgView = (ImageView)findViewById(R.id.image);
        endorsedBy  = (TextView) findViewById(R.id.endoresedBY);

        locationTxt = (TextView) findViewById(R.id.location);
        workTxt = (TextView) findViewById(R.id.work);

        socialScoreText = (TextView) findViewById(R.id.socialScore);
        creditScoreText = (TextView) findViewById(R.id.creditScore);




    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {
        switch (action) {

            case "Get User Data":

                try {
                    String name = result.get("name").getAsString();
                    String endorsements = result.get("endorsedBy").getAsString();
                    String socialScore = result.get("socialScore").getAsString();
                    String email = result.get("email").getAsString();
                    String creditScore = result.get("creditScore").getAsString();
                    String work = result.get("work").getAsString();
                    String location = result.get("location").getAsString();
                    String photo = result.get("photo").getAsString();

                    Picasso.get().load(photo).resize(500, 500).into(imgView);
                    nameTxt.setText(name);
                    endorsedBy.setText("\uD83D\uDC4D " + endorsements);

                    locationTxt.setText(location);
                    workTxt.setText(work);

                    socialScoreText.setText(socialScore);
                    creditScoreText.setText(creditScore);



                } catch (Exception e) {
                    Log.d("Error parsing", e.toString());
                }


                break;

        }
    }
}