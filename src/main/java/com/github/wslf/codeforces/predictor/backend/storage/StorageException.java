package com.github.wslf.codeforces.predictor.backend.storage;

import java.io.IOException;

public class StorageException extends IOException {
  public StorageException(Throwable cause) {
    super(cause);
  }

  public StorageException(String message) {
    super(message);
  }

  public StorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
