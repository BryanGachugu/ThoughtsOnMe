package com.gachugusville.thoughtsonme.Intro;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class IntroViewPager extends FragmentPagerAdapter {

    // creation of constructor of viewPager class
    public IntroViewPager(@NonNull FragmentManager fm, int behaviour) {
        super(fm, behaviour);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FirstIntroFragment();
            case 1:
                return new SecondIntroFragment();
            case 2:
                return new ThirdIntroFragment();
            case 3:

                return new FourthIntroFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public boolean isLastFragmentVisible(){
        return getItem(4).isVisible();
    }

}