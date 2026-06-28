package com.nzv.astro.ephemeris.lunar;

/**
 * Registry of available Moon-position models. Each constant maps an id to its resource folder and
 * lazily loads the corresponding {@link TableDrivenMoonModel} on first use.
 * <p>
 * {@link #AFFC_1900} is the abridged AFFC chapter-30 theory (epoch 1900.0, ~10&Prime; in
 * longitude). A higher-precision model would be added here as a sibling constant pointing at its
 * own folder; selecting it is then a one-line change at the call site.
 */
public enum MoonModels {

	/** AFFC chapter-30 abridged theory, epoch 1900.0. */
	AFFC_1900("/com/nzv/astro/ephemeris/lunar/affc-1900");

	private final String resourceBase;
	private volatile MoonPositionModel model;

	MoonModels(String resourceBase) {
		this.resourceBase = resourceBase;
	}

	/** @return the parsed model, loaded once and cached. */
	public MoonPositionModel getModel() {
		MoonPositionModel m = model;
		if (m == null) {
			synchronized (this) {
				m = model;
				if (m == null) {
					m = MoonTableLoader.load(resourceBase);
					model = m;
				}
			}
		}
		return m;
	}
}
