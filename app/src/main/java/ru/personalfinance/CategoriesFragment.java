package ru.personalfinance;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ru.personalfinance.Model.Data;

public class CategoriesFragment extends Fragment {

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

    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    if (data.getChange().equals("expense")) totalSum += data.getAmount();
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
                }

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



        return view;
    }
}