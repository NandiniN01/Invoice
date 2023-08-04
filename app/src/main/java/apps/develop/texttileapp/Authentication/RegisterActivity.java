package apps.develop.texttileapp.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apps.develop.texttileapp.Model.UserModel;
import apps.develop.texttileapp.PreferenceManager;
import apps.develop.texttileapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {


    ActivityRegisterBinding binding;
    DatabaseReference reference;
    FirebaseAuth auth;

    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        binding.btnSignUp.setOnClickListener(v -> {
            String username = binding.inputFullName.getText().toString();
            String businessName = binding.inputBusinessName.getText().toString();
            String address = binding.inputAddress.getText().toString();
            String email = binding.inputEmail.getText().toString();
            String phone = binding.inputPhone.getText().toString();
            String password = binding.inputPassword.getText().toString();

            if (phone.isEmpty() || email.isEmpty()  || password.isEmpty() || username.isEmpty() ||
            businessName.isEmpty() || address.isEmpty()){
                Toast.makeText(RegisterActivity.this, "All fields required", Toast.LENGTH_SHORT).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(RegisterActivity.this, "Enter valid email address", Toast.LENGTH_SHORT).show();
            }else if (password.length() < 6){
                Toast.makeText(RegisterActivity.this, "Password must be greater than 6 chars!", Toast.LENGTH_SHORT).show();
            }else {


                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Crating account....");
                progressDialog.setCancelable(false);
                progressDialog.show();


                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        UserModel model = new UserModel();
                        model.setFullName(username);
                        model.setAddress(address);
                        model.setBusinessName(businessName);
                        model.setUid(user.getUid());
                        model.setEmail(email);
                        model.setPhone(phone);


                        reference.child(user.getUid()).setValue(model).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                preferenceManager.putBoolean("signed",false);
                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Failed: "+
                                        task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Error: "+
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });






            }





        });

        binding.txtSignIn.setOnClickListener(v -> finish());

    }
}