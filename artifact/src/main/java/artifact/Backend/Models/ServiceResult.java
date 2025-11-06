package artifact.Backend.Models;

import java.util.HashMap;
import java.util.Map;

public class ServiceResult {
    private boolean success;
    private String globalError; // For popup alerts
    private Map<String, String> fieldErrors; // For specific labels (name, phone, etc)

    public ServiceResult() {
        this.success = true;
        this.fieldErrors = new HashMap<>();
    }

    public void addError(String field, String message) {
        this.fieldErrors.put(field, message);
        this.success = false;
    }

    public void setGlobalError(String message) {
        this.globalError = message;
        this.success = false;
    }

    public boolean isSuccess() { return success; }
    public String getGlobalError() { return globalError; }
    public String getFieldError(String field) { return fieldErrors.getOrDefault(field, ""); }
}