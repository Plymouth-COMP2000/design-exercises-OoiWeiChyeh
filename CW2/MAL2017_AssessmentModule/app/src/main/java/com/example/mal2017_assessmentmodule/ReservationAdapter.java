package com.example.mal2017_assessmentmodule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mal2017_assessmentmodule.models.Reservation;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

/**
 * ReservationAdapter - FIXED RecyclerView adapter for displaying reservations
 */
public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private static final String TAG = "ReservationAdapter";

    private Context context;
    private List<Reservation> reservations;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Reservation reservation);
    }

    public ReservationAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
        Log.d(TAG, "Adapter created with " + this.reservations.size() + " items");
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.item_staff_view_reservation, parent, false);
            return new ViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (reservations == null || position >= reservations.size()) {
                Log.e(TAG, "Invalid position: " + position);
                return;
            }

            Reservation reservation = reservations.get(position);

            if (reservation == null) {
                Log.e(TAG, "Reservation at position " + position + " is NULL");
                return;
            }

            // Set guest name
            if (holder.tvGuestName != null) {
                String guestName = reservation.getGuestName();
                holder.tvGuestName.setText(guestName != null ? guestName : "Unknown Guest");
            }

            // Set date
            if (holder.tvDate != null) {
                try {
                    holder.tvDate.setText("Date: " + reservation.getFormattedDate());
                } catch (Exception e) {
                    holder.tvDate.setText("Date: N/A");
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                }
            }

            // Set time
            if (holder.tvTime != null) {
                try {
                    holder.tvTime.setText("Time: " + reservation.getFormattedTime());
                } catch (Exception e) {
                    holder.tvTime.setText("Time: N/A");
                    Log.e(TAG, "Error formatting time: " + e.getMessage());
                }
            }

            // Set party size
            if (holder.tvPartySize != null) {
                try {
                    holder.tvPartySize.setText("Party Size: " + reservation.getPartySizeText());
                } catch (Exception e) {
                    holder.tvPartySize.setText("Party Size: " + reservation.getPartySize());
                    Log.e(TAG, "Error formatting party size: " + e.getMessage());
                }
            }

            // Set status chip
            if (holder.chipStatus != null) {
                try {
                    String status = reservation.getStatus();
                    holder.chipStatus.setText(status != null ? status.toUpperCase() : "UNKNOWN");
                    setStatusChipColor(holder.chipStatus, status);
                } catch (Exception e) {
                    holder.chipStatus.setText("UNKNOWN");
                    Log.e(TAG, "Error setting status: " + e.getMessage());
                }
            }

            // Click listener
            if (holder.itemView != null) {
                holder.itemView.setOnClickListener(v -> {
                    try {
                        if (listener != null && reservation != null) {
                            listener.onItemClick(reservation);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error handling click: " + e.getMessage(), e);
                    }
                });
            }

        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder at position " + position + ": " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return reservations != null ? reservations.size() : 0;
    }

    private void setStatusChipColor(Chip chip, String status) {
        try {
            if (chip == null || status == null) return;

            int colorResId;
            switch (status.toLowerCase()) {
                case "confirmed":
                    colorResId = R.color.status_confirmed;
                    break;
                case "pending":
                    colorResId = R.color.status_pending;
                    break;
                case "cancelled":
                    colorResId = R.color.status_cancelled;
                    break;
                case "completed":
                    colorResId = R.color.status_completed;
                    break;
                default:
                    colorResId = R.color.status_pending;
            }
            chip.setChipBackgroundColorResource(colorResId);
        } catch (Exception e) {
            Log.e(TAG, "Error setting chip color: " + e.getMessage(), e);
        }
    }

    public void updateData(List<Reservation> newReservations) {
        try {
            this.reservations = newReservations != null ? newReservations : new ArrayList<>();
            notifyDataSetChanged();
            Log.d(TAG, "Data updated: " + this.reservations.size() + " items");
        } catch (Exception e) {
            Log.e(TAG, "Error updating data: " + e.getMessage(), e);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvGuestName;
        TextView tvDate;
        TextView tvTime;
        TextView tvPartySize;
        Chip chipStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                cardView = itemView.findViewById(R.id.card_reservation);
                tvGuestName = itemView.findViewById(R.id.tv_guest_name);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvTime = itemView.findViewById(R.id.tv_time);
                tvPartySize = itemView.findViewById(R.id.tv_party_size);
                chipStatus = itemView.findViewById(R.id.chip_status);

                // Log any null views
                if (tvGuestName == null) Log.e(TAG, "tvGuestName is NULL in ViewHolder");
                if (tvDate == null) Log.e(TAG, "tvDate is NULL in ViewHolder");
                if (tvTime == null) Log.e(TAG, "tvTime is NULL in ViewHolder");
                if (tvPartySize == null) Log.e(TAG, "tvPartySize is NULL in ViewHolder");
                if (chipStatus == null) Log.e(TAG, "chipStatus is NULL in ViewHolder");
            } catch (Exception e) {
                Log.e(TAG, "Error in ViewHolder constructor: " + e.getMessage(), e);
            }
        }
    }
}