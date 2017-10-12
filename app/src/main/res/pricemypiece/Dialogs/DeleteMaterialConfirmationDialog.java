package pricemypiece.Dialogs;

import android.content.Context;
import android.widget.Toast;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class DeleteMaterialConfirmationDialog {

    public interface onMaterialDeleteConfirmationListener{
        public void onDeleteConfirmationSuccess();
    }
    Context context;
    onMaterialDeleteConfirmationListener mListener;
    public void showDialog(Context c, final TiposMateriais material, final onMaterialDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(c, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                DB db = new DB(context);
                try {
                    int ret = db.deleteMaterialSingle(material);
                    if(ret == 1){
                        Toast.makeText(context, R.string.delete_confirmation, Toast.LENGTH_SHORT).show();
                        mListener.onDeleteConfirmationSuccess();
                    }
                }catch(Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
