package pricemypiece.Objects;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sawyer.advadapters.widget.NFRolodexArrayAdapter;

import java.util.List;

import br.com.cozinheiro.pricemypiece.Database.DB;
import br.com.cozinheiro.pricemypiece.R;

/**
 * Created by Mendes on 29/08/2016.
 */
public class CustomExpandableListAdvancedAdapter extends NFRolodexArrayAdapter<Project,Piece> {
    List<Project> projectList;
    customButtonListener btnListener;

    public interface customButtonListener{
        public void onButtonClickListener(View v, int position, Long iIDProjeto);
    }

    public CustomExpandableListAdvancedAdapter(Context activity){
        super(activity);
    }
    public CustomExpandableListAdvancedAdapter(Context activity,List<Piece> list,List<Project> projectList,customButtonListener btnListener){
        super(activity,list);
        this.projectList = projectList;
        this.btnListener = btnListener;
    }
    public CustomExpandableListAdvancedAdapter(Context activity,List<Piece> list,customButtonListener btnListener){
        super(activity,list);
        this.btnListener = btnListener;
    }
    public List<Project> getParentList(){
        return projectList;
    }
    @NonNull
    @Override
    public Project createGroupFor(Piece childItem) {
        return childItem.getOproject();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).getiIDPiece();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getGroup(groupPosition).getiIDProjeto();
    }

    @NonNull
    @Override
    public View getChildView(@NonNull LayoutInflater inflater, int groupPosition, int childPosition, boolean isLastChild, View convertView, @NonNull ViewGroup parent) {
        ViewChildHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_expandable_child_piece,parent,false);
            holder = new ViewChildHolder();
            holder.cNome = (TextView) convertView.findViewById(R.id.nome);
            holder.cMaterial = (TextView) convertView.findViewById(R.id.material);
            holder.fPreco = (TextView) convertView.findViewById(R.id.preco);
            holder.fTempo = (TextView) convertView.findViewById(R.id.tempo);
            holder.iFilamento = (TextView) convertView.findViewById(R.id.comprimento);
            convertView.setTag(holder);
        }else{
            holder = (ViewChildHolder) convertView.getTag();
        }
        Piece obj = getChild(groupPosition,childPosition);
        holder.cNome.setText(obj.getcNome());
        holder.iFilamento.setText(obj.getiFilamento()+"mm");
        int h,m;
        int tMin = (int) Math.round(obj.getfTempo()*60);
        m = (int) tMin%60;
        h = (int) (tMin/60);
        holder.fTempo.setText(h+"h "+m+"min");
        String preco = String.format("%.2f",obj.getfPreco());
        holder.fPreco.setText("R$ "+preco);
        holder.cMaterial.setText(obj.getiIDTipoMaterial().getcNome());
        return convertView;
    }

    @NonNull
    @Override
    public View getGroupView(@NonNull LayoutInflater inflater, final int groupPosition, boolean isExpanded, View convertView, @NonNull ViewGroup parent) {
        ViewGroupHolder holder;
        if(convertView == null){
            holder = new ViewGroupHolder();
            convertView = inflater.inflate(R.layout.list_expandable_projeto,parent,false);
            holder.cNome = (TextView) convertView.findViewById(R.id.nome);
            holder.fPreco = (TextView) convertView.findViewById(R.id.preco);
            holder.iFilamento = (TextView) convertView.findViewById(R.id.comprimento);
            holder.fTempo = (TextView) convertView.findViewById(R.id.tempo);
            holder.btnAdd = (ImageButton) convertView.findViewById(R.id.btnAdd_new_piece);
            convertView.setTag(holder);
        }else{
            holder = (ViewGroupHolder) convertView.getTag();
        }
        final Project project = getGroup(groupPosition);
        List<Piece> list = null;
        try {
            list = getGroupChildren(groupPosition);

        }catch(Exception e){

        }
        holder.cNome.setText(getGroup(groupPosition).getcNome());
        //Fazer a soma dos comprimentos e preços das peças para informar aqui:
        Double fPreco = 0.0;
        Double fTempo = 0.0;
        int iFilamento = 0;
        if(list !=null) {
            for (Piece obj : list) {
                fPreco += obj.getfPreco();
                fTempo += obj.fTempo;
                iFilamento += obj.getiFilamento();
            }
        }
        String preco = String.format("%.2f", fPreco);
        holder.fPreco.setText("R$ "+preco);
        int h,m;
        int tMin = (int) Math.round(fTempo*60);
        m = (int) tMin%60;
        h = (int) (tMin/60);

        holder.fTempo.setText(h+"h "+m+"min");
        holder.iFilamento.setText(iFilamento+"mm");
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnListener != null) {
                    btnListener.onButtonClickListener(v, groupPosition, project.getiIDProjeto());
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
		/*
		 Any time choice mode is enabled, stable IDs should also be enabled. Otherwise, restoring
		 the activity from saved state may activate the incorrect item in the adapter. Additionally,
		 don't forget to have getGroupId() and getChildId() actually return unique and stable ids.
		 Else it defeats the purpose of enabling this feature.
		 */
        return true;
    }

    class ViewGroupHolder{
        TextView cNome,fPreco,fTempo,iFilamento;
        ImageButton btnAdd;
    }
    class ViewChildHolder{
        TextView cNome,fPreco,fTempo,iFilamento,cMaterial;
    }
}
