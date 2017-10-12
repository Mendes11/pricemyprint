package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.text.NumberFormat;

import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 16/10/2016.
 */
public class QuickPriceDialog {
    public QuickPriceDialog(){

    }
    public interface onQuickPriceInterface{
        public void onAddToProject(Piece oPiece);
    }
    public void showMessageDialog(Context c, final Piece oPiece, final onQuickPriceInterface mInterface){
        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.DialogTheme);
        String title = c.getString(R.string.quickprice_dialog_title);
        String message = c.getString(R.string.quickprice_dialog_message,NumberFormat.getCurrencyInstance().format(oPiece.getfPreco()));
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(R.string.quickprice_dialog_add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mInterface.onAddToProject(oPiece);
                    }
                }).setNegativeButton(R.string.quickprice_dialog_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
