package com.nzv.astro.ephemeris;

/**
 * The four principal phases of the Moon, following chapter 32 of Jean Meeus'
 * <i>Astronomical Formulae for Calculators</i>.
 * <p>
 * Each phase is identified by the fractional part added to the integer lunation
 * number {@code k} in formula (32.1): an integer {@code k} gives a New Moon,
 * {@code k + 0.25} a First Quarter, {@code k + 0.50} a Full Moon and
 * {@code k + 0.75} a Last Quarter.
 */
public enum MoonPhase {

	NEW_MOON(0.00),
	FIRST_QUARTER(0.25),
	FULL_MOON(0.50),
	LAST_QUARTER(0.75);

	private final double fraction;

	MoonPhase(double fraction) {
		this.fraction = fraction;
	}

	/**
	 * Returns the fractional part (0.00, 0.25, 0.50 or 0.75) added to the integer
	 * lunation number {@code k} for this phase.
	 */
	public double getFraction() {
		return fraction;
	}
}
