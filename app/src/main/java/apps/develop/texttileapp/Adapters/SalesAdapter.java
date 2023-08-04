package apps.develop.texttileapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apps.develop.texttileapp.Model.CartModel;
import apps.develop.texttileapp.databinding.ItemSalesCountBinding;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    private Context context;
    ArrayList<CartModel> list;

    ItemSalesCountBinding binding;

    public SalesAdapter(Context context, ArrayList<CartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSalesCountBinding.inflate(LayoutInflater.from(context));

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartModel model = list.get(position);
        if (model !=null){

            binding.serialNo.setText(String.valueOf(position+1));
            binding.itemName.setText(model.getName());
            binding.totalCount.setText(String.valueOf(model.getQuantity()));

            int totalValue = model.getQuantity() * model.getPrice();
            binding.totalPrice.setText("₹"+totalValue+".0/-");


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull ItemSalesCountBinding countBinding) {
            super(countBinding.getRoot());
            binding = countBinding;
        }
    }
}
