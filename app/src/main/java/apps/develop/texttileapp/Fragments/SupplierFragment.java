package apps.develop.texttileapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import apps.develop.texttileapp.Activities.UploadActivity;
import apps.develop.texttileapp.Adapters.CustomerAdapter;
import apps.develop.texttileapp.Adapters.SupplierAdapter;
import apps.develop.texttileapp.Model.CustomerModel;
import apps.develop.texttileapp.Model.SupplierModel;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.FragmentSupplierBinding;

public class SupplierFragment extends Fragment {



    FragmentSupplierBinding binding;

    DatabaseReference reference;

    ArrayList<SupplierModel> list = new ArrayList<>();
    SupplierAdapter adapter;



    public SupplierFragment(){

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSupplierBinding.inflate(getLayoutInflater());

        reference = FirebaseDatabase.getInstance().getReference().child("Suppliers");

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        binding.recyclerView.setLayoutManager(manager);

        getData();









        binding.fab.setOnClickListener(v -> startActivity(new Intent(getContext(), UploadActivity.class)
                .putExtra("from","supplier")));




        return binding.getRoot();
    }

    private void getData(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    binding.progressBar.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        SupplierModel model = dataSnapshot.getValue(SupplierModel.class);
                        list.add(model);
                    }
                    adapter = new SupplierAdapter(getContext(),list);
                    binding.recyclerView.setAdapter(adapter);

                    if (list.size() > 0){
                        binding.layout.getRoot().setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.layout.getRoot().setVisibility(View.VISIBLE);

                    }

                }else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.layout.getRoot().setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}