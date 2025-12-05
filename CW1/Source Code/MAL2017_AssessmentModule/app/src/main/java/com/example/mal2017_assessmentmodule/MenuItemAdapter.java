package com.example.mal2017_assessmentmodule;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * MenuItemAdapter - Adapter for displaying menu items.
 * Stub implementation for Exercise 5 (UI only).
 */
public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

    private Context context;

    public MenuItemAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO (CW2): Inflate item_menu.xml and create ViewHolder
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO (CW2): Bind data to views
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
        }
    }
}