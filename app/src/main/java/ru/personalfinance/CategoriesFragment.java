package ru.personalfinance;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.personalfinance.Model.Account;
import ru.personalfinance.Model.Data;

public class CategoriesFragment extends Fragment {

    // Floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    // Floating button textview

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    // boolean

    private boolean isOpen = false;

    // animation

    private Animation fadeOpen, fadeClose;

    private ImageView btnProducts;
    private ImageView btnCafe;
    private ImageView btnLeisure;
    private ImageView btnTransport;
    private ImageView btnGifts;
    private ImageView btnPurchases;
    private ImageView btnOther;
    private ImageView btnSport;
    private ImageView btnTravel;
    private ImageView btnFamily;
    private ImageView btnHealth;

    private TextView productsCost;
    private TextView cafeCost;
    private TextView leisureCost;
    private TextView transportCost;
    private TextView giftsCost;
    private TextView purchasesCost;
    private TextView otherCost;
    private TextView sportCost;
    private TextView travelCost;
    private TextView familyCost;
    private TextView healthCost;

    private TextView expenseCaption;

    private FirebaseAuth mAuth;
    private DatabaseReference mChangeDatabase;
    private DatabaseReference mAccountDatabase;
    private List<Account> accountsList;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_categories, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mChangeDatabase = FirebaseDatabase.getInstance().getReference().child("ChangeData").child(uid);

        productsCost = view.findViewById(R.id.products_cost);
        cafeCost = view.findViewById(R.id.cafe_cost);
        leisureCost = view.findViewById(R.id.leisure_cost);
        transportCost = view.findViewById(R.id.transport_cost);
        giftsCost = view.findViewById(R.id.gifts_cost);
        purchasesCost = view.findViewById(R.id.purchases_cost);
        otherCost = view.findViewById(R.id.other_cost);
        sportCost = view.findViewById(R.id.sport_cost);
        travelCost = view.findViewById(R.id.travel_cost);
        familyCost = view.findViewById(R.id.family_cost);
        healthCost = view.findViewById(R.id.health_cost);

        expenseCaption = view.findViewById(R.id.expense_caption);

        mChangeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalSum = 0;
                int products = 0;
                int cafe = 0;
                int leisure = 0;
                int transport = 0;
                int gifts = 0;
                int purchases = 0;
                int other = 0;
                int sport = 0;
                int travel = 0;
                int family = 0;
                int health = 0;

                for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                    //String[] expenseType = {"Продукты", "Семья", "Транспорт", "Досуг", "Кафе", "Здоровье", "Покупки",Подарки "Поездки", "Спорт", "Другое"};
                    Data data = mySnapshot.getValue(Data.class);
                    if (data.getChange().equals("expense")) {
                        totalSum += data.getAmount();
                        if (data.getType().equals("Продукты")) products += data.getAmount();
                        else if (data.getType().equals("Кафе")) cafe += data.getAmount();
                        else if (data.getType().equals("Досуг")) leisure += data.getAmount();
                        else if (data.getType().equals("Транспорт")) transport += data.getAmount();
                        else if (data.getType().equals("Подарки")) gifts += data.getAmount();
                        else if (data.getType().equals("Покупки")) purchases += data.getAmount();
                        else if (data.getType().equals("Другое")) other += data.getAmount();
                        else if (data.getType().equals("Спорт")) sport += data.getAmount();
                        else if (data.getType().equals("Поездки")) travel += data.getAmount();
                        else if (data.getType().equals("Семья")) family += data.getAmount();
                        else if (data.getType().equals("Здоровье")) health += data.getAmount();
                    }
                }

                String strProducts = products + " ₽";
                String strCafe = cafe + " ₽";
                String strLeisure = leisure + " ₽";
                String strTransport = transport + " ₽";
                String strGifts = gifts + " ₽";
                String strPurchases = purchases + " ₽";
                String strOther = other + " ₽";
                String strSport = sport + " ₽";
                String strTravel = travel + " ₽";
                String strFamily = family + " ₽";
                String strHealth = health + " ₽";

                productsCost.setText(strProducts);
                cafeCost.setText(strCafe);
                leisureCost.setText(strLeisure);
                transportCost.setText(strTransport);
                giftsCost.setText(strGifts);
                purchasesCost.setText(strPurchases);
                otherCost.setText(strOther);
                sportCost.setText(strSport);
                travelCost.setText(strTravel);
                familyCost.setText(strFamily);
                healthCost.setText(strHealth);

                expenseCaption.setText("Расходы\n" + totalSum + " ₽");

                PieChart pieChart = view.findViewById(R.id.pieChart);
                final ArrayList<PieEntry> categories = new ArrayList<>();
                categories.add(new PieEntry(products)); // products
                categories.add(new PieEntry(cafe)); // cafe
                categories.add(new PieEntry(leisure)); // leisure
                categories.add(new PieEntry(transport)); // transport
                categories.add(new PieEntry(gifts)); // gifts
                categories.add(new PieEntry(purchases)); // purchases
                categories.add(new PieEntry(other)); // other
                categories.add(new PieEntry(sport)); // sport
                categories.add(new PieEntry(travel)); // travel
                categories.add(new PieEntry(family)); // family
                categories.add(new PieEntry(health)); // health
                PieDataSet pieDataSet = new PieDataSet(categories, "Categories");
                pieDataSet.setSliceSpace(0f);
                pieDataSet.setColors(
                        Color.parseColor("#92D7E7"),
                        Color.parseColor("#6B88C1"),
                        Color.parseColor("#DC67BB"),
                        Color.parseColor("#FBB03B"),
                        Color.parseColor("#E97070"),
                        Color.parseColor("#BB7A1A"),
                        Color.parseColor("#B139A5"),
                        Color.parseColor("#64C3C9"),
                        Color.parseColor("#B6DC4F"),
                        Color.parseColor("#B875C9"),
                        Color.parseColor("#AAC16B")
                );

                pieDataSet.setValueTextColor(Color.parseColor("#00000000"));
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.setHoleRadius(70);
                Legend l = pieChart.getLegend();
                l.setEnabled(false);
                pieChart.setDrawMarkers(false);
                pieChart.setDrawEntryLabels(false);
                pieChart.getDescription().setEnabled(false);
                pieChart.setHoleColor(Color.parseColor("#CB997E"));
                pieChart.invalidate();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mAccountDatabase = FirebaseDatabase.getInstance().getReference().child("AccountData").child(uid);

        accountsList = new ArrayList();
        mAccountDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (accountsList.isEmpty()) {
                    for (DataSnapshot mySnapshot : snapshot.getChildren()) {
                        accountsList.add(mySnapshot.getValue(Account.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        final ArrayList<String> accounts = new ArrayList<String>();

        mAccountDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                accounts.clear();

                for (DataSnapshot mySnapshot : snapshot.getChildren()) {

                    Account account = mySnapshot.getValue(Account.class);
                    accounts.add(account.getName());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // connect floating button to layout

        fab_main_btn = view.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = view.findViewById(R.id.income_ft_btn);
        fab_expense_btn = view.findViewById(R.id.expense_ft_btn);

        // connect floating text

        fab_income_txt = view.findViewById(R.id.income_ft_text);
        fab_expense_txt = view.findViewById(R.id.expense_ft_text);

        // animation connect

        fadeOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_open);
        fadeClose = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData(accounts);

                if (isOpen) {

                    fab_income_btn.startAnimation(fadeClose);
                    fab_expense_btn.startAnimation(fadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(fadeClose);
                    fab_expense_txt.startAnimation(fadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen = false;

                } else {

                    fab_income_btn.startAnimation(fadeOpen);
                    fab_expense_btn.startAnimation(fadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(fadeOpen);
                    fab_expense_txt.startAnimation(fadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen = true;

                }

            }
        });

        btnProducts = view.findViewById(R.id.products);
        btnCafe = view.findViewById(R.id.cafe);
        btnLeisure = view.findViewById(R.id.leisure);
        btnTransport = view.findViewById(R.id.transport);
        btnGifts = view.findViewById(R.id.gifts);
        btnPurchases = view.findViewById(R.id.purchases);
        btnOther = view.findViewById(R.id.other);
        btnSport = view.findViewById(R.id.sport);
        btnTravel = view.findViewById(R.id.travel);
        btnFamily = view.findViewById(R.id.family);
        btnHealth = view.findViewById(R.id.health);

        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 0);
            }
        });
        btnCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 4);
            }
        });
        btnLeisure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 3);
            }
        });
        btnTransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 2);
            }
        });
        btnGifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 7);
            }
        });
        btnPurchases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 6);
            }
        });
        btnOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 10);
            }
        });
        btnSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 9);
            }
        });
        btnTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 8);
            }
        });
        btnFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 1);
            }
        });
        btnHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 5);
            }
        });

        return view;
    }


    private void ftAnimation() {
        if (isOpen) {

            fab_income_btn.startAnimation(fadeClose);
            fab_expense_btn.startAnimation(fadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(fadeClose);
            fab_expense_txt.startAnimation(fadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen = false;

        } else {

            fab_income_btn.startAnimation(fadeOpen);
            fab_expense_btn.startAnimation(fadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(fadeOpen);
            fab_expense_txt.startAnimation(fadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;

        }
    }

    private void addData(final ArrayList<String> accounts) {
        // Fab button income

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert(accounts);
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert(accounts, 0);
            }
        });
    }

    public void incomeDataInsert(ArrayList<String> accounts) {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText edtAmount = myview.findViewById(R.id.amount_edt);
        final EditText edtNote = myview.findViewById(R.id.note_edt);

        String[] incomeType = {"Зарплата", "Сбережения", "Подарки", "Премия", "Проценты", "Другое"};

        // spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, incomeType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtType = myview.findViewById(R.id.type_edt);
        edtType.setAdapter(adapter);
        edtType.setPrompt("Title");
        edtType.setSelection(0);

        ArrayAdapter<String> adapterAccounts = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        adapterAccounts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtAccount = myview.findViewById(R.id.account_edt);
        edtAccount.setAdapter(adapterAccounts);
        edtAccount.setPrompt("Title");
        edtAccount.setSelection(0);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = edtType.getSelectedItem().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();
                String account = edtAccount.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Обязательное поле");
                    return;
                }

                int ourAmountInt = Integer.parseInt(amount);

                String id = mChangeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(ourAmountInt, type, "income", note, id, mDate, account);
                mChangeDatabase.child(id).setValue(data);
                for (int i = 0; i < accountsList.size(); i++) {
                    if (accountsList.get(i).getName().equals(account)) {
                        int newAmount = accountsList.get(i).getAmount() + ourAmountInt;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                }
                Toast.makeText(getActivity(), "Данные сохранены", Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void expenseDataInsert(ArrayList<String> accounts, int i) {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText amount = myview.findViewById(R.id.amount_edt);
        final EditText note = myview.findViewById(R.id.note_edt);

        String[] expenseType = {"Продукты", "Семья", "Транспорт", "Досуг", "Кафе", "Здоровье", "Покупки", "Подарки", "Поездки", "Спорт", "Другое"};

        // spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, expenseType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtType = myview.findViewById(R.id.type_edt);
        edtType.setAdapter(adapter);
        edtType.setPrompt("Title");
        edtType.setSelection(i);

        ArrayAdapter<String> adapterAccounts = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        adapterAccounts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtAccount = myview.findViewById(R.id.account_edt);
        edtAccount.setAdapter(adapterAccounts);
        edtAccount.setPrompt("Title");
        edtAccount.setSelection(0);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmAmount = amount.getText().toString().trim();
                String tmType = edtType.getSelectedItem().toString().trim();
                String tmNote = note.getText().toString().trim();
                String account = edtAccount.getSelectedItem().toString().trim();

                if (TextUtils.isEmpty(tmAmount)) {
                    amount.setError("Обязательное поле");
                    return;
                }

                int inAmount = Integer.parseInt(tmAmount);

                String id = mChangeDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(inAmount, tmType, "expense", tmNote, id, mDate, account);

                mChangeDatabase.child(id).setValue(data);
                for (int i = 0; i < accountsList.size(); i++) {
                    if (accountsList.get(i).getName().equals(account)) {
                        int newAmount = accountsList.get(i).getAmount() - inAmount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                }
                Toast.makeText(getActivity(), "Данные сохранены", Toast.LENGTH_SHORT).show();

                if (isOpen) ftAnimation();
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}