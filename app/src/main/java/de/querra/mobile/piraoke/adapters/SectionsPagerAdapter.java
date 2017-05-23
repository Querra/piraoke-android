package de.querra.mobile.piraoke.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.querra.mobile.piraoke.fragments.YTVideoFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private int count = 1;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments.add(YTVideoFragment.newInstance(1));
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        */
        return String.format(Locale.getDefault(), "Section %d", position);
    }
}