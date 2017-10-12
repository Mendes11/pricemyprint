package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter.ImportConfigAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter.ImportMaterialAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter.ImportPieceAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/08/2016.
 */
public class NewProjectDialog extends DialogFragment {
    newProjectListener nListener;
    EditText cNome;
    Spinner importConfigSpinner,importMaterialSpinner;
    ListView importConfigList,importMaterialList;
    AppCompatCheckBox chkImportConfig,chkImportMaterial;
    TextView importConfigText,importMaterialText;
    Project project;
    Boolean editable = false;
    Boolean newProject = false; //Vou usar isso por enquanto para deixar importar só para novo projeto
    Piece oPiece;
    List<Project> projectList;
    List<Configurations> configList;
    List<TiposMateriais> materiaisList;
    ImportConfigAdapter configAdapter;
    ImportMaterialAdapter materialAdapter;

    public interface newProjectListener {
        public void onNewProjectPositiveClick(Project project,Piece oPiece,List<Configurations> configurationsList,List<TiposMateriais> materiaisList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            nListener = (newProjectListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static NewProjectDialog newInstance(List<Project> projectList){
        NewProjectDialog frag = new NewProjectDialog();
        Bundle args = new Bundle();
        args.putSerializable("projectList", (Serializable) projectList);
        frag.setArguments(args);
        return frag;
    }
    public static NewProjectDialog newInstance(List<Project> projectList,Piece oPiece){
        NewProjectDialog frag = new NewProjectDialog();
        Bundle args = new Bundle();
        args.putParcelable("Piece",oPiece);
        args.putSerializable("projectList", (Serializable) projectList);
        frag.setArguments(args);
        return frag;
    }
    public static NewProjectDialog newInstance(List<Project> projectList,Project project){
        NewProjectDialog frag = new NewProjectDialog();
        Bundle args = new Bundle();
        args.putSerializable("projectList", (Serializable) projectList);
        args.putParcelable("Project",project);
        args.putBoolean("Editable", true);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            if(bundle.containsKey("Editable")){
                editable = bundle.getBoolean("Editable");
                project = bundle.getParcelable("Project");
            }
            if(bundle.containsKey("Piece")){
                oPiece = bundle.getParcelable("Piece");
            }
            if(bundle.containsKey("projectList")){
                projectList = (List<Project>) getArguments().getSerializable("projectList");
                //newProject = true;
            }
        }
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("Editable")){
                if(savedInstanceState.getBoolean("Editable")){
                    editable = true;
                    project = savedInstanceState.getParcelable("Project");
                }
                if(savedInstanceState.containsKey("Piece")){
                    oPiece = savedInstanceState.getParcelable("Piece");
                }
            }
            if(savedInstanceState.containsKey("projectList")){
                projectList = (List<Project>) savedInstanceState.getSerializable("projectList");
                //newProject = true;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_new_project, null);
        builder.setView(inflator);
        builder.setTitle(R.string.new_project_dialog_title);
        if(editable) builder.setTitle(R.string.edit_project_dialog_title);
        cNome = (EditText) inflator.findViewById(R.id.nome_project);
        importConfigSpinner = (Spinner) inflator.findViewById(R.id.spinner_projetos_config);
        importMaterialSpinner = (Spinner) inflator.findViewById(R.id.spinner_projetos_material);
        chkImportConfig = (AppCompatCheckBox) inflator.findViewById(R.id.project_import_config);
        chkImportMaterial = (AppCompatCheckBox) inflator.findViewById(R.id.project_import_material);
        importConfigList = (ListView) inflator.findViewById(R.id.import_config_list);
        importMaterialList = (ListView) inflator.findViewById(R.id.import_material_list);
        importConfigText = (TextView) inflator.findViewById(R.id.project_import_config_text);
        importMaterialText = (TextView) inflator.findViewById(R.id.project_import_material_text);
        importConfigList.setVisibility(View.GONE);
        importMaterialList.setVisibility(View.GONE);
        if(editable){
            cNome.setText(project.getcNome());
        }
        builder.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //Setando os checkbox on checked
        chkImportConfig.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(importConfigList.getAdapter() == null)
                    setConfigAdapter();
                    importConfigSpinner.setVisibility(View.VISIBLE);
                    importConfigList.setVisibility(View.VISIBLE);
                    setListViewHeightBasedOnChildren(importConfigList);
                }else{
                    if(configList != null)
                    configList.clear();
                    importConfigList.setAdapter(null);
                    importConfigSpinner.setVisibility(View.GONE);
                    importConfigList.setVisibility(View.GONE);
                }
            }
        });
        chkImportMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(importMaterialList.getAdapter() == null) setMaterialAdapter();
                    importMaterialSpinner.setVisibility(View.VISIBLE);
                    importMaterialList.setVisibility(View.VISIBLE);
                    setListViewHeightBasedOnChildren(importMaterialList);
                }else{
                    if(materiaisList != null)
                    materiaisList.clear();
                    importMaterialList.setAdapter(null);
                    importMaterialSpinner.setVisibility(View.GONE);
                    importMaterialList.setVisibility(View.GONE);
                }
            }
        });

        //Setando as listas com seus adapters
        fillSpinner();
        setConfigAdapter();
        setMaterialAdapter();
        return builder.create();
    }

    private void fillSpinner(){
        if(projectList == null){
            projectList = new ArrayList<>();
        }
        ArrayAdapter adapterConfig = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,projectList);
        ArrayAdapter adapterMaterial = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,projectList);
        importConfigSpinner.setAdapter(adapterConfig);
        importMaterialSpinner.setAdapter(adapterMaterial);
        importConfigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setConfigAdapter();
                setListViewHeightBasedOnChildren(importConfigList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        importMaterialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setMaterialAdapter();
                setListViewHeightBasedOnChildren(importMaterialList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setConfigAdapter(){
        Project oProject = (Project) importConfigSpinner.getSelectedItem();
        if(oProject != null) {
            if (oProject.getConfigurationsList() == null) {
                oProject.setConfigurationsList(new ArrayList<Configurations>());
            }
            configAdapter = new ImportConfigAdapter(getActivity(), oProject.getConfigurationsList(), new ImportConfigAdapter.onAdapterListener() {
                @Override
                public void onItemStateChanged(Boolean isChecked, Configurations configurationsList) {
                    if (isChecked) {
                        if (configList == null) {
                            configList = new ArrayList<>();
                        }
                        if (!configList.contains(configurationsList)) {
                            configList.add(configurationsList);
                        }
                    } else {
                        if (configList == null) {
                            configList = new ArrayList<>();
                        }
                        if (configList.contains(configurationsList)) {
                            configList.remove(configurationsList);
                        }
                    }
                }
            });
            importConfigList.setAdapter(configAdapter);
        }
    }
    private void setMaterialAdapter(){
        Project oProject = (Project) importMaterialSpinner.getSelectedItem();
        if(oProject != null) {
            if (oProject.getMateriaisList() == null) {
                oProject.setMateriaisList(new ArrayList<TiposMateriais>());
            }
            materialAdapter = new ImportMaterialAdapter(getActivity(), oProject.getMateriaisList(), new ImportMaterialAdapter.onAdapterListener() {
                @Override
                public void onItemStateChanged(Boolean isChecked, TiposMateriais oMaterial) {
                    if (isChecked) {
                        if (materiaisList == null) {
                            materiaisList = new ArrayList<>();
                        }
                        if (!materiaisList.contains(oMaterial)) {
                            materiaisList.add(oMaterial);
                        }
                    } else {
                        if (materiaisList == null) {
                            materiaisList = new ArrayList<>();
                        }
                        if (materiaisList.contains(oMaterial)) {
                            materiaisList.remove(oMaterial);
                        }
                    }
                }
            });
            importMaterialList.setAdapter(materialAdapter);
        }
    }
    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    if(!cNome.getText().toString().trim().equals("")){
                        wantToCloseDialog = true;
                    }
                    if(wantToCloseDialog) {
                        if(project == null){
                            project = new Project();
                        }
                        project.setcNome(cNome.getText().toString());
                        if(configList != null){
                            if(configList.size() == 0){
                                configList = null;
                            }
                        }
                        if(materiaisList != null){
                            if(materiaisList.size() == 0){
                                materiaisList = null;
                            }
                        }
                        nListener.onNewProjectPositiveClick(project,oPiece,configList,materiaisList);
                        d.dismiss();
                    }else{
                        Toast.makeText(getActivity(), R.string.campo_incorreto, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
