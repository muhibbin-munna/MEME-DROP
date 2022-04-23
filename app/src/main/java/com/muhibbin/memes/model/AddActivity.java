package com.muhibbin.memes.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.muhibbin.memes.MainActivity;
import com.muhibbin.memes.R;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private ImageView mImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    Query categoryDatabaseRef;
    private StorageTask mUploadTask;
//    private Spinner categorySpinner;
    private Button categoryButton;
    private ProgressBar progressBar;
    List<String> uploadCategory = new ArrayList<>();
    List<String> category = new ArrayList<>();
    ArrayList selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mButtonChooseImage = findViewById(R.id.chooseButtonId);
        mButtonUpload = findViewById(R.id.uploadButtonId);
        mImageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.uploadProgressBar);
//        categorySpinner = findViewById(R.id.categorySpinnerId);
        categoryButton = findViewById(R.id.categoryButtonId);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        categoryDatabaseRef = FirebaseDatabase.getInstance().getReference("category").orderByValue();

        categoryDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                category.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String areaName = postSnapshot.getValue(String.class);
                    category.add(areaName);
                }
//                ArrayAdapter<String> cateoryAdapter = new ArrayAdapter<String>(AddActivity.this, R.layout.view_spinner_item, category) {
//                    public View getView(int position, View convertView, ViewGroup parent) {
//                        View v = super.getView(position, convertView, parent);
//                        if (position == 0)
//                            uploadCategory = null;
//                        else
//                            uploadCategory = category.get(position);
//                        return v;
//                    }
//
//                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                        View v = super.getDropDownView(position, convertView, parent);
//                        v.setBackgroundColor(Color.parseColor("#424242"));
//                        return v;
//                    }
//                };
//                cateoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                categorySpinner.setAdapter(cateoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] categoryArray = category.toArray(new String[category.size()]);
                selectedItems = new ArrayList();  // Where we track the selected items
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this,R.style.MyDialogTheme);
                // Set the dialog title
                builder.setTitle("Select Category")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setMultiChoiceItems(categoryArray, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    int count = 0;
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        count += isChecked ? 1 : -1;
                                        if(count>3)
                                        {
                                            Toast.makeText(AddActivity.this, "Select at most 3", Toast.LENGTH_SHORT).show();
                                            count--;
                                            ((AlertDialog) dialog).getListView().setItemChecked(which, false);
                                        }
                                        else {
                                            if (isChecked) {
                                                // If the user checked the item, add it to the selected items
                                                selectedItems.add(which);

                                            } else if (selectedItems.contains(which)) {
                                                // Else, if the item is already in the array, remove it
                                                selectedItems.remove(Integer.valueOf(which));
                                            }
                                        }

                                    }
                                })
                        // Set the action buttons
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked OK, so save the selectedItems results somewhere
                                // or return them to the component that opened the dialog
                                StringBuilder buttonText = new StringBuilder();
                                for(int i=0; i< selectedItems.size() ; i++)
                                {
                                    if(i==selectedItems.size()-1)
                                    {
                                        buttonText.append(category.get((Integer) selectedItems.get(i)));
                                        categoryButton.setText(buttonText);
                                    }
                                    else{
                                        buttonText.append(category.get((Integer) selectedItems.get(i))).append(", ");
                                    }
                                    uploadCategory.add(category.get((Integer) selectedItems.get(i)));
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                builder.show();
            }
        });



        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(AddActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else if (uploadCategory == null) {
                    Toast.makeText(AddActivity.this, "Select a category", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            String ext = getFileExtension(data.getData());
            if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png")) {
                mImageUri = data.getData();
                Glide.with(this).load(mImageUri).into(mImageView);
            } else {
                Toast.makeText(this, "enter a jpg/jpeg/png", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            Toast.makeText(AddActivity.this, "Uploading", Toast.LENGTH_SHORT).show();
            final long time = System.currentTimeMillis();
            final String uploadId = mDatabaseRef.push().getKey();

            StorageReference fileReference = mStorageRef.child(time
                    + "." + "jpg");
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 5000);

                            Toast.makeText(AddActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            Upload upload = new Upload(uploadId, downloadUrl.toString(), 0, time,uploadCategory);
                            mDatabaseRef.child(uploadId).setValue(upload);
                            uploadCategory.clear();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
