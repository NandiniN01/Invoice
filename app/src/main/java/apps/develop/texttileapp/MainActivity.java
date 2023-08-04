package apps.develop.texttileapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.athbk.ultimatetablayout.IFTabAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import apps.develop.texttileapp.Activities.EditActivity;
import apps.develop.texttileapp.Activities.NotificationActivity;
import apps.develop.texttileapp.Activities.SalesActivity;
import apps.develop.texttileapp.Adapters.TabAdapter;
import apps.develop.texttileapp.Authentication.LoginActivity;
import apps.develop.texttileapp.Fragments.ConsumerDuesFragment;
import apps.develop.texttileapp.Fragments.DailyFragment;
import apps.develop.texttileapp.Fragments.StocksFragment;
import apps.develop.texttileapp.Fragments.SupplierFragment;
import apps.develop.texttileapp.Model.CartModel;
import apps.develop.texttileapp.Model.UserModel;
import apps.develop.texttileapp.databinding.ActivityMainBinding;
import apps.develop.texttileapp.databinding.ProfitDialogueBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

//    TabAdapter adapter;
//    IFTabAdapter adapter;

    DatabaseReference reference;
    FirebaseUser user;

    double totalPrice =0;
    Dialog dialog;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        reference = FirebaseDatabase.getInstance().getReference().child("Cart");
        user = FirebaseAuth.getInstance().getCurrentUser();


        initTabs();

        askSMSPermissions();
        getData();

        initNavigationView();

        setHeaderData();

    }

    private void initTabs(){
//        adapter = new TabAdapter(getSupportFragmentManager());
//
//        adapter.addFragment(new DailyFragment(),"Daily Sales");
//        adapter.addFragment(new StocksFragment(),"Stock Details");
//        adapter.addFragment(new ConsumerDuesFragment(),"Consumer Dues");
//        adapter.addFragment(new SupplierFragment(),"Suppliers");
//
//        binding.viewPager.setAdapter(adapter);
//        binding.tabLyt.setupWithViewPager(binding.viewPager);
//        binding.viewPager.setOffscreenPageLimit(4);


        binding.tabLyt.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos){
                    case 0:
                        setTransaction(new DailyFragment());
                        break;

                    case 1:
                        setTransaction(new StocksFragment());
                        break;

                    case 2:
                        setTransaction(new ConsumerDuesFragment());
                        break;

                    case 3:
                        setTransaction(new SupplierFragment());
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setTransaction(new DailyFragment());
    }

    private void getData(){
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        CartModel model = dataSnapshot.getValue(CartModel.class);

                        totalPrice +=model.getTotalPrice();

                    }


//                    ProfitDialogueBinding dialogueBinding = ProfitDialogueBinding.inflate(getLayoutInflater());
//                    dialog = new Dialog(MainActivity.this);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(dialogueBinding.getRoot());
//                    dialog.setCancelable(false);
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                    dialogueBinding.profitText.setText("₹"+totalPrice+"./-");
//
//                    dialogueBinding.txtClose.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void askSMSPermissions(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_PHONE_STATE}
                    ,19);
        }
    }

    private void setTransaction(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer,fragment)
                .commit();
    }

    @SuppressLint("NonConstantResourceId")
    private void initNavigationView(){
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> binding.drawerLyt.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.editProfile:
                    startActivity(new Intent(MainActivity.this, EditActivity.class));
                    binding.drawerLyt.closeDrawer(GravityCompat.START);
                    break;

                case R.id.notifications:
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));

                    binding.drawerLyt.closeDrawer(GravityCompat.START);
                    break;

                case R.id.salesCount:
                    startActivity(new Intent(MainActivity.this, SalesActivity.class));

                    binding.drawerLyt.closeDrawer(GravityCompat.START);
                    break;

                case R.id.logout:
                    AuthUI.getInstance().signOut(this)
                            .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            preferenceManager.clear();


                                        }else {
                                            Toast.makeText(MainActivity.this, "Failed: "+
                                                    task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                    binding.drawerLyt.closeDrawer(GravityCompat.START);
                    break;

            }
            return false;
        });









    }

    private void setHeaderData(){
        View view = binding.navigationView.getHeaderView(0);
        ImageView profileImage = view.findViewById(R.id.profileImage);
        TextView username = view.findViewById(R.id.username);
        TextView email = view.findViewById(R.id.email);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    try {
                        Picasso.get().load(model.getProfile()).placeholder(R.drawable.placeholder)
                                .into(profileImage);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    username.setText(model.getFullName());
                    email.setText(model.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}