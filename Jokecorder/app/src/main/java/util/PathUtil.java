package util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class PathUtil {
    public static String getDirectoryPath(Context context) {
        if (isExternalStorageWritable()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS), "AudioRecorder");
            if (file.mkdirs()) {
                return file.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath(); // use internal storage if external storage is not available
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
