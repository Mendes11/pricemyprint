package pricemypiece.Dialogs;

import android.content.Context;
import android.widget.Toast;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 15/09/2016.
 */
public class DeleteProjectConfirmationDialog {

    public interface onProjectDeleteConfirmationListener{
        public void onDeleteConfirmationSuccess();
        public void onDeleteConfirmationFailure(String error);
    }
    onProjectDeleteConfirmationListener mListener;
    Context context;
    Project project;
    public DeleteProjectConfirmationDialog(Context c, Project project, onProjectDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        this.project = project;
    }
    public void showDialog(){
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(context, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                DB db = new DB(context);
                try {
                    int ret = db.deleteProject(project);
                    if(ret == 1){
                        Toast.makeText(context,R.string.delete_confirmation, Toast.LENGTH_SHORT).show();
                        mListener.onDeleteConfirmationSuccess();
                    }
                }catch(Exception e){
                    Toast.makeText(context,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
