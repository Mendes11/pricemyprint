package pricemypiece.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.sawyer.advadapters.widget.PatchedExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Database.DB_PIECE;
import br.com.cozinheiro.pricemypiece.Database.DB_PROJECT;
import br.com.cozinheiro.pricemypiece.Dialogs.ConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.DeletePieceConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.DeleteProjectConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.MessageDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewMaterialDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewPieceDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewProjectDialog;
import br.com.cozinheiro.pricemypiece.Fragments.ConfigurationsItemFragment;
import br.com.cozinheiro.pricemypiece.Fragments.MainFragment;
import br.com.cozinheiro.pricemypiece.Objects.Configurations;
import br.com.cozinheiro.pricemypiece.Objects.CustomExpandableListAdapter;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.Objects.SharedPrefs;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;
//@// TODO: 24/09/2016 Padronizar todos os erros para Strings.h, Incluindo os no DB
public class MainActivity extends AppCompatActivity implements NewProjectDialog.newProjectListener, NewMaterialDialog.newMaterialListener,NewPieceDialog.newPieceListener,ConfigurationsItemFragment.OnConfigurationsItemListener{
    DB db;
    Configurations config;
    List<Project> projectList;
    ExpandableListView expandableList;
    ProgressDialog d;
    CustomExpandableListAdapter adapter;
    private static final String USER_FIRST_TIME = "userFirstTime";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        expandableList = (ExpandableListView) findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                DialogFragment dialog = NewProjectDialog.newInstance();
                dialog.show(getSupportFragmentManager(),"NewProjectDialog");
            }
        });
        db = new DB(getApplicationContext());
        config = db.checkConfigs();
        d = new ProgressDialog(MainActivity.this);
        projectList = new ArrayList<>();
        loadData();
        expandableList.setAdapter(adapter);
        expandableList.setGroupIndicator(getResources().getDrawable(R.drawable.icon_expand));
        registerForContextMenu(expandableList);
        expandableList.setOnCreateContextMenuListener(this);
        Boolean firstEntry = Boolean.valueOf(SharedPrefs.readSharedSetting(this, USER_FIRST_TIME, "true"));
        if(firstEntry){
            MessageDialog d = new MessageDialog();
            d.showMessageDialog(this,getString(R.string.first_access_title),getString(R.string.first_access_message));
            SharedPrefs.saveSharedSetting(this,USER_FIRST_TIME,"false");
        }
       // expandableList.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE_MODAL);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListContextMenuInfo info=
                (ExpandableListContextMenuInfo)menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info =
                (ExpandableListContextMenuInfo) item.getMenuInfo();
        int groupPos = 0, childPos = 0;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition); // Tipo da seleção Group ou Child
        groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); // Posição do grupo
        DialogFragment dialog = null;
        int id = item.getItemId();
        switch(id){
            case R.id.edit:
                if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) // é child.
                {
                    childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); // Pega posição do item relacionado ao grupo.
                    Piece piece = (Piece) adapter.getChild(groupPos,childPos);
                    dialog = NewPieceDialog.newInstance((Project) adapter.getGroup(groupPos),piece);
                }else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                    Project project = (Project) adapter.getGroup(groupPos);
                    dialog = NewProjectDialog.newInstance(project);
                }
                dialog.show(getSupportFragmentManager(),"EditDialog");
                return super.onContextItemSelected(item);
            case R.id.delete:
                if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) // é child.
                {
                    childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); // Pega posição do item relacionado ao grupo.
                    Piece piece = (Piece) adapter.getChild(groupPos,childPos);
                    DeletePieceConfirmationDialog p = new DeletePieceConfirmationDialog();
                    p.showDialog(this, piece, new DeletePieceConfirmationDialog.onPieceDeleteConfirmationListener() {
                        @Override
                        public void onDeleteConfirmationSuccess() {
                            loadData();
                        }
                    });
                }else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                    Project project = (Project) adapter.getGroup(groupPos);
                    DeleteProjectConfirmationDialog p = new DeleteProjectConfirmationDialog(this, project, new DeleteProjectConfirmationDialog.onProjectDeleteConfirmationListener() {
                        @Override
                        public void onDeleteConfirmationSuccess() {
                            loadData();
                        }

                        @Override
                        public void onDeleteConfirmationFailure(String error) {

                        }
                    });
                    p.showDialog();
                return super.onContextItemSelected(item);
                }
            default:
        }
        return false;
    }

    public void loadData(){
        //d.setMessage("Carregando Dados, Aguarde...");
        //d.show();
        projectList = db.getProjects(null, null, null, null);
        if(projectList != null) {
            for (Project obj : projectList) {
                int iIDPiece;
                long iIDProjeto = obj.getiIDProjeto();
                List<Piece> childList = db.getPieces(DB_PIECE.Estrutura_Peca.COLUMN_PROJETO_FK + " = ?", new String[]{String.valueOf(iIDProjeto)}, null, null);
                obj.setChildList(childList);
            }

            if(adapter == null){
                adapter = new CustomExpandableListAdapter(MainActivity.this, projectList, new CustomExpandableListAdapter.customButtonListener() {
                    @Override
                    public void onButtonClickListener(View v, int position, Long iIDProjeto) {
                        // Chama um dialog de adicionar Peça
                        Project obj = (Project) adapter.getGroup(position);
                        DialogFragment dialog = NewPieceDialog.newInstance(obj);
                        dialog.show(getSupportFragmentManager(),"NewPieceDialog");
                    }
                });
                expandableList.setAdapter(adapter);
            }else{
                adapter.setList(projectList);
                adapter.notifyDataSetChanged();
            }
        }else{
            if(adapter != null) {
                projectList = new ArrayList<>();
                adapter.setList(projectList);
                adapter.notifyDataSetChanged();
            }
        }
        //d.dismiss();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, ConfigurationsActivity.class);
            startActivity(i);
            return true;
        }else if (id == R.id.action_materials){
            Intent i = new Intent(MainActivity.this,MaterialActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewProjectPositiveClick(Project project) {
        try{
            project = db.insertProject(project);
            loadData();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNewMaterialPositiveClick(TiposMateriais obj) {
        try{
            obj = db.insertTipoMaterial(obj);
            if(obj.getiIDTipoMaterial() != 0) {
                Toast.makeText(getApplicationContext(),"Material Inserido com Sucesso.",Toast.LENGTH_SHORT).show();
                // Check se NewPieceDialog está visivel.
                NewPieceDialog d = (NewPieceDialog) getSupportFragmentManager().findFragmentByTag("NewPieceDialog");
                if (d.isAdded()) {
                    d.fillSpinner(obj);
                }
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNewPiecePositiveClick(Piece obj) {
        try{
            obj = db.insertPiece(obj);
            if(obj.getiIDPiece() != 0){
                Toast.makeText(getApplicationContext(),"Peça Inserida com Sucesso.",Toast.LENGTH_SHORT).show();
                loadData();
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),R.string.new_piece_error_2,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onConfigurationsIterator(Configurations oConfiguration) {
        ConfigurationsItemFragment frag = (ConfigurationsItemFragment) getSupportFragmentManager().findFragmentByTag("ConfigurationsItemFragment");
        frag.dismiss();
        NewPieceDialog d = (NewPieceDialog) getSupportFragmentManager().findFragmentByTag("NewPieceDialog");
        d.fillConfig(oConfiguration);
    }
}