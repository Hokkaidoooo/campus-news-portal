package com.citywy.storage;

import java.io.IOException;

public final class UploadException extends IOException {
    private final int status;
    public UploadException(int status, String message) { super(message); this.status = status; }
    public int getStatus() { return status; }
}
