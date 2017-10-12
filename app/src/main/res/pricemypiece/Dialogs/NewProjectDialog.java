package pricemypiece.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 24/08/2016.
 */
public class NewProjectDialog extends DialogFragment{
    newProjectListener nListener;
    EditText cNome;
    Project project;
    Boolean editable = false;
    public interface newProjectListener {
        public void onNewProjectPositiveClick(Project project);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            nListener = (newProjectListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public static NewProjectDialog newInstance(){
        NewProjectDialog frag = new NewProjectDialog();
        return frag;
    }
    public static NewProjectDialog newInstance(Project project){
        NewProjectDialog frag = new NewProjectDialog();
        Bundle args = new Bundle();
        args.putParcelable("Project",project);
        args.putBoolean("Editable", true);
        frag.setArguments(args);
        return frag;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflator = inflater.inflate(R.layout.dialog_new_project, null);
        builder.setView(inflator);
        builder.setTitle(R.string.new_project_dialog_title);
        Bundle bundle = getArguments();
        if(bundle != null){
            if(bundle.containsKey("Editable")){
                editable = bundle.getBoolean("Editable");
                project = bundle.getParcelable("Project");
                builder.setTitle(R.string.edit_project_dialog_title);
            }
        }
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("Editable")){
                if(savedInstanceState.getBoolean("Editable")){
                    editable = true;
                    project = savedInstanceState.getParcelable("Project");
                }
            }
        }

        cNome = (EditText) inflator.findViewById(R.id.nome_project);
        if(editable){
            cNome.setText(project.getcNome());
        }
        builder.setPositiveButton(R.string.confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean wantToCloseDialog = false;
                    if(!cNome.getText().toString().trim().equals("")){
                        wantToCloseDialog = true;
                    }
                    if(wantToCloseDialog) {
                        if(project == null){
                            project = new Project();
                        }
                        project.setcNome(cNome.getText().toString());
                        nListener.onNewProjectPositiveClick(project);
                        d.dismiss();
                    }else{
                        Toast.makeText(getActivity(), "Atenção, Preencha o campo corretamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
