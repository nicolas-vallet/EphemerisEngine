package com.nzv.astro.ephemeris;

/**
 * The four instants at which the Sun's apparent longitude is a multiple of 90
 * degrees, following chapter 20 of Jean Meeus' <i>Astronomical Formulae for
 * Calculators</i>.
 * <p>
 * The associated integer {@code k} (0 to 3) is the value used in formula (20.1)
 * and (20.2): {@code k = 0} marks the March equinox (apparent longitude 0
 * degrees), {@code k = 1} the June solstice (90 degrees), {@code k = 2} the
 * September equinox (180 degrees) and {@code k = 3} the December solstice (270
 * degrees).
 */
public enum Season {

	MARCH_EQUINOX(0),
	JUNE_SOLSTICE(1),
	SEPTEMBER_EQUINOX(2),
	DECEMBER_SOLSTICE(3);

	private final int k;

	Season(int k) {
		this.k = k;
	}

	/**
	 * Returns the integer {@code k} (0 to 3) identifying this event in the
	 * chapter-20 formulae.
	 */
	public int getK() {
		return k;
	}
}
