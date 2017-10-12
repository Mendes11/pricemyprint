package pricemypiece.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Activities.ConfigurationsActivity;
import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Dialogs.MessageDialog;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.MascaraMonetaria;
import br.com.cozinheiro.pricemypiece.R;

public class ConfigurationsItemFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "oConfiguration";


    private OnConfigurationsItemListener mListener;

    public ConfigurationsItemFragment() {
        // Required empty public constructor
    }


    public static ConfigurationsItemFragment newInstance(Configurations param1) {
        ConfigurationsItemFragment fragment = new ConfigurationsItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    TextInputEditText fPreco,iConsumo,fTarifa,fHoraTrabalho,fMediaUso,iVidaUtil,cNomeConfiguracao;
    Spinner fReparo,fFalha;
    ImageButton btnHelpPreco,btnHelpConsumo,btnHelpTarifa,btnHelpHoraTrabalho,btnHelpMediaUso,btnHelpVidaUtil,btnHelpReparo,btnHelpFalha;
    AppCompatButton btnSalvar;
    Configurations conf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            conf = getArguments().getParcelable(ARG_PARAM1);
            if(conf == null){
                conf = new Configurations();
                conf.setDefaultValues();
            }
        }else{
            conf = new Configurations();
            conf.setDefaultValues();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configurations_item, container, false);
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
        cNomeConfiguracao = (TextInputEditText) view.findViewById(R.id.nome_impressora);
        fPreco.addTextChangedListener(new MascaraMonetaria(fPreco));
        fPreco.setText(R.string.currency+"0,00");
        fTarifa.addTextChangedListener(new MascaraMonetaria(fTarifa));
        fTarifa.setText(R.string.currency+"0,00");
        fHoraTrabalho.addTextChangedListener(new MascaraMonetaria(fHoraTrabalho));
        fHoraTrabalho.setText(R.string.currency+"0,00");
        btnHelpPreco = (ImageButton) view.findViewById(R.id.btnHelp_preco);
        btnHelpConsumo = (ImageButton) view.findViewById(R.id.btnHelp_consumo);
        btnHelpTarifa = (ImageButton) view.findViewById(R.id.btnHelp_tarifa);
        btnHelpHoraTrabalho = (ImageButton) view.findViewById(R.id.btnHelp_hora_trabalho);
        btnHelpMediaUso = (ImageButton) view.findViewById(R.id.btnHelp_media_uso);
        btnHelpVidaUtil = (ImageButton) view.findViewById(R.id.btnHelp_anos_uso);
        btnHelpReparo = (ImageButton) view.findViewById(R.id.btnHelp_reparos);
        btnHelpFalha = (ImageButton) view.findViewById(R.id.btnHelp_falhas);
        btnSalvar = (AppCompatButton) view.findViewById(R.id.btnSalvar);
        // Preenchendo os spinners de %

        List<String> porcents = new ArrayList<>();
        for(int i = 0; i<=100 ;i++){
            porcents.add(i+"%");
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, porcents);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, porcents);
        fReparo.setAdapter(adapter1);
        fFalha.setAdapter(adapter2);

        fPreco.setText(getString(R.string.currency) + String.format("%.2f", conf.getfPrecoImpressora()).replace(".", ","));
        iConsumo.setText(""+conf.getiConsumo());
        fTarifa.setText(getString(R.string.currency)+String.format("%.2f",conf.getfTarifa()).replace(".", ","));
        fReparo.setSelection((int) (conf.getfReparo() * 100));
        fHoraTrabalho.setText(getString(R.string.currency) + String.format("%.2f", conf.getfHoraTrabalho()).replace(".", ","));
        fFalha.setSelection((int) (conf.getfTaxaFalhas() * 100));
        fMediaUso.setText("" + conf.getfMediaUso());
        iVidaUtil.setText(""+conf.getfTempoVida());
        cNomeConfiguracao.setText(conf.getcNomeConfig());
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!cNomeConfiguracao.getText().toString().trim().equals("")) {

                        conf.setfPrecoImpressora(Double.parseDouble(fPreco.getText().toString().substring(2).replace(".", "").replace(",", ".")));
                        conf.setiConsumo((int) Math.round(Double.parseDouble(iConsumo.getText().toString()) - 0.5));
                        conf.setfTarifa(Double.parseDouble(fTarifa.getText().toString().substring(2).replace(".", "").replace(",", ".")));
                        conf.setfReparo(fReparo.getSelectedItemPosition() / 100.0);
                        conf.setfHoraTrabalho(Double.parseDouble(fHoraTrabalho.getText().toString().substring(2).replace(".", "").replace(",", ".")));
                        conf.setfTaxaFalhas(fFalha.getSelectedItemPosition() / 100.0);
                        conf.setfMediaUso(Double.parseDouble(fMediaUso.getText().toString()));
                        conf.setfTempoVida(Double.parseDouble(iVidaUtil.getText().toString()));
                        conf.setcNomeConfig(cNomeConfiguracao.getText().toString());
                        DB db = new DB(getActivity().getApplication());
                        conf = db.insertConfigurations(conf);
                        Toast.makeText(getActivity().getApplicationContext(), R.string.configuracoes_save_success, Toast.LENGTH_SHORT).show();
                        // Chamar Iterator para trocar de fragment.
                        mListener.onConfigurationsIterator(conf);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.campo_incorreto, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       Dialog dialog = super.onCreateDialog(savedInstanceState);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(R.string.nova_configuracao);
        //btnSalvar.setVisibility(View.GONE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        return dialog;
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
        void onConfigurationsIterator(Configurations oConfiguration);
    }
}
