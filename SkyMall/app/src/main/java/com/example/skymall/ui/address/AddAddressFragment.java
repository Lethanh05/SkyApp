package com.example.skymall.ui.address;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.skymall.R;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Toast;

public class AddAddressFragment extends Fragment {

    private TextInputEditText edName, edPhone, edLine1, edWard, edDistrict, edProvince, edNote;
    private CheckBox cbDefault;
    private Address editing;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_address, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        edName     = v.findViewById(R.id.edName);
        edPhone    = v.findViewById(R.id.edPhone);
        edLine1    = v.findViewById(R.id.edLine1);
        edWard     = v.findViewById(R.id.edWard);
        edDistrict = v.findViewById(R.id.edDistrict);
        edProvince = v.findViewById(R.id.edProvince);
        edNote     = v.findViewById(R.id.edNote);
        cbDefault  = v.findViewById(R.id.cbDefault);
        Button btnSave = v.findViewById(R.id.btnSave);

        if (getArguments() != null) {
            editing = (Address) getArguments().getSerializable("edit");
            if (editing != null) {
                edName.setText(editing.name);
                edPhone.setText(editing.phone);
                edLine1.setText(editing.line1);
                edWard.setText(editing.ward);
                edDistrict.setText(editing.district);
                edProvince.setText(editing.province);
                edNote.setText(editing.note);
                cbDefault.setChecked(editing.isDefault);
            }
        }

        btnSave.setOnClickListener(vw -> save());
    }

    private void save() {
        String name = str(edName), phone = str(edPhone), line1 = str(edLine1),
                ward = str(edWard), district = str(edDistrict), province = str(edProvince),
                note = str(edNote);
        boolean isDefault = cbDefault.isChecked();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(line1) || TextUtils.isEmpty(ward) ||
                TextUtils.isEmpty(district) || TextUtils.isEmpty(province)) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editing == null) {
            Address a = new Address();
            a.name = name; a.phone = phone;
            a.line1 = line1; a.ward = ward; a.district = district; a.province = province;
            a.note = note; a.isDefault = isDefault;
            AddressRepository.get().add(a);
            Toast.makeText(getContext(), "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show();
        } else {
            editing.name = name; editing.phone = phone;
            editing.line1 = line1; editing.ward = ward; editing.district = district; editing.province = province;
            editing.note = note; editing.isDefault = isDefault;
            AddressRepository.get().update(editing);
            Toast.makeText(getContext(), "Đã cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
        }

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private String str(TextInputEditText ed) {
        return ed.getText() == null ? "" : ed.getText().toString().trim();
    }
}
