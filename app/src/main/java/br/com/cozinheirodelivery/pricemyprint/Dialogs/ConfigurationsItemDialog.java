package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;

public class ConfigurationsItemDialog extends DialogFragment {

    private static final String ARG_PARAM1 = "oConfiguration";
    private static final String ARG_PARAM2 = "oProject";
    private static final String ACTION = "ACTION";

    private OnConfigurationsItemListener mListener;

    public ConfigurationsItemDialog() {
        // Required empty public constructor
    }


    public static ConfigurationsItemDialog newInstance(Configurations param1, Project param2, String action) {
        ConfigurationsItemDialog fragment = new ConfigurationsItemDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        args.putParcelable(ARG_PARAM2, param2);
        args.putString(ACTION,action);
        fragment.setArguments(args);
        return fragment;
    }


    TextInputEditText fPreco,iConsumo,fTarifa,fHoraTrabalho,fMediaUso,iVidaUtil,cNomeConfiguracao;
    Spinner fReparo,fFalha,iLucro;
    ImageButton btnHelpPreco,btnHelpConsumo,btnHelpTarifa,btnHelpHoraTrabalho,btnHelpMediaUso,btnHelpVidaUtil,btnHelpReparo,btnHelpFalha,btnHelpLucro;
    Configurations conf;
    Project oProject;
    String cAction;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            conf = getArguments().getParcelable(ARG_PARAM1);
            oProject = getArguments().getParcelable(ARG_PARAM2);
            cAction = getArguments().getString(ACTION);
            if(conf == null){
                conf = new Configurations();
                conf.setDefaultValues();
            }
        }
        if(savedInstanceState != null){
            conf = savedInstanceState.getParcelable(ARG_PARAM1);
            oProject = savedInstanceState.getParcelable(ARG_PARAM2);
            cAction = savedInstanceState.getString(ACTION);
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(R.string.nova_configuracao);
        //btnSalvar.setVisibility(View.GONE);
        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;*/
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_configurations_item, null);
        dialog.setView(view);
        // Inflate the layout for this fragment
        setHasOptionsMenu(false);
        fPreco = (TextInputEditText) view.findViewById(R.id.preco_impressora);
        iConsumo = (TextInputEditText) view.findViewById(R.id.consumo_impressora);
        fTarifa = (TextInputEditText) view.findViewById(R.id.tarifa);
        fHoraTrabalho = (TextInputEditText) view.findViewById(R.id.hora_trabalho);
        fMediaUso = (TextInputEditText) view.findViewById(R.id.taxa_media_uso);
        iVidaUtil = (TextInputEditText) view.findViewById(R.id.tempo_vida);
        fReparo = (Spinner) view.findViewById(R.id.reparo);
        fFalha = (Spinner) view.findViewById(R.id.taxa_falhas);
        iLucro = (Spinner) view.findViewById(R.id.lucro);
        cNomeConfiguracao = (TextInputEditText) view.findViewById(R.id.nome_impressora);
        //fPreco.addTextChangedListener(new MascaraMonetaria(fPreco));
        fPreco.setText("0.00");
        //fPreco.setText(NumberFormat.getCurrencyInstance().format(0.0));
        //fTarifa.addTextChangedListener(new MascaraMonetaria(fTarifa));
        fTarifa.setText("0.00");
        //fTarifa.setText(NumberFormat.getCurrencyInstance().format(0.0));
        //fHoraTrabalho.addTextChangedListener(new MascaraMonetaria(fHoraTrabalho));
        fHoraTrabalho.setText("0.00");
        //fHoraTrabalho.setText(NumberFormat.getCurrencyInstance().format(0.0));
        btnHelpPreco = (ImageButton) view.findViewById(R.id.btnHelp_preco);
        btnHelpConsumo = (ImageButton) view.findViewById(R.id.btnHelp_consumo);
        btnHelpTarifa = (ImageButton) view.findViewById(R.id.btnHelp_tarifa);
        btnHelpHoraTrabalho = (ImageButton) view.findViewById(R.id.btnHelp_hora_trabalho);
        btnHelpMediaUso = (ImageButton) view.findViewById(R.id.btnHelp_media_uso);
        btnHelpVidaUtil = (ImageButton) view.findViewById(R.id.btnHelp_anos_uso);
        btnHelpReparo = (ImageButton) view.findViewById(R.id.btnHelp_reparos);
        btnHelpFalha = (ImageButton) view.findViewById(R.id.btnHelp_falhas);
        btnHelpLucro = (ImageButton) view.findViewById(R.id.btnHelp_lucro);
        // Preenchendo os spinners de %

        List<String> porcents = new ArrayList<>();
        for(int i = 0; i<=100 ;i++){
            porcents.add(i+"%");
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, porcents);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, porcents);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, porcents.subList(0,porcents.size()-1));
        fReparo.setAdapter(adapter1);
        fFalha.setAdapter(adapter2);
        iLucro.setAdapter(adapter3);
        fPreco.setText(conf.getfPrecoImpressora()+"");
        //fPreco.setText(NumberFormat.getCurrencyInstance().format(conf.getfPrecoImpressora()));
        iConsumo.setText("" + conf.getiConsumo());
        fTarifa.setText(conf.getfTarifa()+"");
        //fTarifa.setText(NumberFormat.getCurrencyInstance().format(conf.getfTarifa()));
        fReparo.setSelection((int) (conf.getfReparo() * 100));
        iLucro.setSelection(conf.getiLucro());
        fHoraTrabalho.setText(conf.getfHoraTrabalho()+"");
        //fHoraTrabalho.setText(NumberFormat.getCurrencyInstance().format(conf.getfHoraTrabalho()));
        fFalha.setSelection((int) (conf.getfTaxaFalhas() * 100));
        fMediaUso.setText("" + conf.getfMediaUso());
        iVidaUtil.setText(""+conf.getfTempoVida());
        cNomeConfiguracao.setText(conf.getcNomeConfig());
        btnHelpFalha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null, getResources().getString(R.string.help_falhas));
            }
        });
        btnHelpReparo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_reparos));
            }
        });
        btnHelpVidaUtil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_vida_util));
            }
        });
        btnHelpMediaUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_media_uso));
            }
        });
        btnHelpConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_consumo));
            }
        });
        btnHelpHoraTrabalho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_hora_trabalho));
            }
        });
        btnHelpPreco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_preco));
            }
        });
        btnHelpTarifa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getResources().getString(R.string.help_tarifa));
            }
        });
        btnHelpLucro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageDialog(null,getString(R.string.help_lucro));
            }
        });
        dialog.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //dialog.getWindow().setAttributes(lp);
        return dialog.show();
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
                    if (!cNomeConfiguracao.getText().toString().trim().equals("")) {
                        wantToCloseDialog = true;
                    }
                    if(wantToCloseDialog){
                        try{

                            conf.setfPrecoImpressora(Double.parseDouble(fPreco.getText().toString().replace(",",".")));
                            //conf.setfPrecoImpressora(dPrecoImpressora);
                            conf.setiConsumo((int) Math.round(Double.parseDouble(iConsumo.getText().toString()) - 0.5));
                            conf.setfTarifa(Double.parseDouble(fTarifa.getText().toString().replace(",",".")));
                            //conf.setfTarifa(dTarifa);
                            conf.setfReparo(fReparo.getSelectedItemPosition() / 100.0);
                            conf.setfHoraTrabalho(Double.parseDouble(fHoraTrabalho.getText().toString().replace(",",".")));
                            conf.setiLucro(iLucro.getSelectedItemPosition());
                            //conf.setfHoraTrabalho(dHoraTrabalho);
                            conf.setfTaxaFalhas(fFalha.getSelectedItemPosition() / 100.0);
                            conf.setfMediaUso(Double.parseDouble(fMediaUso.getText().toString().replace(",",".")));
                            conf.setfTempoVida(Double.parseDouble(iVidaUtil.getText().toString().replace(",",".")));
                            conf.setcNomeConfig(cNomeConfiguracao.getText().toString());
                            if(oProject != null){
                                // É para criar, logo adiciona o iIDProjeto
                                conf.setiIDProjeto(oProject.getiIDProjeto());
                            }
                            DB db = new DB(getActivity().getApplication());
                            db.insertConfigurations(conf);
                            Toast.makeText(getActivity().getApplicationContext(), R.string.configuracoes_save_success, Toast.LENGTH_SHORT).show();
                            // Chamar Iterator para trocar de fragment.
                            mListener.onConfigurationsIterator(conf,cAction);
                            dismiss();
                    } catch (Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(),R.string.default_error, Toast.LENGTH_SHORT).show();
                    }
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), R.string.campo_incorreto, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConfigurationsItemListener) {
            mListener = (OnConfigurationsItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void showMessageDialog(String title,String message){
        MessageDialog d = new MessageDialog();
        d.showMessageDialog(getActivity(), title, message);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_PARAM1,conf);
        outState.putParcelable(ARG_PARAM2,oProject);
        outState.putString(ACTION,cAction);
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
    public interface OnConfigurationsItemListener {
        void onConfigurationsIterator(Configurations oConfiguration,String cAction);
    }
}
