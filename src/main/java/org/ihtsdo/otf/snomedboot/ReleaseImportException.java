package org.ihtsdo.otf.snomedboot;

import java.io.Serial;

public class ReleaseImportException extends Exception {

	@Serial
	private static final long serialVersionUID = 1L;

	public ReleaseImportException(String message) {
		super(message);
	}

	public ReleaseImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
