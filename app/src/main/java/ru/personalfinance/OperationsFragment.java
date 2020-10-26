package ru.personalfinance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

import ru.personalfinance.Model.Data;

public class OperationsFragment extends Fragment {

    // Firebase database

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mChangeDatabase;

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

    private String type;
    private String note;
    private String change;
    private int amount;

    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_operations, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

//        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
//        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mChangeDatabase = FirebaseDatabase.getInstance().getReference().child("ChangeData").child(uid);

        incomeTotalSum = myview.findViewById(R.id.income_txt_result);
        expenseTotalSum = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_income);

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


        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.income_recycler_data,
                        MyViewHolder.class,
                        mChangeDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, final Data model, final int position) {

                myViewHolder.setType(model.getType());
                myViewHolder.setNote(model.getNote());
                myViewHolder.setDate(model.getDate());
                myViewHolder.setAmount(model.getAmount(), model.getChange());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();

                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        change = model.getChange();

                        updateDataItem();
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

    private void updateDataItem() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt);
        edtType = myview.findViewById(R.id.type_edt);
        edtNote = myview.findViewById(R.id.note_edt);

        String[] incomeType = {"Зарплата", "Сбережения", "Подарки", "Премия", "Проценты", "Другое"};
        String[] expenseType = {"Продукты", "Семья", "Транспорт", "Досуг", "Кафе", "Здоровье", "Покупки", "Поездки", "Спорт", "Другое"};

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

        // set data to EditText

//        edtType.setSelected(type);
//        edtType.setSelection(type.length());

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
                type = edtType.getSelectedItem().toString().trim();
                note = edtNote.getText().toString().trim();
                String mAmount = String.valueOf(amount);
                mAmount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(mAmount);
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(myAmount, type, change, note, post_key, mDate);

                mChangeDatabase.child(post_key).setValue(data);
//                if (change.equals("income")) {
//                    mIncomeDatabase.child(post_key).setValue(data);
//                } else if (change.equals("expense")) {
//                    mExpenseDatabase.child(post_key).setValue(data);
//                }

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (change.equals("income")) {
//                    mIncomeDatabase.child(post_key).removeValue();
//                } else if (change.equals("expense")) {
//                    mExpenseDatabase.child(post_key).removeValue();
//                }
//                mIncomeDatabase.child(post_key).removeValue();
//                mExpenseDatabase.child(post_key).removeValue();
                mChangeDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

}