package br.com.cozinheirodelivery.pricemyprint.Adapters.ConfigurationAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.com.cozinheirodelivery.pricemyprint.Adapters.PrintAdapter.PrintAdapter;
import br.com.cozinheirodelivery.pricemyprint.Adapters.SelectableAdapter;
import br.com.cozinheirodelivery.pricemyprint.Objects.Configurations;
import br.com.cozinheirodelivery.pricemyprint.R;

/**
 * Created by Mendes on 31/12/2016.
 */

public class ConfigurationsAdapter extends SelectableAdapter<ConfigurationsAdapter.ViewHolder>{
    List<Configurations> configurationsList;
    Context c;
    onAdapterListener mListener;
    public interface onAdapterListener{
        void onClickListener(int position);
        void onLongClickListener(int position);
    }
    public ConfigurationsAdapter(Context c, List<Configurations> configurationsList,onAdapterListener mListener){
        this.c = c;
        this.configurationsList = configurationsList;
        this.mListener = mListener;
    }
    @Override
    public ConfigurationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_configurations,parent,false);
        return new ConfigurationsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConfigurationsAdapter.ViewHolder holder, int position) {
        Configurations oConf = configurationsList.get(position);
        holder.text.setText(oConf.getcNomeConfig());
        holder.mSelectedOverlay.setBackgroundResource(isSelected(position)? R.color.highlited_color:R.drawable.ripple_custom);
    }

    @Override
    public int getItemCount() {
        return configurationsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView text;
        LinearLayout linear;
        View mSelectedOverlay;
        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.cNome);
            linear = (LinearLayout) itemView.findViewById(R.id.linear);
            mSelectedOverlay = itemView.findViewById(R.id.linear);
            linear.setOnClickListener(this);
            linear.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClickListener(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mListener.onLongClickListener(getAdapterPosition());
            return true;
        }
    }
}
