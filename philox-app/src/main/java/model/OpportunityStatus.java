package model;

public enum OpportunityStatus {
    OPEN(1),
    CLOSED(0),
    CANCELLED(-1),
    FLAGGED(-2);

    private final int code;

    OpportunityStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OpportunityStatus fromCode(int code) {
        return switch (code) {
            case 1 -> OPEN;
            case 0 -> CLOSED;
            case -1 -> CANCELLED;
            case -2 -> FLAGGED;
            default -> OPEN;
        };
    }

}
