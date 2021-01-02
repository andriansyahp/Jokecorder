package id.ac.ui.cs.mobileprogramming.andriansyah.jokecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class ConnectivityReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        try {
//            if (isOnline(context)) {
//                Toast.makeText(context, "Network enabled!", Toast.LENGTH_SHORT).show();
//            } else {
//                new AlertDialog.Builder(context)
//                        .setTitle("Network Connection disabled")
//                        .setMessage("You need to enable network connection to connect MusicX to internet.")
//                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .create().show();
//                return;
//            }
//        } catch (NullPointerException e) {}
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            //should check null because in airplane mode it will be null
            if (networkInfo != null && networkInfo.isConnected()) {
                Toast.makeText(context, "You're connected to the internet", Toast.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("No Network Detected")
                        .setMessage("Internet availability is advised in order to use Jokecorder.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }
        } catch (NullPointerException e) {}
    }
//
//
//
//
//    private boolean isOnline(Context context) {
//        try {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//
//            //should check null because in airplane mode it will be null
//            return (networkInfo != null && networkInfo.isConnected());
//
//            if(networkInfo != null && networkInfo.isConnected()){
//                Toast.makeText(context, "Network enabled!", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (NullPointerException e) {
//            return false;
//        }
//    }
}
