package com.muhibbin.memes.photoEditorMainClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.muhibbin.memes.R;

public class TextEditorDialogFragment extends DialogFragment {

    public static final String TAG = TextEditorDialogFragment.class.getSimpleName();
    public static final String EXTRA_INPUT_TEXT = "extra_input_text";
    public static final String EXTRA_TEXT_COLOR = "extra_text_color";
    public static final String EXTRA_SHADOW_COLOR = "extra_shadow_color";
    public static final String EXTRA_TEXT_SIZE = "extra_text_size";
    public static final String EXTRA_SHADOW_SIZE = "extra_shadow_size";
    public static final String EXTRA_TEXT_FONT = "extra_text_font";
    private static final String EXTRA_GRAVITY = "extra_gravity";
    private EditText mAddTextEditText;
    private TextView mAddTextDoneTextView;
    private InputMethodManager mInputMethodManager;
    private int mColorCode;
    private TextEditor mTextEditor;

    Button confirm, cancel, textColor,shadowColor;
    ColorPickerDialog textColorPickerDialog, shadowColorPickerDialog;
    int textHexColor;
    int shadowHexColor,shadowSize,textSize;
    int gravity;
    String textFont;
    Spinner spinner;
    SeekBar borderSizeSeekBar;
    SeekBar textSizeSeekBar;

    ImageView left,center,right;

    public interface TextEditor {
        void onDone(String inputText, int textHexColor, int shadowHexColor,int textSize,int shadowSize, String textFont,int gravity);
    }


    //Show dialog with provide text and text color
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity,
                                                @NonNull String inputText,
                                                @ColorInt int textHexColor,
                                                @ColorInt int shadowHexColor,
                                                int textSize,
                                                int shadowSize,
                                                @NonNull String textFont,
                                                int gravity) {
        Bundle args = new Bundle();
        args.putString(EXTRA_INPUT_TEXT, inputText);
        args.putInt(EXTRA_TEXT_COLOR, textHexColor);
        args.putInt(EXTRA_SHADOW_COLOR, shadowHexColor);
        args.putInt(EXTRA_TEXT_SIZE, textSize);
        args.putInt(EXTRA_SHADOW_SIZE, shadowSize);
        args.putString(EXTRA_TEXT_FONT, textFont);
        args.putInt(EXTRA_GRAVITY, gravity);
        TextEditorDialogFragment fragment = new TextEditorDialogFragment();
        fragment.setArguments(args);
        fragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return fragment;
    }

    //Show dialog with default text input as empty and text color white
    public static TextEditorDialogFragment show(@NonNull AppCompatActivity appCompatActivity) {
        return show(appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white),
                ContextCompat.getColor(appCompatActivity, R.color.white),
                24,0, "open_sans.ttf",Gravity.CENTER);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //Make dialog full screen with transparent background
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_text_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text);
//        Toast.makeText(getActivity(), "Tap on the screen to add text", Toast.LENGTH_SHORT).show();
        mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv);
        textColor = view.findViewById(R.id.textColor);
        shadowColor = view.findViewById(R.id.shadowColor);
        spinner = view.findViewById(R.id.font_spinner);
        borderSizeSeekBar=view.findViewById(R.id.borderSizeSeekBar);
        textSizeSeekBar=view.findViewById(R.id.textSizeSeekBar);
        textSizeSeekBar.setProgress((int) mAddTextEditText.getTextSize());

        left=view.findViewById(R.id.leftAlignment);
        center=view.findViewById(R.id.centerAlignment);
        right=view.findViewById(R.id.rightAlignment);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left.setBackgroundResource(R.drawable.background_border);
                center.setBackgroundResource(0);
                right.setBackgroundResource(0);
                mAddTextEditText.setGravity(Gravity.CENTER | Gravity.RIGHT);
                gravity = Gravity.CENTER | Gravity.RIGHT;
            }
        });
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                center.setBackgroundResource(R.drawable.background_border);
                left.setBackgroundResource(0);
                right.setBackgroundResource(0);
                mAddTextEditText.setGravity(Gravity.CENTER );
                gravity = Gravity.CENTER;
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                right.setBackgroundResource(R.drawable.background_border);
                center.setBackgroundResource(0);
                left.setBackgroundResource(0);
                mAddTextEditText.setGravity(Gravity.CENTER | Gravity.LEFT);
                gravity = Gravity.CENTER | Gravity.LEFT;
            }
        });

        final String[] items = new String[25];
        items[0] = "Select Font";
        items[1] = "open_sans";
        items[2] = "alex_brush";
        items[3] = "aclonica";
        items[4] = "acme";
        items[5] = "anton";
        items[6] = "architects_daughter";
        items[7] = "bangers";
        items[8] = "BebasNeue-Regular";
        items[9] = "chelsea_market";
        items[10] = "dancing_script";
        items[11] = "dancing_script_bold";
        items[12] = "indie_flower";
        items[13] = "LexendTera-Regular";
        items[14] = "lobster";
        items[15] = "pacifico";
        items[16] = "poppins";
        items[17] = "roboto";
        items[18] = "roboto_black";
        items[19] = "roboto_condensed_bold";
        items[20] = "roboto_condensed_regular";
        items[21] = "roboto_thin";
        items[22] = "roboto_thin_italic";
        items[23] = "shadows_into_light";
        items[24] = "tenali_ramakrishna";
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.view_spinner_item, items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position != 0) {
                    Typeface font = Typeface.createFromAsset(getResources().getAssets(), "font/" + items[position] + ".ttf");
                    ((TextView) v).setTypeface(font);
                }
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (position != 0) {
                    Typeface font = Typeface.createFromAsset(getResources().getAssets(), "font/" + items[position] + ".ttf");
                    ((TextView) v).setTypeface(font);
                }
                v.setBackgroundColor(Color.parseColor("#424242"));
                return v;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0)
                {
//                    textView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "font/"+textFont));
                }
                else {
                    textFont = items[position] + ".ttf";
                    mAddTextEditText.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "font/" + textFont));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textColorPickerDialog = ColorPickerDialog.createColorPickerDialog(getContext(), ColorPickerDialog.DARK_THEME);
        shadowColorPickerDialog = ColorPickerDialog.createColorPickerDialog(getContext(), ColorPickerDialog.DARK_THEME);

        textColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textColorPickerDialog.show();
            }
        });
        textColorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                textHexColor = color;
//                textView.setTextColor(Color.parseColor(hexVal));
                mAddTextEditText.setTextColor(Color.parseColor(hexVal));
                textColor.setBackgroundColor(Color.parseColor(hexVal));
            }
        });

        shadowColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shadowColorPickerDialog.show();
            }
        });
        shadowColorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                shadowHexColor = color;
                mAddTextEditText.setShadowLayer(shadowSize, 0, 0,shadowHexColor);
                shadowColor.setBackgroundColor(Color.parseColor(hexVal));
            }
        });

        borderSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                borderSizeSeekBar.setProgress(progress);
                shadowSize = progress/4;
                mAddTextEditText.setShadowLayer( shadowSize,0,0,shadowHexColor);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSizeSeekBar.setProgress(progress);
                textSize = progress;
                mAddTextEditText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mAddTextEditText.setText(getArguments().getString(EXTRA_INPUT_TEXT));
        textHexColor = getArguments().getInt(EXTRA_TEXT_COLOR);
        shadowHexColor = getArguments().getInt(EXTRA_SHADOW_COLOR);
        textSize=getArguments().getInt(EXTRA_TEXT_SIZE);
        shadowSize=getArguments().getInt(EXTRA_SHADOW_SIZE);
        textFont = getArguments().getString(EXTRA_TEXT_FONT);
        gravity=getArguments().getInt(EXTRA_GRAVITY);

        if(gravity==Gravity.CENTER)
        {
            center.setBackgroundResource(R.drawable.background_border);
            left.setBackgroundResource(0);
            right.setBackgroundResource(0);
        }
        else if(gravity==(Gravity.CENTER|Gravity.LEFT))
        {
            right.setBackgroundResource(R.drawable.background_border);
            center.setBackgroundResource(0);
            left.setBackgroundResource(0);
        }

        else if(gravity==(Gravity.CENTER|Gravity.RIGHT))
        {
            left.setBackgroundResource(R.drawable.background_border);
            center.setBackgroundResource(0);
            right.setBackgroundResource(0);
        }

        textSizeSeekBar.setProgress((int)textSize);
        borderSizeSeekBar.setProgress((int) (shadowSize*4));
        mAddTextEditText.setTextColor(textHexColor);
        textColor.setBackgroundColor(textHexColor);
        shadowColor.setBackgroundColor(shadowHexColor);


        mAddTextEditText.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "font/" + textFont));
        mAddTextEditText.setTextSize(textSize);
        mAddTextEditText.setShadowLayer(shadowSize,0,0,shadowHexColor);
        mAddTextEditText.setGravity(gravity);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                dismiss();
                String inputText = mAddTextEditText.getText().toString();
                if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                    mTextEditor.onDone(inputText, textHexColor,shadowHexColor,textSize,shadowSize,textFont,gravity);
                }
            }
        });

    }


    //Callback to listener if user is done with text editing
    public void setOnTextEditorListener(TextEditor textEditor) {
        mTextEditor = textEditor;
    }
}
