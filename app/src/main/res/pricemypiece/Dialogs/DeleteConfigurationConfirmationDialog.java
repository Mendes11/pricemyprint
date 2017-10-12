package pricemypiece.Dialogs;

import android.content.Context;
import android.widget.Toast;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 24/09/2016.
 */
public class DeleteConfigurationConfirmationDialog {

    public interface onConfigurationDeleteConfirmationListener{
        public void onConfigurationConfirmationSuccess();
    }
    Context context;
    onConfigurationDeleteConfirmationListener mListener;
    public void showDialog(Context c, final Configurations oConf, final onConfigurationDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(c, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                DB db = new DB(context);
                try {
                    int ret = db.deleteConfiguration(oConf);
                    if(ret == 1){
                        Toast.makeText(context, R.string.delete_confirmation, Toast.LENGTH_SHORT).show();
                        mListener.onConfigurationConfirmationSuccess();
                    }
                }catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
