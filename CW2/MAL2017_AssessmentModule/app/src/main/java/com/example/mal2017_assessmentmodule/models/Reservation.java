package com.example.mal2017_assessmentmodule.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Reservation Model - Represents a table reservation.
 *
 * Lifecycle States:
 * - Pending: Newly created reservation
 * - Confirmed: Approved by staff
 * - Cancelled: Cancelled by guest or staff
 * - Completed: Guest has visited
 *
 * @author BSCS2509254
 * @version 1.0
 */
public class Reservation {

    private int reservationId;
    private int userId;  // Foreign key to User
    private String guestName;
    private String guestEmail;
    private String guestContact;
    private int partySize;
    private long dateTime;  // Timestamp in milliseconds
    private String notes;
    private String status;  // pending, confirmed, cancelled, completed

    public Reservation() {}

    public Reservation(int userId, String guestName, String guestEmail, String guestContact,
                       int partySize, long dateTime, String notes, String status) {
        this.userId = userId;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestContact = guestContact;
        this.partySize = partySize;
        this.dateTime = dateTime;
        this.notes = notes;
        this.status = status;
    }

    // Getters and Setters
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getGuestContact() { return guestContact; }
    public void setGuestContact(String guestContact) { this.guestContact = guestContact; }

    public int getPartySize() { return partySize; }
    public void setPartySize(int partySize) { this.partySize = partySize; }

    public long getDateTime() { return dateTime; }
    public void setDateTime(long dateTime) { this.dateTime = dateTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * Format date for display (e.g., "Dec 25, 2025")
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(dateTime));
    }

    /**
     * Format time for display (e.g., "7:30 PM")
     */
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date(dateTime));
    }

    /**
     * Get party size text (e.g., "4 people")
     */
    public String getPartySizeText() {
        return partySize == 1 ? "1 person" : partySize + " people";
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", guestName='" + guestName + '\'' +
                ", partySize=" + partySize +
                ", status='" + status + '\'' +
                '}';
    }
}