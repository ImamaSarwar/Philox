package model;

public enum ApplicationStatus {
    //0: Pending, 1: Accepted, -1: Rejected, 2: finalised, -2: withdrawn (bc it was flagged
    PENDING(0),
    ACCEPTED(1),
    REJECTED(-1),
    FINALISED(2),
    WITHDRAWN(-2);

    private final int code;

    ApplicationStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ApplicationStatus fromCode(int code) {
        return switch (code) {
            case 0 -> PENDING;
            case 1 -> ACCEPTED;
            case -1 -> REJECTED;
            case 2 -> FINALISED;
            case -2 -> WITHDRAWN;
            default -> PENDING;
        };
    }
}
