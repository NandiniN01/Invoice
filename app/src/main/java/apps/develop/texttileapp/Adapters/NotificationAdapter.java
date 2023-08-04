package apps.develop.texttileapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apps.develop.texttileapp.Model.NotifyModel;
import apps.develop.texttileapp.databinding.ItemCustomerBinding;
import apps.develop.texttileapp.databinding.NotifyCustomerBinding;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotifyModel> list;

    NotifyCustomerBinding binding;

    public NotificationAdapter(Context context, ArrayList<NotifyModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = NotifyCustomerBinding.inflate(LayoutInflater.from(context));

        return new ViewHolder(binding);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotifyModel model = list.get(position);
        if (model !=null){
            if (model.getType().equals("customer")){
                binding.cardCustomer.setVisibility(View.VISIBLE);
                binding.customerDetailText.setText("Hello Mr."+model.getUsername()+"\n"+"Your pending due date will be " +
                        "expired by today night. Please pay your Rs.500");
            }else if (model.getType().equals("supplier")){

                binding.supplierCard.setVisibility(View.VISIBLE);
                binding.supplierData.setText("Your due date will be end by tonight. You need to pay ₹"+model.getAmount()+" " +
                        "to "+model.getUsername()+" as soon as possible.");


            }else if (model.getType().equals("stock")){
                binding.cardStock.setVisibility(View.VISIBLE);

                binding.stockText.setText("This item has been reach to minimum quantity\nItem Name: "+model.getItemName());


            }


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NotifyCustomerBinding binding;
        public ViewHolder(@NonNull NotifyCustomerBinding customerBinding) {
            super(customerBinding.getRoot());
            binding = customerBinding;
        }
    }
}
