package br.com.cozinheirodelivery.pricemyprint.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
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
import br.com.cozinheirodelivery.pricemyprint.Adapters.PrintAdapter.PrintAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.RecyclerTouchListener;
import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.DeletePieceConfirmationDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.DeleteProjectConfirmationDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.MessageDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewPieceDialog;
import br.com.cozinheirodelivery.pricemyprint.Dialogs.NewProjectDialog;
import br.com.cozinheirodelivery.pricemyprint.Objects.Calculator;
import br.com.cozinheirodelivery.pricemyprint.Objects.PhotoManager;
import br.com.cozinheirodelivery.pricemyprint.Objects.Piece;
import br.com.cozinheirodelivery.pricemyprint.Objects.PricingDetails;
import br.com.cozinheirodelivery.pricemyprint.Objects.Project;
import br.com.cozinheirodelivery.pricemyprint.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrintsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrintsFragment extends Fragment {
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    Project oProject;
    List<Piece> pieceList;
    RecyclerView recyclerView;
    PrintAdapter adapter;
    Piece oClickedPiece;
    ActionMode actionMode;
    private String mCurrentPhotoPath;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    public PrintsFragment() {
        // Required empty public constructor
    }

    public static PrintsFragment newInstance(Project oProject) {
        PrintsFragment fragment = new PrintsFragment();
        Bundle args = new Bundle();
        args.putParcelable("oProject",oProject);
        args.putSerializable("pieceList", (Serializable) oProject.getChildList()); //Só para não ter de refazer muita coisa...
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pieceList = (List<Piece>) getArguments().getSerializable("pieceList");
            oProject = getArguments().getParcelable("oProject");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prints, container, false);
        if(pieceList == null){
            pieceList = new ArrayList<>();
        }
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("pieceList")){
                pieceList = (List<Piece>) savedInstanceState.getSerializable("pieceList");
            }
        }
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        setAdapter();
        return v;
    }

    private void setAdapter(){
        adapter = new PrintAdapter(getActivity().getApplicationContext(), pieceList, new PrintAdapter.AdapterListener() {
            @Override
            public void onImageClickListener(final Piece oPiece) {
                oClickedPiece = oPiece;
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
                                String imageFileName = "Piece_"+oClickedPiece.getcNome()+oClickedPiece.getiIDPiece();
                                mCurrentPhotoPath = PhotoManager.preparePath(getActivity(),imageFileName);
                                callTakePhoto();
                                break;
                            case 1:
                                imageFileName = "Piece_"+oClickedPiece.getcNome()+oClickedPiece.getiIDPiece();
                                mCurrentPhotoPath = PhotoManager.preparePath(getActivity(),imageFileName);
                                callSelectPhoto();
                                break;
                            case 2:

                                try {
                                    oPiece.setcImagePath(null);
                                    new DB(getActivity()).insertPiece(oPiece);
                                    adapter.notifyItemChanged(pieceList.indexOf(oPiece));
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
                    Piece piece = pieceList.get(position);
                    DialogFragment dialog = NewPieceDialog.newInstance(oProject,piece);
                    dialog.show(getActivity().getSupportFragmentManager(),"EditDialog");
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

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
        adapter.notifyDataSetChanged();
    }
    public void updateRecyclerView(Piece oPiece,String action) {
        switch (action){
            case "UPDATE":
                int position = pieceList.indexOf(oPiece);
                adapter.notifyItemChanged(pieceList.indexOf(oPiece));
                break;
            case "INSERT":
                adapter.notifyItemInserted(pieceList.size());
                break;
            case "DELETE":

                break;
            default:
        }
    }

    public void callSelectPhoto(){
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
            PhotoManager.askWritePermission(PrintsFragment.this,PhotoManager.MY_PERMISSIONS_REQUEST_SELECT_PHOTO);
        }
    }
    public void callTakePhoto(){
        if(PhotoManager.checkWritePermission(getActivity())) {
            Intent takePicture = null;
            try {
                takePicture = PhotoManager.preparePhotoIntent(getActivity(), mCurrentPhotoPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (takePicture != null) {
                startActivityForResult(takePicture, 0);
            } else {

            }
        }else{
            PhotoManager.askWritePermission(PrintsFragment.this,PhotoManager.MY_PERMISSIONS_REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                onResultFromIntent(PhotoManager.onResult(getActivity(),mCurrentPhotoPath,data,requestCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onResultFromIntent(File file){
        oClickedPiece.setcImagePath(file.getPath());
        DB db = new DB(getActivity());
        try {
            db.insertPiece(oClickedPiece);
            adapter.notifyItemChanged(pieceList.indexOf(oClickedPiece));
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.default_error, Toast.LENGTH_SHORT).show();
        }
    }
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if(requestCode == PhotoManager.MY_PERMISSIONS_REQUEST_TAKE_PHOTO) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callTakePhoto();
            }
        }else if(requestCode == PhotoManager.MY_PERMISSIONS_REQUEST_SELECT_PHOTO){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callSelectPhoto();
            }
        }
        return;


    }

    public void refreshRecyclerView() {
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = PrintsFragment.ActionModeCallback.class.getSimpleName();

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
                    List<Piece> mSelectedList = new ArrayList<>();
                    final List<Integer> mSelectedIndexes = adapter.getSelectedItems();
                    for(int i:mSelectedIndexes){
                        mSelectedList.add(pieceList.get(i));
                    }
                    DeletePieceConfirmationDialog p = new DeletePieceConfirmationDialog(getActivity(), mSelectedList, new DeletePieceConfirmationDialog.onPieceDeleteConfirmationListener() {
                        @Override
                        public void onDeleteConfirmationSuccess() {
                            int start = mSelectedIndexes.get(0);
                            int j = 0;
                            for(int i: mSelectedIndexes){
                                pieceList.remove(i-j);
                                adapter.notifyItemRemoved(i-j);
                                j++;
                            }
                        }
                    });
                    p.showDialog();
                    mode.finish();
                    return true;
                case R.id.ic_edit:
                    Piece piece = pieceList.get(adapter.getSelectedItems().get(0));
                    DialogFragment dialog = NewPieceDialog.newInstance(oProject,piece);
                    dialog.show(getActivity().getSupportFragmentManager(),"EditDialog");
                    mode.finish();
                    return true;
                case R.id.ic_details:
                    List<Piece> mList = new ArrayList<>();
                    for(int i : adapter.getSelectedItems()){
                        mList.add(pieceList.get(i));
                    }
                    PricingDetails oPriceDetail = Calculator.getDetailsString(getActivity(),mList);
                    MessageDialog d = new MessageDialog();
                    d.showMessageDialog(getActivity(),getString(R.string.menu_detalhes),oPriceDetail.generateMessageString(getActivity()));
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
    public void notifyFABClick(Project oProject){

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(pieceList != null){
            outState.putSerializable("pieceList", (Serializable) pieceList);
            outState.putParcelable("oProject",oProject);
        }
        if(oClickedPiece != null){
            outState.putParcelable("oClickedPiece",oClickedPiece);
        }
        if(mCurrentPhotoPath != null){
            outState.putString("mCurrentPhotoPath",mCurrentPhotoPath);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("pieceList")){
                pieceList = (List<Piece>) savedInstanceState.getSerializable("pieceList");
                oProject = savedInstanceState.getParcelable("oProject");
            }
            if(savedInstanceState.containsKey("oClickedPiece")){
                oClickedPiece = (Piece) savedInstanceState.getParcelable("oClickedPiece");
            }
            if(savedInstanceState.containsKey("mCurrentPhotoPath")){
                mCurrentPhotoPath = savedInstanceState.getString("mCurrentPhotoPath");
            }
        }
    }
}
