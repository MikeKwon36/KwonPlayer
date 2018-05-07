package kwondeveloper.com.kwonplayer.SupportClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WallpaperMethods {

    public WallpaperMethods() {
    }

    public static Bitmap decodeFile(File file) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        int mImageRealWidth = options.outWidth;
        int mImageRealHeight = options.outHeight;
        Bitmap pic = null;
        try {
            pic = BitmapFactory.decodeFile(file.getPath(), options);
        } catch (Exception ex) {ex.printStackTrace();}
        return pic;
    }

    public static Bitmap rescaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static String SaveImage(Bitmap image, String localImagePath) {
        String fileName = localImagePath + System.currentTimeMillis() +  ".jpg";
        try {
            File file = new File(fileName);
            FileOutputStream fileStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fileStream);
            try {
                fileStream.flush();
                fileStream.close();
            } catch (IOException e) {e.printStackTrace();}
        } catch (FileNotFoundException e) {e.printStackTrace();}
        return fileName;
    }

    public static String createLocalImageFolder(Context context, String localImagePath) {
        if (localImagePath.length() == 0) {
            localImagePath = context.getFilesDir().getAbsolutePath() + "/kwon/";
            File folder = new File(localImagePath);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (!success)
                Toast.makeText(context, "Cannot create local folder", Toast.LENGTH_LONG).show();
        }
        return localImagePath;
    }

    public static Bitmap rotateBitmap(Bitmap pic, int deg) {
        Matrix rotate90DegAntiClock = new Matrix();
        rotate90DegAntiClock.preRotate(deg);
        Bitmap newPic = Bitmap.createBitmap(pic, 0, 0, pic.getWidth(), pic.getHeight(), rotate90DegAntiClock, true);
        return newPic;
    }

}
