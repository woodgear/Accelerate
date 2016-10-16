package com.misakimei.accelerate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

/**
 * Created by 吴聪 on 2016/6/3.
 */
class ThumbnailAdapter extends BaseAdapter {

    private static final String TAG = "SampleGridViewAdapter";
    private Context context;
    private File[] files;
    private LayoutInflater mInflater;

    ThumbnailAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(File[] data) {
        files = data;
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView picture;


        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.view_grid_item, parent, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);


        File image = (File) getItem(position);

        Glide.with(context)
                .load(image)
                .placeholder(Master.getPlaceHolder())
                .dontAnimate()
                .centerCrop()
                .signature(new StringSignature(System.currentTimeMillis() + ""))
                .into(picture);
        String filename = image.getName();
        filename = filename.substring(0, filename.length() - 4);
        name.setText(filename);
        return v;


    }
}
