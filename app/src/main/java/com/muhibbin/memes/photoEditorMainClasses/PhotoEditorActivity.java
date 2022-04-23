package com.muhibbin.memes.photoEditorMainClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.muhibbin.memes.model.AddActivity;
import com.muhibbin.memes.photoEditorMainClasses.base.BaseActivity;
import com.muhibbin.memes.model.Upload;
import com.muhibbin.memes.photoEditorSDK.OnPhotoEditorListener;
import com.muhibbin.memes.photoEditorSDK.PhotoEditor;
import com.muhibbin.memes.photoEditorSDK.PhotoEditorView;
import com.muhibbin.memes.photoEditorSDK.SaveSettings;
import com.muhibbin.memes.photoEditorSDK.TextStyleBuilder;
import com.muhibbin.memes.photoEditorSDK.ViewType;
import com.muhibbin.memes.photoEditorMainClasses.tools.EditingToolsAdapter;
import com.muhibbin.memes.photoEditorMainClasses.tools.ToolType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PhotoEditorActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    //    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    int option;
    File file;
    String path;

    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    Uri mImageUri;

    List<String> uploadCategory = new ArrayList<>();
    List<String> category = new ArrayList<>();
    ArrayList selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_photo_editor);

        try {
            initViews();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Query categoryDatabaseRef;
        categoryDatabaseRef = FirebaseDatabase.getInstance().getReference("category").orderByValue();
        categoryDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                category.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String areaName = postSnapshot.getValue(String.class);
                    category.add(areaName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        GridLayoutManager llmTools = new GridLayoutManager(this, 3);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
//                .setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        mPhotoEditor.setOnClickClear();

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);


    }


    private void initViews() throws IOException {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        Button imgSave;
        Button imgClose;
        Button imgUpload;


        mPhotoEditorView = findViewById(R.id.photoEditorView);
//        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

//        imgCamera = findViewById(R.id.imgCamera);
//        imgCamera.setOnClickListener(this);

//        imgGallery = findViewById(R.id.imgGallery);
//        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgUpload = findViewById(R.id.uploadId);
        imgUpload.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        Intent intent = getIntent();
        option = intent.getIntExtra("option", 1);
        if (option == 1) {
            String mImageUrl = intent.getStringExtra("mImageUrl");
            Glide.with(this)
                    .asBitmap()
                    .load(mImageUrl)
                    .placeholder(R.drawable.default_image_thumbnail)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mPhotoEditorView.getSource().setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        } else {
            Uri mImageUri = Uri.parse(intent.getStringExtra("mImageUri"));
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            mPhotoEditorView.getSource().setImageBitmap(bitmap);

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveImage();
                break;
            case R.id.uploadId:
                saveAndUploadImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;

//            case R.id.imgCamera:
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                break;
//
//            case R.id.imgGallery:
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
//                break;
        }
    }

    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showLoading("Saving...");
            String root = Environment.getExternalStorageDirectory().toString();
            File file = new File(root + "/MEME DROPS");
            if (!file.exists()) {
                file.mkdirs();
            }
            String name = "memes_" + System.currentTimeMillis() % 10000 + ".png";
            file = new File(file, name);
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                final File finalFile = file;
                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        path = imagePath;
                        hideLoading();
                        showSnackbar("Image Saved Successfully");
                        mPhotoEditorView.getSource().setImageURI(Uri.fromFile(new File(path)));
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(finalFile));
                        PhotoEditorActivity.this.sendBroadcast(intent);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

    private void saveAndUploadImage() {
        if (!mPhotoEditor.isCacheEmpty()) {
            showSnackbar("Save File First");
        } else {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                showSnackbar("Upload in progress");
            } else {
                if(path != null){
                    mImageUri = Uri.fromFile(new File(path));
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoEditorActivity.this, R.style.MyDialogTheme);
                builder.setMessage("Are you sure want to upload?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                selectCategory();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showSnackbar("canceled");
                            }
                        }).show();
            }else {
                    showSnackbar("Save File First");
                }
            }
        }
    }

    private void selectCategory() {
        String[] categoryArray = category.toArray(new String[category.size()]);
        selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoEditorActivity.this,R.style.MyDialogTheme);
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
                                    Toast.makeText(PhotoEditorActivity.this, "Select at most 3", Toast.LENGTH_SHORT).show();
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

                        for(int i=0; i< selectedItems.size() ; i++)
                        {
                            uploadCategory.add(category.get((Integer) selectedItems.get(i)));
                        }
                        uploadFile(uploadCategory);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.show();

    }

    private void uploadFile(List<String> categoryName) {
        if (mImageUri != null) {
            showSnackbar("Uploading");
            final long time = System.currentTimeMillis();
            final String uploadId = mDatabaseRef.push().getKey();
            StorageReference fileReference = mStorageRef.child(time
                    + "." + "jpg");
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            Upload upload = new Upload(uploadId, downloadUrl.toString(), 0, time,categoryName);
                            mDatabaseRef.child(uploadId).setValue(upload);
                            showSnackbar("Upload successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PhotoEditorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
//        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
//        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
//        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int textColor, int shadowColor, int textSize,int shadowSize, String textFont,int gravity) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, textColor, shadowColor, textSize,shadowSize, textFont,gravity);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int textColor, int shadowColor, int textSize,int shadowSize, String textFont, int gravity) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(textColor);
                styleBuilder.withTextFont(Typeface.createFromAsset(getResources().getAssets(), "font/" + textFont));
                styleBuilder.withSetTag(textFont);
                styleBuilder.withsetShadowLayer(shadowSize,shadowColor);
                styleBuilder.withTextSize(textSize);
                styleBuilder.withGravity(gravity);
                mPhotoEditor.editText(rootView, inputText, styleBuilder);
//                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onEmojiClick(String emojiUnicode) {

        mPhotoEditor.addEmoji(emojiUnicode);
//        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
//        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
//            case BRUSH:
//                mPhotoEditor.setBrushDrawingMode(true);
//                mTxtCurrentTool.setText(R.string.label_brush);
//                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
//                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode, int shadowHexColor, int textSize,int shadowSize, String textFont,int gravity) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);
                        styleBuilder.withTextFont(Typeface.createFromAsset(getResources().getAssets(), "font/" + textFont));
                        styleBuilder.withSetTag(textFont);
                        styleBuilder.withsetShadowLayer(shadowSize,shadowHexColor);
                        styleBuilder.withTextSize(textSize);
                        styleBuilder.withGravity(gravity);
//                        Toast.makeText(PhotoEditorActivity.this, ""+shadowSize+"  "+shadowHexColor, Toast.LENGTH_SHORT).show();

                        mPhotoEditor.addText(inputText, styleBuilder);
//                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
//            case ERASER:
//                mPhotoEditor.brushEraser();
//                mTxtCurrentTool.setText(R.string.label_eraser_mode);
//                break;
//            case FILTER:
//                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/MEME DROPS/Stickers");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
           }

    @Override
    public void onBackPressed() {
        if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }
}