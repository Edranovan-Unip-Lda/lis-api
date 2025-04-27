package tl.gov.mci.lis.enums;

public enum EmailTemplate {
    ACTIVATION, OTP;

    @Override
    public String toString() {
        // Return the name of the template as stored in your templates directory (e.g., Thymeleaf template name)
        return switch (this) {
            case ACTIVATION -> "activation";
            case OTP -> "otp";
        };
    }
}
