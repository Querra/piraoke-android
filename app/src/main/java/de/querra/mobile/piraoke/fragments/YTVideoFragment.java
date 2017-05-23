package de.querra.mobile.piraoke.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.List;

import de.querra.mobile.piraoke.R;
import de.querra.mobile.piraoke.adapters.MyYTVideoRecyclerViewAdapter;
import de.querra.mobile.piraoke.data_adapters.YTVideo;
import de.querra.mobile.piraoke.services.HttpServiceFactory;

public class YTVideoFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    private MyYTVideoRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public YTVideoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static YTVideoFragment newInstance(int columnCount) {
        YTVideoFragment fragment = new YTVideoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ytvideo_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            this.adapter = new MyYTVideoRecyclerViewAdapter(getContext(), mListener);
            recyclerView.setAdapter(adapter);
            new LoadVideosTask().execute("foreigner");
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(YTVideo ytVideo);
    }

    private class LoadVideosTask extends AsyncTask<String, ObjectUtils.Null, List<YTVideo>> {

        @Override
        protected List<YTVideo> doInBackground(String... strings) {
            String search = strings[0];
            List<YTVideo> ytVideos = null;

            try {
                ytVideos = HttpServiceFactory.getInstance().searchVideos(search);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ytVideos;
        }

        @Override
        protected void onPostExecute(List<YTVideo> ytVideos) {
            YTVideoFragment.this.adapter.setVideos(ytVideos);
        }
    }
}
