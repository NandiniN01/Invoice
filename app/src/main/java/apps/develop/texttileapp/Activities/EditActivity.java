package apps.develop.texttileapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import apps.develop.texttileapp.Model.UserModel;
import apps.develop.texttileapp.databinding.ActivityEditBinding;

public class EditActivity extends AppCompatActivity {

    ActivityEditBinding binding;

    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser user;
    Uri imageUri;
    String link;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("Profiles");

        user = FirebaseAuth.getInstance().getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating");
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.inputFullName.getText().toString();
                String businessName = binding.inputBusinessName.getText().toString();
                String address = binding.inputAddress.getText().toString();
                String phone = binding.inputPhone.getText().toString();

                if (username.isEmpty() || businessName.isEmpty() || address.isEmpty() || phone.isEmpty()){
                    Toast.makeText(EditActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }else if (phone.length() < 10){
                    Toast.makeText(EditActivity.this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
                }else if (imageUri == null){
                    updateData(username,businessName,address,phone);
                }else {
                    uploadImage(username,businessName,address,phone);
                }

            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

        getUserData();

        checkPermissions();
    }
    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
         != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    777);
        }
    }

    private void updateData(String username, String businessName, String address, String phone){
        progressDialog.show();


        HashMap<String,Object> map = new HashMap<>();
        map.put("fullName",username);
        map.put("phone",phone);
        map.put("profile",link);
        map.put("businessName",businessName);
        map.put("address",address);

        reference.child(user.getUid()).updateChildren(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.dismiss();
                Toast.makeText(EditActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getUserData(){
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserModel model = snapshot.getValue(UserModel.class);
                    if (model !=null){
                        binding.inputFullName.setText(model.getFullName());
                        binding.inputAddress.setText(model.getAddress());
                        binding.inputBusinessName.setText(model.getBusinessName());
                        binding.inputPhone.setText(model.getPhone());

                        link = model.getProfile();
                    }
                }else {
                    Toast.makeText(EditActivity.this, "Please sign in!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditActivity.this, "Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (resultCode == RESULT_OK && data !=null){
                imageUri = data.getData();
                binding.profileImage.setImageURI(imageUri);
            }
        }else {
            Toast.makeText(this, "Please select image!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(String username, String businessName, String address, String phone){
        progressDialog.show();

        StorageReference sRef = storageReference.child(user.getUid()).child(System.currentTimeMillis()+".jpg");
        sRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {


                        String URL = uri.toString();

                        HashMap<String,Object> map = new HashMap<>();
                        map.put("fullName",username);
                        map.put("phone",phone);
                        map.put("profile",URL);
                        map.put("businessName",businessName);
                        map.put("address",address);

                        reference.child(user.getUid()).updateChildren(map).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(EditActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(EditActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditActivity.this, "Error: "+
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}