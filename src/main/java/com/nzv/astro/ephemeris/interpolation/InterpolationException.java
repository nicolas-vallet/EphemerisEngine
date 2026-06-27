package com.nzv.astro.ephemeris.interpolation;

/**
 * An exception which can be raised during interpolation operations.
 */
@SuppressWarnings("serial")
public class InterpolationException extends Exception {

	public InterpolationException() {
		super();
	}

	public InterpolationException(String message) {
		super(message);
	}

	public InterpolationException(String message, Throwable cause) {
		super(message, cause);
	}
}
