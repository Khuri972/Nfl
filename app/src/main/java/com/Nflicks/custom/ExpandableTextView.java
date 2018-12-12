package com.Nflicks.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.Nflicks.R;


/**
 * Created by CRAFT BOX on 12/9/2016.
 */

public class ExpandableTextView extends TextView {
    private static final int DEFAULT_TRIM_LENGTH = 200;
    //String s= ""+Html.fromHtml("<b>.....Readmore<b>");
    private static final String ELLIPSIS = "...Readmore";

    private CharSequence originalText;
    private CharSequence trimmedText;
    private BufferType bufferType;
    private boolean trim = true;
    private int trimLength;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView);
        this.trimLength = typedArray.getInt(R.styleable.ExpandableTextView_trimLength, DEFAULT_TRIM_LENGTH);
        typedArray.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trim = !trim;
                setText();
                requestFocusFromTouch();
            }
        });
    }

    private void setText() {
        String te=getDisplayableText().toString();
        if (getDisplayableText().toString().indexOf("...Readmore") > 0) {
            //logo.setImageResource(R.drawable.local);
            super.setText(trimmedText, bufferType);
        } else {
            super.setText(Html.fromHtml("<font color=\"#000000\">"+getDisplayableText()+"</font>"), bufferType);
        }
    }

    private CharSequence getDisplayableText() {
        return trim ? trimmedText : originalText;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        trimmedText = getTrimmedText(text);
        bufferType = type;
        setText();
    }

    private CharSequence getTrimmedText(CharSequence text) {
        if (originalText != null && originalText.length() > trimLength) {
            return new SpannableStringBuilder(Html.fromHtml("<font color=\"#000000\">"+originalText+"</font>"), 0, trimLength + 1).append(Html.fromHtml("<font color=\"#c1c1c1\"><b>"+ELLIPSIS+"<b></font>"));
        } else {
            return originalText;
        }
    }

}
