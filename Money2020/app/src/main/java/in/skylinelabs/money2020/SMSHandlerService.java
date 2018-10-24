package in.skylinelabs.money2020;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;


public class SMSHandlerService extends Service implements AsyncTaskComplete{

    private ActionHandler actionHandler;

    @Override
    public void handleResult(JsonObject result, String action) throws JSONException {


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        String message = intent.getStringExtra("MESSAGE");
        Toast.makeText(getApplicationContext(),"Messsssage is" + message, Toast.LENGTH_SHORT).show();

        Log.i("In service", "In service");


        actionHandler = new ActionHandler(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("userId", "");
        Toast.makeText(getApplicationContext(),userId, Toast.LENGTH_SHORT).show();

        actionHandler.sendSMS(userId, message);

        return START_NOT_STICKY;
    }



}
