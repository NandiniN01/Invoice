package apps.develop.texttileapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import apps.develop.texttileapp.Adapters.SalesAdapter;
import apps.develop.texttileapp.Model.CartModel;
import apps.develop.texttileapp.databinding.ActivitySalesBinding;

public class SalesActivity extends AppCompatActivity {

    ActivitySalesBinding binding;

    ArrayList<CartModel> list = new ArrayList<>();
    SalesAdapter adapter;

    DatabaseReference reference;
    FirebaseUser user;
    double totalPrice =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySalesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        user = FirebaseAuth.getInstance().getCurrentUser();


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());



        getData();
    }
    private void getData(){
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        CartModel model = dataSnapshot.getValue(CartModel.class);
                        if (model !=null){
                            list.add(model);

                            totalPrice +=model.getTotalPrice();

                        }
                    }
                    binding.totalAmount.setText("₹"+totalPrice+"./-");

                    LinearLayoutManager manager = new LinearLayoutManager(SalesActivity.this,LinearLayoutManager.VERTICAL,false);
                    binding.recyclerView.setLayoutManager(manager);

                    adapter = new SalesAdapter(SalesActivity.this,list);
                    binding.recyclerView.setAdapter(adapter);

                }else {

                    Toast.makeText(SalesActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SalesActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}