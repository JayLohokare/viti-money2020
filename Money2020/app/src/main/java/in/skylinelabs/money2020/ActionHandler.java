package in.skylinelabs.money2020;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jaylo on 20-10-2018.
 */


public class ActionHandler {
    AsyncTaskComplete callback;
    ProgressDialog progressDialog;
    private Context context;

    private String address = "a4cbd55c.ngrok.io";

    private String GET_ALL_LOANS = "https://" + address +"/getLoans";
    private String GOT_SMS_URL = "https://" + address +"/handleSMS";
    private String GET_USER_DATA = "https://" + address +"/getUserDetails";
    private String CREATE_USER = "https://" + address +"/createUser";

    public ActionHandler(AsyncTaskComplete callback) {
        this.callback = callback;
    }


    public void getAllLoans(){
        JsonObject jsonObject = new JsonObject();
        postJsonObject(jsonObject, GET_ALL_LOANS, "Get Loan", "Fetching");
    }

    public void sendSMS(String id, String message) {
        postJsonObject(createSMSJsonObject(id, message), GOT_SMS_URL, "SMS", "Sending SMS");
    }

    public void createUser(String id, String name, String  email) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("name", name);

        postJsonObject(jsonObject, CREATE_USER, "Login", "Loging in");
    }

    public void getUserData(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        postJsonObject(jsonObject, GET_USER_DATA, "Get User Data", "Getting data");
    }

    private JsonObject createSMSJsonObject(String id, String message){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("message", message);
        return jsonObject;
    }

    private void postJsonObject(final JsonObject jsonObject, String url, final String action, String progress_status) {
        HttpJsonPost httpJsonPost = new HttpJsonPost(url, action, progress_status, callback);
        httpJsonPost.execute(jsonObject);
    }

}
