package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class DeletePieceConfirmationDialog {



    public interface onPieceDeleteConfirmationListener{
        public void onDeleteConfirmationSuccess();
    }
    onPieceDeleteConfirmationListener mListener;
    Context context;
    Piece piece;
    List<Piece> pieceList;
    Boolean isList = false;
    public DeletePieceConfirmationDialog(Context c, final Piece piece, final onPieceDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        this.piece = piece;
    }
    public DeletePieceConfirmationDialog(Context c, List<Piece> pieceList, final onPieceDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        this.pieceList = pieceList;
        isList = true;
    }
    // TODO: 23/12/2016 Atualmente o retorno do db não faz nada... Pensar em uma forma de tratar os erros.
    public void showDialog(){
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(context, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                DB db = new DB(context);
                if(!isList){
                    pieceList = new ArrayList<Piece>();
                    pieceList.add(piece);
                }
                try {
                    int ret = 0;
                    for(Piece obj: pieceList) {
                        ret = db.deletePieces(obj);
                    }
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
