package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class DeleteMaterialConfirmationDialog {

    public interface onMaterialDeleteConfirmationListener{
        public void onDeleteConfirmationSuccess(List<TiposMateriais> materiaisList);
    }
    public DeleteMaterialConfirmationDialog(Context c, List<TiposMateriais> materialList, onMaterialDeleteConfirmationListener mListener){
        this.context = c;
        this.materialList = materialList;
        this.mListener = mListener;
    }
    Context context;
    List<TiposMateriais> materialList;
    onMaterialDeleteConfirmationListener mListener;
    public void showDialog(){
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(context, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                mListener.onDeleteConfirmationSuccess(materialList);

            }
        });
    }

}
