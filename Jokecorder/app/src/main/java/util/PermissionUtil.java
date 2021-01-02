package util;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermissionUtil {
    private static String[] PERMISSIONS = {
            (Manifest.permission.RECORD_AUDIO),
            (Manifest.permission.READ_EXTERNAL_STORAGE),
            (Manifest.permission.WRITE_EXTERNAL_STORAGE),
            (Manifest.permission.ACCESS_FINE_LOCATION),
            (Manifest.permission.ACCESS_COARSE_LOCATION)
    };

    // Returns an array of the permissions to ask (not already granted).
    public static String[] checkPermissions(Fragment fragment) {
        // Check permissions.
        List<String> permissionsToAsk = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            boolean granted = ContextCompat.checkSelfPermission(Objects.requireNonNull(fragment.getActivity()), permission) == PackageManager.PERMISSION_GRANTED;
            if (!granted)
                permissionsToAsk.add(permission);
        }

        String[] arrPermissions = new String[permissionsToAsk.size()];
        arrPermissions = permissionsToAsk.toArray(arrPermissions);

        return arrPermissions;
    }
}
