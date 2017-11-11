package com.acpnctr.acpnctr.models;

/**
 * Defines a data structure for an object representing a list of clients
 */

public class ClientListItem {

    private String clientName;
    private String lastSessionDate;

    public ClientListItem(){
        // Default constructor required for calls to DataSnapshot.getValue(ClientListItem.class)
    }

    public ClientListItem(String clientName, String lastSessionDate) {
        this.lastSessionDate = lastSessionDate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getLastSessionDate() {
        return lastSessionDate;
    }

    public void setLastSessionDate(String lastSessionDate) {
        this.lastSessionDate = lastSessionDate;
    }
}
