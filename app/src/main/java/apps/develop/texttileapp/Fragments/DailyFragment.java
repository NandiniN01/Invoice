package apps.develop.texttileapp.Fragments;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import apps.develop.texttileapp.Adapters.DailyAdapter;
import apps.develop.texttileapp.Adapters.StockAdapter;
import apps.develop.texttileapp.Model.CartModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.MyUpdateCartEvent;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.FragmentDailyBinding;

public class DailyFragment extends Fragment {



    FragmentDailyBinding binding;
    DatabaseReference reference;

    ArrayList<StockModel> list = new ArrayList<>();
    DailyAdapter adapter;



    public DailyFragment(){}

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
//            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onUpdateCart(MyUpdateCartEvent event){
//        getData();
//    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDailyBinding.inflate(getLayoutInflater());

        reference = FirebaseDatabase.getInstance().getReference().child("Stocks");

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.recyclerView.setHasFixedSize(true);



        getData();

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
                        StockModel model = dataSnapshot.getValue(StockModel.class);
                        list.add(model);
                    }
                    LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                    binding.recyclerView.setLayoutManager(manager);
                    adapter = new DailyAdapter(getContext(),list);
                    binding.recyclerView.setAdapter(adapter);

                    if (list.size() > 0){
                        binding.layout.getRoot().setVisibility(View.GONE);
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.layout.getRoot().setVisibility(View.VISIBLE);

                    }
                    adapter.notifyDataSetChanged();

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