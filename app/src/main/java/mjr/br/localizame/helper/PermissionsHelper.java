package mjr.br.localizame.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by marcos on 11/01/16.
 */

public class PermissionsHelper {
    private static final String TAG = "PermissionsHelper";
    public static final int REQUEST_ALL      = 0;

    // Permissão usada nas funcionalidades de envio de localização do smartphone e no envio de alertas
    public static final int REQUEST_FINE_LOCATION = 2;

    // Permissão usada nas funcionalidades de envio de localização do smartphone e no envio de alertas
    public static final int REQUEST_PHONE_STATE    = 3;

    public boolean checkPermission(final Context context, final int permissionToCheck) throws RuntimeException {
        Integer hasPermission = null;

        switch (permissionToCheck) {
            case REQUEST_ALL :
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED)
                {
                    hasPermission = PackageManager.PERMISSION_DENIED;
                } else {
                    hasPermission = PackageManager.PERMISSION_GRANTED;
                }
            break;

            case REQUEST_FINE_LOCATION :
                hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            break;

            case REQUEST_PHONE_STATE :
                hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            break;
        }

        if (hasPermission != null){
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new RuntimeException("Permissão não identificada");
        }
    }

    public void requestPermission(final AppCompatActivity activity, final Integer permissionRequestCode){
        switch (permissionRequestCode) {
            case REQUEST_ALL :
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
            break;

            case REQUEST_PHONE_STATE :
                ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.READ_PHONE_STATE}, permissionRequestCode);
            break;

            case REQUEST_FINE_LOCATION :
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
            break;
        }
    }
}
