package br.com.cozinheirodelivery.pricemyprint.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Activities.MainActivity;
import br.com.cozinheirodelivery.pricemyprint.Adapters.MaterialAdapter.CustomMaterialListAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.MaterialAdapter.MaterialAdapter;
import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.ConfigurationsItemDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.DeleteConfigurationConfirmationDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.DeleteMaterialConfirmationDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewMaterialDialog;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 29/12/2016.
 */

public class MaterialFragment extends Fragment {
    Project oProject;
    MaterialAdapter adapter;
    RecyclerView recyclerView;
    ActionMode actionMode;
    private MaterialFragment.ActionModeCallback actionModeCallback = new MaterialFragment.ActionModeCallback();

    public static MaterialFragment newInstance(Project oProject){
        MaterialFragment frag = new MaterialFragment();
        Bundle args = new Bundle();
        args.putParcelable("oProject",oProject);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            oProject = getArguments().getParcelable("oProject");
        }
        if(savedInstanceState != null){
            oProject = savedInstanceState.getParcelable("oProject");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_material,container,false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        loadData();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(getActivity()).getOrientation()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return v;
    }

    public void loadData(){
        if(oProject.getMateriaisList() == null){
            oProject.setMateriaisList(new ArrayList<TiposMateriais>());
        }
        if (adapter == null){
            adapter = new MaterialAdapter(getActivity(), oProject.getMateriaisList(), new MaterialAdapter.onMaterialListener() {
                @Override
                public void onMaterialClickListener(int position) {
                    if(actionMode != null){
                        toggleSelection(position);
                    }else{
                        DialogFragment frag = NewMaterialDialog.newInstance(oProject.getMateriaisList().get(position),oProject,MainActivity.ACTION_EDIT);
                        frag.show(getChildFragmentManager(),"NewMaterialDialog");
                    }
                }

                @Override
                public void onMaterialLongClickListener(int position) {
                    if (actionMode == null) {
                        AppCompatActivity activity = (AppCompatActivity)getActivity();
                        actionMode = activity.startSupportActionMode(actionModeCallback);

                    }

                    toggleSelection(position);
                }
            });
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("oProject",oProject);
    }
    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            if(count == 1){
                if(actionMode.getMenu().size()== 1) {
                    actionMode.getMenu().clear();
                    actionMode.getMenuInflater().inflate(R.menu.list_toolbar_config_selected_single, actionMode.getMenu());
                }
            }else{
                actionMode.getMenu().clear();
                actionMode.getMenuInflater().inflate(R.menu.list_toolbar_config_selected,actionMode.getMenu());
            }
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    public void updateRecyclerView(TiposMateriais oMaterial, String cAction) {
        switch (cAction){
            case MainActivity.ACTION_NEWFAB:
                if(oProject.getMateriaisList() == null){
                    oProject.setMateriaisList(new ArrayList<TiposMateriais>());
                }
                oProject.getMateriaisList().add(oMaterial);
                adapter.notifyItemInserted(oProject.getMateriaisList().size());
                break;
            case MainActivity.ACTION_NEWPIECE:
                if(oProject.getMateriaisList() == null){
                    oProject.setMateriaisList(new ArrayList<TiposMateriais>());
                }
                oProject.getMateriaisList().add(oMaterial);
                adapter.notifyItemInserted(oProject.getMateriaisList().size());
                break;
            case MainActivity.ACTION_NEWQUICK:
                if(oProject.getMateriaisList() == null){
                    oProject.setMateriaisList(new ArrayList<TiposMateriais>());
                }
                oProject.getMateriaisList().add(oMaterial);
                adapter.notifyItemInserted(oProject.getMateriaisList().size());
                break;
            case MainActivity.ACTION_EDIT:
                adapter.notifyItemChanged(oProject.getMateriaisList().indexOf(oMaterial));
                break;
            case MainActivity.ACTION_REMOVE:

                break;
            default:
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = MaterialFragment.ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if(adapter.getSelectedItems().size()==0) {
                mode.getMenuInflater().inflate(R.menu.list_toolbar_config_selected_single, menu);
            }else{
                mode.getMenuInflater().inflate(R.menu.list_toolbar_config_selected, menu);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ic_remove:
                    List<TiposMateriais> mSelectedList = new ArrayList<>();
                    final List<Integer> mSelectedIndexes = adapter.getSelectedItems();
                    for(int i:mSelectedIndexes){
                        mSelectedList.add(oProject.getMateriaisList().get(i));
                    }
                    DeleteMaterialConfirmationDialog dialog = new DeleteMaterialConfirmationDialog(getActivity(), mSelectedList, new DeleteMaterialConfirmationDialog.onMaterialDeleteConfirmationListener() {
                        @Override
                        public void onDeleteConfirmationSuccess(List<TiposMateriais> materiaisList) {
                            int i = -1;
                            int j = 0;
                                DB db = new DB(getActivity());
                                for(TiposMateriais oMaterial:materiaisList) {
                                    try {
                                        i++;
                                        db.deleteMaterialSingle(oMaterial);
                                        //mDeletedIndexes.add(mSelectedIndexes.get(i));
                                        oProject.getMateriaisList().remove(mSelectedIndexes.get(i)-j);
                                        adapter.notifyItemRemoved(mSelectedIndexes.get(i)-j);
                                        j++;
                                    }catch(Exception e){
                                        Toast.makeText(getActivity().getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show(); // Erro provindo do trow do DB.
                                    }
                                }
                        }
                    });
                    dialog.showDialog();
                    mode.finish();
                    return true;
                case R.id.ic_edit:
                    TiposMateriais oMaterial = oProject.getMateriaisList().get(adapter.getSelectedItems().get(0));
                    DialogFragment d = NewMaterialDialog.newInstance(oMaterial,oProject, MainActivity.ACTION_EDIT);
                    d.show(getChildFragmentManager(),"NewMaterialDialog");
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
        }
    }
}
