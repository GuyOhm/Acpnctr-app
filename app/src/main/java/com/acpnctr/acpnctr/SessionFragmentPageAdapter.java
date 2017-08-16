package com.acpnctr.acpnctr;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by guiguette on 10/07/2017.
 *
 * Fragment page adapter to display different fragments of the session activity
 *
 */

public class SessionFragmentPageAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    public SessionFragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FourStepsFragment();
        } else if (position == 1){
            return new DiagnosisFragment();
        } else {
            return new TreatmentFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.title_four_steps);
        } else if (position == 1) {
            return mContext.getString(R.string.title_energetics_diagnosis);
        } else {
            return mContext.getString(R.string.title_treatment);
        }
    }
}
