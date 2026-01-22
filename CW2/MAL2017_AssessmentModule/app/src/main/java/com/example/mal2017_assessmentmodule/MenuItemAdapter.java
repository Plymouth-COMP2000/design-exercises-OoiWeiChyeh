package com.example.mal2017_assessmentmodule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mal2017_assessmentmodule.models.MenuItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuItemAdapter - FIXED to load local drawable images
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    private static final String TAG = "MenuItemAdapter";

    private Context context;
    private List<MenuItem> menuItems;
    private List<MenuItem> menuItemsFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public MenuItemAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
        this.menuItemsFull = new ArrayList<>(menuItems);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_guest_view_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            MenuItem item = menuItems.get(position);

            holder.tvName.setText(item.getName());
            holder.tvDescription.setText(item.getDescription());
            holder.tvPrice.setText(item.getFormattedPrice());
            holder.tvCategory.setText(item.getCategory());

            // Load image from drawable folder
            loadImageFromDrawable(holder.ivImage, item.getImageUrl());

            // Set availability indicator
            if (!item.isAvailable()) {
                holder.tvAvailability.setVisibility(View.VISIBLE);
                holder.cardView.setAlpha(0.6f);
            } else {
                holder.tvAvailability.setVisibility(View.GONE);
                holder.cardView.setAlpha(1.0f);
            }

            // Click listener
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error binding item: " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /**
     * Load image from drawable folder
     */
    private void loadImageFromDrawable(ImageView imageView, String imageFileName) {
        try {
            if (imageFileName == null || imageFileName.isEmpty()) {
                imageView.setImageResource(R.drawable.ic_menu_placeholder);
                return;
            }

            // Get resource ID from drawable name
            int resourceId = context.getResources().getIdentifier(
                    imageFileName,
                    "drawable",
                    context.getPackageName()
            );

            if (resourceId != 0) {
                // Image found in drawable
                Glide.with(context)
                        .load(resourceId)
                        .placeholder(R.drawable.ic_menu_placeholder)
                        .error(R.drawable.ic_menu_placeholder)
                        .centerCrop()
                        .into(imageView);

                Log.d(TAG, "Loaded image: " + imageFileName);
            } else {
                // Image not found, use placeholder
                Log.w(TAG, "Image not found: " + imageFileName);
                imageView.setImageResource(R.drawable.ic_menu_placeholder);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + e.getMessage(), e);
            imageView.setImageResource(R.drawable.ic_menu_placeholder);
        }
    }

    /**
     * Filter menu items by search query
     */
    public void filter(String query) {
        menuItems.clear();

        if (query.isEmpty()) {
            menuItems.addAll(menuItemsFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (MenuItem item : menuItemsFull) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    menuItems.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Filter by category
     */
    public void filterByCategory(String category) {
        menuItems.clear();

        if (category.equals("All")) {
            menuItems.addAll(menuItemsFull);
        } else {
            for (MenuItem item : menuItemsFull) {
                if (item.getCategory().equals(category)) {
                    menuItems.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Update full dataset
     */
    public void updateData(List<MenuItem> newItems) {
        this.menuItems = newItems;
        this.menuItemsFull = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        ImageView ivImage;
        TextView tvName;
        TextView tvDescription;
        TextView tvPrice;
        TextView tvCategory;
        TextView tvAvailability;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            ivImage = itemView.findViewById(R.id.iv_menu_image);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            tvPrice = itemView.findViewById(R.id.tv_item_price);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAvailability = itemView.findViewById(R.id.tv_availability);
        }
    }
}