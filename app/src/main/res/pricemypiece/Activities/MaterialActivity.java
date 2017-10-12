package pricemypiece.Activities;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Dialogs.DeleteMaterialConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.DeletePieceConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.DeleteProjectConfirmationDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewMaterialDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewPieceDialog;
import br.com.cozinheiro.pricemypiece.Dialogs.NewProjectDialog;
import br.com.cozinheiro.pricemypiece.Objects.CustomMaterialListAdapter;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.Objects.TiposMateriais;
import br.com.cozinheiro.pricemypiece.R;

public class MaterialActivity extends AppCompatActivity implements NewMaterialDialog.newMaterialListener {
    ListView listview;
    Toolbar t;
    List<TiposMateriais> lista;
    CustomMaterialListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);
        listview = (ListView) findViewById(R.id.list);
        registerForContextMenu(listview);
        loadData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DialogFragment dialog = null;
        int position = info.position;
        int id = item.getItemId();
        switch(id){
            case R.id.edit:
                TiposMateriais obj = lista.get(position);
                dialog = NewMaterialDialog.newInstance(obj);
                dialog.show(getSupportFragmentManager(),"EditDialog");
                return super.onContextItemSelected(item);
            case R.id.delete:
                    TiposMateriais material =  lista.get(position);
                    DeleteMaterialConfirmationDialog m = new DeleteMaterialConfirmationDialog();
                    m.showDialog(this, material, new DeleteMaterialConfirmationDialog.onMaterialDeleteConfirmationListener() {
                        @Override
                        public void onDeleteConfirmationSuccess() {
                            loadData();
                        }
                    });
                return super.onContextItemSelected(item);
            default:
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            DialogFragment dialog = NewMaterialDialog.newInstance();
            dialog.show(getSupportFragmentManager(),"NewMaterialDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void loadData(){
        DB db = new DB(this);
        lista = db.getTiposMateriais(null,null,null,null);
        if(lista != null){
            if(adapter == null){
                adapter = new CustomMaterialListAdapter(this,lista);
                listview.setAdapter(adapter);
            }else{
                adapter.setList(lista);
                adapter.notifyDataSetChanged();
            }
        }else{
            if(adapter != null){
                lista = new ArrayList<>();
                adapter.setList(lista);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onNewMaterialPositiveClick(TiposMateriais obj) {
        DB db = new DB(this);
        try {
            obj = db.insertTipoMaterial(obj);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
