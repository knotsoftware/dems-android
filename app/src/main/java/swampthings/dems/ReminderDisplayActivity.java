package swampthings.dems;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class ReminderDisplayActivity extends Activity implements DialogInterface.OnClickListener{

    private AlertDialog alert;
    private Bundle reminderInfo;
    protected String patientAPIURL = "http://demsweb.herokuapp.com/api/patient/";
    private int level;

    @Override
    protected void onStart() {
        super.onStart();
        reminderInfo = getIntent().getExtras();
        level = reminderInfo.getInt("level");

        DisplayReminder(reminderInfo);
    }

    @Override
    protected void onPause() {
        if(alert.isShowing()){
            alert.dismiss();
        }
        super.onPause();
    }

    private void DisplayReminder(Bundle details) {
        if (level == 1) {
            alert = new AlertDialog.Builder(this)
                    .setTitle(details.getString("title"))
                    .setMessage(details.getString("message"))
                    .setPositiveButton("Yes", this)
                    .setNegativeButton("No", this)
                    .create();
        } else if (level == 2) {
            alert = new AlertDialog.Builder(this)
                    .setTitle(details.getString("title"))
                    .setMessage(details.getString("message"))
                    .setPositiveButton("Done", this)
                    .setNegativeButton("Not Done", this)
                    .create();
        } else {
            alert = new AlertDialog.Builder(this)
                    .setTitle(details.getString("title"))
                    .setMessage(details.getString("message"))
                    .setPositiveButton("OK", this)
                    .create();
        }
        alert.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();

        if (level == 1) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                reminderInfo.putString("acknowledgement", "Yes");
            } else {
                reminderInfo.putString("acknowledgement", "No");
            }
        } else if (level == 2) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                reminderInfo.putString("acknowledgement", "Done");
            } else {
                reminderInfo.putString("acknowledgement", "Not Done");
            }
        } else {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                reminderInfo.putString("acknowledgement", "OK");
            }
        }

        new UpdateReminderRESTful().execute(reminderInfo);
    }

    public AlertDialog getAlert() {
        return this.alert;
    }

    protected class UpdateReminderRESTful extends AsyncTask<Bundle, Integer, Boolean>  {

        @Override
        protected Boolean doInBackground(Bundle... params) {
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android");
            Bundle bundle = params[0];
            HttpPut request = new HttpPut(patientAPIURL + bundle.getString("patientID") + "/reminder/" + bundle.getString("id"));
            HttpResponse response;
            boolean success = false;

            try {
                JSONObject reminderUpdate = new JSONObject();

                reminderUpdate.put("acknowledgement", bundle.getString("acknowledgement"));

                StringEntity stringEntity = new StringEntity(reminderUpdate.toString());

                request.setEntity(stringEntity);
                request.setHeader("Accept", "application/json");
                request.setHeader("Content-type", "application/json");

                response = httpClient.execute(request);

                if (response.getStatusLine().getStatusCode() == 200) {
                    success = true;
                }

            } catch (Exception e) {
                success =  false;
            } finally {
                httpClient.close();
            }

            return success;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            ReminderDisplayActivity.this.finish();
        }
    }
}
