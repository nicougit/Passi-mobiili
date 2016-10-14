package fi.softala.passi.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by vllle on 14.10.2016.
 */

public class ImageManipulation {

    public static File pienennaKuvaa(final File file) {
        try {
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 1;
            FileInputStream inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

            outputStream.flush();
            outputStream.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
