package apps.develop.texttileapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import apps.develop.texttileapp.Authentication.LoginActivity;
import apps.develop.texttileapp.Model.CustomerModel;
import apps.develop.texttileapp.Model.StockModel;
import apps.develop.texttileapp.Model.SupplierModel;
import apps.develop.texttileapp.databinding.ActivityUploadBinding;

public class UploadActivity extends AppCompatActivity {


    ActivityUploadBinding binding;
    String from;

    DatabaseReference reference;
    private String dueDate,supplierDue;

    private int mDay,mMonth,mYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        reference = FirebaseDatabase.getInstance().getReference();

        from = getIntent().getStringExtra("from");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        switch (from) {
            case "stock":
                binding.stockCard.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Stock Details");


                binding.btnUploadStock.setOnClickListener(v -> {
                    String itemName = binding.inputItemName.getText().toString();
                    String quantity = binding.inputQuantity.getText().toString();
                    String costEach = binding.costOfEach.getText().toString();
                    String minQuantity = binding.inputMinQuantity.getText().toString();
                    if (itemName.isEmpty() || quantity.isEmpty() || costEach.isEmpty() || minQuantity.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "All fileds are required!", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(quantity) < 1) {
                        Toast.makeText(UploadActivity.this, "Quantity minimum is 1", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(costEach) < 1) {
                        Toast.makeText(UploadActivity.this, "Enter valid cost", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(minQuantity) > Integer.parseInt(quantity)){
                        Toast.makeText(this, "Min qunatity must not greater than Quantity", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        uploadStock(itemName, quantity, costEach,minQuantity);
                    }
                });

                break;
            case "customer":
                binding.customerCard.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Add customers details");

                binding.btnUploadCustomer.setOnClickListener(v -> {
                    String customerName = binding.customerName.getText().toString();
                    String contactNo = binding.inputContactNo.getText().toString();
                    String amount = binding.pendingAmount.getText().toString();

                    if (customerName.isEmpty() || contactNo.isEmpty() || amount.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(amount) < 1) {
                        Toast.makeText(UploadActivity.this, "Quantity minimum is 1", Toast.LENGTH_SHORT).show();
                    } else if (dueDate.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "Choose due date", Toast.LENGTH_SHORT).show();
                    } else {

                        uploadCustomer(customerName, contactNo, amount);

                    }


                });

                binding.selectDueDate.setOnClickListener(v -> {
                    Calendar mcurrentDate = Calendar.getInstance();
                    mYear = mcurrentDate.get(Calendar.YEAR);
                    mMonth = mcurrentDate.get(Calendar.MONTH);
                    mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker = new DatePickerDialog(UploadActivity.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            Calendar myCalendar = Calendar.getInstance();
                            myCalendar.set(Calendar.YEAR, selectedyear);
                            myCalendar.set(Calendar.MONTH, selectedmonth);
                            myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                            String myFormat = "dd-MM-yyyy"; //Change as you need
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                            binding.selectDueDate.setText(sdf.format(myCalendar.getTime()));
                            dueDate = sdf.format(myCalendar.getTime());
                            mDay = selectedday;
                            mMonth = selectedmonth;
                            mYear = selectedyear;
                        }
                    }, mYear, mMonth, mDay);
                    //mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                });




                break;
            case "supplier":

                binding.supplierCard.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Add Material Suppliers");

                binding.btnUploadSupplier.setOnClickListener(v -> {

                    String supplierName = binding.inputSupplierName.getText().toString();
                    String amount = binding.inputSupplierAmount.getText().toString();
                    if (supplierName.isEmpty() || amount.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(amount) < 1) {
                        Toast.makeText(UploadActivity.this, "Quantity minimum is 1", Toast.LENGTH_SHORT).show();
                    } else {

                        uploadSupplier(supplierName, amount);
                    }

                });

                binding.supplierDue.setOnClickListener(v -> {
                    Calendar mcurrentDate = Calendar.getInstance();
                    mYear = mcurrentDate.get(Calendar.YEAR);
                    mMonth = mcurrentDate.get(Calendar.MONTH);
                    mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker = new DatePickerDialog(UploadActivity.this, new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            Calendar myCalendar = Calendar.getInstance();
                            myCalendar.set(Calendar.YEAR, selectedyear);
                            myCalendar.set(Calendar.MONTH, selectedmonth);
                            myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                            String myFormat = "dd-MM-yyyy"; //Change as you need
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                            binding.supplierDue.setText(sdf.format(myCalendar.getTime()));
                            supplierDue = sdf.format(myCalendar.getTime());
                            mDay = selectedday;
                            mMonth = selectedmonth;
                            mYear = selectedyear;
                        }
                    }, mYear, mMonth, mDay);
                    //mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                });




                break;
        }




    }

    private void uploadSupplier(String supplierName, String amount){
        ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);
        progressDialog.show();


        SupplierModel model = new SupplierModel();
        String id = reference.push().getKey();


        model.setSupplierName(supplierName);
        model.setAmount(Integer.parseInt(amount));
        model.setId(id);
        model.setTimestamp(System.currentTimeMillis());
        model.setDueDate(supplierDue);
        model.setNotified(false);


        reference.child("Suppliers").child(id).setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            resetSupplierFields();
                            Toast.makeText(UploadActivity.this, "Stock uploaded!", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(UploadActivity.this, "Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    private void uploadCustomer(String customerName, String contactNo, String amount) {
        ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        CustomerModel model = new CustomerModel();
        String id = reference.push().getKey();


        model.setCustomerName(customerName);
        model.setAmount(Integer.parseInt(amount));
        model.setCustomerNumber(contactNo);
        model.setDueDate(dueDate);
        model.setTimestamp(System.currentTimeMillis());
        model.setId(id);
        model.setNotified(false);


        reference.child("Customers").child(id).setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            resetCustomerFields();
                            Toast.makeText(UploadActivity.this, "Stock uploaded!", Toast.LENGTH_SHORT).show();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(UploadActivity.this, "Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void uploadStock(String itemName, String quantity, String costEach, String minQuantity){
        ProgressDialog progressDialog = new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Uploading....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StockModel model = new StockModel();
        String id = reference.push().getKey();

        model.setItemName(itemName);
        model.setId(id);
        model.setQuantity(Integer.parseInt(quantity));
        model.setCost_of_each(Integer.parseInt(costEach));
        model.setTimestamp(System.currentTimeMillis());
        model.setMinQuantity(Integer.parseInt(minQuantity));
        model.setNotified(false);


        assert id != null;
        reference.child("Stocks").child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Stock uploaded!", Toast.LENGTH_SHORT).show();
                    resetStockFields();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this, "Failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void resetStockFields(){
        binding.inputItemName.setText("");
        binding.inputQuantity.setText("");
        binding.costOfEach.setText("");
    }
    private void resetCustomerFields(){
        binding.customerName.setText("");
        binding.pendingAmount.setText("");
        binding.inputContactNo.setText("");
        binding.selectDueDate.setHint("Choose Date");
        dueDate = "";
    }

    private void resetSupplierFields(){
        binding.inputSupplierName.setText("");
        binding.inputSupplierAmount.setText("");
    }
}