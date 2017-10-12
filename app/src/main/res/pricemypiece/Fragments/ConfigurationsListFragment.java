package pricemypiece.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Dialogs.DeleteConfigurationConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.DeleteMaterialConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewMaterialDialog;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnConfigurationsListFragmentInteraction}
 * interface.
 */
public class ConfigurationsListFragment extends Fragment {

    ArrayAdapter<Configurations> adapter;
    List<Configurations> confList;
    ListView lista;

    private OnConfigurationsListFragmentInteraction mListener;
    private static final String mConfigurationListParam = "configurationsList";
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(confList != null){
            outState.putParcelableArrayList(mConfigurationListParam,(ArrayList<? extends Parcelable>)confList);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configurations, container, false);
        lista = (ListView) view.findViewById(R.id.list);
        setHasOptionsMenu(true);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(mConfigurationListParam)){
                confList = savedInstanceState.getParcelableArrayList(mConfigurationListParam);
            }else{
                getUpdatedConfList();
            }
        }else{
            getUpdatedConfList();
        }
        updateList();
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onConfigurationListIteration(confList.get(position));
            }
        });
        registerForContextMenu(lista);
        return view;
    }

    private void getUpdatedConfList(){
        DB db = new DB(getActivity().getApplicationContext());
        confList = db.getConfigurations(null,null,null,null);
    }
    private void updateList(){
        if(adapter == null) {
            adapter = new ArrayAdapter<Configurations>(getActivity(), android.R.layout.simple_list_item_1, confList);
            lista.setAdapter(adapter);
        }else{
            lista.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConfigurationsListFragmentInteraction) {
            mListener = (OnConfigurationsListFragmentInteraction) context;
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        int id = item.getItemId();
        switch(id){
            case R.id.edit:
                Configurations obj = confList.get(position);
                mListener.onConfigurationListIteration(obj);
                return super.onContextItemSelected(item);
            case R.id.delete:
                final Configurations oConf =  confList.get(position);
                DeleteConfigurationConfirmationDialog m = new DeleteConfigurationConfirmationDialog();
                m.showDialog(getActivity(), oConf, new DeleteConfigurationConfirmationDialog.onConfigurationDeleteConfirmationListener() {

                    @Override
                    public void onConfigurationConfirmationSuccess() {
                        confList.remove(oConf);
                        updateList();
                    }
                });
                return super.onContextItemSelected(item);
            default:
        }
        return false;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_material,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                mListener.onConfigurationListIteration(null);
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
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
    public void updateConfigurationItem(Configurations oConfiguration){
        if(confList.contains(oConfiguration)){
            //Significa que ele simplesmente Editou um valor existente, apenas atualiza a lista
            onSaveInstanceState(new Bundle());
        }else{
            // Aqui ele terá de adicionar este valor à lista.
            confList.add(oConfiguration);
            onSaveInstanceState(new Bundle());
        }
    }

    public interface OnConfigurationsListFragmentInteraction {
        void onConfigurationListIteration(Configurations oConfiguration);
    }
}
