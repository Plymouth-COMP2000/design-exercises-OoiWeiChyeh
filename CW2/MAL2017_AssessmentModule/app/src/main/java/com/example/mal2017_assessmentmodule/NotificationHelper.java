package com.example.mal2017_assessmentmodule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mal2017_assessmentmodule.MyReservationsActivity;
import com.example.mal2017_assessmentmodule.R;
import com.example.mal2017_assessmentmodule.models.Reservation;

/**
 * NotificationHelper - Helper class for managing notifications.
 *
 * Features:
 * - Create notification channels
 * - Show reservation confirmation notifications
 * - Show reservation update notifications
 * - Handle notification clicks
 *
 * @author BSCS2509254
 * @version 2.0
 */
public class NotificationHelper {

    private Context context;
    private NotificationManager notificationManager;

    /**
     * Constructor - Initialize notification helper
     *
     * @param context Application context
     */
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels();
    }

    /**
     * Create notification channels for Android O and above
     * Required for showing notifications on Android 8.0+
     */
    private void createNotificationChannels() {
        // Only needed for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Reservation notifications channel
            NotificationChannel reservationChannel = new NotificationChannel(
                    Constants.CHANNEL_ID_RESERVATIONS,
                    Constants.CHANNEL_NAME_RESERVATIONS,
                    NotificationManager.IMPORTANCE_HIGH
            );
            reservationChannel.setDescription(Constants.CHANNEL_DESC_RESERVATIONS);
            reservationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(reservationChannel);
        }
    }

    /**
     * Show reservation confirmation notification
     * Called when a new reservation is created
     *
     * @param reservation The reservation object
     */
    public void showReservationConfirmation(Reservation reservation) {
        String title = "Reservation Confirmed!";
        String message = String.format(
                "Your reservation for %s on %s at %s has been confirmed.",
                reservation.getPartySizeText(),
                reservation.getFormattedDate(),
                reservation.getFormattedTime()
        );

        showReservationNotification(title, message, reservation.getReservationId());
    }

    /**
     * Show reservation status update notification
     * Called when reservation status changes
     *
     * @param reservation The updated reservation
     * @param newStatus The new status
     */
    public void showReservationUpdate(Reservation reservation, String newStatus) {
        String title = "Reservation Updated";
        String message;

        switch (newStatus.toLowerCase()) {
            case Constants.STATUS_CONFIRMED:
                message = "Your reservation has been confirmed!";
                break;
            case Constants.STATUS_CANCELLED:
                message = "Your reservation has been cancelled.";
                break;
            case Constants.STATUS_COMPLETED:
                message = "Thank you for dining with us!";
                break;
            default:
                message = "Your reservation status has been updated.";
        }

        showReservationNotification(title, message, reservation.getReservationId());
    }

    /**
     * Show pending reservation notification for staff
     * Called when a new reservation needs staff attention
     *
     * @param count Number of pending reservations
     */
    public void showPendingReservationsNotification(int count) {
        String title = "New Reservations";
        String message = String.format("You have %d pending reservation%s", count, count > 1 ? "s" : "");

        showReservationNotification(title, message, Constants.NOTIFICATION_ID_RESERVATION);
    }

    /**
     * Helper method to show a reservation notification
     *
     * @param title Notification title
     * @param message Notification message
     * @param notificationId Unique notification ID
     */
    private void showReservationNotification(String title, String message, int notificationId) {
        // Create intent to open reservations activity when notification is clicked
        Intent intent = new Intent(context, MyReservationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_RESERVATIONS)
                .setSmallIcon(R.drawable.ic_reservations)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 250, 500});

        // Show notification
        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * Cancel a specific notification
     *
     * @param notificationId Notification ID to cancel
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    /**
     * Cancel all notifications
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}