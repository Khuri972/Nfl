package com.Nflicks.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.Nflicks.MainActivity;
import com.Nflicks.fragment.AllFlickFragment;
import com.Nflicks.fragment.ContactsFragment;
import com.Nflicks.fragment.FollowingFragment;
import com.Nflicks.model.FollowingModel;

import java.util.ArrayList;

/**
 * Created by CRAFT BOX on 12/23/2016.
 */

public class MyPagerAdapter extends FragmentPagerAdapter{

    private final String[] TITLES = { "Flicks","FOLLOWING", "CONTACTS"};
    Fragment f;
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
        {
            f=new AllFlickFragment();
            Bundle b = new Bundle();
            b.putInt("ARG_PAGE",0);
            f.setArguments(b);
        }
        else if(position==1)
        {
            f=new FollowingFragment();
            Bundle b = new Bundle();
            b.putInt("ARG_PAGE",1);
            f.setArguments(b);
        }
        else
        {

            f=new ContactsFragment();
            Bundle b = new Bundle();
            b.putInt("ARG_PAGE",2);
            f.setArguments(b);
        }
        return f;
    }

    public void ContactFragmentMethod()
    {
        if(f instanceof ContactsFragment){
            ((ContactsFragment) f).ContatSync();
        }
    }

}
