package apps.develop.texttileapp.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import apps.develop.texttileapp.Model.CustomerModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.databinding.ItemCustomerBinding;
import apps.develop.texttileapp.databinding.ItemStockBinding;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {


    Context context;
    ArrayList<CustomerModel> list;

    ItemCustomerBinding binding;

    public CustomerAdapter(Context context, ArrayList<CustomerModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemCustomerBinding.inflate(LayoutInflater.from(context));

        return new CustomerAdapter.ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {

        CustomerModel model = list.get(position);

        binding.customerName.setText(model.getCustomerName());

        binding.pendingAmount.setText("₹"+model.getAmount()+".0/-");
        binding.contactNo.setText(model.getCustomerNumber());
        binding.dueDate.setText(model.getDueDate());

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteDialogue(position,model);
            }
        });






    }
    private void deleteDialogue(int position,CustomerModel model){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete item");
        builder.setMessage("Are you sure want to delete cart item?");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", (dialog, which) -> {
            notifyItemRemoved(position);
            FirebaseDatabase.getInstance().getReference().child("Customers")
                    .child(model.getId())
                    .removeValue();

            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCustomerBinding binding;
        public ViewHolder(@NonNull ItemCustomerBinding stockBinding) {
            super(stockBinding.getRoot());
            binding = stockBinding;
        }
    }

}
