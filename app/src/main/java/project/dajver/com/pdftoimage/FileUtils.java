package project.dajver.com.pdftoimage;

import android.content.Context;

import java.io.File;

/**
 * Created by gleb on 12/6/17.
 */

public class FileUtils {
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }
}
