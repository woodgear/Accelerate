package com.misakimei.accelerate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.misakimei.accelerate.tool.L;
import com.misakimei.accelerate.view.BasicActivity;

import java.io.File;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ExplorerActivity extends BasicActivity {

    private static final String TAG = "ExplorerActivity";
    @Nullable
    @InjectView(R.id.gridview)
    GridView gridView;
    File[] thumbnails;
    @Nullable
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    ThumbnailAdapter adapter;
    private Context mcontext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        ButterKnife.inject(this);

        Glide.get(this).clearMemory();
        mcontext = this;
        L.d(TAG, "onCreate");

        adapter = new ThumbnailAdapter(this);
        thumbnails = Master.getThumbs();
        L.d(TAG, Arrays.toString(thumbnails));
        adapter.setData(thumbnails);

        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent("draw");
                intent.putExtra("from", "EXPLOER");
                String file = thumbnails[position].getName();
                file = file.substring(0, file.length() - 4);
                intent.putExtra("file", file);
                mcontext.startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("draw");
                intent.putExtra("from", "DRAW");
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        thumbnails = Master.getThumbs();
        adapter.setData(thumbnails);

        adapter.notifyDataSetChanged();
    }
}

