package pt.ipleiria.project;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;

import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Singleton;

/**
 * MyFenceReceiver: Mostra Dialogs dependendo da fence que foi despoletada
 */
public class MyFenceReceiver extends BroadcastReceiver {
    private static final String TAG = "TAG";
    private AlertDialog alertDialog;
    String alert = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        ////////////////////HEADPHONES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if (fenceState.getFenceKey().startsWith("headphone")) {
            if (fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when headphones plugged in:";
                Log.i(TAG, "Headphones are plugged in.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Headphones:Plugged")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            } else {
                alert = "Notes found when headphones are unplugged:";
                Log.i(TAG, "Headphones are NOT plugged in.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Headphones:Unplugged")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
        }
        /////////////////////////////////ACTIVITY\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if (fenceState.getFenceKey().startsWith("activity")) {
            if (fenceState.getFenceKey().contains("In Vehicle") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when in Vehicle activity:";
                Log.i(TAG, "In Vehicle.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:In Vehicle")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("On Bicycle") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when in Bicycle activity:";
                Log.i(TAG, "In Bicycle.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:In Bicycle")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("On Foot") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when on Foot activity:";
                Log.i(TAG, "On Foot.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:On Foot")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Still") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Still activity:";
                Log.i(TAG, "Still.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:Still")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("In Vehicle") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when in Vehicle activity:";
                Log.i(TAG, "In Vehicle.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:In Vehicle")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Walking") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when walking activity:";
                Log.i(TAG, "Walking.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:Walking")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Running") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Running activity:";
                Log.i(TAG, "Running.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Activity:Walking")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
        }
        ///////////////////////////////LOCATION\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if (fenceState.getFenceKey().startsWith("location")) {
            alert = "Notes found near this location";
            for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                for (String keyword : n.getKeyword()) {
                    if (keyword.startsWith("#Location:")) {
                        String[] latlng = keyword.split(":");
                        if (fenceState.getFenceKey().contains(latlng[1])){
                            if(fenceState.getCurrentState() == FenceState.TRUE){
                                alert += "\nTittle: " + n.getTitle();
                                break;
                            }else {
                                alert = "No notes found near this location";
                            }
                        }
                    }
                }
            }
            alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
        }
        ////////////////////////////////////TIME\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if (fenceState.getFenceKey().startsWith("time")) {
            if (fenceState.getFenceKey().contains("Weekday") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Weekday:";
                Log.i(TAG, "Weekday.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Weekday")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Weekend") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Weekend:";
                Log.i(TAG, "Weekend.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Weekend")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Holiday") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Holiday:";
                Log.i(TAG, "Holiday.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Holiday")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Morning") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Morning:";
                Log.i(TAG, "Morning.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Morning")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Afternoon") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Afternoon:";
                Log.i(TAG, "Afternoon.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Afternoon")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Evening") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Evening:";
                Log.i(TAG, "Evening.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Evening")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
            if (fenceState.getFenceKey().contains("Night") && fenceState.getCurrentState() == FenceState.TRUE) {
                alert = "Notes found when Night:";
                Log.i(TAG, "Night.");
                if (alertDialog != null) alertDialog.dismiss();
                for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Time:Night")) {
                            alert += "\nTittle: " + n.getTitle();
                            break;
                        }
                    }
                }
                alertDialog = new AlertDialog.Builder(context).setMessage(alert).show();
            }
        }
    }
}

