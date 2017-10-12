package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter.ImportConfigAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter.ImportMaterialAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.ImportAdapter.ImportPieceAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 02/01/2017.
 */

public class ImportDialog extends DialogFragment {
    List<Project> projectList;
    String cAction;
    ListView lista;
    Spinner projects;
    ImportConfigAdapter configAdapter;
    ImportPieceAdapter pieceAdapter;
    ImportMaterialAdapter materialAdapter;
    Project oProject;
    Project oSelectedProject;
    List<Piece> pieceList;
    List<Configurations> confList;
    List<TiposMateriais> materiaisList;
    onImportListener mListener;
    public static String IMPORT_PIECE = "ImportPiece";
    public static String IMPORT_CONFIG = "ImportConfig";
    public static String IMPORT_MATERIAL = "ImportMaterial";

    public static ImportDialog newInstance(Project oSelectedProject,List<Project> projectList, String cAction){
        ImportDialog frag = new ImportDialog();
        Bundle args = new Bundle();
        args.putParcelable("oSelectedProject",oSelectedProject);
        args.putSerializable("projectList",(Serializable)projectList);
        args.putString("cAction",cAction);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (onImportListener) context;
        }catch(Exception e){
            throw new ClassCastException(getClass()+"Implementar o Listener");
        }
    }

    public interface onImportListener{
        void onConfirmImportPiece(Project oProject,List<Piece> mList);
        void onConfirmImportConfig(Project oProject,List<Configurations> mList);
        void onConfirmImportMaterial(Project oProject,List<TiposMateriais> mList);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            oSelectedProject = getArguments().getParcelable("oSelectedProject");
            projectList = (List<Project>) getArguments().getSerializable("projectList");
            cAction = getArguments().getString("cAction");
        }
        if(savedInstanceState != null){
            oSelectedProject = savedInstanceState.getParcelable("oSelectedProject");
            projectList = (List<Project>) savedInstanceState.getSerializable("projectList");
            cAction = savedInstanceState.getString("cAction");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_import, null);
        dialog.setView(inflator);
        if(cAction.equals(IMPORT_MATERIAL)){
            dialog.setTitle(R.string.action_import_material);
        }else if(cAction.equals(IMPORT_CONFIG)){
            dialog.setTitle(R.string.action_import_config);
        }else if(cAction.equals(IMPORT_PIECE)){
            dialog.setTitle(R.string.action_import_piece);
        }
        projects = (Spinner) inflator.findViewById(R.id.spinner_projetos);
        lista = (ListView) inflator.findViewById(R.id.list);
        fillSpinner();
        oProject = projectList.get(0);
        setAdapter();
        projects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                oProject = projectList.get(position);
                setAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dialog.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 if(cAction.equals(IMPORT_PIECE)){
                    mListener.onConfirmImportPiece(oSelectedProject,pieceList);
                 }else if (cAction.equals(IMPORT_CONFIG)){
                    mListener.onConfirmImportConfig(oSelectedProject,confList);
                 }else if (cAction.equals(IMPORT_MATERIAL)){
                    mListener.onConfirmImportMaterial(oSelectedProject,materiaisList);
                 }
            }
        }).setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return dialog.show();
    }
    private void fillSpinner(){
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,projectList);
        projects.setAdapter(adapter);
    }
    private void setAdapter(){
        if(cAction.equals(IMPORT_PIECE)){
            if(oProject.getChildList() == null){
                oProject.setChildList(new ArrayList<Piece>());
            }
            pieceAdapter = new ImportPieceAdapter(getActivity(),oProject.getChildList(), new ImportPieceAdapter.onAdapterListener() {
                @Override
                public void onItemStateChanged(Boolean isChecked, Piece oPiece) {
                    if(isChecked){
                        if(pieceList == null){
                            pieceList = new ArrayList<>();
                        }
                        if(!pieceList.contains(oPiece)){
                            pieceList.add(oPiece);
                        }
                    }else{
                        if(pieceList == null){
                            pieceList = new ArrayList<>();
                        }
                        if(pieceList.contains(oPiece)){
                            pieceList.remove(oPiece);
                        }
                    }
                }
            });
            lista.setAdapter(pieceAdapter);
        }else if(cAction.equals(IMPORT_CONFIG)){
            if(oProject.getConfigurationsList() == null){
                oProject.setConfigurationsList(new ArrayList<Configurations>());
            }
            configAdapter = new ImportConfigAdapter(getActivity(),oProject.getConfigurationsList(), new ImportConfigAdapter.onAdapterListener() {
                @Override
                public void onItemStateChanged(Boolean isChecked, Configurations configurationsList) {
                    if(isChecked){
                        if(confList == null){
                            confList = new ArrayList<>();
                        }
                        if(!confList.contains(configurationsList)){
                            confList.add(configurationsList);
                        }
                    }else{
                        if(confList == null){
                            confList = new ArrayList<>();
                        }
                        if(confList.contains(configurationsList)){
                            confList.remove(configurationsList);
                        }
                    }
                }
            });
            lista.setAdapter(configAdapter);
        }else if(cAction.equals(IMPORT_MATERIAL)){
            if(oProject.getMateriaisList() == null){
                oProject.setMateriaisList(new ArrayList<TiposMateriais>());
            }
            materialAdapter = new ImportMaterialAdapter(getActivity(),oProject.getMateriaisList(), new ImportMaterialAdapter.onAdapterListener() {
                @Override
                public void onItemStateChanged(Boolean isChecked, TiposMateriais oMaterial) {
                    if(isChecked){
                        if(materiaisList == null){
                            materiaisList = new ArrayList<>();
                        }
                        if(!materiaisList.contains(oMaterial)){
                            materiaisList.add(oMaterial);
                        }
                    }else{
                        if(materiaisList == null){
                            materiaisList = new ArrayList<>();
                        }
                        if(materiaisList.contains(oMaterial)){
                            materiaisList.remove(oMaterial);
                        }
                    }
                }
            });
            lista.setAdapter(materialAdapter);
        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("projectList", (Serializable) projectList);
        outState.putString("cAction",cAction);
        outState.putParcelable("oSelectedProject",oSelectedProject);
    }
}
