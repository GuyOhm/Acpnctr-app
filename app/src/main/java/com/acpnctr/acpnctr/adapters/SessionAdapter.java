package com.acpnctr.acpnctr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acpnctr.acpnctr.R;
import com.acpnctr.acpnctr.models.Session;
import com.acpnctr.acpnctr.utils.DateFormatUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

/**
 * Adapter to feed data from firestore to the RecyclerView via ViewHolder
 * for the sessions list in {@link com.acpnctr.acpnctr.fragments.SessionsListFragment}
 */

public class SessionAdapter extends FirestoreRecyclerAdapter<Session, SessionAdapter.SessionHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SessionAdapter(FirestoreRecyclerOptions<Session> options) {
        super(options);
    }

    @Override
    public SessionHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.session_list_item, viewGroup, false);

        return new SessionHolder(view);
    }

    @Override
    protected void onBindViewHolder(SessionHolder holder, int position, Session model) {
        holder.bind(model);
    }

    static class SessionHolder extends RecyclerView.ViewHolder {

        // display the session date
        private TextView listItemSessionDate;

        // display the session goal
        private TextView listItemSessionGoal;

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
        }

        public void bind(Session session){
            listItemSessionDate.setText(DateFormatUtil.convertTimestampToString(
                    session.getTimestampCreated()));
            listItemSessionGoal.setText(session.getGoal());
        }
    }
}
