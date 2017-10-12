package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;

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
    List<Project> projectList;
    Boolean isList = false;
    public DeleteProjectConfirmationDialog(Context c, Project project, onProjectDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        this.project = project;
    }
    public DeleteProjectConfirmationDialog(Context c, List<Project> projectList, onProjectDeleteConfirmationListener mListener){
        this.context = c;
        this.mListener = mListener;
        this.projectList = projectList;
        isList = true;

    }

    // TODO: 21/12/2016 Atualmente o retorno do db não faz nada... Pensar em uma forma de tratar os erros.
    public void showDialog(){
        ConfirmationDialog d = new ConfirmationDialog();
        d.showMessageDialog(context, R.string.delete_title, R.string.confirmar_text, new ConfirmationDialog.onConfirmationListener() {
            @Override
            public void onConfirmationPositive() {
                DB db = new DB(context);
                try {
                    if(!isList){
                        projectList = new ArrayList<Project>();
                        projectList.add(project);
                    }
                    int ret = 1;
                    for(Project oProject:projectList) {
                        ret = db.deleteProject(oProject);
                    }
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
