package pricemypiece.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.sawyer.advadapters.widget.PatchedExpandableListAdapter;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import br.com.cozinheiro.pricemypiece.Activities.MainActivity;
import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.Database.DB_PIECE;
import br.com.cozinheiro.pricemypiece.Dialogs.NewPieceDialog;
import br.com.cozinheiro.pricemypiece.Objects.CustomExpandableListAdvancedAdapter;
import br.com.cozinheiro.pricemypiece.Objects.Piece;
import br.com.cozinheiro.pricemypiece.Objects.Project;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 29/08/2016.
 */
public class MainFragment extends ExpandableListFragment {
    private static final String STATE_LIST = "StateList_CHILD";
    private static final String STATE_LIST_PARENT = "StateList_PARENT";
    private EventListener mEventListener;
    private ActionMode mActionMode;
    List<Project> projectList;
    DB db;
    public static MainFragment newInstance() {
        return new MainFragment();
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
        }
        //d.dismiss();
    }

    private List<Piece> converteLista(){
        List<Piece> list = new ArrayList<>();
        if(projectList != null){
            for(Project obj : projectList){
                if(obj.getChildList() != null) {
                    for (Piece objPiece : obj.getChildList()) {
                        objPiece.setOproject(obj);
                        list.add(objPiece);
                    }
                }
            }
        }
        return list;
    }
    public interface EventListener {
        PatchedExpandableListAdapter.ChoiceMode getChoiceMode();

        public void onAdapterCountUpdated();
    }

    public ActionMode getmActionMode() {
        return mActionMode;
    }

    public void setmActionMode(ActionMode mActionMode) {
        this.mActionMode = mActionMode;
    }

    @Override
    public CustomExpandableListAdvancedAdapter getListAdapter() {
        return (CustomExpandableListAdvancedAdapter) super.getListAdapter();
    }

    @Override
    public void setListAdapter(ExpandableListAdapter adapter) {
        if (adapter instanceof CustomExpandableListAdvancedAdapter) {
            super.setListAdapter(adapter);
        } else {
            throw new ClassCastException(
                    "Adapter must be of type " + CustomExpandableListAdvancedAdapter.class.getSimpleName());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventListener) {
            mEventListener = (EventListener) context;
        } else {
            throw new ClassCastException(
                    "Activity must implement " + EventListener.class.getSimpleName());
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                int childPosition, long id) {
        //Only modal should update child click.  All others should just cause activation state change
        if (!mEventListener.getChoiceMode().isModal())
            return false;

        //getListAdapter().update(groupPosition, childPosition, piece);
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        db = new DB(getContext());
        loadData();
        List<Piece> list = null;
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList(STATE_LIST);
        } else {
            list = converteLista();
        }
        setListAdapter(new CustomExpandableListAdvancedAdapter(getContext(), list, new CustomExpandableListAdvancedAdapter.customButtonListener() {
            @Override
            public void onButtonClickListener(View v, int position, Long iIDProjeto) {// Chama um dialog de adicionar Peça
                Project obj = (Project) getListAdapter().getGroup(position);
                DialogFragment dialog = NewPieceDialog.newInstance(obj);
                dialog.show(getActivity().getSupportFragmentManager(),"NewPieceDialog");
            }
        }));
        PatchedExpandableListAdapter.ChoiceMode choiceMode = mEventListener.getChoiceMode();
        if (choiceMode.isModal())
            getListAdapter().setMultiChoiceModeListener(new DemoChoiceModeListener());
        getListAdapter().setChoiceMode(choiceMode);
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_LIST, getListAdapter().getList());
    }

    private void onRetainItemsClicked(List<Piece> items) {
        getListAdapter().retainAll(items);
    }

    private void onRemoveItemsClicked(List<Piece> items) {
        //Easy way to test both remove methods
        if (items.size() == 1) {
            getListAdapter().remove(items.iterator().next());
        } else {
            getListAdapter().removeAll(items);
        }
    }

    private class DemoChoiceModeListener implements
            PatchedExpandableListAdapter.ChoiceModeListener {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            boolean result;
            Integer[] groupPositions;
            Long[] packedPositions;
            List<Piece> pieces;

            switch (item.getItemId()) {
                case R.id.menu_context_remove:
                    packedPositions = getListAdapter().getCheckedChildPositions();
                    pieces = new ArrayList<>(packedPositions.length);
                    for (Long packedPos : packedPositions) {
                        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                        int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                        pieces.add(getListAdapter().getChild(groupPosition, childPosition));
                    }
                    if (pieces.size() == 0) {    //Will only occur for single_modal mode
                        groupPositions = getListAdapter().getCheckedGroupPositions();
                        for (Integer groupPos : groupPositions) {
                            pieces.addAll(getListAdapter().getGroupChildren(groupPos));
                        }
                    }
                    onRemoveItemsClicked(pieces);
                    mode.finish();
                    result = true;
                    break;

                case R.id.menu_context_edit:
                    /*packedPositions = getListAdapter().getCheckedChildPositions();
                    pieces = new ArrayList<>(packedPositions.length);
                    for (Long packedPos : packedPositions) {
                        int groupPosition = ExpandableListView.getPackedPositionGroup(packedPos);
                        int childPosition = ExpandableListView.getPackedPositionChild(packedPos);
                        pieces.add(getListAdapter().getChild(groupPosition, childPosition));
                    }
                    if (pieces.size() == 0) {    //Will only occur for single_modal mode
                        groupPositions = getListAdapter().getCheckedGroupPositions();
                        for (Integer groupPos : groupPositions) {
                            pieces.addAll(getListAdapter().getGroupChildren(groupPos));
                        }
                    }
                    onRetainItemsClicked(pieces);
                    mode.finish();
                    result = true;*/
                    result = true;
                    break;

                default:
                    result = false;
                    break;
            }

            if (result && mEventListener != null) {
                mEventListener.onAdapterCountUpdated();
            }
            return result;
        }

        @Override
        public void onChildCheckedStateChanged(ActionMode mode, int groupPosition, long groupId,
                                               int childPosition, long childId, boolean checked) {
            mode.setTitle(getListAdapter().getCheckedChildCount() + getString(R.string.desc_selected));
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            setmActionMode(mode);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_array, menu);
            mode.setTitle(getListAdapter().getCheckedChildCount() + getString(R.string.desc_selected));
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public void onGroupCheckedStateChanged(ActionMode mode, int groupPosition, long groupId,
                                               boolean checked) {
            //If group is expanded, then the onChildCheckedStateChanged method will be invoked. Which
            //means it'll safely take care of updating our screen.
            if (getExpandableListView().isGroupExpanded(groupPosition)) return;
            mode.setTitle(getListAdapter().getCheckedChildCount() + getString(R.string.desc_selected));
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}

