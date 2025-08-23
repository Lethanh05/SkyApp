package com.example.skymall.ui.address;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AddressListFragment extends Fragment implements AddressAdapter.Listener {

    private AddressAdapter adapter;
    private SwipeRefreshLayout srl;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_address_list, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = v.findViewById(R.id.rvAddresses);
        srl = v.findViewById(R.id.srlRefresh);
        FloatingActionButton fab = v.findViewById(R.id.fabAdd);

        adapter = new AddressAdapter(this);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        srl.setOnRefreshListener(() -> {
            adapter.submit(AddressRepository.get().list());
            srl.setRefreshing(false);
        });

        fab.setOnClickListener(view -> openAddAddress(null));

        // Load lần đầu
        adapter.submit(AddressRepository.get().list());
    }

    private void openAddAddress(@Nullable Address edit) {
        Fragment f = new AddAddressFragment();
        if (edit != null) {
            Bundle b = new Bundle();
            b.putSerializable("edit", edit);
            f.setArguments(b);
        }
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, f)  // Sửa từ fragment_container_view thành fragment_container
                .addToBackStack("AddAddress")
                .commit();
    }

    // Adapter callbacks
    @Override public void onSetDefault(Address a) {
        AddressRepository.get().setDefault(a.id);
        adapter.submit(AddressRepository.get().list());
        Toast.makeText(getContext(), "Đã đặt làm mặc định", Toast.LENGTH_SHORT).show();
    }

    @Override public void onEdit(Address a) {
        openAddAddress(a);
    }

    @Override public void onDelete(Address a) {
        AddressRepository.get().remove(a.id);
        adapter.submit(AddressRepository.get().list());
        Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
    }

    @Override public void onResume() {
        super.onResume();
        // Refresh khi quay lại từ màn thêm/sửa
        if (adapter != null) adapter.submit(AddressRepository.get().list());
    }
}
