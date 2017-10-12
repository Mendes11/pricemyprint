package br.com.cozinheirodelivery.pricemyprint.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Activities.MainActivity;
import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.DeleteProjectConfirmationDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.MessageDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewProjectDialog;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.PhotoManager;
import br.com.cozinheirodelivery.pricemyprint.Objects.PricingDetails;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.Adapters.ProjectAdapter.ProjectAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.RecyclerTouchListener;
import br.com.cozinheirodelivery.pricemyprint.Objects.SharedPrefs;
import br.com.cozinheirodelivery.pricemyprint.R;

import static android.app.Activity.RESULT_OK;

// TODO: 21/12/2016 Ver como faz aquela camera/galeria que seleciona parte da imagem para servir de perfil. 
/**
 * Created by Mendes on 20/12/2016.
 */

public class MainFragment extends Fragment {
    private List<Project> projectList;
    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private MainFragmentListener mListener;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private Project oClickedProject = null;
    //** Método do multi-select
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private String mCurrentPhotoPath;
    private PhotoManager mPhotoManager;
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    // Teste de Interface Genérica através de ID
    public interface MainFragmentListener{
        public void onMainFragmentListUpdate(String action, int position, Project oProject);
    }
    public static MainFragment newInstance(List<Project> projectList){
        MainFragment frag = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable("projectList",(Serializable) projectList);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (MainFragmentListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement the Listener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main,container,false);
        if(getArguments()!= null){
            projectList = (List<Project>) getArguments().getSerializable("projectList");
        }
        if(savedInstanceState!= null){
            if(savedInstanceState.containsKey("projectList")){
                projectList = (List<Project>) savedInstanceState.getSerializable("projectList");
            }
            if(savedInstanceState.containsKey("oClickedProject")){
                oClickedProject = (Project) savedInstanceState.getParcelable("oClickedProject");
            }
            if(savedInstanceState.containsKey("mPhotoManager")){
                mPhotoManager = (PhotoManager) savedInstanceState.getParcelable("mPhotoManager");
            }
        }
        if(projectList == null) projectList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        adapter = new ProjectAdapter(getActivity().getApplicationContext(), projectList, new ProjectAdapter.AdapterListener() {
            @Override
            public void onImageClickListener(final Project oProject) {
                oClickedProject = oProject;
                // Chama o Dialog que irá perguntar se é para galeria ou para Câmera.
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                List<String> list = new ArrayList<>();
                list.add(getString(R.string.take_picture));
                list.add(getString(R.string.select_picture));
                list.add(getString(R.string.remove_picture));
                dialog.setItems(list.toArray(new CharSequence[list.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                String imageFileName = "Project_"+oClickedProject.getcNome()+oClickedProject.getiIDProjeto();
                                mCurrentPhotoPath = PhotoManager.preparePath(getActivity(),imageFileName);
                                callTakePhoto();
                                break;
                            case 1:
                                imageFileName = "Project_"+oClickedProject.getcNome()+oClickedProject.getiIDProjeto();
                                mCurrentPhotoPath = PhotoManager.preparePath(getActivity(),imageFileName);
                                callSelectPhoto();
                                break;
                            case 2:

                                try {
                                    oProject.setcPicturePath(null);
                                    new DB(getActivity()).insertProject(oProject);
                                    adapter.notifyItemChanged(projectList.indexOf(oProject));
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), R.string.default_error, Toast.LENGTH_SHORT).show();
                                }

                                break;
                            default:

                        }
                    }
                });
                dialog.show();
            }

            @Override
            public void onClickListener(int position) {
                if(actionMode!=null){
                    toggleSelection(position);
                }else{

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    ProjectComponentsFragment frag = (ProjectComponentsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("ProjectComponentsFragment");
                    if(frag == null) frag = ProjectComponentsFragment.newInstance(projectList.get(position));
                    transaction.replace(R.id.container, frag, "ProjectComponentsFragment");
                    transaction.addToBackStack("MainFragment");
                    transaction.commit();
                }
            }

            @Override
            public void onLongClickListener(int position) {
                if (actionMode == null) {
                    AppCompatActivity activity = (AppCompatActivity)getActivity();
                    actionMode = activity.startSupportActionMode(actionModeCallback);

                }

                toggleSelection(position);
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(actionMode!=null){
                    toggleSelection(position);
                }else{
                    if(view != v.findViewById(R.id.imagem)) {

                        ProjectComponentsFragment frag = ProjectComponentsFragment.newInstance(projectList.get(position));
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, frag, "ProjectComponentsFragment");
                        transaction.addToBackStack("MainFragment");
                        transaction.commit();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (actionMode == null) {
                    AppCompatActivity activity = (AppCompatActivity)getActivity();
                    actionMode = activity.startSupportActionMode(actionModeCallback);

                }

                toggleSelection(position);
            }
        }));*/
        adapter.notifyDataSetChanged();
        return v;
    }

    public void updateRecyclerView(Project oProject,String action) {
        switch (action){
            case "UPDATE":
                int position = projectList.indexOf(oProject);
                adapter.notifyItemChanged(projectList.indexOf(oProject));
                //mListener.onMainFragmentListUpdate("UPDATE",position,oProject);
            break;
            case "INSERT":
                this.projectList.add(oProject);
                adapter.notifyItemInserted(projectList.size());
                break;
            case "DELETE":

                break;
            default:
        }
    }
    private void callSelectPhoto(){
        if(PhotoManager.checkWritePermission(getActivity())) {
            Intent getPicture = null;
            try {
                getPicture = PhotoManager.prepareSelectPhotoIntent(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (getPicture != null) {
                startActivityForResult(getPicture, 1);//one can be replaced with any action code
            } else {

            }
        }else{
            PhotoManager.askWritePermission(MainFragment.this,PhotoManager.MY_PERMISSIONS_REQUEST_SELECT_PHOTO);
        }
    }
    private void callTakePhoto(){
        if(PhotoManager.checkWritePermission(getActivity())) {
            Intent takePicture = null;
            try {
                takePicture = PhotoManager.preparePhotoIntent(getActivity(), mCurrentPhotoPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(takePicture != null){
                startActivityForResult(takePicture,0);
            }else{

            }
        }else{
            PhotoManager.askWritePermission(MainFragment.this,PhotoManager.MY_PERMISSIONS_REQUEST_TAKE_PHOTO);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                onResultFromIntent(PhotoManager.onResult(getActivity(),mCurrentPhotoPath,data,requestCode));
            } catch (Exception e) {
                //@// TODO: 08/08/2017 Jogar um message aqui. 
                e.printStackTrace();
            }
        }
    }


    public void onResultFromIntent(File file){
        oClickedProject.setcPicturePath(file.getPath());
        DB db = new DB(getActivity());
        try {
            db.insertProject(oClickedProject);
            adapter.notifyItemChanged(projectList.indexOf(oClickedProject));
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.default_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if(requestCode == PhotoManager.MY_PERMISSIONS_REQUEST_TAKE_PHOTO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }else if(requestCode == PhotoManager.MY_PERMISSIONS_REQUEST_SELECT_PHOTO){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
        return;


    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            if(count == 1){
                if(actionMode.getMenu().size()== 2) {
                    actionMode.getMenu().clear();
                    actionMode.getMenuInflater().inflate(R.menu.list_toolbar_selected_single, actionMode.getMenu());
                }
            }else{
                actionMode.getMenu().clear();
                actionMode.getMenuInflater().inflate(R.menu.list_toolbar_selected,actionMode.getMenu());
            }
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main,menu);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if(adapter.getSelectedItems().size()==0) {
                mode.getMenuInflater().inflate(R.menu.list_toolbar_selected_single, menu);
            }else{
                mode.getMenuInflater().inflate(R.menu.list_toolbar_selected, menu);
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ic_remove:
                    List<Project> mSelectedList = new ArrayList<>();
                    final List<Integer> mSelectedIndexes = adapter.getSelectedItems();
                    for(int i:mSelectedIndexes){
                        mSelectedList.add(projectList.get(i));
                    }
                    DeleteProjectConfirmationDialog p = new DeleteProjectConfirmationDialog(getActivity(), mSelectedList, new DeleteProjectConfirmationDialog.onProjectDeleteConfirmationListener() {
                        @Override
                        public void onDeleteConfirmationSuccess() {
                            int start = mSelectedIndexes.get(0);
                            int j = 0;
                            for(int i: mSelectedIndexes){
                                projectList.remove(i-j);
                                adapter.notifyItemRemoved(i-j);
                                j++;
                            }
                        }

                        @Override
                        public void onDeleteConfirmationFailure(String error) {

                        }
                    });
                    p.showDialog();
                    mode.finish();
                    return true;
                case R.id.ic_edit:
                    Project project = projectList.get(adapter.getSelectedItems().get(0));
                    DialogFragment dialog = NewProjectDialog.newInstance(projectList,project);
                    dialog.show(getActivity().getSupportFragmentManager(),"EditDialog");
                    mode.finish();
                    return true;
                case R.id.ic_details:
                    List<Project> mList = new ArrayList<>();
                    for(int i : adapter.getSelectedItems()){
                        mList.add(projectList.get(i));
                    }
                    PricingDetails oPriceDetail = Calculator.getProjectDetails(getActivity(),mList);
                    MessageDialog d = new MessageDialog();
                    d.showMessageDialog(getActivity(),getString(R.string.menu_detalhes),oPriceDetail.generateMessageString(getActivity()));
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(projectList != null){
            outState.putSerializable("projectList", (Serializable) projectList);
        }
        if(oClickedProject != null){
            outState.putParcelable("oClickedProject",oClickedProject);
        }
        if(mCurrentPhotoPath != null){
            outState.putString("mCurrentPhotoPath",mCurrentPhotoPath);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            projectList = (List<Project>) savedInstanceState.getSerializable("projectList");
            if(savedInstanceState.containsKey("mCurrentPhotoPath")){
                mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            }
        }
    }

}
