package apps.develop.texttileapp.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import apps.develop.texttileapp.MainActivity;
import apps.develop.texttileapp.PreferenceManager;
import apps.develop.texttileapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {


    ActivityLoginBinding binding;

    FirebaseAuth auth;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        if (preferenceManager.getBoolean("signed")){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        auth = FirebaseAuth.getInstance();



        binding.btnLogin.setOnClickListener(v -> {

            String email = binding.inputEmail.getText().toString();
            String password = binding.inputPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "This fields can't be empty", Toast.LENGTH_SHORT).show();

            }else {
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Logging....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        preferenceManager.putBoolean("signed",true);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        binding.txtSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));

    }
}