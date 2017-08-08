package sigalov.arttodevelop.weatherclient.helpers;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import sigalov.arttodevelop.weatherclient.R;

public final class AlertDialogHelper {

    private AlertDialogHelper() {}

    public static void showWarningDialog(Context context, String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
