package com.misakimei.accelerate;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by 吴聪 on 2016/6/4.
 */
public class OptionItem extends FrameLayout {

    View container;
    ImageView image;
    TextView text;
    private Context mcontext;

    public OptionItem(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        container = LayoutInflater.from(context).inflate(R.layout.view_option_item, null, false);
        image = (ImageView) container.findViewById(R.id.option_item_image);
        text = (TextView) container.findViewById(R.id.option_item_text);

        TypedArray typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.OptionItem);

        int size = typeArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int attr = typeArray.getIndex(i);
            switch (attr) {
                case R.styleable.OptionItem_imagesrc:
                    int str = typeArray.getResourceId(R.styleable.OptionItem_imagesrc, 0);
                    setImage(str);
                    break;
                case R.styleable.OptionItem_text:
                    int textid = typeArray.getResourceId(
                            R.styleable.OptionItem_text, 0);
                    setText(textid);
                    break;
            }
        }
        addView(container);

    }

    private void setText(int textid) {
        String text = mcontext.getResources().getString(textid);
        setText(text);
    }

    public void setText(String name) {
        text.setText(name);
    }

    public void setImage(int drawableid) {
        Drawable drawable = mcontext.getResources().getDrawable(drawableid);
        setImage(drawable);
    }

    public void setImage(Drawable drawable) {
        image.setImageDrawable(drawable);
    }

}
