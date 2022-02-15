package com.gmp.android.darts42;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ThrowKeyboard extends ConstraintLayout implements View.OnClickListener {

    /*
    Custom keyboard based on
    https://stackoverflow.com/questions/9577304/how-can-you-make-a-custom-keyboard-in-android/45005691#45005691
     */

    // constructors
    public ThrowKeyboard(Context context) {
        this(context, null, 0);
    }

    public ThrowKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThrowKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private static final String scorePattern = "^([bom]{1})|^(^[dt]?[12]{1}[0]{1})|^(^[dt]?[1]?[1-9]{1})";
    private static final String TAG = "ThrowKeyboard";


    // keyboard keys (buttons)
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton0;
    private Button mButtonDelete;
    private Button mButtonEnter;
    private Button mButtonDouble;
    private Button mButtonTreble;
    private Button mButtonOuter;
    private Button mButtonBull;
    private Button mButtonMiss;

    private ExtractedText extractedText = new ExtractedText();
    private CharSequence extractedCharSequence;
    private String editedString;



    // This will map the button resource id to the String value that we want to
    // input when that button is clicked.
    SparseArray<String> keyValues = new SparseArray<>();

    // Our communication link to the EditText
    InputConnection inputConnection;

    private void init(Context context, AttributeSet attrs) {

        // initialize buttons
        LayoutInflater.from(context).inflate(R.layout.throw_keyboard, this, true);
        mButton1 = (Button) findViewById(R.id.button_1);
        mButton2 = (Button) findViewById(R.id.button_2);
        mButton3 = (Button) findViewById(R.id.button_3);
        mButton4 = (Button) findViewById(R.id.button_4);
        mButton5 = (Button) findViewById(R.id.button_5);
        mButton6 = (Button) findViewById(R.id.button_6);
        mButton7 = (Button) findViewById(R.id.button_7);
        mButton8 = (Button) findViewById(R.id.button_8);
        mButton9 = (Button) findViewById(R.id.button_9);
        mButton0 = (Button) findViewById(R.id.button_0);
        mButtonDelete = (Button) findViewById(R.id.button_delete);
        mButtonEnter = (Button) findViewById(R.id.button_enter);
        mButtonDouble = (Button) findViewById(R.id.button_double);
        mButtonTreble = (Button) findViewById(R.id.button_treble);
        mButtonOuter = (Button) findViewById(R.id.button_outer);
        mButtonBull = (Button) findViewById(R.id.button_bull);
        mButtonMiss = (Button) findViewById(R.id.button_miss);

        // set button click listeners
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);
        mButtonEnter.setOnClickListener(this);
        mButtonDouble.setOnClickListener(this);
        mButtonTreble.setOnClickListener(this);
        mButtonOuter.setOnClickListener(this);
        mButtonBull.setOnClickListener(this);
        mButtonMiss.setOnClickListener(this);

        // map buttons IDs to input strings
        keyValues.put(R.id.button_1, "1");
        keyValues.put(R.id.button_2, "2");
        keyValues.put(R.id.button_3, "3");
        keyValues.put(R.id.button_4, "4");
        keyValues.put(R.id.button_5, "5");
        keyValues.put(R.id.button_6, "6");
        keyValues.put(R.id.button_7, "7");
        keyValues.put(R.id.button_8, "8");
        keyValues.put(R.id.button_9, "9");
        keyValues.put(R.id.button_0, "0");
        keyValues.put(R.id.button_enter, "\n");
        keyValues.put(R.id.button_double, "d");
        keyValues.put(R.id.button_treble, "t");
        keyValues.put(R.id.button_outer, "o");
        keyValues.put(R.id.button_bull, "b");
        keyValues.put(R.id.button_miss, "m");

        mButtonEnter.setEnabled(false);

    }

    @Override
    public void onClick(View v) {

        // do nothing if the InputConnection has not been set yet
        if (inputConnection == null) return;

        // Delete text or input key value
        // All communication goes through the InputConnection
        if (v.getId() == R.id.button_delete) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                // delete the selection
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
        extractedText = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
        extractedCharSequence = extractedText.text;
        editedString = extractedCharSequence.toString();
        Log.d(TAG,editedString);


        if (editedString.matches(scorePattern) && editedString.length() > 0)
        {
            mButtonEnter.setAlpha(1.0f);
            mButtonEnter.setEnabled(true);
         }
        else
        {
            mButtonEnter.setAlpha(0.5f);
            mButtonEnter.setEnabled(false);
        }

    }

    // The activity (or some parent or controller) must give us
    // a reference to the current EditText's InputConnection
    public void setInputConnection(InputConnection ic) {
        this.inputConnection = ic;
    }
}
