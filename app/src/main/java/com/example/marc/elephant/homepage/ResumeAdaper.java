package com.example.marc.elephant.homepage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.marc.elephant.fragment.YigeFrag;
import com.example.marc.elephant.fragment.GuokeFrag;
import com.example.marc.elephant.fragment.Zhihu2Frag;
import com.example.marc.elephant.fragment.ZhihuFrag;

/**
 * Created by marc on 17-4-20.
 */

public class ResumeAdaper extends FragmentPagerAdapter {

    public ResumeAdaper(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (0 == position) {
            fragment = new ZhihuFrag();
        } else if (1 == position) {
            fragment = new YigeFrag();
        }else if (2==position){
            fragment=new GuokeFrag();
        }else if (3==position){
            fragment=new Zhihu2Frag();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "知乎日报";
            case 1:
                return "one一个";
            case 2:
                return "果壳精选";
            case 3:
                return "知乎推荐";
        }
        return null;
    }
}
