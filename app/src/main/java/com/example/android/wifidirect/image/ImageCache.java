package com.example.android.wifidirect.image;

import com.example.android.wifidirect.ndn.*;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by NIck on 2/18/2016.
 */
public class ImageCache {
    private Caching db;
    public final static String TAG = "ImageStuffs";

    // For image write check if directory exist then no need to make if not need to make it
    // Write the image to it, given a bytearray write as image.


    public ImageCache(Context context) {
        this.db = new Caching(context);
    }

    public boolean isCached(String interest) {
        return db.containsKey(interest);
    }

    public byte[] imageRead(String interest) throws IOException{
        db.updateLastAccessTimestamp(interest);
        Log.d(TAG, "Interest found in DataBase now retrieving file directory");
        String directory = db.getDirectory(interest);
        Log.d(TAG, "Directory found: " + directory);
        File file = new File(directory);
        byte bytes[] = FileUtils.readFileToByteArray(file);
        return bytes;
    }


    public String imageWrite(String interest, byte[] data) {
        File directory = new File("/sdcard/spyder/");
        directory.mkdirs();
        File imageFile = new File("/sdcard/spyder/" + interest + ".png");

        FileOutputStream fos = null;
        try {
            if(!imageFile.exists()) {
                imageFile.createNewFile();
            }
            fos = new FileOutputStream(imageFile);
            fos.write(data);
            fos.flush();
        } catch(IOException ex) {

        }
        finally {
            if(fos != null){
                try{
                    fos.close();
                } catch (IOException ex){
                    System.err.println("exception while trying to close the fileoutputstream");
                    ex.printStackTrace();
                }

            }
        }
        db.addRow(interest, imageFile.getAbsolutePath());
        return (imageFile == null) ? "" : imageFile.getAbsolutePath();
    }
}
