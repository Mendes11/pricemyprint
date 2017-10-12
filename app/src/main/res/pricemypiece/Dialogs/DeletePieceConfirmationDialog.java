package pricemypiece.Dialogs;

import android.content.Context;
import android.widget.Toast;

import br.com.cozinheiro.pricemypiece.Activities.MainActivity;
import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class DeletePieceConfirmationDialog {



    public interface onPieceDeleteConfirmationListener{
        public void onDeleteConfirmationSuccess();
    }
    onPieceDeleteConfirmationListener mListener;
    Context context;
    public void showDialog(Context c, final Piece piece, final onPieceDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(c, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                DB db = new DB(context);
                try {
                    int ret = db.deletePieces(piece);
                    if(ret == 1){
                        Toast.makeText(context,R.string.delete_confirmation, Toast.LENGTH_SHORT).show();
                        mListener.onDeleteConfirmationSuccess();
                    }
                }catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
