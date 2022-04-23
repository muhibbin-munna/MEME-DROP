package com.muhibbin.memes.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.muhibbin.memes.MainActivity;
import com.muhibbin.memes.fragments.Fragent_favorites;
import com.muhibbin.memes.fragments.Fragent_leaders;
import com.muhibbin.memes.fragments.Fragment_Settings;
import com.muhibbin.memes.fragments.Fragment_gamification;
import com.muhibbin.memes.fragments.Fragment_new;
import com.muhibbin.memes.fragments.Fragent_trending;
import com.muhibbin.memes.R;

import org.jetbrains.annotations.NotNull;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2,R.string.tab_text_3,R.string.tab_text_4,R.string.tab_text_5};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
//        this.mCategoryName = mCategoryName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment= new Fragment_new();
                break;
            case 1:
                fragment= new Fragment_gamification();
                break;
            case 2:
                fragment= new Fragent_trending();
                break;
            case 3:
                fragment= new Fragent_leaders();
                break;
            case 4:
                fragment= new Fragent_favorites();
                break;
        }
        return fragment;

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 5;
    }

}