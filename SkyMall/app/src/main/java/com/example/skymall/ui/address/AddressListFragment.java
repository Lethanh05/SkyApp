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

import java.util.List;

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

        srl.setOnRefreshListener(this::loadAddresses);
        fab.setOnClickListener(view -> openAddAddress(null));

        // Load addresses on first time
        loadAddresses();
    }

    private void loadAddresses() {
        AddressRepository.get(requireContext()).loadAddresses(new AddressRepository.AddressCallback() {
            @Override
            public void onSuccess(List<Address> addresses) {
                srl.setRefreshing(false);
                if (getContext() != null) {
                    adapter.submit(addresses);
                }
            }

            @Override
            public void onError(String error) {
                srl.setRefreshing(false);
                if (getContext() != null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    // Show cached data if available
                    adapter.submit(AddressRepository.get(requireContext()).list());
                }
            }
        });
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
        AddressRepository.get(requireContext()).setDefault(a.id, new AddressRepository.AddressActionCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Đã đặt làm mặc định", Toast.LENGTH_SHORT).show();
                    loadAddresses(); // Reload to reflect changes
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override public void onEdit(Address a) {
        openAddAddress(a);
    }

    @Override public void onDelete(Address a) {
        AddressRepository.get(requireContext()).remove(a.id, new AddressRepository.AddressActionCallback() {
            @Override
            public void onSuccess() {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                    loadAddresses(); // Reload to reflect changes
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        // Refresh khi quay lại từ màn thêm/sửa
        if (adapter != null) adapter.submit(AddressRepository.get(requireContext()).list());
    }
}
