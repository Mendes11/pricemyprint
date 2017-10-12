package br.com.cozinheirodelivery.pricemyprint.Objects;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 08/08/2017.
 */

public class PhotoManager extends Activity {
    public static final int MY_PERMISSIONS_REQUEST_TAKE_PHOTO = 0;
    public static final int MY_PERMISSIONS_REQUEST_SELECT_PHOTO = 1;
    public PhotoManager(){}
    private static String getRealPathFromURI(Activity activity,Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private static File getPhotoFile(String photoName) throws IOException {
        //String imageFileName = "Project_"+oProject.getcNome()+oProject.getiIDProjeto();
        String imageFileName = photoName;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"PriceMyPrint");
        if(!storageDir.exists()){
            storageDir.mkdir();
        }
        File image = null;
        try {
            image = new File(storageDir,/* directory */
                    imageFileName+  /* prefix */
                            ".jpg"         /* suffix */
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }
    private static void galleryAddPic(Activity activity,String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }
    private static Intent takePhoto(Activity activity,String photoPath) {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new File(photoPath);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, R.string.take_photo_error, Toast.LENGTH_SHORT).show();
            }
            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,"br.com.cozinheirodelivery.pricemyprint.fileprovider",photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            }
        }else{
            Toast.makeText(activity, R.string.no_camera_error, Toast.LENGTH_SHORT).show();
        }
        return takePicture;
    }
    public static String preparePath(Activity activity,String photoName){
        File photoFile = null;
        try {
            photoFile = getPhotoFile(photoName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity,R.string.take_photo_error, Toast.LENGTH_SHORT).show();
        }
        if(photoFile != null) {
            return photoFile.getAbsolutePath();
        }else {
            return null;
        }
    }
    private static Intent selectPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return pickPhoto;
    }
    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }
    public static Intent preparePhotoIntent(Activity activity,String photoPath) throws Exception {
        //Primeiramente deve-se ver se já há a permissão para se escrever e se não houver, pede-a.
        if(checkWritePermission(activity)) {
            return takePhoto(activity, photoPath);
        }else{
            throw new Exception("No Permission!");
        }
    }
    public static Intent preparePhotoIntent(Fragment mFragment,String photoPath) throws Exception {
        if(checkWritePermission(mFragment.getActivity())){
            return takePhoto(mFragment.getActivity(),photoPath);
        }else{
            throw new Exception("No Permission!");
        }
    }
    public static Intent prepareSelectPhotoIntent(Activity activity) throws Exception {
        if(checkWritePermission(activity)) {
            return selectPhoto();
        }else{
            throw new Exception("No Permission!");
        }
    }
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    public static File onResult(Activity activity, String photoPath,Intent data, int requestCode) throws Exception{
        File finalFile = null;
        boolean copy = true;
        if(data != null) {
            Uri tempUri = data.getData();
            String filePath = getRealPathFromURI(activity, tempUri);
            finalFile = new File(filePath);
            if(filePath.equals(photoPath)){
                copy = false;
            }
        }else {
            finalFile = new File(photoPath);
        }
        if(requestCode == GALLERY_REQUEST){
            try {
                if(copy) {
                    copyFile(finalFile, new File(photoPath));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        galleryAddPic(activity,photoPath);
        return finalFile;
    }

    public static boolean checkWritePermission(Activity activity){
        boolean hasPermission = false;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true;
        }
        return hasPermission;
    }
    public static void askWritePermission(Activity activity,int requestCode){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
    }
    public static void askWritePermission(Fragment mFragment,int requestCode){
        mFragment.requestPermissions(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
    }
}
