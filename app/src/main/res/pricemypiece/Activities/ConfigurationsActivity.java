package pricemypiece.Activities;

import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Dialogs.MessageDialog;
import br.com.cozinheiro.pricemypiece.Fragments.ConfigurationsItemFragment;
import br.com.cozinheiro.pricemypiece.Fragments.ConfigurationsListFragment;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.MascaraMonetaria;
import br.com.cozinheiro.pricemypiece.R;

public class ConfigurationsActivity extends AppCompatActivity implements ConfigurationsListFragment.OnConfigurationsListFragmentInteraction,ConfigurationsItemFragment.OnConfigurationsItemListener{
    FragmentManager manager;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurations);
        setTitle(R.string.configuracoes_title);
        if(manager == null){
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
        }
        transaction.add(R.id.container,new ConfigurationsListFragment().newInstance(),"ConfigurationsListFragment");
        transaction.commit();
    }

    @Override
    public void onConfigurationsIterator(Configurations oConfiguration) {
        // É chamado ao se salvar as configurações, neste caso, faz um pop da lista e atualiza-a?
        if(manager == null){
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
        }
        manager.popBackStack("ConfigurationsListFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        //Chama método que recebe o objeto modificado e atualiza-o na lista.
        ConfigurationsListFragment mFrag = (ConfigurationsListFragment) manager.findFragmentByTag("ConfigurationsListFragment");
        mFrag.updateConfigurationItem(oConfiguration);
    }

    @Override
    public void onConfigurationListIteration(Configurations oConfiguration) {
        if(manager == null){
            manager = getSupportFragmentManager();
        }
        transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new ConfigurationsItemFragment().newInstance(oConfiguration));
        transaction.addToBackStack("ConfigurationsListFragment");
        transaction.commit();
    }
}
