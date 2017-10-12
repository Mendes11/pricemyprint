package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/09/2016.
 */
public class DeleteConfigurationConfirmationDialog {
    Context c;
    List<Configurations> configurationsList;
    onConfigurationDeleteConfirmationListener mListener;
    public interface onConfigurationDeleteConfirmationListener{
        public void onConfigurationConfirmationSuccess(List<Configurations> confList);
    }
    public DeleteConfigurationConfirmationDialog(Context c, List<Configurations> confList, onConfigurationDeleteConfirmationListener mListener){
        this.c = c;
        this.mListener = mListener;
        this.configurationsList = confList;
    }

    public void showDialog(){
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(c, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                mListener.onConfigurationConfirmationSuccess(configurationsList);
            }
        });
    }
}
