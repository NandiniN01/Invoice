package apps.develop.texttileapp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import apps.develop.texttileapp.Activities.UploadActivity;
import apps.develop.texttileapp.Adapters.CustomerAdapter;
import apps.develop.texttileapp.Adapters.StockAdapter;
import apps.develop.texttileapp.Model.CustomerModel;
import apps.develop.texttileapp.Model.NotifyModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.Model.UserModel;
import apps.develop.texttileapp.R;
import apps.develop.texttileapp.databinding.FragmentConsumerDuesBinding;

public class ConsumerDuesFragment extends Fragment {


    FragmentConsumerDuesBinding binding;
    DatabaseReference reference;

    ArrayList<CustomerModel> list = new ArrayList<>();
    CustomerAdapter adapter;
    CustomerModel model;
    String current_Date;

    ArrayList<String> numbersList = new ArrayList<>();
    ArrayList<String> datesList = new ArrayList<>();
    ArrayList<Integer> amountList = new ArrayList<>();
    ArrayList<String> customersList = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<>();
    ArrayList<Boolean> notifiedList = new ArrayList<>();



    public ConsumerDuesFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConsumerDuesBinding.inflate(getLayoutInflater());

        reference = FirebaseDatabase.getInstance().getReference().child("Customers");

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        binding.recyclerView.setLayoutManager(manager);

        getData();


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),UploadActivity.class)
                        .putExtra("from","customer"));

            }
        });


        Calendar calendar = Calendar.getInstance();

        Date c = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        current_Date = df.format(c);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());




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
                        model = dataSnapshot.getValue(CustomerModel.class);
                        list.add(model);
                        numbersList.add(model.getCustomerNumber());
                        datesList.add(model.getDueDate());
                        customersList.add(model.getCustomerName());
                        amountList.add(model.getAmount());
                        idList.add(model.getId());
                        notifiedList.add(model.getNotified());


                        for (int i=0; i<datesList.size(); i++){
                            if (current_Date.equals(datesList.get(i))){

                                if (!notifiedList.get(i)){
                                    String message = "Hello Mr."+customersList.get(i)+" Your pending due date will be expired by today night. " +
                                            "Please pay your Rs."+amountList.get(i);
                                    sendSmsMsgFnc(numbersList.get(i),message);

                                    //setNotified
                                    setNotified(idList.get(i),amountList.get(i),customersList.get(i),
                                            datesList.get(i),numbersList.get(i));
                                }
//                                else {
//                                    Toast.makeText(getContext(), "Today no due date!", Toast.LENGTH_SHORT).show();
//
//                                }





                            }
//                            else {
//                                Toast.makeText(getContext(), "Today no due date!", Toast.LENGTH_SHORT).show();
//                            }
                        }


                    }




                    adapter = new CustomerAdapter(getContext(),list);
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

    private void setNotified(String id,Integer amount,String name,String date,String number){
        HashMap<String,Object> map = new HashMap<>();
        map.put("notified",true);

        //

        NotifyModel model = new NotifyModel();
        model.setAmount(String.valueOf(amount));
        model.setId(id);
        model.setItemName("");
        model.setUsername(name);
        model.setDuaDate(date);
        model.setType("customer");
        model.setPhone(number);

        FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(id)
                .setValue(model);

        reference.child(id).updateChildren(map);

    }

    void sendSmsMsgFnc(String mblNumVar, String smsMsgVar) {
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//            try {
//                SmsManager smsMgrVar = SmsManager.getDefault();
//                smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
//                Toast.makeText(getContext(), "Message Sent",
//                        Toast.LENGTH_LONG).show();
//            } catch (Exception ErrVar) {
//                Toast.makeText(getContext(), ErrVar.getMessage().toString(),
//                        Toast.LENGTH_LONG).show();
//                ErrVar.printStackTrace();
//            }
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 10);
//            }
//        }

        SmsManager smsMgrVar = SmsManager.getDefault();
        smsMgrVar.sendTextMessage(mblNumVar, null, smsMsgVar, null, null);
        Toast.makeText(getContext(), "Message Sent",
                Toast.LENGTH_LONG).show();



    }



}