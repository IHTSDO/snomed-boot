package org.ihtsdo.otf.snomedboot;

public class ReleaseImportException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReleaseImportException(String message) {
		super(message);
	}

	public ReleaseImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
