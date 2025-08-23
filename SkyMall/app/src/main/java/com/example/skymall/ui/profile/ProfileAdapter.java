package com.example.skymall.ui.profile;

import android.view.*; import android.widget.*;
import androidx.annotation.NonNull; import androidx.recyclerview.widget.RecyclerView;
import com.example.skymall.R; import java.util.*;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnClick { void onClick(ProfileItem item, int pos); }
    private final List<ProfileItem> data; private final OnClick onClick;

    public ProfileAdapter(List<ProfileItem> data, OnClick onClick) { this.data=data; this.onClick=onClick; }

    @Override public int getItemViewType(int position){ return data.get(position).type; }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        if (vt==ProfileItem.SECTION){
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_section,p,false);
            return new SectionVH(v);
        } else if (vt==ProfileItem.HEADER){
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_header,p,false);
            return new HeaderVH(v);
        } else if (vt==ProfileItem.QUICK_ACTIONS){
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_quick_actions,p,false);
            return new QuickActionsVH(v);
        } else if (vt==ProfileItem.ORDER_TRACKING){
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_order_tracking,p,false);
            return new OrderTrackingVH(v);
        } else {
            View v=LayoutInflater.from(p.getContext()).inflate(R.layout.item_profile_row,p,false);
            return new RowVH(v);
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        ProfileItem it = data.get(pos);
        if (getItemViewType(pos)==ProfileItem.SECTION){
            ((SectionVH)h).tv.setText(it.title);
        } else if (getItemViewType(pos)==ProfileItem.HEADER){
            HeaderVH vh=(HeaderVH)h;
            vh.tvName.setText(it.title);
            vh.tvPhone.setText(it.subtitle);
            vh.btnEdit.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "EDIT_PROFILE"), pos));

            if (it.avatarUrl != null && !it.avatarUrl.isEmpty()) {
                android.util.Log.d("ProfileAdapter", "Loading avatar from URL: " + it.avatarUrl);
                Glide.with(vh.itemView.getContext())
                    .load(it.avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            android.util.Log.e("ProfileAdapter", "Failed to load avatar: " + it.avatarUrl, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            android.util.Log.d("ProfileAdapter", "Successfully loaded avatar: " + it.avatarUrl);
                            return false;
                        }
                    })
                    .into(vh.imgAvatar);
            } else {
                android.util.Log.d("ProfileAdapter", "No avatar URL, using default icon");
                vh.imgAvatar.setImageResource(R.drawable.ic_avatar);
            }
        } else if (getItemViewType(pos)==ProfileItem.QUICK_ACTIONS){
            QuickActionsVH vh=(QuickActionsVH)h;
            vh.qaOrders.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "ORDERS"), pos));
            vh.qaVoucher.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "VOUCHER"), pos));
            vh.qaWallet.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "WALLET"), pos));
        } else if (getItemViewType(pos)==ProfileItem.ORDER_TRACKING){
            OrderTrackingVH vh=(OrderTrackingVH)h;
            vh.stepPending.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "ORDER_PENDING"), pos));
            vh.stepPacking.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "ORDER_PACKING"), pos));
            vh.stepShipping.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "ORDER_SHIPPING"), pos));
            vh.stepDelivered.setOnClickListener(v -> onClick.onClick(ProfileItem.row("", 0, null, "ORDER_DELIVERED"), pos));
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
    static class HeaderVH extends RecyclerView.ViewHolder {
        ShapeableImageView imgAvatar; TextView tvName, tvPhone; Button btnEdit;
        HeaderVH(View v){
            super(v);
            imgAvatar=v.findViewById(R.id.imgAvatar);
            tvName=v.findViewById(R.id.tvName);
            tvPhone=v.findViewById(R.id.tvPhone);
            btnEdit=v.findViewById(R.id.btnEditProfile);
        }
    }
    static class QuickActionsVH extends RecyclerView.ViewHolder {
        View qaOrders, qaVoucher, qaWallet;
        QuickActionsVH(View v){ super(v); qaOrders=v.findViewById(R.id.qaOrders); qaVoucher=v.findViewById(R.id.qaVoucher); qaWallet=v.findViewById(R.id.qaWallet); }
    }
    static class OrderTrackingVH extends RecyclerView.ViewHolder {
        View stepPending, stepPacking, stepShipping, stepDelivered;
        OrderTrackingVH(View v){
            super(v);
            stepPending=v.findViewById(R.id.stepPending);
            stepPacking=v.findViewById(R.id.stepPacking);
            stepShipping=v.findViewById(R.id.stepShipping);
            stepDelivered=v.findViewById(R.id.stepDelivered);
        }
    }
}
