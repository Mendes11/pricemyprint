package br.com.cozinheirodelivery.pricemyprint.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 16/10/2016.
 */
public class PickProjectDialog {
    public PickProjectDialog(){

    }
    public interface onPickProjectInterface{
        public void onSelectedProject(Project oProject,Piece oPiece);
        public void onNewProject(Piece oPiece);
    }
    public void showMessageDialog(Context c, final Piece oPiece, final List<Project> projectList, final onPickProjectInterface mInterface){
        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.DialogTheme);
        //ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(c, android.R.layout.simple_list_item_1,projectList);
        CharSequence[] cs = new CharSequence[projectList.size()];
        int i = 0;
        for(Project obj: projectList){
            cs[i] = obj.getcNome();
            i++;
        }
        builder.setTitle(R.string.pickproject_dialog_title)
        .setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    mInterface.onNewProject(oPiece);
                }else{
                    mInterface.onSelectedProject(projectList.get(which),oPiece);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
