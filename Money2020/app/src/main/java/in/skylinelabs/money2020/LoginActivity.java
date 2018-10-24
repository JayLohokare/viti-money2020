package in.skylinelabs.money2020;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity  implements  AsyncTaskComplete {

    private ActionHandler actionHandler;
    String userid;
    String email;

    Context context = this.getBaseContext();

    private static final String [] DANGEROUS_PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private void initPermissions() {
        List<String> missingPermissions = new ArrayList<String>();
        for(String permission : DANGEROUS_PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (missingPermissions.size() > 0) {
            String [] permissions = new String[missingPermissions.size()];
            ActivityCompat.requestPermissions(
                    this,
                    missingPermissions.toArray(permissions),
                    1);
        } else {
            // we have all permissions, move on
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean loggedIn = preferences.getBoolean("LoggedIn", false);
        if(loggedIn)
        {
            Intent intent = new Intent(LoginActivity.this, Lend.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_login);

            initPermissions();

            final EditText usernameedt;
            final EditText useridedt;
            final EditText emailedt;

            Button loginbtn;

            usernameedt = findViewById(R.id.username);
            useridedt = findViewById(R.id.userid);
            emailedt = findViewById(R.id.email);
            loginbtn = findViewById(R.id.loginButton);

            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = usernameedt.getText().toString();
                    userid = useridedt.getText().toString();
                    email = emailedt.getText().toString();

                    actionHandler = new ActionHandler(LoginActivity.this);
                    actionHandler.createUser(userid, username, email);
                }
            });
        }
    }

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {

        switch (action) {
            case "Login":
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("LoggedIn", true);
                editor.putString("userId", userid);
                editor.putString("email", email);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, Lend.class);
                startActivity(intent);
                finish();

        }
    }
}
