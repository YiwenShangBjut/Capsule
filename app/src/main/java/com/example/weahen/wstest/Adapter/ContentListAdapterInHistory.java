package com.example.weahen.wstest.Adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.weahen.wstest.Activity.MainActivity;
import com.example.weahen.wstest.Activity.ShowImageActivity;
import com.example.weahen.wstest.Model.Content;
import com.example.weahen.wstest.Obj.ImageInfoObj;
import com.example.weahen.wstest.Obj.ImageWidgetInfoObj;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.widget.CircleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ContentListAdapterInHistory extends BaseAdapter implements View.OnClickListener {


    private Context context;
    private List<Content> contentItems;

    Content m;
    ImageView imageView;
    Uri uri;
    private ImageInfoObj imageInfoObj;
    private ImageWidgetInfoObj imageWidgetInfoObj;


    public ContentListAdapterInHistory(Context context, List<Content> navDrawerItems) {
        this.context = context;
        this.contentItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return contentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return contentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        m = contentItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        Log.e("ContentListAdapter","on position "+position+" isSelf is "+contentItems.get(position).isSelf());
        // 识别消息发送方
        if (contentItems.get(position).isSelf()) {
            // message belongs to you, so load the right aligned layout
            if (contentItems.get(position).isPicture()) {
                convertView = mInflater.inflate(R.layout.item_image_send, null);
                imageView = convertView.findViewById(R.id.bivPic);
                imageView.setImageBitmap(m.getPicture());

//               uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), m.getPicture(), null,null));
//
//                Log.e("uri",uri+"");
//
//                init();
//                imageView.setOnClickListener(this);


            } else {
                convertView = mInflater.inflate(R.layout.list_item_message_right, null);
                TextView txtMsg = convertView.findViewById(R.id.txtMsg);
                txtMsg.setText(m.getContent());
            }
        } else {
            if (contentItems.get(position).isPicture()) {
                convertView = mInflater.inflate(R.layout.item_image_receive, null);
                imageView = convertView.findViewById(R.id.bivPic);
                imageView.setImageBitmap(m.getPicture());
//                init();
//                imageView.setOnClickListener(this);

//
            } else {
                // message belongs to other person, load the left aligned layout
                convertView = mInflater.inflate(R.layout.list_item_message_left, null);
                TextView txtMsg = convertView.findViewById(R.id.txtMsg);
                txtMsg.setText(m.getContent());


            }
        }

        TextView txtlblMsgFrom = convertView.findViewById(R.id.lblMsgFrom);
        txtlblMsgFrom.setText(m.getUserName());
        CircleImageView imageSend=convertView.findViewById(R.id.chat_item_header);
        Log.e("Tag1","MainActivity.sendHeadImage"+m.getHeadImage());

        int i=Integer.parseInt(m.getHeadImage());
        Log.e("Tag1","i"+i);
        imageSend.setImageResource(i);

//        TextView txtTime = convertView.findViewById(R.id.time);
//        txtTime.setText(m.getTime());


        return convertView;
    }

    private void init() {


        imageInfoObj = new ImageInfoObj();
        imageInfoObj.imageUrl = uri.toString();
        imageInfoObj.imageWidth = 1280;
        imageInfoObj.imageHeight = 720;

        imageWidgetInfoObj = new ImageWidgetInfoObj();
        imageWidgetInfoObj.x = imageView.getLeft();
        imageWidgetInfoObj.y = imageView.getTop();
        imageWidgetInfoObj.width = imageView.getLayoutParams().width;
        imageWidgetInfoObj.height = imageView.getLayoutParams().height;

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra("imageInfoObj", imageInfoObj);
        intent.putExtra("imageWidgetInfoObj", imageWidgetInfoObj);
        context.startActivity(intent);

    }
}


