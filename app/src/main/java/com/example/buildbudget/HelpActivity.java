package com.example.buildbudget;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<faq> faqList;
    ImageButton BackButtonh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        recyclerView=findViewById(R.id.recyclerView);
        initData();
        setRecyclerView();
       BackButtonh= (ImageButton) findViewById(R.id.BackButtonh);
        BackButtonh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setRecyclerView() {
        FaqAdapter faqAdapter=new FaqAdapter(faqList);
        recyclerView.setAdapter(faqAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initData() {
        faqList=new ArrayList<>();

        faqList.add(new faq("How do I add an expense?","To add an expense, open the app and navigate to the (+) icon, here you will find 2 options. You can either record your expense via scanning the memo, for this select OCR, you can also record the expense manually, click 'Manual'. You will come across a + button and - button. For expense record, select the - button."));
        faqList.add(new faq("How do I add an income?","To add an income, open the app and navigate to the (+) icon, here you will find 2 options.Click 'Manual'. You will come across a + button and - button. For income record, select the + button."));
        faqList.add(new faq("Can I categorize my expenses?","Yes, you can categorize your expenses in Build&Budget. The app usually provides predefined categories such as food, transportation, bills, etc. You can assign each expense to an appropriate category to better track and analyze your spending habits."));
        faqList.add(new faq("How can I set a budget for the month?","To set a budget for a category, go to the 'Budget' section of the app in the drawer menu.Set a budget for the month and enter the desired amount. The app will help you monitor your expenses in that month"));
        faqList.add(new faq("Can I track my income in the app?","Yes, you can track your income in the budget app. Look for the 'Income' section within the app. You can add your income sources, specify the amount, and set the frequency (e.g., monthly, weekly). Tracking your income will provide a comprehensive view of your financial inflows and outflows."));
        faqList.add(new faq("Can I track my debts in the app?","Yes, you can track your debts in Build&budget app. Look for the 'Debt' section in the drawer within the app. You can add your debts, specify the amount, and also enter the amount of how much you have repaid till now. Tracking your debts will will help you dodge unwanted situations. Once the debt is repaid you can reset the value."));
        faqList.add(new faq("Is it possible to sync my bank accounts with the app?","Many budget apps offer the ability to sync your bank accounts or credit cards for automatic transaction updates. Check if your app supports this feature and follow the instructions to securely link your accounts. It simplifies expense tracking by automatically importing transactions into the app."));
        faqList.add(new faq("Can I create financial goals within the app?","Yes, you can create financial goals within the app. Look for the 'Goals' section in the drawer where you can set specific goals, such as saving for a vacation or down payment. Define the target amount, target date, and track your progress towards achieving your goals."));
        faqList.add(new faq("Is my financial data secure in the app?","Ensuring the security of your financial data is a top priority for the app. They employ encryption techniques, secure connections, and follow industry-standard security practices. Your data is stored securely and is not shared with third parties without your consent."));
        faqList.add(new faq("How can I generate visualizations for my expenses?","Yes, you can generate visualizations for your income and expenses. For this, you can select the 'Statistics' section from the drawer. These visual representations help you gain insights into your financial habits."));
        faqList.add(new faq("How can I contact customer support?","For any problem, you can contact through the provided emails. We will get to as soon as possible."));
        faqList.add(new faq("Necessary Emails for Communication","ummetasnimhasan@iut-dhdka.edu " +
                " rafihassan@iut-dhaka.edu " +
                " lamiyatahsin@iut-dhaka.edu "));

    }


}
