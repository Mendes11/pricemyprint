package br.com.cozinheirodelivery.pricemyprint.Objects;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Database.DB;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 24/08/2016.
 */

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    List<Project> list;
    LayoutInflater inflator;
    customButtonListener btnListener;
    Context c;
    private SparseBooleanArray mSelectedItemsIds;

    public interface customButtonListener{
        public void onButtonClickListener(View v, int position, Long iIDProjeto);
    }
    public void setCustomButtonListner(customButtonListener listener) {
        this.btnListener = listener;
    }
    public CustomExpandableListAdapter(Context c,List<Project> list,customButtonListener btnListener){
        inflator = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        this.c = c;
        this.btnListener = btnListener;
        this.list = list;
    }
    public void setList(List<Project> projectList){
        this.list = projectList;
    }
    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(list.get(groupPosition).getChildList()!=null) {
            return list.get(groupPosition).getChildList().size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return list.get(groupPosition).getiIDProjeto();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return list.get(groupPosition).getChildList().get(childPosition).getiIDPiece();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewGroupHolder holder;
        if (convertView == null){
            holder = new ViewGroupHolder();
            convertView = inflator.inflate(R.layout.list_expandable_projeto, null);
            convertView.setTag(holder);
            holder.cNome = (TextView) convertView.findViewById(R.id.nome);
            holder.fPreco = (TextView) convertView.findViewById(R.id.preco);
            holder.iFilamento = (TextView) convertView.findViewById(R.id.comprimento);
            holder.fTempo = (TextView) convertView.findViewById(R.id.tempo);
            holder.btnAdd = (ImageButton) convertView.findViewById(R.id.btnAdd_new_piece);
        }else{
            holder = (ViewGroupHolder) convertView.getTag();
        }
        holder.cNome.setText(list.get(groupPosition).getcNome());
        //Fazer a soma dos comprimentos e preços das peças para informar aqui:
        Double fPreco = 0.0;
        Double fTempo = 0.0;
        int iFilamento = 0;
        Double fPeso = 0.0;
        if(list.get(groupPosition).getChildList() !=null) {
            for (Piece obj : list.get(groupPosition).getChildList()) {
                fPreco += Calculator.getPrice(c,obj);
                //fPreco += obj.getfPreco();
                fTempo += obj.fTempo;
                //iFilamento += obj.getiFilamento();
                fPeso += obj.getfGramas();
            }
        }
        //String preco = String.format("%.2f",fPreco);
        holder.fPreco.setText(NumberFormat.getCurrencyInstance().format(fPreco));
        int h,m;
        int tMin = (int) Math.round(fTempo*60);
        m = (int) tMin%60;
        h = (int) (tMin/60);

        holder.fTempo.setText(h+"h "+m+"min");
        holder.iFilamento.setText(String.format("%.2f",fPeso)+"g");
        //holder.iFilamento.setText(iFilamento+"mm");
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnListener != null) {
                    btnListener.onButtonClickListener(v, groupPosition, list.get(groupPosition).getiIDProjeto());
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildHolder holder;
        if (convertView == null){
            holder = new ViewChildHolder();
            convertView = inflator.inflate(R.layout.list_expandable_child_piece, null);
            convertView.setTag(holder);
            holder.cNome = (TextView) convertView.findViewById(R.id.nome);
            holder.cMaterial = (TextView) convertView.findViewById(R.id.material);
            holder.fPreco = (TextView) convertView.findViewById(R.id.preco);
            holder.fTempo = (TextView) convertView.findViewById(R.id.tempo);
            holder.iQuant = (TextView) convertView.findViewById(R.id.quantidade);
            holder.iFilamento = (TextView) convertView.findViewById(R.id.comprimento);
            holder.cConfiguracao = (TextView) convertView.findViewById(R.id.config);
        }else{
            holder = (ViewChildHolder) convertView.getTag();
        }

        Piece obj = list.get(groupPosition).getChildList().get(childPosition);

        holder.cNome.setText(obj.getcNome());
        holder.iFilamento.setText(String.format("%.2f",obj.getfGramas())+"g");
        //holder.iFilamento.setText(obj.getiFilamento()+"mm");
        int h,m;
        int tMin = (int) Math.round(obj.getfTempo()*60);
        m = (int) tMin%60;
        h = (int) (tMin/60);
        holder.fTempo.setText(h+"h "+m+"min");
        int qtd = obj.getiQuant();
        String quant = c.getString(R.string.quantidade,qtd);
        holder.iQuant.setText(quant);
        //String preco = String.format("%.2f",obj.getfPreco());
        //String preco = String.format("%.2f",Calculator.getPrice(c,obj));
        holder.fPreco.setText(NumberFormat.getCurrencyInstance().format(Calculator.getPrice(c,obj)));
        holder.cConfiguracao.setText(obj.getoConfigurations().getcNomeConfig());
        DB db = new DB(inflator.getContext());
        //List<TiposMateriais> listTipo = db.getTiposMateriais(DB_PIECE.Estrutura_Peca.COLUMN_MATERIAL_FK + " = ?", new String[]{String.valueOf(obj.getiIDTipoMaterial())}, null, null);
        holder.cMaterial.setText(obj.getiIDTipoMaterial().getcNome());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewGroupHolder{
        TextView cNome,fPreco,fTempo,iFilamento;
        ImageButton btnAdd;
    }
    class ViewChildHolder{
        TextView cNome,fPreco,fTempo,iQuant,iFilamento,cMaterial,cConfiguracao;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
    public Double getTotalPrice(){
        Double fPrice = 0.0;
        try {
            if (list != null) {
                for (Project parent : list) {
                    if (parent.getChildList() != null) {
                        for (Piece child : parent.getChildList()) {
                            fPrice += Calculator.getPrice(c, child);
                        }
                    }
                }
            }
        }catch(Exception e){
            return 0.0;
        }
        return fPrice;
    }
    public void selectView(int position, boolean value) {
        if(value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
