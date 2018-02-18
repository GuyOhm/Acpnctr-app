package com.acpnctr.acpnctr.adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.PulseType;

/**
 * Adapter to display the 28 types of pulse
 */

public class PulseTypesAdapter extends BaseAdapter {

    private Context mContext;
    private PulseType[] pulses;

    public PulseTypesAdapter(Context mContext, PulseType[] pulses) {
        this.mContext = mContext;
        this.pulses = pulses;
    }

    @Override
    public int getCount() {
        return pulses.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView typeNameView;
        TextView typeTranslationView;
        PulseType pulse = pulses[position];

        if (convertView == null){
            // if it's not recycled, inflate a new grid item
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.pulse_grid_item, null);
        }

        // hook up views
        typeNameView = convertView.findViewById(R.id.tv_pulses_28_type);
        typeTranslationView = convertView.findViewById(R.id.tv_pulses_28_transl);

        // display data in views
        typeNameView.setText(mContext.getString(pulse.getName()));
        typeTranslationView.setText(mContext.getString(pulse.getTranslation()));

        // change color of text and background if item is selected
        typeNameView.setTextColor(ResourcesCompat
                .getColor(mContext.getResources(),
                        pulse.isSelected()? R.color.white: R.color.colorAccent,
                        mContext.getTheme()));
        typeTranslationView.setTextColor(ResourcesCompat
                .getColor(mContext.getResources(),
                        pulse.isSelected()? R.color.white: R.color.colorAccent,
                        mContext.getTheme()));
        convertView.setBackgroundColor(ResourcesCompat
                .getColor(mContext.getResources(),
                        pulse.isSelected()? R.color.colorAccent: R.color.white,
                        mContext.getTheme()));

        return convertView;
    }
}
