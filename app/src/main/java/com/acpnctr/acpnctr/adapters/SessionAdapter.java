package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

/**
 * Adapter to feed data from firestore to the RecyclerView via ViewHolder
 * for the sessions list in {@link com.acpnctr.acpnctr.fragments.SessionsListFragment}
 */

public class SessionAdapter extends FirestoreRecyclerAdapter<Session, SessionAdapter.SessionHolder> {

    private OnSessionSelectedListener mListener;

    public interface OnSessionSelectedListener {
        void onSessionSelected(Session session, int position);
        void onSessionRated(float rating, int position);
        void onSessionLongClicked(int position);
    }


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options recycler options with firestore Query
     */
    public SessionAdapter(FirestoreRecyclerOptions<Session> options, OnSessionSelectedListener listener) {
        super(options);
        mListener = listener;
    }

    @Override
    public SessionHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.session_list_item, viewGroup, false);

        return new SessionHolder(view);
    }

    @Override
    protected void onBindViewHolder(SessionHolder holder, int position, Session model) {
        holder.bind(model, mListener);
    }

    static class SessionHolder extends RecyclerView.ViewHolder {

        // display the session date
        private TextView listItemSessionDate;

        // display the session goal
        private TextView listItemSessionGoal;

        // display session rating
        private RatingBar listItemSessionRating;

        // display the treatment list of abbreviated points
        private TextView listItemSessionTreatment;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextViews.
         * @param itemView The View that we inflate in onCreateViewHolder(ViewGroup, int) when creating
         *                 the FirestoreRecyclerAdapter in {@link com.acpnctr.acpnctr.fragments.SessionsListFragment}
         */
        public SessionHolder(View itemView) {
            super(itemView);

            // hook the views in the item view
            listItemSessionDate = itemView.findViewById(R.id.tv_sessions_list_date);
            listItemSessionGoal = itemView.findViewById(R.id.tv_sessions_list_goal);
            listItemSessionRating = itemView.findViewById(R.id.rb_session_rating);
            listItemSessionTreatment = itemView.findViewById(R.id.tv_session_list_treatment);
        }

        public void bind(final Session session, final OnSessionSelectedListener listener){
            listItemSessionDate.setText(DateFormatUtil.convertTimestampToString(
                    session.getTimestampCreated()));
            listItemSessionGoal.setText(session.getGoal());
            listItemSessionRating.setRating(session.getSessionRating());

            // build the treatment list string
            ArrayList<String> treatmentList = session.getTreatmentList();
            StringBuilder treatmentString = new StringBuilder();
            if (treatmentList != null) {
                for (int i = 0; i < treatmentList.size(); i++) {
                    treatmentString.append(treatmentList.get(i));
                    if (i < treatmentList.size() - 1) treatmentString.append(", ");
                }
            }
            listItemSessionTreatment.setText(treatmentString);

            // Listen for click to select item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        listener.onSessionSelected(session, getAdapterPosition());
                    }
                }
            });

            // Listen to session rating action
            listItemSessionRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    listener.onSessionRated(v, getAdapterPosition());
                }
            });

            // Listen to long click to delete item
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null){
                        listener.onSessionLongClicked(getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
