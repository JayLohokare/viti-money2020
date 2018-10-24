package in.skylinelabs.money2020;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class MessageArrived extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        /* Your SBI account XXX has been credited/debited with Rs 100 for transaction at Flipkart . Current balance: 56 */
        /*  Car loan EMI payment of Rs 1000 for account XXX is due
            Bill for your postpaid airtel mobile is Rs 500 .*/
        final Bundle bundle = intent.getExtras();
        try {

            //actionHandler = new ActionHandler(context, intent.);
            //Toast.makeText(context,"In SMS recieveer", Toast.LENGTH_SHORT).show();

            if (bundle != null) {
                String message = null;
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int j = 0; j < pdusObj.length; j++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[j]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: " + senderNum + ", message: " + message, duration);
                    toast.show();
                    //String msg = "Thank you for using your SBI card 7798720001 for a transaction of Rs 99 at flipkart account balance: 2000";
                    Intent i = new Intent(context, SMSHandlerService.class);
                    i.putExtra("MESSAGE", message);
                    context.startService(i);

                }
            }



        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }
}
