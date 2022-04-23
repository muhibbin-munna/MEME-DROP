package com.muhibbin.memes.model;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.muhibbin.memes.PhotoEditor.EraserActivity;
import com.muhibbin.memes.R;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ShowSaveActivity extends AppCompatActivity {

    TextView textChanger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_show_save);
//        textChanger = findViewById(R.id.textView10);
//        CutOut.activity()
//                .bordered()
//                .noCrop()
//                .start(this);


        Intent intent= new Intent(this, EraserActivity.class);
        startActivity(intent);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CutOut.CUTOUT_ACTIVITY_REQUEST_CODE) {
//            switch (resultCode) {
//                case Activity.RESULT_OK:
//                    Uri imageUri = CutOut.getUri(data);
//                    // Save the image using the returned Uri here
//                    saveImage(imageUri);
//
//                    break;
//                case CutOut.CUTOUT_ACTIVITY_RESULT_ERROR_CODE:
//                    Exception ex = CutOut.getError(data);
//                    break;
//                default:
//                    textChanger.setText("User has cancelled the process.Please press Back Button");
//                    onBackPressed();
//            }
//        }
    }

    private void saveImage(Uri imageUri) {

        if (checkPermission()) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/MEME DROPS/Stickers");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                String name = "sticker_" + System.currentTimeMillis() % 10000 + ".png";
                myDir = new File(myDir, name);
                FileOutputStream out = new FileOutputStream(myDir);
                bitmap = Bitmap.createScaledBitmap(
                        bitmap, bitmap.getWidth()/4, bitmap.getHeight()/4, false);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(ShowSaveActivity.this, "saved", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(myDir));
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 111);
            }
        }
    }

    private boolean checkPermission() {
        int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
    }

    public Uri getUriFromDrawable(int drawableId) {
        return Uri.parse("android.resource://" + getPackageName() + "/drawable/" + getApplicationContext().getResources().getResourceEntryName(drawableId));
    }
}