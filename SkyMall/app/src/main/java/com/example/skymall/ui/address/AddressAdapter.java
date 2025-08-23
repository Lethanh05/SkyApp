package com.example.skymall.ui.address;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skymall.R;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.VH> {

    public interface Listener {
        void onSetDefault(Address a);
        void onEdit(Address a);
        void onDelete(Address a);
    }

    private final Listener listener;
    private final List<Address> items = new ArrayList<>();

    public AddressAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submit(List<Address> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Address a = items.get(position);
        h.tvNamePhone.setText(a.name + " · " + a.phone);
        h.tvFullAddress.setText(a.fullAddress());

        if (a.note != null && !a.note.trim().isEmpty()) {
            h.tvNote.setText("Ghi chú: " + a.note);
            h.tvNote.setVisibility(View.VISIBLE);
        } else h.tvNote.setVisibility(View.GONE);

        h.tvDefault.setVisibility(a.isDefault ? View.VISIBLE : View.GONE);
        h.btnSetDefault.setVisibility(a.isDefault ? View.GONE : View.VISIBLE);

        h.btnSetDefault.setOnClickListener(v -> listener.onSetDefault(a));
        h.btnEdit.setOnClickListener(v -> listener.onEdit(a));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(a));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNamePhone, tvFullAddress, tvNote, tvDefault;
        Button btnSetDefault, btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvDefault = itemView.findViewById(R.id.tvDefault);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
