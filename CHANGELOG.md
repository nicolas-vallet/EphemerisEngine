# Changelog

## 1.9.0 — Phase 4 (step 2): Parabolic-motion engine (Chapter 26)

This release adds the **parabolic-motion engine** (Chapter 26) — geocentric positions of comets in
parabolic orbits — and factors out the geocentric-reduction tail now shared with Chapter 25. Like
the elliptic engine it is algorithm-bound and table-free. The suite grows from 121 to 131 tests, all
passing.

### New features

- **Chapter 26 — Parabolic Motion.** New classes in `com.nzv.astro.ephemeris.orbit`:
  - **`BarkerEquation`** — solves `s³ + 3s − W = 0` (26.3) for `s = tan(v/2)`, with the coefficient
    `W` (26.1), both solving methods of the chapter — the iteration (26.4, the recommended default)
    and the closed form (26.5, after Bauschinger) — and the true anomaly / radius vector (26.2). It
    is to parabolic orbits what `KeplerEquation` is to elliptic ones (a parabola has `e = 1`, so the
    equation of Kepler does not apply).
  - **`ParabolicElements`** record — `q, i, ω, Ω, T` (perihelion distance, orientation, and time of
    perihelion passage), with `daysSincePerihelion(jd)`.
  - **`ParabolicMotion`** — `geocentricPosition(...)` (static and `julianDay` overloads) and
    `lightTimeCorrected(...)`. The reduction reuses the Chapter-25 Gaussian constants (25.13), the
    heliocentric rectangular coordinates (25.14) and the Sun combination (25.15), and the Chapter-19
    Sun. Comet total magnitude (25.16) is already available on `EllipticMotion`.

### Internal

- **Shared geocentric reduction.** The `{x, y, z, r} + Sun → OrbitPosition` tail (25.14/25.15 plus
  the elongation and phase angle) is extracted into a package-private `GeocentricReduction` helper
  and reused by both the elliptic second method and the parabolic engine. Behaviour-preserving — the
  existing Chapter-25 tests are unchanged and still pass.

### Validation

- **Example 26.a** (comet Kohler 1977m): the coefficient `W`, the root `s` by **both** solving
  methods, the true anomaly `v`, radius vector `r`, the equatorial `α₁₉₅₀`/`δ₁₉₅₀`, Earth distance
  `Δ`, elongation and total magnitude all reproduce the book to its printed precision against its
  printed Sun `X, Y, Z`. Through the library's Chapter-19 Sun and the light-time correction the
  agreement is ~0.004° and the value is pinned.

## 1.8.0 — Phase 4 (step 1): Elliptic-motion engine (Chapter 25)

This release adds the **elliptic-motion engine** (Chapter 25), the keystone that turns orbital
elements into a geocentric position. It is algorithm-bound and table-free: it consumes
caller-supplied `OrbitalElements`, so it ships independently of the Chapter 23/24 planetary-data
track. Both of the chapter's methods are implemented. The suite grows from 108 to 121 tests, all
passing.

### New features

- **Chapter 25 — Elliptic Motion.** New package `com.nzv.astro.ephemeris.orbit` with a static
  `EllipticMotion` utility, an `OrbitalElements` record and an `OrbitPosition` result.
  - **First method** (major planets, elements of the mean equinox of date): `heliocentricEcliptic`
    (25.1–25.6), `geocentricEcliptic` (25.7–25.9) and `firstMethod(...)`, with a `julianDay`
    overload that takes the Sun's geometric longitude, radius vector and the obliquity of the date
    from the engine itself.
  - **Second method** (minor planets and comets, standard-equinox elements): `gaussianConstants`
    (25.13), `heliocentricEquatorialRectangular` (25.14) and `secondMethod(...)` (25.15), reusing
    `sunRectangularEquatorialCoordinates(jd, equinox)` (Chapter 19) directly. A
    `secondMethodLightTimeCorrected(...)` overload applies the light-time correction (25.10),
    iterating the body to `t − τ` while the Sun stays at `t`.
  - Elongation and phase angle (page 120) on every result, plus the comet (25.16) and minor-planet
    magnitude helpers.
- **`OrbitalElements`** record: classical elements `a, e, i, ω, Ω` plus a mean anomaly at an epoch
  and a daily mean motion, with `meanAnomalyAt(jd)` and factories `fromMeanAnomalyAtEpoch`,
  `fromPerihelionPassage`, and the derivations `meanMotionFromSemiMajorAxis` (25.12) and
  `semiMajorAxisFromPerihelionDistance`.

### Scope

The positions returned are the body's **geometric** coordinates, exactly as the chapter's worked
examples produce them, plus the well-defined light-time/astrometric correction. Nutation and
aberration (the remaining corrections to a fully *apparent* place, Chapter 16) are deliberately left
to the caller: the book itself stops at the geometric position and defers those, and the correct
combination of light-time and stellar aberration for a solar-system body is a separate concern best
handled as its own step.

### Validation

- **Example 25.a** (Mercury, first method): heliocentric `l, b, r`, geocentric `λ, β, Δ` and the
  equatorial `α, δ` of date all reproduce the book to its printed precision; the `julianDay`
  overload reproduces it through the library's own Sun to ~1e-4°.
- **Example 25.b** (433 Eros, second method): the Gaussian constants, `x, y, z`, `Δ`, `α₁₉₅₀`,
  `δ₁₉₅₀`, elongation, phase angle and magnitude all reproduce the book against its printed Sun
  `X, Y, Z`. Through the library's Chapter-19 Sun the agreement is ~0.04° — the near-Earth geometry
  (Δ = 0.175 AU) amplifies the known ~1e-4 AU equinox-reduction residual — and the value is pinned.
- **Minor planet 234 Barbara** (the chapter's exercise): the `julianDay` + light-time path matches
  the published *Ephemerides of Minor Planets for 1979* to ≤0.012° across the tabulated dates.

## 1.7.0 — Phase 3 (step 3a): Equation of Kepler

This release adds **Chapter 22** (the equation of Kepler), the small transcendental solver that
underpins elliptic motion and binary-star orbits. Chapter 38 (Binary Stars) follows in a later
release. The suite grows from 102 to 108 tests, all passing.

### New features

- **Chapter 22 — Equation of Kepler.** New `KeplerEquation` static utility solving `E = M + e sin E`
  for the eccentric anomaly, with all three methods of the chapter:
  - `solveEccentricAnomaly(M, e)` / `solveEccentricAnomaly(M, e, tolerance)` — Newton's correction
    (formula 22.3, the recommended method); converges quickly for every eccentricity.
  - `solveEccentricAnomalyByIteration(M, e, tolerance)` — the simple fixed-point iteration (first
    method); slow for large e.
  - `approximateEccentricAnomaly(M, e)` — the closed-form approximation (formula 22.4), for small e.
  All angles in degrees, using the "modified" eccentricity `e0 = e × 180/π` so the work stays in
  degrees. No external dependency: the iteration is hand-coded.

### Validation

- Anchored on the chapter's worked examples: 22.a (e = 0.100, M = 5° → E = 5.554589 by iteration),
  22.b (same data → E = 5.554589253 by Newton), the hard case e = 0.990, M = 2° → E = 32.361007,
  and the closed-form approximation (E = 5.554599). Guard tests confirm E = M when e = 0 and that a
  returned E satisfies Kepler's equation across a range of eccentricities and mean anomalies.

## 1.6.0 — Phase 3 (step 2): Equation of Time, Parallax, and edition re-anchoring

This release adds **Chapter 21** (Equation of Time) and **Chapter 29** (Correction for Parallax), and
re-anchors the project's chapter numbering to the actual 39-chapter reference edition. The suite
grows from 94 to 102 tests, all passing.

### New features

- **Chapter 21 — Equation of Time.** `equationOfTime(jd)` returns the value in minutes of time from
  W. M. Smart's self-contained series (formula 21.1), built on the Chapter-18 solar elements. The
  static `equationOfTimeFromApparentValues(θ₀, αSun, ΔT)` reproduces the A.E.-based form at the head
  of the chapter. Validated on Examples 21.a (−11ᵐ09.7ˢ) and 21.b (−11ᵐ10.3ˢ).
- **Chapter 29 — Correction for Parallax.** New `ParallaxCorrection` static utility:
  `parallaxFromDistanceInDegrees` (29.1), the rigorous `topocentric` (29.2/29.3, required for the
  Moon) and the non-rigorous `topocentricApproximate` (29.4/29.5). Plus the Moon convenience
  `moonTopocentricEquatorialCoordinates(jd, observer, height, θ₀)`. Validated on Example 29.a (Mars,
  both formula sets) and exercised at Moon-scale parallax.

### Edition re-anchoring

- The project's reference is the **39-chapter edition** of AFFC (ending at chapter 39, *Linear
  Regression; Correlation*), not the 4th edition's 43-chapter numbering used in earlier docs. The two
  agree for chapters 1–34; thereafter the docs are corrected: the phantom *Central Meridian of
  Jupiter* is removed, and **Stellar Magnitudes 38→37**, **Binary Stars 39→38**, **Linear Regression
  40→39**. *Atmospheric Refraction* and *Rising/Transit/Setting* are reclassified as **supplementary
  utilities** beyond the reference edition (code unchanged); *Heliocentric Position of Pluto* is
  dropped from the roadmap. Code comments and the coverage report / plan / refcard are updated;
  immutable history (older CHANGELOG entries, past PR titles) keeps its original numbering.

### Notes

- The Moon rise/transit/set iterative refinement previously pencilled in for this step is dropped:
  the reference edition has no Rising/Transit/Setting chapter, so there is no text to transcribe or
  worked example to validate against. The first-approximation `RiseTransitSetCalculator` remains as a
  supplementary utility.

## 1.5.0 — Phase 3 (step 1): Moon phenomena and Sun bonuses

This release opens Phase 3 ("Harvest") with the first batch of derived Sun and Moon phenomena,
now short geometric additions on top of the two keystones. It covers **Chapter 31** (illuminated
fraction of the Moon's disk), **Chapter 13** (position angle of the bright limb), **Chapter 32**
(phases of the Moon), **Chapter 20** (equinoxes and solstices) and **Chapter 19** (rectangular
coordinates of the Sun). The suite grows from 82 to 94 tests, all passing.

### New features

- **Chapter 31 — Illuminated fraction of the Moon's disk.** `moonPhaseAngle(jd)` and
  `moonIlluminatedFraction(jd)` (high accuracy, formulae 31.1–31.3), plus
  `moonPhaseAngleApproximate(jd)` (the latitude-free formula 31.4). The Sun's **true** longitude is
  used when forming the elongation so nutation and aberration are not double-counted.
- **Chapter 13 — Position angle of the Moon's bright limb.** `moonBrightLimbPositionAngle(jd)`,
  computed from the apparent equatorial coordinates of the Sun and Moon with the quadrant resolved
  by `atan2`.
- **Chapter 32 — Phases of the Moon.** `moonPhaseJulianDay(year, MoonPhase)` returns the Julian
  Ephemeris Day of the New / First-Quarter / Full / Last-Quarter nearest a decimal-year time, with
  the mean phase (32.1) corrected by the periodic series (32.4 / 32.5). New `MoonPhase` enum.
- **Chapter 20 — Equinoxes and solstices.** `equinoxSolsticeJulianDay(year, Season)` iterates the
  Sun's **apparent** longitude to the instant it reaches a multiple of 90°. New `Season` enum.
- **Chapter 19 — Rectangular coordinates of the Sun.** `sunRectangularEquatorialCoordinates(jd)`
  for the mean equinox of date (19.1), and `sunRectangularEquatorialCoordinates(jd, targetEquinox)`
  reducing to a chosen equinox via the Chapter-14 precessional angles (19.2).

### Validation

- Anchored on the Meeus worked examples: 31.a/31.b (k = 0.36 for 1979 Dec. 25.0), 13.a
  (χ = 250°.38), 19.a (X/Y/Z of date and reduced to 1950.0), 20.a (September equinox 1979 at
  JD 2444140.137) and 32.a/32.b (New Moon and Last Quarter). Pure geometry (31, 13) is exercised
  directly with the book's A.E. coordinates to separate a transcription slip from a model
  difference; high-precision computed values are pinned as regression anchors.

## 1.4.0 — Phase 2 (step 2): the Moon keystone

This release completes Phase 2 with **Chapter 30 — Position of the Moon**, implemented through a
new table-driven evaluator so a higher-precision lunar model can later be added as data, not code.
The suite grows from 68 to 82 tests, all passing.

### New features

- **Chapter 30 — Position of the Moon.** `EphemerisEngine` gains `moonGeocentricLongitude(T)`,
  `moonGeocentricLatitude(T)` and `moonEquatorialHorizontalParallaxe(T)` (true position of date),
  plus the `jd`-based conveniences `moonApparentEquatorialCoordinates`,
  `moonGeocentricEclipticCoordinates` and `moonDistanceToEarthInKilometers`. The apparent
  equatorial method adds the nutation in longitude and rotates with the true obliquity of date.
  Accuracy follows the AFFC abridged theory (~10″ longitude, 3″ latitude, 0.2″ parallax).
- **Table-driven lunar model** (`com.nzv.astro.ephemeris.lunar`): `PeriodicTerm`,
  `MoonPositionModel`, `TableDrivenMoonModel`, `MoonTableLoader` and a `MoonModels` registry. The
  AFFC-1900 coefficients ship as external CSV resources
  (`resources/com/nzv/astro/ephemeris/lunar/affc-1900/`). A different model is a sibling resource
  folder selected at construction: `new EphemerisEngineImpl(MoonModels.X.getModel())`.

### Validation

- Anchored on Meeus' worked Example 30.a (1979 Dec. 7.0 ET): longitude and parallax match the
  book; latitude is validated against the Astronomical Ephemeris value (the book's printed
  example B carries a ~2″ hand-arithmetic slip).

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
