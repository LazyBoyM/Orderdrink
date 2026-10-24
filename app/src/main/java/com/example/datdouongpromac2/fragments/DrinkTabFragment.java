package com.example.datdouongpromac2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;
import com.example.datdouongpromac2.MainActivity;
import com.example.datdouongpromac2.cart.CartItem;
import com.example.datdouongpromac2.drink.Drink;
import com.example.datdouongpromac2.drink.DrinkAdapter;

import java.util.ArrayList;

public class DrinkTabFragment extends Fragment {
    private String category;
    private ListView listViewDrinks;
    private DatabaseHelper db;
    private DrinkAdapter adapter;
    private ArrayList<Drink> drinkList;

    public static DrinkTabFragment newInstance(String category) {
        DrinkTabFragment fragment = new DrinkTabFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drink_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewDrinks = view.findViewById(R.id.listViewTabDrinks);
        db = new DatabaseHelper(getContext());

        loadDrinks();


        listViewDrinks.setOnItemClickListener((parent, v, position, id) -> {
            Drink selectedDrink = drinkList.get(position);

            // Thêm vào static cart
            MainActivity.sharedCart.add(new CartItem(selectedDrink.getName(), Math.round(selectedDrink.getPrice()), 1));



            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.updateTotalPrice();  // Gọi hàm update
            }

            Toast.makeText(getContext(), "Thêm " + selectedDrink.getName(), Toast.LENGTH_SHORT).show();
        });
    }


    private void loadDrinks() {
        drinkList = db.getDrinksByCategory(category);
        adapter = new DrinkAdapter(getContext(), drinkList);
        listViewDrinks.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (drinkList != null && db != null) {
            drinkList.clear();
            drinkList.addAll(db.getDrinksByCategory(category));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}
