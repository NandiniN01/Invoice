package apps.develop.texttileapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import apps.develop.texttileapp.Adapters.DailyAdapter;
import apps.develop.texttileapp.Adapters.NotificationAdapter;
import apps.develop.texttileapp.Model.NotifyModel;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {


    ActivityNotificationBinding binding;

    ArrayList<NotifyModel> list = new ArrayList<>();
    NotificationAdapter adapter;

    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Notifications");

        binding.progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(this::getData,1000);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());





    }
    private void getData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        NotifyModel model = dataSnapshot.getValue(NotifyModel.class);
                        list.add(model);
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(NotificationActivity.this,LinearLayoutManager.VERTICAL,false);
                    binding.recyclerView.setLayoutManager(manager);
                    adapter = new NotificationAdapter(NotificationActivity.this,list);
                    binding.recyclerView.setAdapter(adapter);

                    if (list.size() > 0){
                        binding.layout.getRoot().setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);

                    }else {
                        binding.layout.getRoot().setVisibility(View.VISIBLE);
                        binding.layout.image.setImageResource(R.drawable.notifications);
                        binding.layout.text.setText("No notifications");
                    }

                }else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.layout.getRoot().setVisibility(View.VISIBLE);
                    binding.layout.image.setImageResource(R.drawable.notifications);
                    binding.layout.text.setText("No notifications");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(NotificationActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}