package com.muhibbin.memes.photoEditorMainClasses;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.muhibbin.memes.PhotoEditor.EraserActivity;
import com.muhibbin.memes.R;
import com.muhibbin.memes.model.ShowSaveActivity;

import java.io.File;

public class StickerBSFragment extends BottomSheetDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    StickerAdapter stickerAdapter;
    RecyclerView rvEmoji;

    public StickerBSFragment() {
        // Required empty public constructor
    }

    private StickerListener mStickerListener;

    public void setStickerListener(StickerListener stickerListener) {
        mStickerListener = stickerListener;
    }

    public interface StickerListener {
        void onStickerClick(Bitmap bitmap);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rvEmoji = contentView.findViewById(R.id.rvEmoji);
        Button addStickerButton = contentView.findViewById(R.id.addStickerButton);
        addStickerButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(),ShowSaveActivity.class);
////            startActivityForResult(intent,1);
            Intent intent= new Intent(getActivity(), EraserActivity.class);
            startActivityForResult(intent,1);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);
        stickerAdapter = new StickerAdapter();
        rvEmoji.setAdapter(stickerAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data); comment this unless you want to pass your result to the activity.

        if (requestCode==1)
        {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            rvEmoji.setLayoutManager(gridLayoutManager);
            stickerAdapter = new StickerAdapter();
            rvEmoji.setAdapter(stickerAdapter);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

//        int[] stickerList = new int[]{R.drawable.aa, R.drawable.bb};


        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/MEME DROPS/Stickers");
//        File[] allFiles = folder.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
//            }
//        });
        String[] fileNames = folder.list();


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imgSticker.setImageBitmap(BitmapFactory.decodeFile(folder.getPath() + "/" + fileNames[position]));
        }

        @Override
        public int getItemCount() {
//            return stickerList.length;
            return fileNames.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mStickerListener != null) {
                            mStickerListener.onStickerClick(
//                                    BitmapFactory.decodeResource(getResources(),
//                                            stickerList[getLayoutPosition()])
                                    BitmapFactory.decodeFile(folder.getPath() + "/" + fileNames[getLayoutPosition()])
                                    ///Now set this bitmap on imageview
                            );
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private String convertEmoji(String emoji) {
        String returnedEmoji = "";
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt);
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}