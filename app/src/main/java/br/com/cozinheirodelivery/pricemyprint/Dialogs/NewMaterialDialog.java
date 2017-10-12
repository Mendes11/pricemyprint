package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.MascaraMonetaria;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/08/2016.
 */
public class NewMaterialDialog extends DialogFragment {

    newMaterialListener nListener;
    Boolean editable = false;
    public interface newMaterialListener {
        public void onNewMaterialPositiveClick(TiposMateriais obj,String cAction);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            nListener = (newMaterialListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static NewMaterialDialog newInstance(Project oProject,String cAction) {
        NewMaterialDialog frag = new NewMaterialDialog();
        Bundle args = new Bundle();
        args.putParcelable("oProject",oProject);
        args.putString("cAction",cAction);
        frag.setArguments(args);
        return frag;
    }
    public static NewMaterialDialog newInstance(TiposMateriais material, Project oProject, String cAction) {
        NewMaterialDialog frag = new NewMaterialDialog();
        Bundle args = new Bundle();
        args.putParcelable("TiposMateriais",material);
        args.putBoolean("Editable",true);
        args.putParcelable("oProject",oProject);
        args.putString("cAction",cAction);
        frag.setArguments(args);
        return frag;
    }
    EditText cNome, fPreco, fDensidade, fDiametro;
    TiposMateriais material;
    Project oProject;
    String cAction;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_new_material, null);
        builder.setView(inflator);
        Bundle bundle = getArguments();
        if(bundle !=null){
            oProject = bundle.getParcelable("oProject");
            cAction = bundle.getString("cAction");
            if(bundle.containsKey("Editable")){
                editable = bundle.getBoolean("Editable");
                material = bundle.getParcelable("TiposMateriais");
            }
        }
        if(savedInstanceState != null){
            oProject = savedInstanceState.getParcelable("oProject");
            cAction = savedInstanceState.getString("cAction");
            if(savedInstanceState.containsKey("Editable")){
                editable = savedInstanceState.getBoolean("Editable");
                material = savedInstanceState.getParcelable("TiposMateriais");
            }
        }
        builder.setTitle(R.string.new_material_dialog_title);
        cNome = (EditText) inflator.findViewById(R.id.nome_material);
        fPreco = (EditText) inflator.findViewById(R.id.preco);
        fDensidade = (EditText) inflator.findViewById(R.id.densidade);
        fDiametro = (EditText) inflator.findViewById(R.id.diametro);
        //fPreco.addTextChangedListener(new MascaraMonetaria(fPreco));
        //fPreco.setText(NumberFormat.getCurrencyInstance().format(0.0));
        fPreco.setText(0.00+"");
        fDiametro.setText(1.75+"");
        if(material != null){
            cNome.setText(material.getcNome());
            //fPreco.setText("R$"+String.format("%.2f",material.getfPreco()).replace(".",","));
            //fPreco.setText(NumberFormat.getCurrencyInstance().format(material.getfPreco()));
            fPreco.setText(material.getfPreco()+"");
            fDiametro.setText(material.getfDiametro()+"");
            fDensidade.setText(material.getfDensidade()+"");

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
        return builder.create();
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
                    if (!(cNome.getText().toString().trim().equals("") || fPreco.toString().trim().equals("") || fDensidade.toString().trim().equals("")) || fDiametro.getText().toString().trim().equals("")) {
                        wantToCloseDialog = true;
                    }
                    if (wantToCloseDialog) {
                        if(material == null) {
                            material = new TiposMateriais();
                        }
                        material.setcNome(cNome.getText().toString());
                        try {
                            //Number nPreco = NumberFormat.getCurrencyInstance().parse(fPreco.getText().toString().replace(".", ""));
                            //Double dPreco = nPreco.doubleValue();
                            Double dPreco = Double.parseDouble(fPreco.getText().toString().replace(",","."));
                            material.setfPreco(dPreco);
                            material.setfDensidade(Double.parseDouble(fDensidade.getText().toString().replace(",",".")));
                            material.setfDiametro(Double.parseDouble(fDiametro.getText().toString().replace(",",".")));
                            if(oProject != null) {
                                material.setiIDProjeto(oProject.getiIDProjeto());
                            }
                            try{
                                DB db = new DB(getActivity());
                                material = db.insertTipoMaterial(material);
                                if(material.getiIDTipoMaterial() != 0) {
                                    Toast.makeText(getActivity(), R.string.item_save_success, Toast.LENGTH_SHORT).show();
                                    nListener.onNewMaterialPositiveClick(material,cAction);
                                }
                            }catch (Exception e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            d.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(),R.string.campo_incorreto, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(),R.string.campo_incorreto, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
