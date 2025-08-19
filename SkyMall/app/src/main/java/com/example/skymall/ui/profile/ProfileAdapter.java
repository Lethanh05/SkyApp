package com.example.skymall.ui.profile;

import android.view.*; import android.widget.*;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R; import java.util.*;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnClick { void onClick(ProfileItem item, int pos); }
    private final List<ProfileItem> data; private final OnClick onClick;

    public ProfileAdapter(List<ProfileItem> data, OnClick onClick) { this.data=data; this.onClick=onClick; }

    @Override public int getItemViewType(int position){ return data.get(position).type; }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        if (vt==ProfileItem.SECTION){
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_section,p,false);
            return new SectionVH(v);
        } else {
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_row,p,false);
            return new RowVH(v);
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        ProfileItem it = data.get(pos);
        if (getItemViewType(pos)==ProfileItem.SECTION){
            ((SectionVH)h).tv.setText(it.title);
        } else {
            RowVH vh=(RowVH)h;
            vh.tvTitle.setText(it.title);
            vh.imgIcon.setImageResource(it.iconRes);
            if (it.badge!=null && !it.badge.isEmpty()){
                vh.tvBadge.setVisibility(View.VISIBLE); vh.tvBadge.setText(it.badge);
            } else vh.tvBadge.setVisibility(View.GONE);
            vh.itemView.setOnClickListener(v -> onClick.onClick(it,pos));
        }
    }

    @Override public int getItemCount(){ return data.size(); }

    static class SectionVH extends RecyclerView.ViewHolder { TextView tv; SectionVH(View v){ super(v); tv=v.findViewById(android.R.id.text1); } }
    static class RowVH extends RecyclerView.ViewHolder {
        ImageView imgIcon; TextView tvTitle, tvBadge;
        RowVH(View v){ super(v); imgIcon=v.findViewById(R.id.imgIcon); tvTitle=v.findViewById(R.id.tvTitle); tvBadge=v.findViewById(R.id.tvBadge); }
    }
}
