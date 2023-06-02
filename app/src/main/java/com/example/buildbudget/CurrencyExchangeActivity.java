package com.example.buildbudget;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyExchangeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText amountEditText;
    private Spinner sourceCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private Button convertButton;
    private TextView resultTextView;

    private ImageButton BackButtonce;
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange);

        amountEditText = findViewById(R.id.editTextAmount);
        sourceCurrencySpinner = findViewById(R.id.spinnerSourceCurrency);
        targetCurrencySpinner = findViewById(R.id.spinnerTargetCurrency);
        convertButton = findViewById(R.id.buttonConvert);
        resultTextView = findViewById(R.id.textViewResult);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sourceCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);

        sourceCurrencySpinner.setOnItemSelectedListener(this);
        targetCurrencySpinner.setOnItemSelectedListener(this);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setText("");
                String amount = amountEditText.getText().toString();
                String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
                String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();
                if (amount.isEmpty()) {
                    Toast.makeText(CurrencyExchangeActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                new CurrencyConverterTask().execute(amount, sourceCurrency, targetCurrency);
                //amountEditText.getText().clear();
            }
        });

        BackButtonce= (ImageButton) findViewById(R.id.BackButtonce);
        BackButtonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Not needed for this implementation
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Not needed for this implementation
    }

    private class CurrencyConverterTask extends AsyncTask<String, Void, Double> {

        @Override
        protected Double doInBackground(String... params) {
            String amount = params[0];
            String sourceCurrency = params[1];
            String targetCurrency = params[2];



            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject rates = jsonObject.getJSONObject("rates");
                    double sourceRate = rates.getDouble(sourceCurrency);
                    double targetRate = rates.getDouble(targetCurrency);

                    double result = (Double.parseDouble(amount) / sourceRate) * targetRate;
                    return result;
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Double result) {
            if (result != null) {
                String amount = amountEditText.getText().toString();
                String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
                String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();
                resultTextView.setText(amount+" "+sourceCurrency+" = " +String.format("%.2f", result)+" "+targetCurrency);
            } else {
                resultTextView.setText("Error converting currency.");
            }
        }

    }
    public void reset(View view) {
        amountEditText.getText().clear();
        resultTextView.setText("");
    }
}
