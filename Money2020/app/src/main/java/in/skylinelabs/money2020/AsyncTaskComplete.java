package in.skylinelabs.money2020;

import com.google.gson.JsonObject;

import org.json.JSONException;

/**
 * Created by jaylo on 20-10-2018.
 */

public interface AsyncTaskComplete {
    void handleResult(JsonObject result, String action) throws JSONException;


}