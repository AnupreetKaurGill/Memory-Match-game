package com.example.gridmemorymatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private int columnWidth;
    GridMemoryMatch grid;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return grid.pos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView img;
        if (view == null) {
            img = new ImageView(this.context);
            img.setLayoutParams (new GridView.LayoutParams(columnWidth, columnWidth));
            img.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        else {
            img = (ImageView)view;
        }
        img.setImageResource(R.drawable.cover);
        return img;
    }

    public void setColumnWidth(int widthArg)
    {
        this.columnWidth = widthArg;
    }

}
