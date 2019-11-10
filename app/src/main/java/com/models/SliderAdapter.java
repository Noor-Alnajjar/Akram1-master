package com.models;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joserv.Akram.R;

/**
 * Created by user on 4/25/18.
 */

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public int[] slide_images = {
            R.drawable.akram_white,
            R.drawable.board1,
            R.drawable.board8,
            R.drawable.board2,
            R.drawable.board9,
            R.drawable.board4,
            R.drawable.board7,
            R.drawable.board6,
            R.drawable.permistion,
            R.drawable.background_splash
            };


    public String[] slide_headings = {
            ""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
    };
    public String[] slide_desc = {
            ""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
            ,""
    };


    public SliderAdapter(Context context){
        this.context =context;


        slide_headings[0]=context.getResources().getString(R.string.title1);

        slide_headings[1]=context.getResources().getString(R.string.title2);
        slide_headings[2]=context.getResources().getString(R.string.title3);
        slide_headings[3]=context.getResources().getString(R.string.title4);
        slide_headings[4]=context.getResources().getString(R.string.title5);
        slide_headings[5]=context.getResources().getString(R.string.title6);
        slide_headings[6]=context.getResources().getString(R.string.title7);
        slide_headings[7]=context.getResources().getString(R.string.title8);
        slide_headings[8]=context.getResources().getString(R.string.title9);
        slide_headings[9]="";

        slide_desc[0]=context.getResources().getString(R.string.screen1);

        slide_desc[1]=context.getResources().getString(R.string.screen2);
        slide_desc[2]=context.getResources().getString(R.string.screen3);
        slide_desc[3]=context.getResources().getString(R.string.screen4);
        slide_desc[4]=context.getResources().getString(R.string.screen5);
        slide_desc[5]=context.getResources().getString(R.string.screen6);
        slide_desc[6]=context.getResources().getString(R.string.screen7);
        slide_desc[7]=context.getResources().getString(R.string.screen8);
        slide_desc[8]=context.getResources().getString(R.string.screen9);
        slide_desc[9]=context.getResources().getString(R.string.screen10);





    }


    //public int[] slide_images = {R.drawable.k11,R.drawable.k22,R.drawable.k33};

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {



        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container,false);

        //ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slidedescription = (TextView) view.findViewById(R.id.slide_desc);
        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);

        if(position!=0)
            slideImageView.setImageResource(slide_images[position]);

        slideHeading.setText(slide_headings[position]);
        slidedescription.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
