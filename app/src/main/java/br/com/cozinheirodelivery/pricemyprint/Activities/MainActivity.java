package br.com.cozinheirodelivery.pricemyprint.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import br.com.cozinheirodelivery.pricemyprint.Adapters.TabAdapter.TabAdapter;
import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Database.DB_PROJECT;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.ImportDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.MessageDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewMaterialDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewPieceDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewProjectDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.PickProjectDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.QuickPriceDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.ConfigurationsItemDialog;
import br.com.cozinheirodelivery.pricemyprint.Fragments.MainFragment;
import br.com.cozinheirodelivery.pricemyprint.Fragments.ProjectComponentsFragment;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.Objects.CustomExpandableListAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Objects.SharedPrefs;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

//@// TODO: 24/09/2016 Padronizar todos os erros para Strings.h, Incluindo os no DB
public class MainActivity extends AppCompatActivity implements NewProjectDialog.newProjectListener,
        NewMaterialDialog.newMaterialListener,
        NewPieceDialog.newPieceListener,
        ConfigurationsItemDialog.OnConfigurationsItemListener,
        MainFragment.MainFragmentListener,ImportDialog.onImportListener{

    // ******** Variaveis *************
    TabAdapter tabAdapter;
    DB db;
    Configurations config;
    List<Project> projectList;
    TextView lblfPrice;
    ExpandableListView expandableList;
    FragmentManager manager;
    FragmentTransaction transaction;
    ProjectComponentsFragment projecComponentFragment;
    MainFragment mainFrag;
    ProgressDialog d;
    CustomExpandableListAdapter adapter;
    private static final String USER_FIRST_TIME = "userFirstTime";
    private static final String ADM_MESSAGE_1 = "admMessage1";
    private static final String QUICK_PRICE_PROJECT_ID = "quickPriceProjectID";
    public static final String NEW_LAYOUT_FIRST_TIME = "newLayoutFirstTime";
    public static final String ACTION_NEWFAB = "NEWFAB";
    public static final String ACTION_NEWPIECE = "NEWPIECE";
    public static final String ACTION_NEWQUICK = "NEWQUICK";
    public static final String ACTION_EDIT = "EDIT";
    public static final String ACTION_REMOVE = "REMOVE";

    Project oQuickPrice;
    // *************** ON CREATE **************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lblfPrice = (TextView) findViewById(R.id.main_total);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //expandableList = (ExpandableListView) findViewById(R.id.list);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                // TODO: 24/12/2016 Realizar uma pesquisa sobre qual fragment esta aberto, assim, joga para uma função desse fragment que trata o click do FAB para ele.
                if (mainFrag.isVisible()) {
                    DialogFragment dialog = NewProjectDialog.newInstance(projectList);
                    dialog.show(getSupportFragmentManager(), "NewProjectDialog");
                }else{
                    projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
                    //projecComponentFragment.notifyFABClick();
                    int position = projecComponentFragment.getSelectedTab();
                    Project oProject = projecComponentFragment.getoProject();
                    DialogFragment dialog = null;
                    switch (position){
                        case 0: // Part Tab, cria nova peça.
                            if(projecComponentFragment.isQuickPrice()){
                                dialog = ConfigurationsItemDialog.newInstance(null,oProject, ACTION_NEWFAB);
                                dialog.show(getSupportFragmentManager(),"ConfigurationsItemFragment");
                            }else {
                                dialog = NewPieceDialog.newInstance(projecComponentFragment.getoProject());
                                dialog.show(getSupportFragmentManager(), "NewPieceDialog");
                            }
                            break;
                        case 1:
                            if(projecComponentFragment.isQuickPrice()){
                                dialog = NewMaterialDialog.newInstance(null,oProject,ACTION_NEWFAB);
                                dialog.show(getSupportFragmentManager(),"NewMaterialDialog");
                            }else {
                                dialog = ConfigurationsItemDialog.newInstance(null, oProject, ACTION_NEWFAB);
                                dialog.show(getSupportFragmentManager(), "ConfigurationsItemFragment");
                            }
                            break;
                        case 2:
                            dialog = NewMaterialDialog.newInstance(null,oProject,ACTION_NEWFAB);
                            dialog.show(getSupportFragmentManager(),"NewMaterialDialog");
                            break;
                    }
                }
            }
        });
        db = new DB(getApplicationContext());
        //config = db.checkConfigs(); -> Não cria uma configuração padrão mais, porém o default ja é carregado
        //d = new ProgressDialog(MainActivity.this);
        //loadData();


        //expandableList.setAdapter(adapter);
        //expandableList.setGroupIndicator(getResources().getDrawable(R.drawable.icon_expand));
        //registerForContextMenu(expandableList);
        //expandableList.setOnCreateContextMenuListener(this);
        Boolean firstEntry = Boolean.valueOf(SharedPrefs.readSharedSetting(this, NEW_LAYOUT_FIRST_TIME, "true"));
        //Boolean admMessage1 = Boolean.valueOf(SharedPrefs.readSharedSetting(this,ADM_MESSAGE_1,"true"));
        Long quickPriceID = Long.valueOf(SharedPrefs.readSharedSetting(this,QUICK_PRICE_PROJECT_ID,"0"));
        if(quickPriceID == 0){
            oQuickPrice = new Project();
            oQuickPrice.setcNome("SystemQuickPrice");
            try {
                db.insertProject(oQuickPrice);
                if(oQuickPrice.getiIDProjeto() != 0){
                    SharedPrefs.saveSharedSetting(this,QUICK_PRICE_PROJECT_ID,String.valueOf(oQuickPrice.getiIDProjeto()));
                    Log.d("QuickPriceProject","QuickPriceProject Created and Saved");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            oQuickPrice = db.getProjects(DB_PROJECT.Estrutura_Projeto.COLUMN_ID+" = ?",new String[]{String.valueOf(quickPriceID)},null,null).get(0);
        }

        projectList = db.getProjects(DB_PROJECT.Estrutura_Projeto.COLUMN_ID+ " != ?",new String[]{String.valueOf(oQuickPrice.getiIDProjeto())},null,null);
        if(projectList == null) projectList = new ArrayList<>();
        if(manager == null){
            manager = getSupportFragmentManager();
            transaction = manager.beginTransaction();
            if(manager.findFragmentByTag("MainFragment") == null) {
                mainFrag = MainFragment.newInstance(projectList);
                transaction.add(R.id.container, mainFrag, "MainFragment");
                transaction.commit();
            }
        }
        if(firstEntry){
            Intent i = new Intent(MainActivity.this,IntroAppActivity.class);
            startActivity(i);
            SharedPrefs.saveSharedSetting(this,NEW_LAYOUT_FIRST_TIME,"false");
        }
       // expandableList.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE_MODAL);
    }

    //****************** MENU DO TOOLBAR E SUAS AÇÕES *******************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_quickMenu) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ProjectComponentsFragment frag = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            if(frag == null) frag = ProjectComponentsFragment.newInstance(oQuickPrice);
            transaction.replace(R.id.container, frag, "ProjectComponentsFragment");
            transaction.addToBackStack("MainFragment");
            transaction.commit();
            return true;
        }else if (id == R.id.action_quickprice){
            DialogFragment d = NewPieceDialog.newInstance(oQuickPrice,true);
            d.show(getSupportFragmentManager(),"NewPieceDialog");
        }/*else if(id == R.id.action_import_piece){
            projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            DialogFragment d = ImportDialog.newInstance(projecComponentFragment.getoProject(),projectList,ImportDialog.IMPORT_PIECE);
            d.show(getSupportFragmentManager(),"ImportDialog");
        }*/else if(id == R.id.action_import_config){
            projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            DialogFragment d = ImportDialog.newInstance(projecComponentFragment.getoProject(),projectList,ImportDialog.IMPORT_CONFIG);
            d.show(getSupportFragmentManager(),"ImportDialog");
        }else if(id == R.id.action_import_material){
            projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            DialogFragment d = ImportDialog.newInstance(projecComponentFragment.getoProject(),projectList,ImportDialog.IMPORT_MATERIAL);
            d.show(getSupportFragmentManager(),"ImportDialog");
        }else if(id == R.id.action_Tutorial){
            Intent i = new Intent(MainActivity.this,IntroAppActivity.class);
            startActivity(i);
        }else if(id == R.id.action_rateApp){
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // ********************** LISTENERS ***************************

    // ********************** Novo Projeto + QuickPrice Opcional ************************
    @Override
    public void onNewProjectPositiveClick(Project project,Piece oPiece,List<Configurations> configurationsList,List<TiposMateriais> materiaisList) {
        String action = "INSERT";
        try{
            if(project.getiIDProjeto()> 0){
                action = "UPDATE";
            }
            db.insertProject(project);
            if(project != null) {
                if(project.getiIDProjeto() != -1) {
                    if (configurationsList != null)
                        onConfirmImportConfig(project, configurationsList);
                    if (materiaisList != null) onConfirmImportMaterial(project, materiaisList);
                }
            }
            if(oPiece != null){
                oPiece.setiIDProject(project.getiIDProjeto());
                oPiece.setcNome("QuickPrice1");
                Configurations oConf = oPiece.getoConfigurations();
                Configurations oConf2 = new Configurations();
                oConf2.setValues(oConf);
                oConf2.setcNomeConfig(oConf.getcNomeConfig()+"_QUICK.P");
                oConf2.setiIDProjeto(project.getiIDProjeto());

                TiposMateriais oMaterial = oPiece.getiIDTipoMaterial();
                TiposMateriais oMaterial2 = new TiposMateriais();
                oMaterial2.setValues(oMaterial);
                oMaterial2.setcNome(oMaterial.getcNome()+"_QUICK.P");
                oMaterial2.setiIDProjeto(project.getiIDProjeto());

                db.insertTipoMaterial(oMaterial2);
                db.insertConfigurations(oConf2);
                oPiece.setiIDTipoMaterial(oMaterial2);
                oPiece.setoConfigurations(oConf2);
                db.insertPiece(oPiece);
                if(project.getChildList()==null)
                project.setChildList(new ArrayList<Piece>());
                project.getChildList().add(oPiece);
                if(project.getMateriaisList() == null)
                project.setMateriaisList(new ArrayList<TiposMateriais>());
                if(project.getConfigurationsList()==null)
                project.setConfigurationsList(new ArrayList<Configurations>());
                project.getConfigurationsList().add(oConf2);
                project.getMateriaisList().add(oMaterial2);
            }
            if(manager != null){
                if(mainFrag == null) {
                    mainFrag = (MainFragment) manager.findFragmentByTag("MainFragment");
                }
                mainFrag.updateRecyclerView(project,action);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // ************************** Adição de Novo Material ao PieceDialog **************************
    @Override
    public void onNewMaterialPositiveClick(TiposMateriais obj,String cAction) {
        if(!cAction.equals(ACTION_REMOVE)) {
            if (cAction.equals(ACTION_NEWPIECE) || cAction.equals(ACTION_NEWQUICK)) { // Casos em que o PieceDialog surge
                NewPieceDialog d = (NewPieceDialog) getSupportFragmentManager().findFragmentByTag("NewPieceDialog");
                if (d != null) {
                    if (d.isAdded()) {
                        d.fillSpinner(obj);
                    }
                }
            }
            if (cAction.equals(MainActivity.ACTION_NEWQUICK)) {
                if (oQuickPrice.getMateriaisList() == null) {
                    oQuickPrice.setMateriaisList(new ArrayList<TiposMateriais>());
                    oQuickPrice.getMateriaisList().add(obj);
                }
            }
            projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            if(projecComponentFragment != null) {
                if (cAction.equals(MainActivity.ACTION_NEWQUICK) && projecComponentFragment.getoProject().getiIDProjeto() == oQuickPrice.getiIDProjeto()) {
                    projecComponentFragment.updateMaterialRecyclerView(obj, cAction);
                }
                if (!cAction.equals(ACTION_NEWQUICK)) {
                    projecComponentFragment.updateMaterialRecyclerView(obj, cAction);
                }
            }
        }
    }


    // ********************* Resposta da Adição de uma Nova Configuração através do NewPieceDialog *******************
    @Override
    public void onConfigurationsIterator(Configurations oConfiguration,String cAction) {
        if(!cAction.equals(ACTION_REMOVE)) {
            if (cAction.equals(ACTION_NEWPIECE) || cAction.equals(ACTION_NEWQUICK)) { // Casos em que o PieceDialog surge
                NewPieceDialog d = (NewPieceDialog) getSupportFragmentManager().findFragmentByTag("NewPieceDialog");
                if (d != null) {
                    d.fillConfig(oConfiguration);
                }
            }
            if (cAction.equals(MainActivity.ACTION_NEWQUICK)) {
                if (oQuickPrice.getConfigurationsList() == null) {
                    oQuickPrice.setConfigurationsList(new ArrayList<Configurations>());
                    oQuickPrice.getConfigurationsList().add(oConfiguration);
                }
            }
            projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            if(projecComponentFragment != null) {
                if (cAction.equals(MainActivity.ACTION_NEWQUICK) && projecComponentFragment.getoProject().getiIDProjeto() == oQuickPrice.getiIDProjeto()){
                    projecComponentFragment.updateConfigRecyclerView(oConfiguration, cAction);
                }
                if (!cAction.equals(ACTION_NEWQUICK)) {
                    projecComponentFragment.updateConfigRecyclerView(oConfiguration, cAction);
                }
            }
        }
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    // **************************** Adição de Nova Peça à um Projeto *********************************
    @Override
    public void onNewPiecePositiveClick(Project oProject, Piece obj) {
        try {
            db.insertPiece(obj);
            if(obj.getiIDPiece() != 0) {
                Toast.makeText(getApplicationContext(), R.string.item_save_success, Toast.LENGTH_SHORT).show();
                List<Piece> list = oProject.getChildList();
                if(list == null){
                    list = new ArrayList<>();
                    oProject.setChildList(list);
                }
                list.add(obj);
                //if(projecComponentFragment == null){
                    projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
               // }
                projecComponentFragment.updatePrintsRecyclerView(obj, "INSERT");
            }
        } catch (Exception e) {
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // **************************** Edição de Peça de um Projeto *********************************
    @Override
    public void onEditPiecePositiveClick(Piece obj) {
        try {
            Piece p = db.insertPiece(obj);
            //if(projecComponentFragment == null) {
                projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
            //}
            projecComponentFragment.updatePrintsRecyclerView(obj, "UPDATE");
            if (p.getiIDPiece() != 0) {
                Toast.makeText(getApplicationContext(), R.string.item_save_success, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // **************************** Resposta do QuickPrice *********************************
    // TODO: 24/12/2016 Fazer com que o quickPrice identifique se esta em ProjectComponentsfragment, atualizando a lista dele, caso o proj add seja esse. 
    @Override
    public void onQuickPricePositiveClick(Piece obj) {
        try {
            Double preco = obj.getfPreco();
            DialogFragment d = (NewPieceDialog) getSupportFragmentManager().findFragmentByTag("NewPieceDialog");
            d.dismiss();
            QuickPriceDialog newDialog = new QuickPriceDialog();
            newDialog.showMessageDialog(this, obj, new QuickPriceDialog.onQuickPriceInterface() {
                @Override
                public void onAddToProject(Piece oPiece) {
                    //Abre uma listview de dialog com os projetos existentes e um Criar Novo Projeto.
                    List<Project> mList = new ArrayList<Project>();
                    //Criando o projeto "Novo Projeto"
                    Project oProject = new Project();
                    oProject.setcNome(getString(R.string.new_project));
                    mList.add(oProject);
                    if(projectList!= null) {
                        if(projectList.size()>0) {
                            mList.addAll(projectList);
                        }
                    }
                    PickProjectDialog pickDialog = new PickProjectDialog();
                    pickDialog.showMessageDialog(MainActivity.this, oPiece, mList, new PickProjectDialog.onPickProjectInterface() {
                        @Override
                        public void onSelectedProject(Project oProject, Piece oPiece) {
                            try {
                                if (db == null) {
                                    db = new DB(getApplicationContext());
                                }
                                oPiece.setiIDProject(oProject.getiIDProjeto());
                                int i = 1;
                                if (oProject.getChildList() != null){
                                    for (Piece obj : oProject.getChildList()) {
                                        if (obj.getcNome().indexOf("QuickPrice") >= 0) {
                                            i++;
                                        }
                                    }
                                }else{
                                    oProject.setChildList(new ArrayList<Piece>());
                                }
                                oPiece.setcNome(String.format("QuickPrice%d",i));
                                // ****** Cria novas Configurações e Material de nome igual ao QuickPrice para este projeto.
                                Configurations oConf = oPiece.getoConfigurations();
                                Configurations oConf2 = new Configurations();
                                oConf2.setValues(oConf);
                                String cNome = oConf.getcNomeConfig()+"_QUICK.P";
                                int quant = configQuickPriceOccurrences(oProject,cNome);
                                if(quant > 0){
                                    cNome += "("+quant+")";
                                }
                                oConf2.setcNomeConfig(cNome);
                                oConf2.setiIDProjeto(oProject.getiIDProjeto());

                                TiposMateriais oMaterial = oPiece.getiIDTipoMaterial();
                                TiposMateriais oMaterial2 = new TiposMateriais();
                                oMaterial2.setValues(oMaterial);
                                cNome = oMaterial.getcNome()+"_QUICK.P";
                                quant = materialQuickPriceOccurrences(oProject,cNome);
                                if(quant > 0){
                                    cNome += "("+quant+")";
                                }
                                oMaterial2.setcNome(cNome);
                                oMaterial2.setiIDProjeto(oProject.getiIDProjeto());

                                db.insertTipoMaterial(oMaterial2);
                                db.insertConfigurations(oConf2);
                                oPiece.setiIDTipoMaterial(oMaterial2);
                                oPiece.setoConfigurations(oConf2);
                                db.insertPiece(oPiece);
                                oProject.getChildList().add(oPiece);
                                if(manager != null){
                                    if(mainFrag == null) {
                                        mainFrag = (MainFragment) manager.findFragmentByTag("MainFragment");
                                    }
                                    if(mainFrag.isVisible()) {
                                        mainFrag.updateRecyclerView(oProject, "UPDATE");
                                    }

                                    // ****** Atualiza caso o quickPrice tenha sido utilizado dentro da tela de prits.
                                    //if(projecComponentFragment == null) {
                                        projecComponentFragment = (ProjectComponentsFragment) manager.findFragmentByTag("ProjectComponentsFragment");
                                    //}
                                    if(projecComponentFragment!=null){
                                        if(projecComponentFragment.getoProject() == oProject){ // Se for o mesmo projeto em questão, atualiza a lista, dando um insert.
                                            projecComponentFragment.updatePrintsRecyclerView(oPiece,"INSERT");
                                            projecComponentFragment.updateConfigRecyclerView(oConf2,MainActivity.ACTION_NEWQUICK);
                                            projecComponentFragment.updateMaterialRecyclerView(oMaterial2,MainActivity.ACTION_NEWQUICK);
                                        }else {
                                            if (oProject.getConfigurationsList() == null) {
                                                oProject.setConfigurationsList(new ArrayList<Configurations>());
                                            }
                                            if (oProject.getMateriaisList() == null) {
                                                oProject.setMateriaisList(new ArrayList<TiposMateriais>());
                                            }
                                            oProject.getConfigurationsList().add(oConf2);
                                            oProject.getMateriaisList().add(oMaterial2);
                                        }
                                    }else{
                                        // Neste caso, atualiza a lista do project por aqui.
                                        if(oProject.getConfigurationsList() == null){
                                            oProject.setConfigurationsList(new ArrayList<Configurations>());
                                        }
                                        if(oProject.getMateriaisList() == null){
                                            oProject.setMateriaisList(new ArrayList<TiposMateriais>());
                                        }
                                        oProject.getConfigurationsList().add(oConf2);
                                        oProject.getMateriaisList().add(oMaterial2);
                                    }
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onNewProject(Piece oPiece) {
                            DialogFragment newProject = NewProjectDialog.newInstance(projectList,oPiece);
                            newProject.show(getSupportFragmentManager(),"NewProjectDialog");
                        }
                    });
                }
            });
        }catch(Exception e){

        }
    }

    private int materialQuickPriceOccurrences(Project oProject,String cNome){
        int i = 0;
        if(oProject.getMateriaisList() == null){
            oProject.setMateriaisList(new ArrayList<TiposMateriais>());
        }
        for(TiposMateriais oMaterial: oProject.getMateriaisList()){
            if(oMaterial.getcNome().contains(cNome)){
                i++;
            }
        }
        return i;
    }
    private int configQuickPriceOccurrences(Project oProject,String cNome){
        int i = 0;
        if(oProject.getConfigurationsList() == null){
            oProject.setConfigurationsList(new ArrayList<Configurations>());
        }
        for(Configurations oConf: oProject.getConfigurationsList()){
            if(oConf.getcNomeConfig().contains(cNome)){
                i++;
            }
        }
        return i;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("projectList", (Serializable) projectList);
        try{
            getSupportFragmentManager().putFragment(outState,"MainFragment",mainFrag);
        }catch (Exception e){}
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            projectList = (List<Project>) savedInstanceState.getSerializable("projectList");
            try{
                mainFrag = (MainFragment) getSupportFragmentManager().getFragment(savedInstanceState,"MainFragment");
                mainFrag.setProjectList(projectList);
            }catch(Exception e){
            }
            try{
                projecComponentFragment = (ProjectComponentsFragment) getSupportFragmentManager().getFragment(savedInstanceState,"ProjectComponentsFragment");
            }catch(Exception e){
            }
        }
    }

    @Override
    public void onMainFragmentListUpdate(String action, int position, Project oProject) {

    }

    public void setAdapter(TabAdapter adapter) {
        this.tabAdapter = adapter;
    }

    @Override
    public void onConfirmImportPiece(Project oProject, List<Piece> mList) {
        projecComponentFragment = (ProjectComponentsFragment) manager.findFragmentByTag("ProjectComponentsFragment");
        for(Piece oPiece:mList) {
            try {

                //projecComponentFragment.updatePrintsRecyclerView(oPiece, "NEW");
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onConfirmImportConfig(Project oProject, List<Configurations> mList) {
        if(mList != null) {
            projecComponentFragment = (ProjectComponentsFragment) manager.findFragmentByTag("ProjectComponentsFragment");
            for (Configurations oConf : mList) {
                try {
                    Configurations oConf2 = new Configurations();
                    oConf2.setValues(oConf);
                    int i = 0;
                    if(oProject.getConfigurationsList()!= null) {
                        for (Configurations obj : oProject.getConfigurationsList()) {
                            if (obj.getcNomeConfig().equals(oConf.getcNomeConfig())) {
                                i++;
                            }
                        }
                    }
                    String cNome = oConf.getcNomeConfig();
                    if (i > 0) {
                        cNome += "(" + i + ")";
                    }
                    oConf2.setcNomeConfig(cNome);
                    oConf2.setiIDProjeto(oProject.getiIDProjeto());
                    db.insertConfigurations(oConf2);
                    Boolean check = false;
                    if(projecComponentFragment != null) {
                        if(projecComponentFragment.isVisible()) check = true;
                    }
                    if(check){
                        projecComponentFragment.updateConfigRecyclerView(oConf2, MainActivity.ACTION_NEWFAB);
                    }else{
                        //Significa que importou por outro meio (create project)
                        if(oProject.getConfigurationsList() == null){
                            oProject.setConfigurationsList(new ArrayList<Configurations>());
                        }
                        oProject.getConfigurationsList().add(oConf2);
                    }
                } catch (Exception e) {

                }
            }
            Toast.makeText(this, R.string.item_save_success, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfirmImportMaterial(Project oProject, List<TiposMateriais> mList) {
        if (mList != null) {
            projecComponentFragment = (ProjectComponentsFragment) manager.findFragmentByTag("ProjectComponentsFragment");
            for (TiposMateriais oMaterial : mList) {
                try {
                    TiposMateriais oMaterial2 = new TiposMateriais();
                    oMaterial2.setValues(oMaterial);
                    int i = 0;
                    if(oProject.getMateriaisList() != null) {
                        for (TiposMateriais obj : oProject.getMateriaisList()) {
                            if (obj.getcNome().equals(oMaterial.getcNome())) {
                                i++;
                            }
                        }
                    }
                    String cNome = oMaterial.getcNome();
                    if (i > 0) {
                        cNome += "(" + i + ")";
                    }
                    oMaterial2.setcNome(cNome);
                    oMaterial2.setiIDProjeto(oProject.getiIDProjeto());
                    db.insertTipoMaterial(oMaterial2);
                    Boolean check = false;
                    if(projecComponentFragment != null) {
                        if(projecComponentFragment.isVisible()) {
                            check = true;
                        }
                    }
                    if(check){
                        projecComponentFragment.updateMaterialRecyclerView(oMaterial2, MainActivity.ACTION_NEWFAB);
                    }else{
                        if(oProject.getMateriaisList() == null){
                            oProject.setMateriaisList(new ArrayList<TiposMateriais>());
                        }
                        oProject.getMateriaisList().add(oMaterial2);
                    }
                } catch (Exception e) {

                }
            }
            Toast.makeText(this, R.string.item_save_success, Toast.LENGTH_SHORT).show();
        }
    }
}