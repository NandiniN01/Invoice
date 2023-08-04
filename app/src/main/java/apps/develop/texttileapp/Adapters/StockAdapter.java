package apps.develop.texttileapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

import apps.develop.texttileapp.Model.NotifyModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.ItemStockBinding;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    Context context;
    ArrayList<StockModel> list;

    ItemStockBinding binding;

    public StockAdapter(Context context, ArrayList<StockModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemStockBinding.inflate(LayoutInflater.from(context));

        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StockModel model = list.get(position);

        binding.itemName.setText(model.getItemName());

        int quantity = model.getQuantity();
        int total = quantity * model.getCost_of_each();
        binding.totalAmount.setText("₹"+total+"/-");

        binding.quantity.setText(String.valueOf(model.getQuantity()));
        binding.txtEachCost.setText("₹"+model.getCost_of_each()+" for each");


        binding.btnMinus.setOnClickListener(v -> {
            if (model.getQuantity() > 1){
                model.setQuantity(model.getQuantity() - 1);
                int quantity12 = model.getQuantity();
                int total12 = quantity12 * model.getCost_of_each();
                binding.totalAmount.setText("₹"+ total12 +"/-");

                binding.quantity.setText(new StringBuilder().append(model.getQuantity()));
                updateFirebase(model);


            }
            else {
                deleteDialogue(position,model);

            }


        });



        binding.btnPlus.setOnClickListener(v -> {
            model.setQuantity(model.getQuantity() + 1);
            int quantity1 = model.getQuantity();
            int total1 = quantity1 * model.getCost_of_each();
            binding.totalAmount.setText("₹"+ total1 +"/-");

            binding.quantity.setText(new StringBuilder().append(model.getQuantity()));
            updateFirebase(model);
        });

        binding.imgDelete.setOnClickListener(v -> deleteDialogue(position,model));


        if (model.getQuantity().equals(model.getMinQuantity())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                saveStockInfo(model);
                Toast.makeText(context, model.getItemName()+" has low quantity! increase quantity", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveStockInfo(StockModel model) {
        NotifyModel notifyModel = new NotifyModel();
        notifyModel.setAmount("");
        notifyModel.setId(model.getId());
        notifyModel.setItemName(model.getItemName());
        notifyModel.setUsername("");
        notifyModel.setDuaDate("");
        notifyModel.setType("stock");
        notifyModel.setPhone("");

        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(model.getId())
                .setValue(notifyModel);
    }

    private void updateFirebase(StockModel model) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        FirebaseDatabase.getInstance().getReference()
                .child("Stocks")
                .child(model.getId())
                .setValue(model);


//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            EventBus.getDefault().postSticky(new MyUpdateCartEvent());
//                        } else {
//                            Toast.makeText(context, "Error updating...", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }
    private void deleteDialogue(int position,StockModel model){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete item");
        builder.setMessage("Are you sure want to delete cart item?");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyItemRemoved(position);
                FirebaseDatabase.getInstance().getReference().child("Stocks")
                        .child(model.getId())
                        .removeValue();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.create().show();
    }




    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemStockBinding binding;
        public ViewHolder(@NonNull ItemStockBinding stockBinding) {
            super(stockBinding.getRoot());
            binding = stockBinding;
        }
    }

//      Alerter.create((Activity) context)
//            .setTitle("Stocks Data")
//                        .setText("This stock has been reach to minimum quantity \n" +
//                                         "Item name: "+model.getItemName())
//            .setBackgroundColorInt(context.getColor(R.color.red_color))
//            .setDuration(10000)
//                        .setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
//        }
//    })
//            .show();
}
