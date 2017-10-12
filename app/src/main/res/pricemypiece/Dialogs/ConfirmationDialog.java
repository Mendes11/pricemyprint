package pricemypiece.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class ConfirmationDialog {

    public interface onConfirmationListener{
        public void onConfirmationPositive();
    }
    onConfirmationListener mListener;
    public void showMessageDialog(Context c,int ResourceTitle,int ResourceMessage, final onConfirmationListener mListener){
        this.mListener = mListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.DialogTheme);
        String title = c.getResources().getString(ResourceTitle);
        String message = c.getResources().getString(ResourceMessage);
        if(title == null){
            title = "";
        }
        if(message == null){
            message = "";
        }
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onConfirmationPositive();
                    }
                }).setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
