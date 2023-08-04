package apps.develop.texttileapp.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import apps.develop.texttileapp.MainActivity;
import apps.develop.texttileapp.Model.NotifyModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.Model.SupplierModel;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.ItemStockBinding;
import apps.develop.texttileapp.databinding.ItemSupplierBinding;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.ViewHolder> {

    Context context;
    ArrayList<SupplierModel> list;

    ItemSupplierBinding binding;

    public SupplierAdapter(Context context, ArrayList<SupplierModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SupplierAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemSupplierBinding.inflate(LayoutInflater.from(context));

        return new SupplierAdapter.ViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull SupplierAdapter.ViewHolder holder, int position) {

        SupplierModel model = list.get(position);


        binding.supplierName.setText(model.getSupplierName());
        binding.supplierAmount.setText("₹"+model.getAmount()+".0/-");

        binding.dueDate.setText("Due: "+model.getDueDate());
        binding.imgDelete.setOnClickListener(v -> {
            deleteDialogue(position, model);



        });

        Calendar calendar = Calendar.getInstance();

        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

       String current_Date = df.format(c);

        if (model.getDueDate().equals(current_Date)){
            if (!model.getNotified()){
                Alerter.create((Activity) context)
                        .setTitle("Payment Due")
                        .setText("You need to pay due amount to: "+model.getSupplierName())
                        .setBackgroundColorInt(context.getColor(R.color.purple_700))
                        .setDuration(10000)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                setNotified(model);


            }





        }


    }

    private void setNotified(SupplierModel model){
        HashMap<String,Object> map = new HashMap<>();
        map.put("notified",true);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Suppliers");
        reference.child(model.getId()).updateChildren(map);

        NotifyModel notifyModel = new NotifyModel();
        notifyModel.setAmount(String.valueOf(model.getAmount()));
        notifyModel.setId(model.getId());
        notifyModel.setItemName("");
        notifyModel.setUsername(model.getSupplierName());
        notifyModel.setDuaDate(model.getDueDate());
        notifyModel.setType("supplier");
        notifyModel.setPhone("");

        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(model.getId())
                .setValue(notifyModel);
    }

    private void deleteDialogue(int position,SupplierModel model){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete item");
        builder.setMessage("Are you sure want to delete cart item?");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notifyItemRemoved(position);
                FirebaseDatabase.getInstance().getReference().child("Suppliers")
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
        ItemSupplierBinding binding;
        public ViewHolder(@NonNull ItemSupplierBinding supplierBinding) {
            super(supplierBinding.getRoot());
            binding = supplierBinding;
        }
    }

    private void showNotification(SupplierModel model){
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.supplier) // notification icon
                .setContentTitle("Due payment") // title for notification
                .setContentText("Hello your due date will be expired by today") // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
