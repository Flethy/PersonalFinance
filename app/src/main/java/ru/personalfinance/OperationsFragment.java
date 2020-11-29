package ru.personalfinance;

import android.annotation.SuppressLint;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class OperationsFragment extends Fragment {

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

    // income and expense result

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    // Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mChangeDatabase;
    private DatabaseReference mAccountDatabase;

    // Recyclerview

    private RecyclerView recyclerView;

    // TextView

    private TextView incomeTotalSum;
    private TextView expenseTotalSum;

    // Update EditText

    private EditText edtAmount;
    private Spinner edtType;
    private EditText edtNote;

    // Button for update and delete

    private Button btnUpdate;
    private Button btnDelete;

    // Data item value

    private String account;
    private String type;
    private String note;
    private String change;
    private int amount;

    private List<Account> accountsList;

    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_operations, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mChangeDatabase = FirebaseDatabase.getInstance().getReference().child("ChangeData").child(uid);
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





        recyclerView = myview.findViewById(R.id.recycler_id_income);

        incomeTotalSum = myview.findViewById(R.id.income_txt_result);
        expenseTotalSum = myview.findViewById(R.id.expense_txt_result);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mChangeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalValue = 0;

                for (DataSnapshot mySnapshot : snapshot.getChildren()) {

                    Data data = mySnapshot.getValue(Data.class);
                    if (data.getChange().equals("income")) {
                        totalValue += data.getAmount();
                    }
                    String strTotalValue = String.valueOf(totalValue);
                    incomeTotalSum.setText(strTotalValue);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mChangeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalValue = 0;

                for (DataSnapshot mySnapshot : snapshot.getChildren()) {

                    Data data = mySnapshot.getValue(Data.class);
                    if (data.getChange().equals("expense")) {
                        totalValue += data.getAmount();
                    }
                    String strTotalValue = String.valueOf(totalValue);
                    expenseTotalSum.setText(strTotalValue);

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

        fab_main_btn = myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn = myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn = myview.findViewById(R.id.expense_ft_btn);

        // connect floating text

        fab_income_txt = myview.findViewById(R.id.income_ft_text);
        fab_expense_txt = myview.findViewById(R.id.expense_ft_text);

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

        return myview;
    }

    // Floating button animation

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
                expenseDataInsert(accounts);
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

    public void expenseDataInsert(ArrayList<String> accounts) {

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

    @Override
    public void onStart() {
        super.onStart();

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

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.income_recycler_data,
                        MyViewHolder.class,
                        mChangeDatabase
                ) {
            @Override
            protected void populateViewHolder(final MyViewHolder myViewHolder, final Data model, final int position) {

                myViewHolder.setType(model.getType());
                myViewHolder.setNote(model.getNote());
                myViewHolder.setDate(model.getDate());
                myViewHolder.setAccount(model.getAccount());
                myViewHolder.setAmount(model.getAmount(), model.getChange());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(myViewHolder.getAdapterPosition()).getKey();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        change = model.getChange();
                        account = model.getAccount();

                        updateDataItem(accounts);
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        private void setAccount(String account) {
            TextView accountTxt = mView.findViewById(R.id.account_txt_income);
            accountTxt.setText(account);
        }

        @SuppressLint("ResourceAsColor")
        private void setAmount(int amount, String change) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String strAmount = String.valueOf(amount);
            if (change.equals("income")) {
                mAmount.setText(String.format("+ %s ₽", strAmount));
                mAmount.setTextColor(Color.parseColor("#699A68"));
            } else if (change.equals("expense")) {
                mAmount.setText(String.format("%s ₽", strAmount));
            }
        }

    }

    private void updateDataItem(ArrayList<String> accounts) {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        String[] incomeType = {"Зарплата", "Сбережения", "Подарки", "Премия", "Проценты", "Другое"};
        String[] expenseType = {"Продукты", "Семья", "Транспорт", "Досуг", "Кафе", "Здоровье", "Покупки", "Подарки", "Поездки", "Спорт", "Другое"};

        // spinner
        ArrayAdapter<String> adapter;
        if (change.equals("income")) {
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, incomeType);
        } else {
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, expenseType);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtType = myview.findViewById(R.id.type_edt);
        edtType.setAdapter(adapter);
        edtType.setPrompt("Title");
        int spinnerPosition = adapter.getPosition(type);
        edtType.setSelection(spinnerPosition);

        ArrayAdapter<String> adapterAccounts = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        adapterAccounts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtAccount = myview.findViewById(R.id.account_edt);
        edtAccount.setAdapter(adapterAccounts);
        edtAccount.setPrompt("Title");
        int spinnerPositionAccount = adapterAccounts.getPosition(account);
        edtAccount.setSelection(spinnerPositionAccount);

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdate);
        btnDelete = myview.findViewById(R.id.btnDelete);

        final AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < accountsList.size(); i++) {
                    if (accountsList.get(i).getName().equals(account) && change.equals("expense")) {
                        int newAmount = accountsList.get(i).getAmount() + amount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                    if (accountsList.get(i).getName().equals(account) && change.equals("income")) {
                        int newAmount = accountsList.get(i).getAmount() - amount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                }

                type = edtType.getSelectedItem().toString().trim();
                account = edtAccount.getSelectedItem().toString().trim();
                note = edtNote.getText().toString().trim();
                String mAmount = String.valueOf(amount);
                mAmount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(mAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(myAmount, type, change, note, post_key, mDate, account);

                mChangeDatabase.child(post_key).setValue(data);
                for (int i = 0; i < accountsList.size(); i++) {
                    if (accountsList.get(i).getName().equals(account) && change.equals("income")) {
                        int newAmount = accountsList.get(i).getAmount() + myAmount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                    if (accountsList.get(i).getName().equals(account) && change.equals("expense")) {
                        int newAmount = accountsList.get(i).getAmount() - myAmount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                }

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < accountsList.size(); i++) {
                    if (accountsList.get(i).getName().equals(account) && change.equals("expense")) {
                        int newAmount = accountsList.get(i).getAmount() + amount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                    if (accountsList.get(i).getName().equals(account) && change.equals("income")) {
                        int newAmount = accountsList.get(i).getAmount() - amount;
                        accountsList.get(i).setAmount(newAmount);
                        mAccountDatabase.child(accountsList.get(i).getId()).setValue(accountsList.get(i));
                        break;
                    }
                }

                mChangeDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

}