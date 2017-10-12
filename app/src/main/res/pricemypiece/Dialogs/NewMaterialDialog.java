package pricemypiece.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.NumberFormat;

import br.com.cozinheiro.pricemypiece.Objects.MascaraMonetaria;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 24/08/2016.
 */
public class NewMaterialDialog extends DialogFragment {

    newMaterialListener nListener;
    Boolean editable = false;
    public interface newMaterialListener {
        public void onNewMaterialPositiveClick(TiposMateriais obj);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            nListener = (newMaterialListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static NewMaterialDialog newInstance() {
        NewMaterialDialog frag = new NewMaterialDialog();
        return frag;
    }
    public static NewMaterialDialog newInstance(TiposMateriais material) {
        NewMaterialDialog frag = new NewMaterialDialog();
        Bundle args = new Bundle();
        args.putParcelable("TiposMateriais",material);
        args.putBoolean("Editable",true);
        frag.setArguments(args);
        return frag;
    }
    EditText cNome, fPreco, fDensidade, fDiametro;
    TiposMateriais material;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_new_material, null);
        builder.setView(inflator);
        Bundle bundle = getArguments();
        if(bundle !=null){
            if(bundle.containsKey("Editable")){
                editable = bundle.getBoolean("Editable");
                material = bundle.getParcelable("TiposMateriais");
            }
        }
        builder.setTitle(R.string.new_material_dialog_title);
        cNome = (EditText) inflator.findViewById(R.id.nome_material);
        fPreco = (EditText) inflator.findViewById(R.id.preco);
        fDensidade = (EditText) inflator.findViewById(R.id.densidade);
        fDiametro = (EditText) inflator.findViewById(R.id.diametro);
        fPreco.addTextChangedListener(new MascaraMonetaria(fPreco));
        fPreco.setText("R$0,00");
        if(material != null){
            cNome.setText(material.getcNome());
            fPreco.setText("R$"+String.format("%.2f",material.getfPreco()).replace(".",","));
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
                            String preco = fPreco.getText().toString().substring(2).replace(",",".");
                            material.setfPreco(Double.parseDouble(preco));
                            material.setfDensidade(Double.parseDouble(fDensidade.getText().toString().replace(",",".")));
                            material.setfDiametro(Double.parseDouble(fDiametro.getText().toString().replace(",",".")));
                            nListener.onNewMaterialPositiveClick(material);
                            d.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Atenção, preencha os campos corretamente.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Atenção, Preencha os campos corretamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
