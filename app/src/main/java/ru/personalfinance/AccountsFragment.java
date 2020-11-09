package ru.personalfinance;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ru.personalfinance.Model.Account;

public class AccountsFragment extends Fragment {

    // Floating button

    private FloatingActionButton fab_main_btn;

    Button btnUpdate;
    Button btnDelete;

    // animation

    private Animation fadeOpen, fadeClose;

    // income and expense result

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    // Recyclerview

    private RecyclerView recyclerView;

    // Data item value

    private String type;
    private String name;
    private int amount;

    private String post_key;

    // TextView

    private TextView moneyAmount;
    private TextView moneyName;

    // Update EditText

    private EditText edtAmount;
    private Spinner edtType;
    private EditText edtName;

    // Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mAccountDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_accounts, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mAccountDatabase = FirebaseDatabase.getInstance().getReference().child("AccountData").child(uid);

        fab_main_btn = myview.findViewById(R.id.fb_account_plus_btn);


//////////
        recyclerView = myview.findViewById(R.id.recycler_id_account);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

//        mAccountDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                int totalValue = 0;
//
//                for (DataSnapshot mySnapshot : snapshot.getChildren()) {
//
//                    Account account = mySnapshot.getValue(Account.class);
////                    if (data.getChange().equals("income")) {
////                        totalValue += data.getAmount();
////                    }
//                    String strTotalValue = String.valueOf(totalValue);
//                    incomeTotalSum.setText(strTotalValue);
//
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
////////











        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accountDataInsert();

            }
        });



        // calculate total income

//        mChangeDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                int totalsum = 0;
//
//                for (DataSnapshot mysnap : snapshot.getChildren()) {
//                    Data data = mysnap.getValue(Data.class);
//                    if (data.getChange().equals("income")) {
//                        totalsum += data.getAmount();
//                    }
//                }
//
//                String strResult = String.valueOf(totalsum);
//                totalIncomeResult.setText(strResult);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        // calculate total expense

//        mChangeDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                int totalsum = 0;
//
//                for (DataSnapshot mysnap : snapshot.getChildren()) {
//                    Data data = mysnap.getValue(Data.class);
//                    if (data.getChange().equals("expense")) {
//                        totalsum += data.getAmount();
//                    }
//                }
//
//                String strResult = String.valueOf(totalsum);
//                totalExpenseResult.setText(strResult);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        return myview;
    }

    public void accountDataInsert() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertaccount, null);
        mydialog.setView(myview);
        final AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        final EditText edtAmount = myview.findViewById(R.id.amount_edt_account);
        final EditText edtName = myview.findViewById(R.id.name_edt_account);

        String[] accountType = {"Наличные", "Карта"};

        // spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountType);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtType = myview.findViewById(R.id.type_edt_account);
        edtType.setAdapter(adapter);
        edtType.setPrompt("Title");
        edtType.setSelection(0);

        Button btnSave = myview.findViewById(R.id.btnSave_account);
        Button btnCancel = myview.findViewById(R.id.btnCancel_account);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = edtType.getSelectedItem().toString().trim();
                String amount = edtAmount.getText().toString().trim();
                String name = edtName.getText().toString().trim();

                if (TextUtils.isEmpty(amount)) {
                    edtAmount.setError("Обязательное поле");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Обязательное поле");
                    return;
                }

                int ourAmountInt = Integer.parseInt(amount);

                String id = mAccountDatabase.push().getKey();
                Account account = new Account(ourAmountInt, type, name, id);

                mAccountDatabase.child(id).setValue(account);
                Toast.makeText(getActivity(), "Данные сохранены", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Account, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Account, MyViewHolder>
                (
                        Account.class,
                        R.layout.account_recycler_data,
                        MyViewHolder.class,
                        mAccountDatabase
                ) {
            @Override
            protected void populateViewHolder(final MyViewHolder myViewHolder, final Account model, final int position) {

                myViewHolder.setName(model.getName());
                myViewHolder.setImage(model.getType());
                myViewHolder.setAmount(model.getAmount());

                myViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(myViewHolder.getAdapterPosition()).getKey();

                        type = model.getType();
                        name = model.getName();
                        amount = model.getAmount();

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

        private void setImage(String type) {
            ImageView imageView = mView.findViewById(R.id.image_account);
            if (type.equals("Карта")) {
                imageView.setImageResource(R.drawable.ic_credit_card);
            }
        }

        private void setName(String name) {
            TextView mName = mView.findViewById(R.id.name_txt_account);
            mName.setText(name);
        }

        private void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.amount_txt_account);
            String strAmount = String.valueOf(amount);
            mAmount.setText(String.format("%s ₽", strAmount));
        }

    }

    private void updateDataItem() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_account_item, null);
        mydialog.setView(myview);

        edtAmount = myview.findViewById(R.id.amount_edt_account);
        edtType = myview.findViewById(R.id.type_edt_account);
        edtName = myview.findViewById(R.id.name_edt_account);

        String[] accountType = {"Наличные", "Карта"};

        // spinner
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, accountType);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner edtType = myview.findViewById(R.id.type_edt_account);
        edtType.setAdapter(adapter);
        edtType.setPrompt("Title");
        int spinnerPosition = adapter.getPosition(type);
        edtType.setSelection(spinnerPosition);

        // set data to EditText

        edtName.setText(name);
        edtName.setSelection(name.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdate_account);
        btnDelete = myview.findViewById(R.id.btnDelete_account);

        final AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getSelectedItem().toString().trim();
                name = edtName.getText().toString().trim();
                String mAmount = String.valueOf(amount);
                mAmount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(mAmount);
                Account account = new Account(myAmount, type, name, post_key);

                mAccountDatabase.child(post_key).setValue(account);
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
                mAccountDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

}