package no.bouvet.p2pcommunication.algorithm;

/**
 * Created by micha on 3/20/2018.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapUtility {

    // TODO: Finish this constructor
    public BitmapUtility() {
    }


    public static byte[] makeByteArray(Bitmap image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();

    }

    public static Bitmap makeBitmap(byte[] bitmapBytes) throws IOException, ClassNotFoundException {
        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }
}
