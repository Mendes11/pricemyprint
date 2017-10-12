package pricemypiece.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Fragments.ConfigurationsItemFragment;
import br.com.cozinheiro.pricemypiece.Objects.Calculator;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 24/08/2016.
 */
public class NewPieceDialog extends DialogFragment {

    newPieceListener nListener;
    Project obj;
    Piece piece;
    Boolean Editable = false;
    List<TiposMateriais> list;
    List<Configurations> confList;
    public interface newPieceListener {
        public void onNewPiecePositiveClick(Piece obj);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            nListener = (newPieceListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void setProject(Project obj) {
        this.obj = obj;
    }

    public static NewPieceDialog newInstance(Project obj) {
        NewPieceDialog frag = new NewPieceDialog();
        frag.setProject(obj);
        return frag;
    }
    public static NewPieceDialog newInstance(Project obj,Piece piece) {
        NewPieceDialog frag = new NewPieceDialog();
        frag.setProject(obj);
        Bundle args = new Bundle();
        args.putBoolean("Editable",true);
        args.putParcelable("Piece",piece);
        frag.setArguments(args);
        return frag;
    }
    EditText cNome, iComprimento,tH,tM;
    Spinner iIDTipoMaterial,iIDConfiguracao;
    AppCompatButton btnNovo,btnNovoConfiguracao;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_new_piece, null);
        builder.setView(inflator);
        builder.setTitle(R.string.new_piece_dialog_title);
        Bundle bundle = getArguments();
        if(bundle != null) {
            if (bundle.containsKey("Editable")) {
                Editable = bundle.getBoolean("Editable");
                piece = bundle.getParcelable("Piece");
                builder.setTitle(R.string.edit_piece_dialog_title);
            }
        }
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("Editable")) {
                if (savedInstanceState.getBoolean("Editable")) {
                    Editable = true;
                    piece = savedInstanceState.getParcelable("Piece");
                    builder.setTitle(R.string.edit_piece_dialog_title);
                }
            }
        }
        cNome = (EditText) inflator.findViewById(R.id.nome_peca);
        iComprimento = (EditText) inflator.findViewById(R.id.comprimento_peca);
        iIDTipoMaterial = (Spinner) inflator.findViewById(R.id.spinner_filamento);
        iIDConfiguracao = (Spinner) inflator.findViewById(R.id.spinner_configuracao);
        tH = (EditText) inflator.findViewById(R.id.tempo_h);
        tM = (EditText) inflator.findViewById(R.id.tempo_m);
        btnNovo = (AppCompatButton) inflator.findViewById(R.id.btnNovo);
        btnNovoConfiguracao = (AppCompatButton) inflator.findViewById(R.id.btnNovoConfiguracao);

        //Preenche os MateriaisDisponíveis
        fillSpinner(null);
        //Preenche as configurações disponíveis.
        fillConfig(null);

        if(piece != null){
            cNome.setText(piece.getcNome());
            iComprimento.setText(piece.getiFilamento()+"");
            int h,m;
            int tMin = (int) Math.round(piece.getfTempo()*60);
            m = (int) tMin%60;
            h = (int) (tMin/60);
            tH.setText(h + "");
            tM.setText("" + m);
            iIDTipoMaterial.setSelection(findSelectedSpinner(piece.getiIDTipoMaterial()));

        }
        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chama o Dialog de novoMaterial
                DialogFragment df = NewMaterialDialog.newInstance();
                df.show(getActivity().getSupportFragmentManager(), "NewMaterialDialog");
            }
        });
        btnNovoConfiguracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o fragment como um dialog neste caso.
                DialogFragment df = new ConfigurationsItemFragment().newInstance(null);
                df.show(getActivity().getSupportFragmentManager(),"ConfigurationsItemFragment");
            }
        });
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

    public void fillConfig(Configurations oConf){
        DB db = new DB(getActivity().getApplicationContext());
        if(oConf != null){
            if(confList == null){
                confList = new ArrayList<>();
            }
            confList.add(oConf);
        }else {
            confList = db.getConfigurations(null, null, null, null);
        }
        if(confList!=null){
            ArrayAdapter<Configurations> dataAdapter = new ArrayAdapter<Configurations>(getActivity(),android.R.layout.simple_spinner_item,confList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            iIDConfiguracao.setAdapter(dataAdapter);
            if(oConf != null) {
                int index = dataAdapter.getPosition(oConf);
                iIDConfiguracao.setSelection(dataAdapter.getPosition(oConf));
            }
        }
    }
    public void fillSpinner(TiposMateriais oTipoMat) {
        DB db = new DB(getActivity().getApplicationContext());
        if(oTipoMat != null){
            //Este caso apenas ocorre quando há um novo cadastro feito pelo dialog, neste caso a lista já é existente.
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(oTipoMat);
        }else {
            list = db.getTiposMateriais(null, null, null, null);
        }
        if (list != null) {
            ArrayAdapter<TiposMateriais> dataAdapter = new ArrayAdapter<TiposMateriais>(getActivity(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            iIDTipoMaterial.setAdapter(dataAdapter);
            if(oTipoMat!=null) {
                iIDTipoMaterial.setSelection(dataAdapter.getPosition(oTipoMat));
            }
        }
    }
    private int findSelectedSpinner(TiposMateriais toFind){
        int i = 0;
        if(list != null) {
            for (TiposMateriais obj : list) {
                if (toFind.getiIDTipoMaterial() == obj.getiIDTipoMaterial()) {
                    return i;
                }
                i++;
            }
        }
        return -1;
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
                    if (!cNome.getText().toString().trim().equals("") || iComprimento.getText().toString().trim().equals("") || tM.getText().toString().trim().equals("")||tH.getText().toString().trim().equals("")) {
                        TiposMateriais material = (TiposMateriais) iIDTipoMaterial.getSelectedItem();
                        Configurations oConfiguration = (Configurations) iIDConfiguracao.getSelectedItem();
                        try {
                            if (material != null && oConfiguration != null) {
                                wantToCloseDialog = true;
                            }
                        }catch(Exception e){

                        }
                    }
                    if (wantToCloseDialog) {
                        if(piece == null){
                            piece = new Piece();
                        }
                        piece.setcNome(cNome.getText().toString());
                        try {
                            TiposMateriais material = (TiposMateriais) iIDTipoMaterial.getSelectedItem();
                            Configurations oConfiguration = (Configurations) iIDConfiguracao.getSelectedItem();
                            piece.setiIDTipoMaterial(material);
                            piece.setoConfigurations(oConfiguration);
                            piece.setiFilamento(Integer.parseInt(iComprimento.getText().toString()));
                            int h = Integer.parseInt(tH.getText().toString());
                            int m = Integer.parseInt(tM.getText().toString());
                            Double t = h + m/60.0;
                            piece.setfTempo(t);
                            Calculator calc = new Calculator(getActivity().getApplicationContext(),piece);
                            piece.setfPreco(calc.getPrice(getActivity(),piece));
                            piece.setiIDProject(obj.getiIDProjeto());
                            nListener.onNewPiecePositiveClick(piece);
                        }catch(Exception e){
                            Toast.makeText(getActivity(),R.string.new_piece_error_1,Toast.LENGTH_LONG).show();
                        }
                        d.dismiss();
                    } else {
                        Toast.makeText(getActivity(),R.string.new_piece_error_1, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
