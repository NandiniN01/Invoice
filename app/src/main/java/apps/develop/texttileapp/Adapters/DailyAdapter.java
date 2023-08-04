package apps.develop.texttileapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import apps.develop.texttileapp.Model.CartModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.MyUpdateCartEvent;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.ItemDailyBinding;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private Context context;
    ArrayList<StockModel> list;

    String strItems;



    public DailyAdapter(Context context, ArrayList<StockModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        binding = ItemDailyBinding.inflate(LayoutInflater.from(context));
        View view = LayoutInflater.from(context).inflate(R.layout.item_daily,parent,false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StockModel stockModel = list.get(position);

        holder.itemName.setText(stockModel.getItemName());
        holder.price.setText("₹"+stockModel.getCost_of_each()+".0/-");




        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stockModel.getQuantity() > 1){

                    stockModel.setQuantity(stockModel.getQuantity()-1);

                    updateFirebase(stockModel);
                }
                addToCart(stockModel);
            }
        });


        checkCartExist(stockModel.getId(), holder.cartLyt, holder.btnAdd,holder.txtQuantity);

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedQuantity = Integer.parseInt(holder.txtQuantity.getText().toString());
                if (selectedQuantity > stockModel.getMinQuantity()){
                    Toast.makeText(context, "Insufficient quantity to this product", Toast.LENGTH_SHORT).show();
                }else {
                    if (stockModel.getQuantity() > 1){

                        stockModel.setQuantity(stockModel.getQuantity()-selectedQuantity);

                        updateFirebase(stockModel);
                    }
                    updateCart(stockModel,selectedQuantity);
                }
            }
        });

        holder.txtQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialogue(holder);
            }
        });

        holder.closeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Items");
                builder.setMessage("Are you sure want to delete this cart items.");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference().child("Cart")
                                .child(user.getUid())
                                .child(stockModel.getId())
                                .removeValue();
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(position);

                    }
                });

                builder.setNegativeButton("Cancel",null);

                builder.create().show();
            }
        });
    }

    private void addToCart(StockModel model) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        CartModel cartModel = new CartModel();
        cartModel.setTotalPrice(model.getCost_of_each());
        cartModel.setPrice(model.getCost_of_each());
        cartModel.setName(model.getItemName());
        cartModel.setQuantity(1);
        cartModel.setKey(model.getId());



        reference.child(user.getUid()).child(model.getId()).setValue(cartModel);




//        Toast.makeText(context, "Q: "+model.getQuantity(), Toast.LENGTH_SHORT).show();

    }

    private void updateCart(StockModel stockModel,int quantity){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String,Object> map = new HashMap<>();

        map.put("quantity",quantity);
        map.put("totalPrice",quantity * stockModel.getCost_of_each());

        reference.child(user.getUid()).child(stockModel.getId()).updateChildren(map);
        Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
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

    private void checkCartExist(String id,LinearLayout txtAdded,TextView btnAdd,TextView txtQuant){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        reference.child(user.getUid()).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //
                    CartModel cartModel = snapshot.getValue(CartModel.class);
                    txtQuant.setText(String.valueOf(cartModel.getQuantity()));

                    txtAdded.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.GONE);
                }else {
                    txtAdded.setVisibility(View.GONE);
                    btnAdd.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showListDialogue(ViewHolder holder){
        ArrayList<String> items = new ArrayList<>();

        items.add("1");
        items.add("2");
        items.add("3");
        items.add("4");
        items.add("5");
        items.add("6");
        items.add("7");
        items.add("8");
        items.add("9");
        items.add("10");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select quantity");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(items.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strItems = items.get(which);
                dialog.dismiss();
                holder.txtQuantity.setText(strItems);
            }
        });

        builder.create().show();


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName,btnAdd,price;
        TextView txtQuantity,btnUpdate;
        ImageView closeCart;
        LinearLayout cartLyt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnUpdate = itemView.findViewById(R.id.btnUpdateCart);
            closeCart = itemView.findViewById(R.id.closeCart);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            price = itemView.findViewById(R.id.price);
            cartLyt = itemView.findViewById(R.id.cartLyt);


        }
    }
}
