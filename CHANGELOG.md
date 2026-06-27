# Changelog

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
