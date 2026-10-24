package com.example.datdouongpromac2.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DrinkTabAdapter extends FragmentStateAdapter {
    public DrinkTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String[] categories = {"Cà Phê", "Trà Sữa", "Nước Khác"};
        return DrinkTabFragment.newInstance(categories[position]);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
