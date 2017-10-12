package pricemypiece.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.sawyer.advadapters.widget.PatchedExpandableListAdapter;

import br.com.cozinheiro.pricemypiece.R;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpandableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpandableListFragment extends Fragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    private static final String STATE_EXPANDABLE_LISTVIEW = "State Expandable ListView";

    @InjectView(R.id.list) ExpandableListView mExpandableListView;
    private ExpandableListAdapter mAdapter;
    public ExpandableListFragment() {
        // Required empty public constructor

    }

    public ExpandableListView getExpandableListView() {
        return mExpandableListView;
    }
    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    public ExpandableListAdapter getListAdapter() {
        return mAdapter;
    }
    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ExpandableListAdapter adapter) {
        mAdapter = adapter;
        if (mExpandableListView != null) mExpandableListView.setAdapter(adapter);
    }
    public static ExpandableListFragment newInstance() {
        ExpandableListFragment fragment = new ExpandableListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
		/*
		 In order to properly restore the activated items in the list, we must call into the adapter
		 to save it's state. The adapter will return a parcelable for us to place in the bundle.
		 It's important to note that the adapter will NOT place it's internal item data into this
		 parcelable. You must still manually call and save the ArrayList returned with getList().
		 */
        if (getListAdapter() instanceof PatchedExpandableListAdapter) {
            PatchedExpandableListAdapter adapter = (PatchedExpandableListAdapter) getListAdapter();
            Parcelable parcel = adapter.onSaveInstanceState();
            outState.putParcelable(STATE_EXPANDABLE_LISTVIEW, parcel);
        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //In case adapter set before ListView inflated
        if (mAdapter != null) mExpandableListView.setAdapter(mAdapter);
        if (mAdapter instanceof PatchedExpandableListAdapter) {
            PatchedExpandableListAdapter adapter = (PatchedExpandableListAdapter) mAdapter;
            adapter.setOnGroupClickListener(this);
            adapter.setOnChildClickListener(this);
            if (savedInstanceState != null) {
                //Restore choice mode state and the activated items
                Parcelable parcel = savedInstanceState.getParcelable(STATE_EXPANDABLE_LISTVIEW);
                adapter.onRestoreInstanceState(parcel);
            }
        } else {
            mExpandableListView.setOnGroupClickListener(this);
            mExpandableListView.setOnChildClickListener(this);
        }
    }
}
