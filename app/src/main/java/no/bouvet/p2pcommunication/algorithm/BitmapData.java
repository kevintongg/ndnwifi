package no.bouvet.p2pcommunication.algorithm;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by micha on 3/20/2018.
 */

public class BitmapData {
    private byte[] imageByteArray;

    public BitmapData(){

    }

    public BitmapData(Bitmap bitmap){
        try{
            this.imageByteArray = BitmapUtility.makeByteArray(bitmap);
        }
        catch(IOException ex){
            System.err.println("could not make byte array from bitmap");
            ex.printStackTrace();
        }
    }

    public BitmapData(InputStream in){
        byte[] buffer = new byte[4096];
        int read = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        }
        catch(IOException ex){
            System.err.println("unable to read inputstream");
            ex.printStackTrace();
        }
        imageByteArray = out.toByteArray();
    }

    public byte[] getImageByteArray(){
        return imageByteArray;
    }

    public void setImageByteArray(byte[] otherByteArray){
        imageByteArray = otherByteArray;
    }
}