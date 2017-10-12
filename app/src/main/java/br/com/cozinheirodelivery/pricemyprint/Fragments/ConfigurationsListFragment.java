package br.com.cozinheirodelivery.pricemyprint.Fragments;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Activities.MainActivity;
import br.com.cozinheirodelivery.pricemyprint.Adapters.ConfigurationAdapter.ConfigurationsAdapter;
import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.ConfigurationsItemDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.DeleteConfigurationConfirmationDialog;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;


public class ConfigurationsListFragment extends Fragment {

    Project oProject;
    RecyclerView recyclerView;
    ConfigurationsAdapter adapter;
    ActionMode actionMode;
    private ConfigurationsListFragment.ActionModeCallback actionModeCallback = new ConfigurationsListFragment.ActionModeCallback();
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConfigurationsListFragment() {
    }


    public static ConfigurationsListFragment newInstance() {
        ConfigurationsListFragment fragment = new ConfigurationsListFragment();
        return fragment;
    }
    public static ConfigurationsListFragment newInstance(Project oProject) {
        ConfigurationsListFragment fragment = new ConfigurationsListFragment();
        Bundle args = new Bundle();
        args.putParcelable("oProject",oProject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(oProject != null){
            outState.putParcelable("oProject",oProject);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            oProject = getArguments().getParcelable("oProject");
        }
        if(savedInstanceState != null){
            oProject = savedInstanceState.getParcelable("oProject");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configurations, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        updateList();
        // @TODO: Alterar a forma de deletar estas listas single para o choiceMode multiple.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),new LinearLayoutManager(getActivity()).getOrientation()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    private void updateList(){
        if(oProject.getConfigurationsList() == null){
            oProject.setConfigurationsList(new ArrayList<Configurations>());
        }
        adapter = new ConfigurationsAdapter(getActivity(), oProject.getConfigurationsList(), new ConfigurationsAdapter.onAdapterListener() {
            @Override
            public void onClickListener(int position) {
                if(actionMode!= null){
                    toggleSelection(position);
                }else{
                    Configurations oConf = oProject.getConfigurationsList().get(position);
                    DialogFragment d = ConfigurationsItemDialog.newInstance(oConf,null,MainActivity.ACTION_EDIT);
                    d.show(getChildFragmentManager(),"ConfigurationsItemFragment");
                }
            }

            @Override
            public void onLongClickListener(int position) {
                if (actionMode == null) {
                    AppCompatActivity activity = (AppCompatActivity)getActivity();
                    actionMode = activity.startSupportActionMode(actionModeCallback);

                }

                toggleSelection(position);
            }
        });
        adapter.notifyDataSetChanged();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void updateRecyclerView(Configurations oConf,String cAction){
        switch (cAction){
            case MainActivity.ACTION_NEWFAB:
                if(oProject.getConfigurationsList() == null){
                    oProject.setConfigurationsList(new ArrayList<Configurations>());
                }
                //Verifica se já fora adicionado a conf. para o Projeto.
                if(!oProject.getConfigurationsList().contains(oConf)) {
                    oProject.getConfigurationsList().add(oConf);
                }
                adapter.notifyItemInserted(oProject.getConfigurationsList().size());
                break;
            case MainActivity.ACTION_NEWPIECE:
                if(oProject.getConfigurationsList() == null){
                    oProject.setConfigurationsList(new ArrayList<Configurations>());
                }
                //Verifica se já fora adicionado a conf. para o Projeto.
                if(!oProject.getConfigurationsList().contains(oConf)) {
                    oProject.getConfigurationsList().add(oConf);
                }
                adapter.notifyItemInserted(oProject.getConfigurationsList().size());
                break;
            case MainActivity.ACTION_NEWQUICK:
                if(oProject.getConfigurationsList() == null){
                    oProject.setConfigurationsList(new ArrayList<Configurations>());
                }
                //Verifica se já fora adicionado a conf. para o Projeto.
                if(!oProject.getConfigurationsList().contains(oConf)) {
                    oProject.getConfigurationsList().add(oConf);
                }
                adapter.notifyItemInserted(oProject.getConfigurationsList().size());
                break;
            case MainActivity.ACTION_EDIT:
                adapter.notifyItemChanged(oProject.getConfigurationsList().indexOf(oConf));
                break;
            case MainActivity.ACTION_REMOVE:

                break;
            default:
        }
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


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ConfigurationsListFragment.ActionModeCallback.class.getSimpleName();

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
                    List<Configurations> mSelectedList = new ArrayList<>();
                    final List<Integer> mSelectedIndexes = adapter.getSelectedItems();
                    for(int i:mSelectedIndexes){
                        mSelectedList.add(oProject.getConfigurationsList().get(i));
                    }
                    DeleteConfigurationConfirmationDialog dialog = new DeleteConfigurationConfirmationDialog(getActivity(), mSelectedList, new DeleteConfigurationConfirmationDialog.onConfigurationDeleteConfirmationListener() {
                        @Override
                        public void onConfigurationConfirmationSuccess(List<Configurations> configurationsList) {
                            int i = -1;
                            int j = 0;
                            DB db = new DB(getActivity());

                            for(Configurations oConf: configurationsList) {
                                try {
                                    i++;
                                    db.deleteConfiguration(oConf);
                                    oProject.getConfigurationsList().remove(mSelectedIndexes.get(i)-j);
                                    adapter.notifyItemRemoved(mSelectedIndexes.get(i)-j);
                                    j++;
                                }catch(Exception e){
                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            //Toast.makeText(getActivity().getApplicationContext(), R.string.delete_confirmation, Toast.LENGTH_SHORT).show();

                        }
                    });
                    dialog.showDialog();
                    mode.finish();
                    return true;
                case R.id.ic_edit:
                    Configurations oConf = oProject.getConfigurationsList().get(adapter.getSelectedItems().get(0));
                    DialogFragment d = ConfigurationsItemDialog.newInstance(oConf,null,MainActivity.ACTION_EDIT);
                    d.show(getChildFragmentManager(),"ConfigurationsItemFragment");
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
