package com.example.uietfest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.uietfest.EventsPackage.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class Events extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        SectionPagerAdapter sectionsPagerAdapter = new SectionPagerAdapter(getContext(), getChildFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        /*This will load all the fragments once and keep from reloading them while swiping.*/
        viewPager.setOffscreenPageLimit(3);
        /*This will load all the fragments once and keep from reloading them while swiping.*/
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

}
