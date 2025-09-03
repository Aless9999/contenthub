package org.macnigor.contenthub.exeption;

import java.io.IOException;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message,Throwable cause) {
        super(message,cause);
    }
}
