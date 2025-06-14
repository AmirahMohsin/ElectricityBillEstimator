package com.example.electricitybillestimator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewMonth;
    private TextInputEditText etUnitsUsed, etRebatePercentage;
    private TextView tvTotalCharges, tvFinalCost;
    private Button btnCalculateSave, btnClear;
    private ListView lvCalculationHistory;
    private Toolbar toolbar; // Declare Toolbar

    private DatabaseReference mDatabase;
    private List<BillEntry> billHistoryList;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Toolbar and set as ActionBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Set the toolbar as the activity's action bar

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("bill_entries");

        // Initialize UI components
        autoCompleteTextViewMonth = findViewById(R.id.autoCompleteTextViewMonth);
        etUnitsUsed = findViewById(R.id.etUnitsUsed);
        etRebatePercentage = findViewById(R.id.etRebatePercentage);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        btnCalculateSave = findViewById(R.id.btnCalculateSave);
        btnClear = findViewById(R.id.btnClear);
        lvCalculationHistory = findViewById(R.id.lvCalculationHistory);

        // Setup Month Dropdown
        String[] months = getResources().getStringArray(R.array.months_array);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, months);
        autoCompleteTextViewMonth.setAdapter(monthAdapter);

        billHistoryList = new ArrayList<>();
        adapter = new CustomListAdapter(this, R.layout.list_item_bill, billHistoryList);
        lvCalculationHistory.setAdapter(adapter);

        btnCalculateSave.setOnClickListener(v -> calculateAndSaveBill());
        btnClear.setOnClickListener(v -> clearFields());

        lvCalculationHistory.setOnItemClickListener((parent, view, position, id) -> {
            BillEntry clickedEntry = billHistoryList.get(position);
            Intent intent = new Intent(MainActivity.this, BillDetailsActivity.class);
            intent.putExtra("month", clickedEntry.getMonth());
            intent.putExtra("unitsUsed", clickedEntry.getUnitsUsed());
            intent.putExtra("totalCharges", clickedEntry.getTotalCharges());
            intent.putExtra("rebatePercentage", clickedEntry.getRebatePercentage());
            intent.putExtra("finalCost", clickedEntry.getFinalCost());
            startActivity(intent);
        });

        loadBillHistory();
    }

    private double calculateTotalCharges(double unitsUsed) {
        double charges = 0;

        if (unitsUsed > 0) {
            if (unitsUsed <= 200) {
                charges += unitsUsed * 0.218;
            } else if (unitsUsed <= 300) {
                charges += (200 * 0.218) + ((unitsUsed - 200) * 0.334);
            } else if (unitsUsed <= 600) {
                charges += (200 * 0.218) + (100 * 0.334) + ((unitsUsed - 300) * 0.516);
            } else { // unitsUsed > 600
                charges += (200 * 0.218) + (100 * 0.334) + (300 * 0.516) + ((unitsUsed - 600) * 0.546);
            }
        }
        return charges;
    }

    private void calculateAndSaveBill() {
        String month = autoCompleteTextViewMonth.getText().toString().trim();
        String unitsUsedStr = etUnitsUsed.getText().toString().trim();
        String rebatePercentageStr = etRebatePercentage.getText().toString().trim();

        if (TextUtils.isEmpty(month)) {
            Toast.makeText(this, "Please select a month", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(unitsUsedStr)) {
            etUnitsUsed.setError("Units Used is required");
            etUnitsUsed.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(rebatePercentageStr)) {
            etRebatePercentage.setError("Rebate Percentage is required");
            etRebatePercentage.requestFocus();
            return;
        }

        try {
            double unitsUsed = Double.parseDouble(unitsUsedStr);
            double rebatePercentage = Double.parseDouble(rebatePercentageStr);

            if (rebatePercentage < 0 || rebatePercentage > 5) {
                etRebatePercentage.setError("Rebate must be between 0% and 5%");
                etRebatePercentage.requestFocus();
                return;
            }

            double totalCharges = calculateTotalCharges(unitsUsed);
            double finalCost = totalCharges - (totalCharges * (rebatePercentage / 100.0));

            tvTotalCharges.setText(String.format(Locale.getDefault(), "RM %.2f", totalCharges));
            tvFinalCost.setText(String.format(Locale.getDefault(), "RM %.2f", finalCost));

            String id = mDatabase.push().getKey();
            BillEntry billEntry = new BillEntry(id, month, unitsUsed, totalCharges, rebatePercentage, finalCost);
            if (id != null) {
                mDatabase.child(id).setValue(billEntry)
                        .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Bill saved successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity.this, "Failed to save bill: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("FirebaseSave", "Error saving bill", e);
                        });
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for units and rebate.", Toast.LENGTH_SHORT).show();
            Log.e("InputError", "NumberFormatException in calculateAndSaveBill", e);
        }
    }


    private void clearFields() {
        autoCompleteTextViewMonth.setText("");
        etUnitsUsed.setText("");
        etRebatePercentage.setText("");
        tvTotalCharges.setText(getString(R.string.initial_cost_value));
        tvFinalCost.setText(getString(R.string.initial_cost_value));
        autoCompleteTextViewMonth.clearFocus();
        etUnitsUsed.clearFocus();
        etRebatePercentage.clearFocus();
    }

    private void loadBillHistory() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                billHistoryList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BillEntry billEntry = postSnapshot.getValue(BillEntry.class);
                    if (billEntry != null) {
                        billHistoryList.add(billEntry);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load history: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FirebaseLoad", "Error loading bill history", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}