package ru.zakharov.models;

public enum FriendshipStatuses {

    ACCEPTED("accepted"), REQUESTED("requested"),
    OUTGOING_REQUEST("outgoingRequests"), NOT_EXISTS("not_exists");

    private final String status;

    FriendshipStatuses(String status) {
        this.status = status;
    }

    public String getStatus() {return status;}

}
