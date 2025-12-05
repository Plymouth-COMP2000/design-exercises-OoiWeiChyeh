package com.example.mal2017_assessmentmodule;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ReservationAdapter - Adapter for displaying reservation items.
 * Stub implementation for Exercise 5 (UI only).
 * Full implementation will be completed in CW2.
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private Context context;

    public ReservationAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO (CW2): Inflate item_reservation.xml and create ViewHolder
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO (CW2): Bind data to views
    }

    @Override
    public int getItemCount() {
        return 0; // Return 0 for now (no data)
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            // TODO (CW2): Initialize views
        }
    }
}