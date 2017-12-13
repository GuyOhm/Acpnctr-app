package com.acpnctr.acpnctr.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.fragments.AnamnesisFragment;
import com.acpnctr.acpnctr.fragments.InformationFragment;
import com.acpnctr.acpnctr.fragments.SessionsListFragment;

/**
 * Created by guiguette on 10/07/2017.
 *
 * Fragment page adapter to display different fragments of the client file
 *
 */

public class ClientFragmentPageAdapter extends FragmentPagerAdapter {

    // TODO: refactor using switch statement

    /** Context of the app */
    private Context mContext;

    public ClientFragmentPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new InformationFragment();
        } else if (position == 1){
            return new AnamnesisFragment();
        } else {
            return new SessionsListFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.title_client_information);
        } else if (position == 1) {
            return mContext.getString(R.string.title_anamnesis);
        } else {
            return mContext.getString(R.string.title_history);
        }
    }
}
