package br.com.cozinheirodelivery.pricemyprint.Adapters.MaterialAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Adapters.ConfigurationAdapter.ConfigurationsAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.SelectableAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.TiposMateriais;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 31/12/2016.
 */

public class MaterialAdapter  extends SelectableAdapter<MaterialAdapter.ViewHolder> {
    List<TiposMateriais> materiaisList;
    onMaterialListener mListener;
    Context c;
    public MaterialAdapter(Context c, List<TiposMateriais> materiaisList, onMaterialListener mListener){
        this.mListener = mListener;
        this.materiaisList = materiaisList;
        this.c = c;
    }
    public interface onMaterialListener{
        void onMaterialClickListener(int position);
        void onMaterialLongClickListener(int position);
    }
    @Override
    public MaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_material,parent,false);
        return new MaterialAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MaterialAdapter.ViewHolder holder, int position) {
        TiposMateriais obj = materiaisList.get(position);
        holder.cNome.setText(obj.getcNome());
        holder.fDiametro.setText(obj.getfDiametro()+" mm");
        holder.fDensidade.setText(obj.getfDensidade()+ "g/cm³");
        holder.fPreco.setText(NumberFormat.getCurrencyInstance().format(obj.getfPreco()));
        holder.linear.setBackgroundResource(isSelected(position)? R.color.highlited_color:R.drawable.ripple_custom);
    }

    @Override
    public int getItemCount() {
        return materiaisList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView cNome,fDensidade,fDiametro,fPreco;
        LinearLayout linear;
        public ViewHolder(View itemView) {
            super(itemView);
            cNome = (TextView) itemView.findViewById(R.id.nome);
            fDensidade = (TextView) itemView.findViewById(R.id.densidade);
            fDiametro = (TextView) itemView.findViewById(R.id.diametro);
            fPreco = (TextView) itemView.findViewById(R.id.preco);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            linear.setOnClickListener(this);
            linear.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onMaterialClickListener(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {

            mListener.onMaterialLongClickListener(getAdapterPosition());
            return true;
        }
    }
}
