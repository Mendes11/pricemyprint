package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Activities.MainActivity;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/08/2016.
 */
public class NewPieceDialog extends DialogFragment {

    newPieceListener nListener;
    Project oProjeto = null;
    Piece piece;
    Boolean Editable = false;
    Boolean isQuickPrice = false;
    List<TiposMateriais> list;
    List<Configurations> confList;
    public interface newPieceListener {
        void onNewPiecePositiveClick(Project oProject, Piece obj); //Nova Peça
        void onEditPiecePositiveClick(Piece obj); //Editar Peça já existente
        void onQuickPricePositiveClick(Piece obj); // QuickPrice
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            nListener = (newPieceListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static NewPieceDialog newInstance(Project oProjeto) { //Nova Peça
        NewPieceDialog frag = new NewPieceDialog();
        Bundle args = new Bundle();
        args.putParcelable("oProjeto",oProjeto);
        frag.setArguments(args);
        return frag;
    }
    public static NewPieceDialog newInstance(Project oProjeto,Piece piece) { //Editar Peça
        NewPieceDialog frag = new NewPieceDialog();
        Bundle args = new Bundle();
        args.putBoolean("Editable",true);
        args.putParcelable("Piece",piece);
        args.putParcelable("oProjeto",oProjeto); // Para pegar a lista de configs/material certa
        frag.setArguments(args);
        return frag;
    }

    // TODO: 29/12/2016 Bugou o EDIT, ver se é algo a ver com a adição do projeto a todos os itens.... Ele adicionou à lista, ao invés de editar (mas não no DB) 
    public static NewPieceDialog newInstance(Project oProjeto,Boolean isQuickPrice){ //QuickPrice
        NewPieceDialog frag = new NewPieceDialog();
        Bundle args = new Bundle();
        args.putBoolean("isQuickPrice", isQuickPrice);
        args.putParcelable("oProjeto",oProjeto); //Projeto QuickPrice, para pegar a lista certa de material/config
        frag.setArguments(args);
        return frag;
    }
    EditText cNome, iComprimento,tH,tM,iQuant,fPeso;
    Spinner iIDTipoMaterial,iIDConfiguracao;
    AppCompatButton btnNovo,btnNovoConfiguracao;
    AppCompatImageButton btnAdd,btnRemove;
    RadioGroup cmbGroup;
    AppCompatRadioButton cmbComprimento,cmbPeso;

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
            if(bundle.containsKey("oProjeto"))oProjeto = bundle.getParcelable("oProjeto");

            if (bundle.containsKey("Editable")) {
                Editable = bundle.getBoolean("Editable");
                piece = bundle.getParcelable("Piece");
                builder.setTitle(R.string.edit_piece_dialog_title);
            }else if(bundle.containsKey("isQuickPrice")){
                isQuickPrice = bundle.getBoolean("isQuickPrice");
            }
        }
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("oProjeto"))oProjeto = savedInstanceState.getParcelable("oProjeto");
            if(savedInstanceState.containsKey("Editable")) {
                if (savedInstanceState.getBoolean("Editable")) {
                    Editable = true;
                    piece = savedInstanceState.getParcelable("Piece");
                    builder.setTitle(R.string.edit_piece_dialog_title);
                }else if(savedInstanceState.containsKey("isQuickPrice")){
                    isQuickPrice = savedInstanceState.getBoolean("isQuickPrice");
                }
            }
        }
        if(isQuickPrice){
            builder.setTitle(R.string.quickprice_title);
        }
        cNome = (EditText) inflator.findViewById(R.id.nome_peca);
        cmbGroup = (RadioGroup) inflator.findViewById(R.id.cmbGroup);
        cmbComprimento = (AppCompatRadioButton) inflator.findViewById(R.id.cmbComprimento);
        cmbPeso = (AppCompatRadioButton) inflator.findViewById(R.id.cmbPeso);
        iComprimento = (EditText) inflator.findViewById(R.id.comprimento_peca);
        fPeso = (EditText) inflator.findViewById(R.id.peso_peca);
        iIDTipoMaterial = (Spinner) inflator.findViewById(R.id.spinner_filamento);
        iIDConfiguracao = (Spinner) inflator.findViewById(R.id.spinner_configuracao);
        tH = (EditText) inflator.findViewById(R.id.tempo_h);
        tM = (EditText) inflator.findViewById(R.id.tempo_m);
        btnNovo = (AppCompatButton) inflator.findViewById(R.id.btnNovo);
        btnNovoConfiguracao = (AppCompatButton) inflator.findViewById(R.id.btnNovoConfiguracao);
        iQuant = (EditText) inflator.findViewById(R.id.quantidade);
        btnAdd = (AppCompatImageButton) inflator.findViewById(R.id.btnAdd);
        btnRemove = (AppCompatImageButton) inflator.findViewById(R.id.btnRemove);
        if(isQuickPrice){
            cNome.setVisibility(View.GONE);
        }
        //Preenche os MateriaisDisponíveis
        fillSpinner(null);
        //Preenche as configurações disponíveis.
        fillConfig(null);

        cmbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cmbComprimento:
                        fPeso.setVisibility(View.GONE);
                        iComprimento.setVisibility(View.VISIBLE);
                        break;
                    case R.id.cmbPeso:
                        fPeso.setVisibility(View.VISIBLE);
                        iComprimento.setVisibility(View.GONE);
                        break;
                    default:
                }
            }
        });

        if(piece != null){
            cNome.setText(piece.getcNome());
            fPeso.setText(String.format("%.2f",piece.getfGramas()).replace(",","."));
            iComprimento.setText(piece.getiFilamento()+"");
            if(piece.getbSelectedInfo() == 0) {
                iComprimento.setVisibility(View.VISIBLE);
                fPeso.setVisibility(View.GONE);
                cmbComprimento.performClick();
            }else{
                iComprimento.setVisibility(View.GONE);
                fPeso.setVisibility(View.VISIBLE);
                cmbPeso.performClick();
            }
            int h,m;
            int tMin = (int) Math.round((piece.getfTempo())*60);
            m = (int) tMin%60;
            h = (int) (tMin/60);
            tH.setText(h + "");
            tM.setText("" + m);
            iQuant.setText(piece.getiQuant()+"");
            iIDTipoMaterial.setSelection(oProjeto.getMateriaisList().indexOf(piece.getiIDTipoMaterial()));
            iIDConfiguracao.setSelection(oProjeto.getConfigurationsList().indexOf(piece.getoConfigurations()));

        }else{
            cmbComprimento.performClick();
        }
        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chama o Dialog de novoMaterial
                DialogFragment df = null;
                if(isQuickPrice) {
                    df = NewMaterialDialog.newInstance(null, oProjeto, MainActivity.ACTION_NEWQUICK);
                }else{
                    df = NewMaterialDialog.newInstance(null, oProjeto, MainActivity.ACTION_NEWPIECE);
                }
                df.show(getActivity().getSupportFragmentManager(), "NewMaterialDialog");
            }
        });
        btnNovoConfiguracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chama o fragment como um dialog neste caso.
                DialogFragment df = null;
                if(isQuickPrice) {
                    df = new ConfigurationsItemDialog().newInstance(null, oProjeto, MainActivity.ACTION_NEWQUICK);
                }else{
                    df = new ConfigurationsItemDialog().newInstance(null, oProjeto, MainActivity.ACTION_NEWPIECE);
                }
                df.show(getActivity().getSupportFragmentManager(),"ConfigurationsItemFragment");
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtd = Integer.parseInt(iQuant.getText().toString());
                qtd++;
                iQuant.setText(qtd+"");
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qtd = Integer.parseInt(iQuant.getText().toString());
                if(qtd > 1){
                    qtd --;
                    iQuant.setText(qtd+"");
                }
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
        // TODO: 16/12/2016 Adicionar um userPreferences, para que ao editar ele já coloque se o usuário prefere dar em gramas ou mm.

        return builder.create();
    }

    public void fillConfig(Configurations oConf){
        if(oConf != null){
            if(confList == null){
                confList = new ArrayList<>();
            }
            confList.add(oConf);
        }else {
            confList = oProjeto.getConfigurationsList();
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
        if(oTipoMat != null){
            //Este caso apenas ocorre quando há um novo cadastro feito pelo dialog, neste caso a lista já é existente.
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(oTipoMat);
        }else {
            list = oProjeto.getMateriaisList();
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
                    if(!isQuickPrice) {
                        if (!(cNome.getText().toString().trim().equals("") || (iComprimento.getText().toString().trim().equals("") && fPeso.getText().toString().trim().equals("")) || tM.getText().toString().trim().equals("") || tH.getText().toString().trim().equals("")) || iQuant.getText().toString().trim().equals("") || iQuant.getText().toString().trim().equals("0")) {
                            TiposMateriais material = (TiposMateriais) iIDTipoMaterial.getSelectedItem();
                            Configurations oConfiguration = (Configurations) iIDConfiguracao.getSelectedItem();
                            try {
                                if (material != null && oConfiguration != null) {
                                    wantToCloseDialog = true;
                                }
                            } catch (Exception e) {

                            }
                        }
                    }else{
                        if (!((iComprimento.getText().toString().trim().equals("") && fPeso.getText().toString().trim().equals("")) || tM.getText().toString().trim().equals("") || tH.getText().toString().trim().equals("") || iQuant.getText().toString().trim().equals("") || iQuant.getText().toString().trim().equals("0"))) {
                            TiposMateriais material = (TiposMateriais) iIDTipoMaterial.getSelectedItem();
                            Configurations oConfiguration = (Configurations) iIDConfiguracao.getSelectedItem();
                            try {
                                if (material != null && oConfiguration != null) {
                                    wantToCloseDialog = true;
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                    if (wantToCloseDialog) {
                        if(piece == null){
                            piece = new Piece();
                        }
                        try {
                            TiposMateriais material = (TiposMateriais) iIDTipoMaterial.getSelectedItem();
                            Configurations oConfiguration = (Configurations) iIDConfiguracao.getSelectedItem();
                            piece.setiIDTipoMaterial(material);
                            piece.setoConfigurations(oConfiguration);
                            piece.setiQuant(Integer.parseInt(iQuant.getText().toString()));
                            switch (cmbGroup.getCheckedRadioButtonId()){
                                case R.id.cmbComprimento:
                                    piece.setiFilamento(Integer.parseInt(iComprimento.getText().toString()));
                                    piece.setfGramas(Calculator.getWeight(material,piece.getiFilamento()));
                                    piece.setbSelectedInfo(0);
                                    break;
                                case R.id.cmbPeso:
                                    piece.setfGramas(Double.parseDouble(fPeso.getText().toString().replace(",",".")));
                                    piece.setiFilamento(Calculator.getLenght(material,piece.getfGramas()));
                                    piece.setbSelectedInfo(1);
                                    break;
                            }

                            int h = Integer.parseInt(tH.getText().toString());
                            int m = Integer.parseInt(tM.getText().toString());


                            Double t = h + m/60.0;
                            piece.setfTempo(t);
                            if(!isQuickPrice) {
                                piece.setcNome(cNome.getText().toString());
                            }
                            Calculator calc = new Calculator(getActivity().getApplicationContext(),piece);
                            piece.setfPreco(calc.getPrice(getActivity(),piece));
                            if(Editable) {
                                nListener.onEditPiecePositiveClick(piece);
                            }else if(isQuickPrice){
                                nListener.onQuickPricePositiveClick(piece);
                            }else{
                                piece.setiIDProject(oProjeto.getiIDProjeto());
                                nListener.onNewPiecePositiveClick(oProjeto,piece);
                            }
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
