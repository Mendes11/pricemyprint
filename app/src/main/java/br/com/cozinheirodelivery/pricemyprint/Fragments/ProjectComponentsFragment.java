package br.com.cozinheirodelivery.pricemyprint.Fragments;



import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Activities.MainActivity;
import br.com.cozinheirodelivery.pricemyprint.Adapters.TabAdapter.TabAdapter;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewProjectDialog;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.SharedPrefs;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;


public class ProjectComponentsFragment extends Fragment {
    TabAdapter adapter;
    List<String> titleList;
    ViewPager viewPager;
    Project oProject;
    TabLayout tabLayout;
    Toolbar toolbar;
    Boolean isQuickPrice = false;
    private static final String QUICK_PRICE_PROJECT_ID = "quickPriceProjectID";
    public ProjectComponentsFragment() {
        // Required empty public constructor
    }

    public static ProjectComponentsFragment newInstance(Project oProject) {
        ProjectComponentsFragment fragment = new ProjectComponentsFragment();
        Bundle args = new Bundle();
        args.putParcelable("oProject", oProject);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            oProject = getArguments().getParcelable("oProject");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_project_components, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        init();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(oProject.getcNome());
        return v;
    }
    public void init(){
        populateTitleList();
        setupViewPager(viewPager);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
    }
    private void populateTitleList(){
        titleList = new ArrayList<>();
        if(oProject.getiIDProjeto() != Long.parseLong(SharedPrefs.readSharedSetting(getActivity(),QUICK_PRICE_PROJECT_ID,String.valueOf(-1)))) {
            titleList.add(getString(R.string.tab_piece_title));
        }else{
            isQuickPrice = true;
        }
        titleList.add(getString(R.string.tab_configuration_title));
        titleList.add(getString(R.string.tab_material_title));
    }
    private void setupViewPager(ViewPager viewPager) {
        Boolean isQuickPrice = false;
        if(oProject.getiIDProjeto() != Long.parseLong(SharedPrefs.readSharedSetting(getActivity(),QUICK_PRICE_PROJECT_ID,String.valueOf(-1)))) {
            isQuickPrice = true;
        }
        if(adapter == null) {
            adapter = new TabAdapter(getActivity(), getChildFragmentManager(), oProject, titleList,isQuickPrice);
            if(viewPager.getAdapter() == null) {
                viewPager.setAdapter(adapter);
            }
        }
    }
    public void updatePrintsRecyclerView(Piece oPiece, String action) {
        if(!isQuickPrice) {
            PrintsFragment frag = (PrintsFragment) adapter.getRegisteredFragment(0);
            if(frag != null) {
                frag.updateRecyclerView(oPiece, action);
            }
        }
    }

    public void updateConfigRecyclerView(Configurations oConf,String cAction){
        ConfigurationsListFragment frag = null;
        if(isQuickPrice){
            frag = (ConfigurationsListFragment) adapter.getRegisteredFragment(0);
        }else{
            frag = (ConfigurationsListFragment) adapter.getRegisteredFragment(1);
        }
        if(frag != null) {
            frag.updateRecyclerView(oConf, cAction);
        }else{// Para caso não encontre o fragmento, por este não estar aberto no adapter, atualiza o projeto por aqui.
            if(oProject.getConfigurationsList() == null){ // Prevenção de null pointer.
                oProject.setConfigurationsList(new ArrayList<Configurations>());
            }
            if(!oProject.getConfigurationsList().contains(oConf)) { //Verifica se não haverá duplicatas (problemas no código =/)
                oProject.getConfigurationsList().add(oConf);
            }
        }
        if(cAction.equals(MainActivity.ACTION_EDIT)){
            if(!isQuickPrice){
                PrintsFragment mFrag = (PrintsFragment) adapter.getRegisteredFragment(0);
                if(mFrag != null) {
                    mFrag.refreshRecyclerView();
                }
            }
        }
    }
    public void updateMaterialRecyclerView(TiposMateriais oMaterial, String cAction){
        MaterialFragment frag = null;
        if(isQuickPrice) {
            frag = (MaterialFragment) adapter.getRegisteredFragment(1);
        }else{
            frag = (MaterialFragment) adapter.getRegisteredFragment(2);
        }
        if(frag != null) {
            frag.updateRecyclerView(oMaterial, cAction);
        }else{
            // Para caso não encontre o fragmento, por este não estar aberto no adapter, atualiza o projeto por aqui.
            if(oProject.getMateriaisList() == null){ //Prevenção de erros...
                oProject.setMateriaisList(new ArrayList<TiposMateriais>());
            }
            if(!oProject.getMateriaisList().contains(oMaterial)) { //Verifica se não há duplicatas (Problemas no Código =/)
                oProject.getMateriaisList().add(oMaterial);
            }
        }
        if(cAction.equals(MainActivity.ACTION_EDIT)){
            if(!isQuickPrice){
                PrintsFragment mFrag = (PrintsFragment) adapter.getRegisteredFragment(0);
                if(mFrag != null) {
                    mFrag.refreshRecyclerView();
                }
            }
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("oProject",oProject);
        getActivity().getSupportFragmentManager().putFragment(outState,"ProjectComponentsFragment",this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            oProject = savedInstanceState.getParcelable("oProject");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Price My Print");
    }

    public Project getoProject(){
        return this.oProject;
    }

    public TabAdapter getAdapter(){
        return adapter;
    }

    public int getSelectedTab() {
        return tabLayout.getSelectedTabPosition();
    }
    public Boolean isQuickPrice(){
        return isQuickPrice;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_project_components,menu);
    }
}
