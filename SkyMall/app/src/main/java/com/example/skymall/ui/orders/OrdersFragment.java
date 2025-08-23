package com.example.skymall.ui.orders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
 import com.example.skymall.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;

public class OrdersFragment extends Fragment {
    private final String[] STATUSES = new String[]{"paid","pending","processing","shipped","completed","cancelled"};
    private final String[] TITLES   = new String[]{"Đã thanh toán","Chờ xác nhận","Xử lý","Vận chuyển","Hoàn tất","Đã huỷ"};

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        // Setup back button
        ImageView btnBack = v.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> requireActivity().onBackPressed());

        // Setup search với EditText có sẵn
        EditText etSearch = v.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.length() > 2 || query.isEmpty()) {
                    performSearch(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        TabLayout tab = v.findViewById(R.id.tabOrders);
        ViewPager2 pager = v.findViewById(R.id.pagerOrders);
        pager.setAdapter(new OrdersPagerAdapter(this, STATUSES));

        new TabLayoutMediator(tab, pager, (t, pos) -> t.setText(TITLES[pos])).attach();

        // Check if a specific tab was requested
        Bundle args = getArguments();
        if (args != null && args.containsKey("tab")) {
            int tabIndex = args.getInt("tab", 0);
            if (tabIndex >= 0 && tabIndex < STATUSES.length) {
                pager.setCurrentItem(tabIndex, false);
            }
        }
    }

    private void performSearch(String query) {
        // TODO: Implement search functionality
        // This method will filter orders based on the search query
    }
}
