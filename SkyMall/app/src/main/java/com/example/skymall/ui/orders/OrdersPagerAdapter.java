package com.example.skymall.ui.orders;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrdersPagerAdapter extends FragmentStateAdapter {
    private final String[] statuses;
    public OrdersPagerAdapter(@NonNull Fragment host, String[] statuses) {
        super(host);
        this.statuses = statuses;
    }
    @NonNull @Override public Fragment createFragment(int position) {
        return OrderListFragment.newInstance(statuses[position]);
    }
    @Override public int getItemCount() { return statuses.length; }
}
