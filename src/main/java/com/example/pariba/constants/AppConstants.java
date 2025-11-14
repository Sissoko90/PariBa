package com.example.pariba.constants;

public class AppConstants {
    
    // JWT
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER = "Authorization";
    public static final long JWT_EXPIRATION_MS = 86400000L; // 24 heures
    
    // OTP
    public static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRATION_MINUTES = 5;
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";
    
    // Formats de date
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    // Limites
    public static final int MIN_GROUP_MEMBERS = 2;
    public static final int MAX_GROUP_MEMBERS = 100;
    public static final int MIN_TOURS = 2;
    public static final int MAX_TOURS = 100;
    public static final int MAX_GRACE_DAYS = 30;
    
    // Montants (en FCFA)
    public static final double MIN_CONTRIBUTION_AMOUNT = 1000.0;
    public static final double MAX_CONTRIBUTION_AMOUNT = 10000000.0;
    
    // Fichiers
    public static final long MAX_FILE_SIZE = 10485760L; // 10 MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/jpg"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
    
    // Notifications
    public static final int NOTIFICATION_REMINDER_DAYS_BEFORE = 3;
    public static final int NOTIFICATION_BATCH_SIZE = 100;
    
    // Export
    public static final String EXPORT_PDF_FORMAT = "PDF";
    public static final String EXPORT_XLSX_FORMAT = "XLSX";
    
    // Regex patterns
    public static final String PHONE_REGEX = "^\\+223[0-9]{8}$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    
    // Rôles
    public static final String ROLE_SUPERADMIN = "SUPERADMIN";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    
    // Actions d'audit
    public static final String AUDIT_CREATE_GROUP = "CREATE_GROUP";
    public static final String AUDIT_UPDATE_GROUP = "UPDATE_GROUP";
    public static final String AUDIT_DELETE_GROUP = "DELETE_GROUP";
    public static final String AUDIT_JOIN_GROUP = "JOIN_GROUP";
    public static final String AUDIT_LEAVE_GROUP = "LEAVE_GROUP";
    public static final String AUDIT_ADD_MEMBER = "ADD_MEMBER";
    public static final String AUDIT_REMOVE_MEMBER = "REMOVE_MEMBER";
    public static final String AUDIT_GENERATE_TOURS = "GENERATE_TOURS";
    public static final String AUDIT_PAY_CONTRIBUTION = "PAY_CONTRIBUTION";
    public static final String AUDIT_PAYMENT = "PAYMENT";
    public static final String AUDIT_PAYOUT_TOUR = "PAYOUT_TOUR";
    public static final String AUDIT_EXPORT_REQUEST = "EXPORT_REQUEST";
    public static final String AUDIT_CREATE_DELEGATION = "CREATE_DELEGATION";
    public static final String AUDIT_APPROVE_DELEGATION = "APPROVE_DELEGATION";
    public static final String AUDIT_REVOKE_DELEGATION = "REVOKE_DELEGATION";
    
    private AppConstants() {
        // Classe utilitaire, constructeur privé
    }
}
