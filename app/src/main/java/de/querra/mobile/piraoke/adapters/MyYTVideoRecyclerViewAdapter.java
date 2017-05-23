package de.querra.mobile.piraoke.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.querra.mobile.piraoke.R;
import de.querra.mobile.piraoke.data_adapters.YTVideo;
import de.querra.mobile.piraoke.fragments.YTVideoFragment;

public class MyYTVideoRecyclerViewAdapter extends RecyclerView.Adapter<MyYTVideoRecyclerViewAdapter.ViewHolder> {

    private final List<YTVideo> mValues = new ArrayList<>();
    private final YTVideoFragment.OnListFragmentInteractionListener mListener;
    private final Context context;

    public MyYTVideoRecyclerViewAdapter(Context context, YTVideoFragment.OnListFragmentInteractionListener listener) {
        mListener = listener;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView image;
        public final TextView title;
        public final TextView duration;
        public YTVideo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.title);
            image = (ImageView) view.findViewById(R.id.image);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ytvideo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        YTVideo ytVideo = mValues.get(position);

        holder.mItem = ytVideo;
        Picasso.with(this.context).load(ytVideo.getImageUrl()).into(holder.image);
        holder.title.setText(ytVideo.getTitle());
        holder.duration.setText(ytVideo.getDuration());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setVideos(List<YTVideo> ytVideos) {
        this.mValues.clear();
        this.mValues.addAll(ytVideos);
        notifyDataSetChanged();
    }
}
