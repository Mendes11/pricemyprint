package pricemypiece.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 25/08/2016.
 */
public class MessageDialog {

    public MessageDialog(){

    }
    public void showMessageDialog(Context c,String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.DialogTheme);
        if(title == null){
            title = "";
        }
        if(message == null){
            message = "";
        }
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
