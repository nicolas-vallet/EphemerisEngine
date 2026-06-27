# Changelog

## 1.3.0 — Phase 2 (step 1): the star keystone

This release implements the first of the two Phase 2 keystones, **Chapter 16 — Apparent
Place of a Star**, closing the star track's hard core. The suite grows from 63 to 68
tests, all passing.

### New features

- **Chapter 16 — Apparent Place of a Star** (`ApparentPlace` / `ApparentPlaceImpl`):
  reduces a mean catalogue position to its apparent place at a given instant by composing
  proper motion, precession (Ch 14), nutation (Ch 15) and annual aberration. The
  aberration includes the terms in the eccentricity of the Earth's orbit and uses the
  Sun's *true* longitude (Ch 18). Proper motions are supplied as plain coordinate rates
  in arcsec/year. The reduction also exposes its intermediate steps
  (`applyProperMotion`, `nutationCorrection`, `aberrationCorrection`) for inspection and
  testing. Validated on Theta Persei reduced to the house date 1978-11-13, with the
  nutation and aberration components cross-checked term by term.

### Conventions

- Right ascension stays in **degrees** throughout, matching the Phase 1 convention, so the
  apparent place feeds straight back into the coordinate-transformation adapters.

## 1.2.0 — Phase 1: enablers and quick wins

This release implements **Phase 1** of the phased implementation plan, closing six
chapters of *Astronomical Formulae for Calculators* that were previously absent or
only partially exposed. Every new computation ships with a regression test built on
a worked example or an independent physical check; the suite grows from 43 to 63
tests, all passing.

### New features

- **Chapter 18 — Solar Coordinates** (`EphemerisEngine` / `EphemerisEngineImpl`):
  added the equation of centre, the Sun's true and apparent longitude, true anomaly,
  radius vector, the mean obliquity of the ecliptic, and the Sun's apparent geocentric
  equatorial coordinates (`sunApparentEquatorialCoordinates`). Cross-checked against
  the canonical 1992-10-13 example to better than 0.01 degrees. This finishes the
  keystone enabler that both later tracks (Moon and star) depend on.
- **Chapter 14 — Precession** (`Precession` / `PrecessionImpl`): rigorous reduction of
  equatorial coordinates between two equinoxes via the precessional angles
  (zeta, z, theta), plus direct access to those angles. Annual rates reproduce the
  precessional constants m and n exactly.
- **Chapter 9 — Angular Separation** (`AngularSeparation`): great-circle separation and
  relative position angle between two bodies. Matches the Arcturus/Spica example
  (32.79 degrees).
- **Chapter 38 — Stellar Magnitudes** (`StellarMagnitudes`): Pogson's ratio, brightness
  ratio from a magnitude difference and vice versa, and combined magnitude of two or
  more bodies.
- **Chapter 6 — Observer Geocentric Coordinates** (`GeocentricCoordinates`): exposed
  clean `getRhoSinPhiPrime()` / `getRhoCosPhiPrime()` accessors (aliases of the existing
  abscissa/ordinate), validated against the Palomar example (+0.546861, +0.836339).
- **Chapter 42 — Rising, Transit and Setting** (`RiseTransitSetCalculator` /
  `RiseTransitSet`): first-approximation rise/transit/set times (UT), rise/set azimuths,
  transit altitude, and circumpolar / never-rises detection. Composes directly with the
  new solar coordinates: the Sun's rise/transit/set from Uccle on 1978-11-13 reproduces
  reality to a couple of minutes.

### Conventions

- All new code expresses right ascension in **degrees** for internal consistency
  (divide by 15 for hours); `sunApparentEquatorialCoordinates` feeds straight into
  precession, angular separation and the rise/transit/set calculator. Geographic
  longitude remains positive **west**, matching the rest of the library.

### Tests

- Added 20 tests across the six chapters (solar coordinates, precession, angular
  separation, stellar magnitudes, observer geocentric coordinates, rise/transit/set),
  each anchored to a Meeus worked example or an independent physical/round-trip check.
  The suite now has **63 tests**.

### Build & project

- Bumped the artifact version to **1.2.0**; the build target (`target/meeus-engine-1.2.0.jar`),
  toolchain and dependencies are otherwise unchanged.

## 1.1.0 — Correctness, test and build overhaul

This release implements the recommendations from the June 2026 code review.

### Bug fixes (correctness)

- **Apparent sidereal time** (`MeeusEphemerisImpl.getApparentSiderealTimeAsHoursFromJulianDay`):
  fixed a misplaced parenthesis that fed `julianDay * cos(obliquity)` into the
  nutation function instead of multiplying the nutation result by `cos(obliquity)`.
  The equation of the equinoxes is now applied correctly (result for the reference
  date changes from `8h01m45.339s` to `8h01m46.135s`, matching Meeus).
- **Nutation in obliquity** (`EphemerisEngineImpl.getNutationInObliquity`): the final
  term was evaluated with its argument in degrees instead of radians; it is now wrapped
  in `toRadians(...)` like the other terms.
- **Galactic longitude** (`EquatorialCoordinatesAdapter.getGalacticLongitude`): restored
  the quadrant-aware `atan2` (a previous change to plain `atan` lost the quadrant and
  produced longitudes wrong by 180 degrees over half the sky).
- **Mean sidereal time** (`MeeusEphemerisImpl`): the fractional revolution is now taken
  with `Math.floor` instead of an `int` cast, so pre-1900 (negative) values wrap
  correctly.
- **Sexagesimal sign** (`Sexagesimal`): values whose magnitude is below one unit
  (e.g. `-0d 35' 35"`) used to lose their sign because the integer unit part is zero.
  The sign is now preserved and round-trips correctly.

### Robustness

- **Deterministic dates:** all `Calendar` usage in `JulianDay` and `MeeusEphemerisImpl`
  is now pinned to UTC (and uses `GregorianCalendar` where the calendar system matters),
  so results no longer depend on the machine's time zone or locale.
- **Iterative solvers:** `InterpolationEngineImpl.findZero(...)` and `findExtremum(...)`
  now use a tolerance-based convergence test with a maximum-iteration cap and throw an
  informative `InterpolationException` on non-convergence (previously
  `while (n - n0 != 0)` could loop indefinitely).
- **Guarded stub:** the unimplemented `findExtremum(List)` now throws
  `UnsupportedOperationException` instead of silently returning `null`.
- `InterpolationException` gained message/cause constructors.

### Cleanup

- Removed `JeanMeeusTest` (a `main()` demo that lived in `src/main`) and the leftover
  `System.out` debug prints in the interpolation engine.
- Removed the obsolete GWT module descriptor (`meusEngine.gwt.xml`) and the
  source-as-resources packaging hack in the POM (GWT is no longer a target).

### Tests

- Replaced all zero-tolerance floating-point assertions with delta-based comparisons,
  so the suite is stable across JVMs/platforms (the old assertions failed under newer
  JDKs by 1-2 ULP).
- Re-baselined expected values from Meeus's published results rather than from the
  program's own (buggy) output, and pinned date formatting to UTC.
- Split the Easter test so the exception case no longer masks the functional assertions.
- Added test coverage for `Sexagesimal` (including the negative sub-unit regression)
  and for the interpolation engine. The suite now has 43 tests.

### Build & project

- Upgraded the build to **Java 17**; updated `maven-compiler-plugin`,
  `maven-surefire-plugin`, JUnit (4.13.2) and Commons Math (3.6.1); attach a sources jar.
- Replaced the Travis stub with a **GitHub Actions** workflow (builds on JDK 17 and 21).
- Added a **LICENSE** (MIT) and expanded the README; added this changelog and a
  `REFCARD.md` reference card.

> **Note on licensing:** an MIT license file has been added per the review
> recommendation, attributed to the original author. The final choice of license
> rests with the copyright holder and should be confirmed before distribution.
