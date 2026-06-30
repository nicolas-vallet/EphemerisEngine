# EphemerisEngine vs. *Astronomical Formulae for Calculators* — Chapter-by-Chapter Coverage Report

**Reference book:** Jean Meeus, *Astronomical Formulae for Calculators* (Willmann-Bell) — the **39-chapter edition** used as this project's working reference, ending at chapter 39 (*Linear Regression; Correlation*).
**Library under review:** `com.nzv.astro:meeus-engine:1.9.0` (the EphemerisEngine project).

> **Edition & chapter-numbering note (since 1.6.0).** Earlier revisions of this report numbered the
> later chapters against the *4th edition* (1988, 43 chapters). The project's working copy is an
> **earlier 39-chapter edition**; the two agree exactly for chapters 1–34, then diverge. This report
> is now re-anchored to the working edition: the 4th edition's *Central Meridian of Jupiter* (its
> chapter 35) does not exist here, so the later chapters shift down by one — *Positions of the
> Satellites of Jupiter* 35, *Semidiameters* 36, *Stellar Magnitudes* 37, *Binary Stars* 38,
> *Linear Regression; Correlation* 39. The 4th edition's *Atmospheric Refraction*, *Rising/Transit/
> Setting* and *Heliocentric Position of Pluto* are not chapters of this edition; the first two are
> nonetheless implemented and are listed below under **Supplementary utilities**. Immutable history
> (CHANGELOG entries, past PR titles) keeps its original numbering.
**What the gauge means:** percentage of each chapter's formulae that the library actually exposes as callable, finished computations. A chapter that only supplies *inputs* (e.g. mean elements) to a calculation the book carries to completion is scored partial, not full.

> Gauge legend: `██████████` = fully implemented · `░░░░░░░░░░` = not implemented.
>
> **Version note (1.2.0):** Phase 1 of the implementation plan is complete. Six chapters
> moved up: **18 Solar Coordinates** (25% → 100%), **14 Precession** (0% → 100%),
> **9 Angular Separation** (0% → 90%), **38 Stellar Magnitudes** (10% → 100%),
> **6 Observer coordinates** (20% → 100%) and **42 Rising/Transit/Setting** (30% → 80%).
>
> **Version note (1.4.0):** Phase 2 is complete. **30 Position of the Moon** moves 20% → 90%:
> the geocentric longitude, latitude and parallax are implemented via a table-driven evaluator
> (external CSV coefficient tables), validated on AFFC Example 30.a.
>
> **Version note (1.5.0):** Phase 3, step 1 (Harvest) is complete. Five derived chapters move to
> full coverage: **13 Bright Limb** (0% → 100%), **31 Illuminated Fraction** (0% → 100%),
> **32 Phases of the Moon** (0% → 100%), **20 Equinoxes and Solstices** (0% → 100%) and
> **19 Rectangular Coordinates of the Sun** (0% → 100%, of-date and reduced to a chosen equinox).
>
> **Version note (1.6.0):** Phase 3, step 2 is complete, and the report is re-anchored to the
> 39-chapter working edition (see the edition note above). Two chapters move to full coverage:
> **21 Equation of Time** (0% → 100%, the self-contained series plus the A.E.-based form) and
> **29 Correction for Parallax** (15% → 100%, rigorous and non-rigorous topocentric reduction, with
> a Moon convenience). The Moon rise/transit/set refinement once pencilled in here is dropped: the
> reference edition has no Rising/Transit/Setting chapter.
>
> **Version note (1.7.0):** Phase 3, step 3a is complete. **22 Equation of Kepler** moves 0% → 100%:
> the new `KeplerEquation` utility solves `E = M + e sin E` with all three methods of the chapter
> (Newton's correction, the simple fixed-point iteration, and the closed-form approximation),
> validated on Examples 22.a/22.b and the high-eccentricity case. Binary Stars (38) follows later.
>
> **Version note (1.8.0):** Phase 4, step 1 (the orbital-motion keystone) is complete.
> **25 Elliptic Motion** moves 0% → 95%: the new `com.nzv.astro.ephemeris.orbit` package implements
> both methods of the chapter — the first (major planets, mean equinox of date) and the second
> (minor planets/comets, standard-equinox elements, reusing the Chapter-19 Sun) — driven by a
> caller-supplied `OrbitalElements`. Validated on Examples 25.a (Mercury) and 25.b (433 Eros) and on
> the chapter's published-ephemeris exercise (234 Barbara). The positions are geometric, with the
> light-time correction available; nutation/aberration onto a fully apparent place (Chapter 16) are
> left to the caller, hence 95% rather than 100%. No table data is involved — this is the engine the
> data-bound planetary track (Chapters 23/24) will feed later.
>
> **Version note (1.9.0):** Phase 4, step 2 is complete. **26 Parabolic Motion** moves 0% → 95%:
> new `BarkerEquation` (solves `s³ + 3s − W = 0` for `s = tan(v/2)`, both the iteration and the
> closed-form methods), `ParabolicElements` and `ParabolicMotion` reuse the Chapter-25 geocentric
> reduction (now factored into a shared `GeocentricReduction` helper) and the Chapter-19 Sun.
> Validated on Example 26.a (comet Kohler 1977m) to the book's printed precision. Geometric +
> light-time, same as Ch 25; suite 121 → 131 tests.

> **Version note (1.3.0):** Phase 2, step 1 (the star keystone) is complete.
> **16 Apparent Place of a Star** moves 25% → 95%: proper motion, precession, nutation
> and annual aberration (with the eccentricity terms) are now composed into a single
> end-to-end routine.

---

## Executive summary

The library is now a faithful, well-tested implementation of the book's **timekeeping,
calendar, coordinate and solar-position foundations**, plus precession, interpolation,
refraction and the common positional utilities (angular separation, photometry,
rise/transit/set) and the apparent place of a star. It now also includes an **orbital-motion engine** (Chapters 25–26) giving geocentric positions of
minor planets and comets from caller-supplied elliptic or parabolic elements. It still stops before
the remaining **physical-ephemeris payload** — built-in major-planet positions and eclipses are
absent — but the Moon's
geocentric position (Chapter 30) is implemented via a table-driven series, the derived Sun/Moon
phenomena (bright limb 13, illuminated fraction 31, phases 32, equinoxes/solstices 20, rectangular
solar coordinates 19) are complete, and the equation of time (21) and the correction for parallax
(29) have now been added.

| Coverage band | Chapters | Count |
|---|---|---|
| **Strong (≥ 90%)** | 2 Interpolation · 3 Julian Day · 4 Easter · 5 ET/UT · 6 Observer coords · 7 Sidereal Time · 8 Coordinate Transformation · 9 Angular Separation · 13 Bright Limb · 14 Precession · 15 Nutation · 16 Apparent place of a star · 18 Solar Coordinates · 19 Rectangular Coords of the Sun · 20 Equinoxes and Solstices · 21 Equation of Time · 22 Equation of Kepler · 25 Elliptic Motion · 26 Parabolic Motion · 29 Correction for Parallax · 30 Position of the Moon · 31 Illuminated Fraction · 32 Phases of the Moon · 37 Stellar Magnitudes | 24 |
| **Partial (10–80%)** | 1 Hints · 39 Linear Regression | 2 |
| **None (0%)** | 10–12, 17, 23, 24, 27, 28, 33–36, 38 | 13 |

**Overall functional coverage: about half the book**, now spanning the entire
foundational arc (Chapters 1–9) plus precession (14), nutation (15), the apparent place of
a star (16), solar coordinates (18) and rectangular solar coordinates (19), equinoxes/solstices (20),
the equation of time (21), the equation of Kepler (22), the correction for parallax (29), the position of the Moon (30) and its
derived phenomena — bright limb (13), illuminated fraction (31) and phases (32) — and stellar
magnitudes (37), plus the orbital-motion engine for minor planets and comets (25–26), with atmospheric refraction and rising/transit/setting available as supplementary
utilities beyond the reference edition.

---

## Overview table

| # | Chapter | Complexity | Library coverage |
|---|---|---|---|
| 1 | Hints and Tips | LOW | `█████░░░░░` 50% |
| 2 | Interpolation | MEDIUM | `██████████` 100% |
| 3 | Julian Day and Calendar Date | LOW | `█████████░` 90% |
| 4 | Date of Easter | LOW | `█████████░` 90% |
| 5 | Ephemeris Time and Universal Time | MEDIUM | `██████████` 100% |
| 6 | Geocentric Rectangular Coords of an Observer | LOW | `██████████` 100% |
| 7 | Sidereal Time at Greenwich | MEDIUM | `██████████` 100% |
| 8 | Transformation of Coordinates | MEDIUM | `██████████` 100% |
| 9 | Angular Separation | LOW | `█████████░` 90% |
| 10 | Conjunction Between Two Planets | MEDIUM | `░░░░░░░░░░` 0% |
| 11 | Bodies in a Straight Line | LOW | `░░░░░░░░░░` 0% |
| 12 | Smallest Circle Containing Three Bodies | MEDIUM | `░░░░░░░░░░` 0% |
| 13 | Position Angle of the Moon's Bright Limb | LOW | `██████████` 100% |
| 14 | Precession | MEDIUM | `██████████` 100% |
| 15 | Nutation | MEDIUM | `█████████░` 90% |
| 16 | Apparent Place of a Star | HIGH | `█████████▌` 95% |
| 17 | Reduction of Ecliptical Elements (equinox→equinox) | MEDIUM | `░░░░░░░░░░` 0% |
| 18 | Solar Coordinates | MEDIUM | `██████████` 100% |
| 19 | Rectangular Coordinates of the Sun | MEDIUM | `██████████` 100% |
| 20 | Equinoxes and Solstices | MEDIUM | `██████████` 100% |
| 21 | Equation of Time | MEDIUM | `██████████` 100% |
| 22 | Equation of Kepler | MEDIUM | `██████████` 100% |
| 23 | Elements of the Planetary Orbits | MEDIUM | `░░░░░░░░░░` 0% |
| 24 | Planets: Principal Perturbations | HIGH | `░░░░░░░░░░` 0% |
| 25 | Elliptic Motion | HIGH | `█████████░` 95% |
| 26 | Parabolic Motion | MEDIUM | `█████████░` 95% |
| 27 | Planets in Perihelion and Aphelion | MEDIUM | `░░░░░░░░░░` 0% |
| 28 | Passages Through the Nodes | MEDIUM | `░░░░░░░░░░` 0% |
| 29 | Correction for Parallax | MEDIUM | `██████████` 100% |
| 30 | Position of the Moon | HIGH | `█████████░` 90% |
| 31 | Illuminated Fraction of the Moon's Disk | LOW | `██████████` 100% |
| 32 | Phases of the Moon | MEDIUM | `██████████` 100% |
| 33 | Eclipses | HIGH | `░░░░░░░░░░` 0% |
| 34 | Illuminated Fraction of the Disk of a Planet | MEDIUM | `░░░░░░░░░░` 0% |
| 35 | Positions of the Satellites of Jupiter | HIGH | `░░░░░░░░░░` 0% |
| 36 | Semidiameters of Sun, Moon and Planets | LOW | `░░░░░░░░░░` 0% |
| 37 | Stellar Magnitudes | LOW | `██████████` 100% |
| 38 | Binary Stars | MEDIUM | `░░░░░░░░░░` 0% |
| 39 | Linear Regression and Correlation | LOW | `█░░░░░░░░░` 10% |

**Supplementary utilities (beyond the reference edition).** Implemented from standard formulae; not
chapters of the 39-chapter edition, so excluded from the per-chapter percentages above.

| Utility | Complexity | Status |
|---|---|---|
| Atmospheric Refraction | MEDIUM | `██████████` implemented |
| Rising, Transit and Setting (first approximation) | MEDIUM | `████████░░` implemented (constant-coordinate form) |

---

## Chapter detail

### 1 — Hints and Tips · Complexity: LOW · `█████░░░░░` 50%
**Formulae.** Computational hygiene rather than astronomy: efficient polynomial evaluation (Horner's nested scheme), correct rounding, keeping angles in range, sign handling, and converting between sexagesimal and decimal forms.
**Applications.** Underpins every other chapter; the difference between a program that loses precision and one that doesn't.
**Coverage.** Not exposed as an API, but the techniques are embodied throughout — the `Sexagesimal` type implements decimal⇄(h/°, m, s) conversion with correct sign handling below one unit, and polynomial series are evaluated in nested form internally.

### 2 — Interpolation · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Interpolation from 3 and 5 equally spaced tabular values; locating an interpolated extremum; finding the instant where a tabulated quantity is zero.
**Applications.** Pinpointing a maximum (transit altitude, greatest elongation, perihelion) or a root (rise/set, conjunction) between tabulated rows.
**Coverage.** Fully realised by `InterpolationEngine`: 3- and 5-point `interpolate`, `findExtremum`, `findZero`, plus Lagrange/Laguerre variants over a list via Commons Math, with a convergence tolerance and iteration cap.

### 3 — Julian Day and Calendar Date · Complexity: LOW · `█████████░` 90%
**Formulae.** Calendar date → Julian Day and back; day of week; day of year; Gregorian leap-year test; Julian/Gregorian cut-over handling.
**Applications.** The universal time axis for all other calculations.
**Coverage.** `JulianDay` covers JD⇄date, day of week, day of year, and the leap-year test. One convenience method (`getJulianDateFromDate(Date)`) is deliberately unimplemented.

### 4 — Date of Easter · Complexity: LOW · `█████████░` 90%
**Formulae.** The computus — an integer algorithm yielding the Gregorian date of Easter for a given year.
**Applications.** Liturgical calendars; anchoring every movable feast; historical date validation.
**Coverage.** `getEasterDateForYear` implements the Gregorian computus and guards years before 1583; the Julian-calendar variant for earlier years is not provided.

### 5 — Ephemeris Time and Universal Time · Complexity: MEDIUM · `██████████` 100%
**Formulae.** ΔT = ET − UT, and conversion of a UT instant to ET and back.
**Applications.** Correcting historical observations (eclipses, occultations) so they line up with where the sky actually was; required before any high-accuracy position.
**Coverage.** Fully implemented: ΔT via a NASA polynomial series (−1999…+3000) with a Meeus fallback outside that window, plus `universalTimeToEphemerisTime` / `ephemerisTimeToUniversalTime`.

### 6 — Geocentric Rectangular Coordinates of an Observer · Complexity: LOW · `██████████` 100%
**Formulae.** The observer's ρ·sin φ′ and ρ·cos φ′ from geographic latitude and altitude, using the Earth's flattening.
**Applications.** Input to the topocentric parallax correction (Ch. 29) and to eclipse/occultation geometry.
**Coverage.** *Completed in 1.2.0.* `GeocentricCoordinates` now exposes the quantities directly via `getRhoSinPhiPrime()` / `getRhoCosPhiPrime()` (clean aliases of the existing abscissa/ordinate), validated against Meeus' Palomar example (+0.546861, +0.836339).

### 7 — Sidereal Time at Greenwich · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Mean sidereal time at 0ʰ and at arbitrary UT (polynomial in T), and apparent sidereal time = mean + equation of the equinoxes (nutation term).
**Applications.** Converting right ascension to hour angle; the bridge between equatorial and horizontal coordinates.
**Coverage.** Fully implemented in `MeeusEphemeris`: mean (0ʰ and at-UT) and apparent sidereal time.

### 8 — Transformation of Coordinates · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Spherical rotations among ecliptic, equatorial, galactic, and horizontal systems, using the obliquity and the observer's location/sidereal time.
**Applications.** The workhorse of positional astronomy — moving any object between catalogue coordinates and what an observer sees.
**Coverage.** Fully implemented through the adapter classes for all four systems, including azimuth/elevation from a site and sidereal time.

### 9 — Angular Separation · Complexity: LOW · `█████████░` 90%
**Formulae.** Great-circle angular distance between two bodies from their coordinates; relative position angle; least separation by interpolation.
**Applications.** Conjunction closeness, double-star measures, "how far is the Moon from that planet tonight."
**Coverage.** *Implemented in 1.2.0.* `AngularSeparation` provides the great-circle distance and the relative position angle (matches the Arcturus/Spica example, 32.79°). The "least separation by interpolation" refinement is not a dedicated method but can be assembled with `InterpolationEngine`.

### 10 — Conjunction Between Two Planets · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Time of conjunction in right ascension by interpolating the difference of two ephemerides to zero, and the separation at that instant.
**Applications.** Predicting close planetary pairings.
**Coverage.** Not implemented (the interpolation machinery it would build on does exist).

### 11 — Bodies in a Straight Line · Complexity: LOW · `░░░░░░░░░░` 0%
**Formulae.** Test/instant at which three bodies become collinear on the sphere.
**Applications.** Alignment phenomena; transit-style geometry.
**Coverage.** Not implemented.

### 12 — Smallest Circle Containing Three Celestial Bodies · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** The minimum enclosing circle of three positions on the sphere.
**Applications.** Framing a group of bodies (e.g. for a photograph or a finder field).
**Coverage.** Not implemented.

### 13 — Position Angle of the Moon's Bright Limb · Complexity: LOW · `██████████` 100%
**Formulae.** Position angle of the Moon's illuminated limb from the relative positions of Sun and Moon.
**Applications.** Drawing the Moon with correct orientation; planning limb observations.
**Coverage.** Implemented: `moonBrightLimbPositionAngle(jd)` forms χ from the apparent equatorial coordinates of Sun and Moon, with the quadrant resolved by `atan2`. Validated on Example 13.a (χ = 250°.38); the pure geometry is also exposed as a static helper for A.E.-coordinate checks.

### 14 — Precession · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Rigorous reduction of equatorial (and ecliptic) coordinates from one equinox/epoch to another via the precessional angles.
**Applications.** Bringing catalogue positions to the date of observation; comparing positions across epochs.
**Coverage.** *Implemented in 1.2.0.* `Precession` performs the rigorous equatorial reduction via the angles ζ, z, θ and exposes those angles directly. Annual rates reproduce the precessional constants m and n exactly; a forward/back reduction round-trips to sub-milliarcsecond level.

### 15 — Nutation · Complexity: MEDIUM · `█████████░` 90%
**Formulae.** Nutation in longitude (Δψ) and obliquity (Δε) from periodic terms in the Sun's and Moon's elements, plus the true obliquity.
**Applications.** Apparent sidereal time, apparent star/planet places, high-accuracy coordinate work.
**Coverage.** `getNutationInLongitude` and `getNutationInObliquity` are implemented (principal terms), and the 1950/2000 obliquity constants are provided.

### 16 — Apparent Place of a Star · Complexity: HIGH · `█████████▌` 95%
**Formulae.** Combine mean catalogue position with proper motion, precession, nutation, and annual aberration to get the apparent place.
**Applications.** Telescope pointing; reducing a catalogue star to tonight's sky.
**Coverage.** *Implemented in 1.3.0.* `ApparentPlace` composes all four ingredients end to end: proper motion (a plain coordinate rate in arcsec/year), precession to the equinox of date (Ch. 14), and the nutation (Ch. 15) and annual-aberration reductions onto right ascension and declination, the latter including the terms in the eccentricity of the Earth's orbit and using the Sun's true longitude (Ch. 18). Validated on Theta Persei reduced to the house date 1978-11-13, with the nutation and aberration components cross-checked term by term.

### 17 — Reduction of Ecliptical Elements from One Equinox to Another · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Transform orbital inclination, node, and perihelion argument between reference equinoxes.
**Applications.** Using orbital elements quoted for a different epoch.
**Coverage.** Not implemented.

### 18 — Solar Coordinates · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Sun's mean longitude and mean anomaly, equation of centre, true and apparent longitude, radius vector, and apparent right ascension/declination.
**Applications.** Day/night and twilight, the Sun's place for refraction and for the Moon's phase, solar-geometry tools.
**Coverage.** *Completed in 1.2.0.* The equation of centre, true anomaly, true and apparent longitude, radius vector, mean obliquity and apparent equatorial coordinates are all implemented. Cross-checked against the canonical 1992-10-13 worked example to better than 0.01°. This is the keystone enabler on which both later tracks build.

### 19 — Rectangular Coordinates of the Sun · Complexity: MEDIUM · `██████████` 100%
**Formulae.** The Sun's geocentric X, Y, Z referred to the equator/ecliptic (19.1), and their reduction to a chosen standard equinox (19.2).
**Applications.** Building geocentric planet/comet positions; eclipse geometry.
**Coverage.** Implemented: `sunRectangularEquatorialCoordinates(jd)` (mean equinox of date) and `sunRectangularEquatorialCoordinates(jd, equinox)` (reduced via the Chapter-14 precessional angles). Validated on Example 19.a, of-date and reduced to 1950.0.

### 20 — Equinoxes and Solstices · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Instants of the equinoxes and solstices, found iteratively from the Sun's apparent longitude.
**Applications.** Season boundaries; calendar and almanac work.
**Coverage.** Implemented: `equinoxSolsticeJulianDay(year, Season)` iterates formula 20.2 on the Sun's apparent longitude until the correction falls below a fraction of a second. Validated on Example 20.a (September equinox 1979 at JD 2444140.137).

### 21 — Equation of Time · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Difference between apparent and mean solar time, by the self-contained series (formula 21.1) and the A.E.-based relation.
**Applications.** Sundials, the analemma, converting clock time to true solar time.
**Coverage.** Implemented: `equationOfTime(jd)` returns the value in minutes of time from the series built on the Chapter-18 solar elements; the static `equationOfTimeFromApparentValues(...)` reproduces the A.E.-based form. Validated on Examples 21.a (−11ᵐ09.7ˢ) and 21.b (−11ᵐ10.3ˢ).

### 22 — Equation of Kepler · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Solving E = M + e·sin E for the eccentric anomaly by Newton's correction (22.3), the simple fixed-point iteration, and the closed-form approximation (22.4).
**Applications.** The heart of every elliptical-orbit position calculation; needed for binary stars (38).
**Coverage.** Implemented in `KeplerEquation`: `solveEccentricAnomaly` (Newton, recommended), `solveEccentricAnomalyByIteration` (first method) and `approximateEccentricAnomaly` (formula 22.4). Validated on Examples 22.a/22.b (E = 5.554589…) and the hard case e = 0.99, M = 2° (E = 32.361007).

### 23 — Elements of the Planetary Orbits · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Mean orbital elements of the planets as polynomials in time.
**Coverage.** Not implemented in code (0%). **Data acquired & verified** on the parallel
data-acquisition track: master `AFFC-tables-chapter-23.xlsx` holds Tables 23.A/B/C (of date /
1950.0 / 2000.0, selectable), self-checked against Example 23.a to sub-arcsecond. The Ch 25 engine (v1.8.0) already consumes
caller-supplied elements; the Phase-5 wiring will feed these built-in elements into it.

### 24 — Planets: Principal Perturbations · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** Periodic perturbation series correcting the two-body elements of the major planets.
**Coverage.** Not implemented in code (0%). **Data fully acquired & verified** on the
data-acquisition track for **all five Ch 24 bodies** — Mercury, Venus, Mars (flat mean-anomaly
series) and Jupiter, Saturn (procedural: auxiliary angles, υ-polynomial amplitudes, product-of-trig
terms, A − B/e). Per-planet masters carry self-generated regression pins (Ch 24 has no worked
examples). Awaiting the Phase-5 `planetary` engine to consume it.

### 25 — Elliptic Motion · Complexity: HIGH · `█████████░` 95%
**Formulae.** From elements → heliocentric → geocentric equatorial coordinates of a body in an elliptical orbit (both methods: 25.1–25.16).
**Applications.** Positions of planets and elliptical-orbit minor planets and comets.
**Coverage.** Implemented in `com.nzv.astro.ephemeris.orbit` (`EllipticMotion`, `OrbitalElements`, `OrbitPosition`). First method (major planets, mean equinox of date) and second method (minor planets/comets, standard-equinox elements, reusing the Chapter-19 Sun and the Chapter-22 Kepler solver), driven by caller-supplied elements. Geometric positions plus elongation, phase angle, light-time correction (25.10) and the magnitude relations (25.16). Validated on Examples 25.a (Mercury) and 25.b (433 Eros) and the 234 Barbara published-ephemeris exercise. The remaining 5% is nutation/aberration onto a fully apparent place (Chapter 16), deliberately left to the caller.

### 26 — Parabolic Motion · Complexity: MEDIUM · `█████████░` 95%
**Formulae.** Position of a comet on a parabolic orbit: Barker's equation `s³ + 3s − W = 0` (26.1–26.5) feeding the Chapter-25 geocentric reduction.
**Applications.** Comet ephemerides.
**Coverage.** Implemented in `com.nzv.astro.ephemeris.orbit`: `BarkerEquation` (both the iteration 26.4 and closed-form 26.5 solving methods), `ParabolicElements` (`q, i, ω, Ω, T`) and `ParabolicMotion`, reusing the shared `GeocentricReduction` tail and the Chapter-19 Sun. Geometric positions plus elongation, phase angle, light-time correction and the comet magnitude relation (25.16). Validated on Example 26.a (comet Kohler 1977m). The remaining 5% is nutation/aberration onto a fully apparent place (Chapter 16), left to the caller — consistent with Chapter 25.

### 27 — Planets in Perihelion and Aphelion · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Times and distances of perihelion/aphelion passage.
**Applications.** Apsides predictions; brightness timing.
**Coverage.** Not implemented.

### 28 — Passages Through the Nodes · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Times a body crosses the ecliptic (ascending/descending node).
**Applications.** Eclipse seasons; latitude-zero crossings.
**Coverage.** Not implemented.

### 29 — Correction for Parallax · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Convert geocentric to topocentric coordinates using the observer's ρ·sin φ′ / ρ·cos φ′ and the body's parallax (rigorous 29.2/29.3 and non-rigorous 29.4/29.5); Earth–body distance from horizontal parallax (29.1).
**Applications.** Moon and near-body positions as seen from a specific site; occultation/eclipse timing.
**Coverage.** Implemented in `ParallaxCorrection`: `parallaxFromDistanceInDegrees`, the rigorous `topocentric` and the non-rigorous `topocentricApproximate`, plus the Moon convenience `moonTopocentricEquatorialCoordinates(jd, observer, height, θ₀)`. Validated on Example 29.a (Mars, both formula sets) and exercised at Moon-scale parallax.

### 30 — Position of the Moon · Complexity: HIGH · `█████████░` 90%
**Formulae.** Moon's mean elements (L′, M′, F, D, Ω) followed by long periodic series for longitude, latitude, and parallax → geocentric position and distance.
**Applications.** Phases, eclipses, occultations, the Moon's place in the sky.
**Coverage.** *Implemented in 1.4.0.* Geocentric longitude, latitude and equatorial horizontal parallax are produced by a table-driven evaluator (`com.nzv.astro.ephemeris.lunar`) reading external CSV coefficient tables for the AFFC-1900 model; `jd`-based conveniences give apparent RA/Dec (nutation + true obliquity of date), geocentric ecliptic coordinates, and the Earth–Moon distance. Validated on Example 30.a (λ and π vs the book; β vs the Astronomical Ephemeris value). The design supports dropping in a higher-precision model as data.

### 31 — Illuminated Fraction of the Moon's Disk · Complexity: LOW · `██████████` 100%
**Formulae.** Phase angle and illuminated fraction from Sun–Moon geometry (31.1–31.3), plus a latitude-free approximation (31.4).
**Applications.** Phase displays; observation planning around moonlight.
**Coverage.** Implemented: `moonPhaseAngle(jd)`, `moonIlluminatedFraction(jd)` and `moonPhaseAngleApproximate(jd)`. Uses the Sun's true longitude to avoid double-counting nutation/aberration. Validated on Examples 31.a/31.b (k = 0.36 for 1979 Dec. 25.0).

### 32 — Phases of the Moon · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Times of new/first-quarter/full/last-quarter from the mean phase (32.1) plus the periodic corrections of 32.4 (New/Full) and 32.5 (First/Last Quarter).
**Applications.** Calendars, almanacs, tide context.
**Coverage.** Implemented: `moonPhaseJulianDay(year, MoonPhase)` returns the Julian Ephemeris Day of the phase nearest a decimal-year time. Validated on Examples 32.a (New Moon, JD 2443192.6523) and 32.b (Last Quarter, JD 2434326.1553).

### 33 — Eclipses · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** Whether and when solar/lunar eclipses occur, and their basic circumstances.
**Applications.** Eclipse prediction.
**Coverage.** Not implemented.

### 34 — Illuminated Fraction of the Disk of a Planet · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Phase angle and illuminated fraction of a planet from heliocentric/geocentric distances.
**Applications.** Appearance of Venus/Mars; magnitude inputs.
**Coverage.** Not implemented.

### 35 — Positions of the Satellites of Jupiter · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** Apparent positions of the four Galilean moons.
**Applications.** Identifying moons; predicting transits, eclipses, occultations of the satellites.
**Coverage.** Not implemented.

### 36 — Semidiameters of Sun, Moon and Planets · Complexity: LOW · `░░░░░░░░░░` 0%
**Formulae.** Apparent angular radius from distance.
**Applications.** Rise/set refinement, eclipse and occultation geometry, sizing for drawings.
**Coverage.** Not implemented.

### 37 — Stellar Magnitudes · Complexity: LOW · `██████████` 100%
**Formulae.** Pogson's relation; combining magnitudes; brightness ratios.
**Applications.** Variable-star work; combined brightness of close pairs; limiting-magnitude estimates.
**Coverage.** *Implemented in 1.2.0.* `StellarMagnitudes` provides Pogson's ratio, the brightness ratio for a magnitude difference and its inverse, and the combined magnitude of two or more bodies (matches the double-star example, combined 1.96 + 2.89 → 1.58).

### 38 — Binary Stars · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Apparent orbit of a visual binary from its elements (a Kepler-equation problem) → separation and position angle at a date.
**Applications.** Ephemerides for double-star observers.
**Coverage.** Not implemented (needs a Kepler solver from Chapter 22; the position-angle/separation output side can reuse Chapter 9). Slated for Phase 3, step 3.

### 39 — Linear Regression and Correlation · Complexity: LOW · `█░░░░░░░░░` 10%
**Formulae.** Least-squares straight-line fit and the correlation coefficient.
**Applications.** Reducing observation series; trend fitting.
**Coverage.** Not exposed directly; the Apache Commons Math dependency could supply it, and the interpolation module offers Lagrange/Laguerre fitting, but regression itself is not part of the public API. This is the final chapter of the reference edition.

### Supplementary utilities (beyond the reference edition)
These are implemented from standard formulae and are not chapters of the 39-chapter reference edition; they are therefore excluded from the per-chapter coverage above.

**Atmospheric Refraction · `██████████` implemented.** True⇄apparent elevation with optional temperature and pressure corrections. Fully implemented by `AtmosphericRefractionCalculator`, in both directions and with conditions (default 10 °C, 1013 mbar), guarding negative elevations.

**Rising, Transit and Setting · `████████░░` implemented (first approximation).** `RiseTransitSetCalculator` returns rise/transit/set times (UT), rise/set azimuths, transit altitude, and circumpolar / never-rises flags, with selectable standard altitudes for stars/planets and the Sun. It uses the first-approximation form (apparent RA/Dec held constant over the day): exact for stars and accurate to a few minutes for the Sun (validated against the Sun from Uccle on 1978-11-13). The iterative refinement for a fast body such as the Moon is not pursued, as the reference edition has no corresponding chapter.

---

## Reading the pattern

With Phase 1 complete, the picture has shifted. First, the **foundational arc is now
unbroken**: Chapters 1–9 are all strong, and the three companion chapters the foundation
leans on (14 Precession, 15 Nutation, 18 Solar Coordinates) are done, as are Stellar Magnitudes (37) and the supplementary refraction utility.
Second, the **two keystones are now de-risked**: Apparent Place of a Star (16) has three of
its four ingredients in place (precession, nutation, and the Sun for aberration), and
Position of the Moon (30) still needs its periodic series but sits on mean elements that are
ready. Third, the **Sun-adjacent harvest is now gathered**: Equation of Time (21), Equinoxes/Solstices
(20) and Rectangular Coordinates of the Sun (19) have all been folded in, alongside the Moon and
star keystones and the parallax reduction (29).

*Notes on sourcing: chapter titles and order are from the working (39-chapter) edition's table of contents; coverage and complexity are assessed against the EphemerisEngine source and its reference card. A handful of percentages (e.g. the partial Moon/observer/star chapters and the 80% on rising/transit/setting) are judgement calls about how much of each chapter's finished output the library actually produces.*
