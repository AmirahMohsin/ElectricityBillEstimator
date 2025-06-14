package com.example.electricitybillestimator; // Replace with your package name

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BillDetailsActivity extends AppCompatActivity {

    private TextView tvDetailsMonth, tvDetailsUnitsUsed, tvDetailsTotalCharges, tvDetailsRebate, tvDetailsFinalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Bill Details");
        }

        tvDetailsMonth = findViewById(R.id.tvDetailsMonth);
        tvDetailsUnitsUsed = findViewById(R.id.tvDetailsUnitsUsed);
        tvDetailsTotalCharges = findViewById(R.id.tvDetailsTotalCharges);
        tvDetailsRebate = findViewById(R.id.tvDetailsRebate);
        tvDetailsFinalCost = findViewById(R.id.tvDetailsFinalCost);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String month = extras.getString("month");
            double unitsUsed = extras.getDouble("unitsUsed");
            double totalCharges = extras.getDouble("totalCharges");
            double rebatePercentage = extras.getDouble("rebatePercentage");
            double finalCost = extras.getDouble("finalCost");

            tvDetailsMonth.setText(month);
            tvDetailsUnitsUsed.setText(String.format(Locale.getDefault(), "%.2f kWh", unitsUsed));
            tvDetailsTotalCharges.setText(String.format(Locale.getDefault(), "RM %.2f", totalCharges));
            tvDetailsRebate.setText(String.format(Locale.getDefault(), "%.1f%%", rebatePercentage));
            tvDetailsFinalCost.setText(String.format(Locale.getDefault(), "RM %.2f", finalCost));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}