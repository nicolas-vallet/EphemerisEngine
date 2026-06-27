# EphemerisEngine vs. *Astronomical Formulae for Calculators* — Chapter-by-Chapter Coverage Report

**Reference book:** Jean Meeus, *Astronomical Formulae for Calculators*, 4th edition (Willmann-Bell, 1988) — 43 chapters.
**Library under review:** `com.nzv.astro:meeus-engine:1.2.0` (the EphemerisEngine project).
**What the gauge means:** percentage of each chapter's formulae that the library actually exposes as callable, finished computations. A chapter that only supplies *inputs* (e.g. mean elements) to a calculation the book carries to completion is scored partial, not full.

> Gauge legend: `██████████` = fully implemented · `░░░░░░░░░░` = not implemented.
>
> **Version note (1.2.0):** Phase 1 of the implementation plan is complete. Six chapters
> moved up: **18 Solar Coordinates** (25% → 100%), **14 Precession** (0% → 100%),
> **9 Angular Separation** (0% → 90%), **38 Stellar Magnitudes** (10% → 100%),
> **6 Observer coordinates** (20% → 100%) and **42 Rising/Transit/Setting** (30% → 80%).

---

## Executive summary

The library is now a faithful, well-tested implementation of the book's **timekeeping,
calendar, coordinate and solar-position foundations**, plus precession, interpolation,
refraction and the common positional utilities (angular separation, photometry,
rise/transit/set). It still stops before most of the **physical-ephemeris payload**: the
full Moon position, the planets, comets, eclipses and the remaining phenomenon chapters
are absent. For the Moon the library computes the *mean elements* but not the periodic
series, so that chapter remains partial.

| Coverage band | Chapters | Count |
|---|---|---|
| **Strong (≥ 90%)** | 2 Interpolation · 3 Julian Day · 4 Easter · 5 ET/UT · 6 Observer coords · 7 Sidereal Time · 8 Coordinate Transformation · 9 Angular Separation · 14 Precession · 15 Nutation · 18 Solar Coordinates · 38 Stellar Magnitudes · 41 Refraction | 13 |
| **Partial (10–80%)** | 1 Hints · 16 Apparent place of a star · 29 Parallax · 30 Moon position · 40 Regression · 42 Rising/Transit/Setting | 6 |
| **None (0%)** | 10–13, 17, 19–28, 31–37, 39, 43 | 24 |

**Overall functional coverage: roughly one third of the book**, now spanning the entire
foundational arc (Chapters 1–9) plus precession (14), nutation (15), solar coordinates (18),
stellar magnitudes (38), refraction (41) and a strong partial on rising/transit/setting (42).

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
| 13 | Position Angle of the Moon's Bright Limb | LOW | `░░░░░░░░░░` 0% |
| 14 | Precession | MEDIUM | `██████████` 100% |
| 15 | Nutation | MEDIUM | `█████████░` 90% |
| 16 | Apparent Place of a Star | HIGH | `██▌░░░░░░░` 25% |
| 17 | Reduction of Ecliptical Elements (equinox→equinox) | MEDIUM | `░░░░░░░░░░` 0% |
| 18 | Solar Coordinates | MEDIUM | `██████████` 100% |
| 19 | Rectangular Coordinates of the Sun | MEDIUM | `░░░░░░░░░░` 0% |
| 20 | Equinoxes and Solstices | MEDIUM | `░░░░░░░░░░` 0% |
| 21 | Equation of Time | MEDIUM | `░░░░░░░░░░` 0% |
| 22 | Equation of Kepler | MEDIUM | `░░░░░░░░░░` 0% |
| 23 | Elements of the Planetary Orbits | MEDIUM | `░░░░░░░░░░` 0% |
| 24 | Planets: Principal Perturbations | HIGH | `░░░░░░░░░░` 0% |
| 25 | Elliptic Motion | HIGH | `░░░░░░░░░░` 0% |
| 26 | Parabolic Motion | MEDIUM | `░░░░░░░░░░` 0% |
| 27 | Planets in Perihelion and Aphelion | MEDIUM | `░░░░░░░░░░` 0% |
| 28 | Passages Through the Nodes | MEDIUM | `░░░░░░░░░░` 0% |
| 29 | Correction for Parallax | MEDIUM | `█▌░░░░░░░░` 15% |
| 30 | Position of the Moon | HIGH | `██░░░░░░░░` 20% |
| 31 | Illuminated Fraction of the Moon's Disk | LOW | `░░░░░░░░░░` 0% |
| 32 | Phases of the Moon | MEDIUM | `░░░░░░░░░░` 0% |
| 33 | Eclipses | HIGH | `░░░░░░░░░░` 0% |
| 34 | Illuminated Fraction of the Disk of a Planet | MEDIUM | `░░░░░░░░░░` 0% |
| 35 | Central Meridian of Jupiter | MEDIUM | `░░░░░░░░░░` 0% |
| 36 | Positions of the Satellites of Jupiter | HIGH | `░░░░░░░░░░` 0% |
| 37 | Semidiameters of Sun, Moon and Planets | LOW | `░░░░░░░░░░` 0% |
| 38 | Stellar Magnitudes | LOW | `██████████` 100% |
| 39 | Binary Stars | MEDIUM | `░░░░░░░░░░` 0% |
| 40 | Linear Regression and Correlation | LOW | `█░░░░░░░░░` 10% |
| 41 | Atmospheric Refraction | MEDIUM | `██████████` 100% |
| 42 | Rising, Transit and Setting | MEDIUM | `████████░░` 80% |
| 43 | Heliocentric Position of Pluto | MEDIUM | `░░░░░░░░░░` 0% |

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

### 13 — Position Angle of the Moon's Bright Limb · Complexity: LOW · `░░░░░░░░░░` 0%
**Formulae.** Position angle of the Moon's illuminated limb from the relative positions of Sun and Moon.
**Applications.** Drawing the Moon with correct orientation; planning limb observations.
**Coverage.** Not implemented (needs the full lunar position, which is still absent — though the Sun's position is now available).

### 14 — Precession · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Rigorous reduction of equatorial (and ecliptic) coordinates from one equinox/epoch to another via the precessional angles.
**Applications.** Bringing catalogue positions to the date of observation; comparing positions across epochs.
**Coverage.** *Implemented in 1.2.0.* `Precession` performs the rigorous equatorial reduction via the angles ζ, z, θ and exposes those angles directly. Annual rates reproduce the precessional constants m and n exactly; a forward/back reduction round-trips to sub-milliarcsecond level.

### 15 — Nutation · Complexity: MEDIUM · `█████████░` 90%
**Formulae.** Nutation in longitude (Δψ) and obliquity (Δε) from periodic terms in the Sun's and Moon's elements, plus the true obliquity.
**Applications.** Apparent sidereal time, apparent star/planet places, high-accuracy coordinate work.
**Coverage.** `getNutationInLongitude` and `getNutationInObliquity` are implemented (principal terms), and the 1950/2000 obliquity constants are provided.

### 16 — Apparent Place of a Star · Complexity: HIGH · `██▌░░░░░░░` 25%
**Formulae.** Combine mean catalogue position with proper motion, precession, nutation, and annual aberration to get the apparent place.
**Applications.** Telescope pointing; reducing a catalogue star to tonight's sky.
**Coverage.** Two of the four ingredients are now present — **precession** (Ch. 14) and **nutation** (Ch. 15) — and the Sun's position needed for annual aberration is available (Ch. 18). The remaining work is to compose them with proper motion and the aberration terms into a single end-to-end routine; this is the Phase 2 star keystone.

### 17 — Reduction of Ecliptical Elements from One Equinox to Another · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Transform orbital inclination, node, and perihelion argument between reference equinoxes.
**Applications.** Using orbital elements quoted for a different epoch.
**Coverage.** Not implemented.

### 18 — Solar Coordinates · Complexity: MEDIUM · `██████████` 100%
**Formulae.** Sun's mean longitude and mean anomaly, equation of centre, true and apparent longitude, radius vector, and apparent right ascension/declination.
**Applications.** Day/night and twilight, the Sun's place for refraction and for the Moon's phase, solar-geometry tools.
**Coverage.** *Completed in 1.2.0.* The equation of centre, true anomaly, true and apparent longitude, radius vector, mean obliquity and apparent equatorial coordinates are all implemented. Cross-checked against the canonical 1992-10-13 worked example to better than 0.01°. This is the keystone enabler on which both later tracks build.

### 19 — Rectangular Coordinates of the Sun · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** The Sun's geocentric X, Y, Z referred to the equator/ecliptic.
**Applications.** Building geocentric planet/comet positions; eclipse geometry.
**Coverage.** Not implemented (now a short add, since the Sun's spherical apparent position and radius vector are available).

### 20 — Equinoxes and Solstices · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Instants of the equinoxes and solstices, found iteratively from the Sun's longitude.
**Applications.** Season boundaries; calendar and almanac work.
**Coverage.** Not implemented (the Sun's longitude it iterates on is now available).

### 21 — Equation of Time · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Difference between apparent and mean solar time.
**Applications.** Sundials, the analemma, converting clock time to true solar time.
**Coverage.** Not implemented, but now a short add: it follows directly from the Sun's mean longitude and the apparent right ascension already produced by Chapter 18.

### 22 — Equation of Kepler · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Solving M = E − e·sin E for the eccentric anomaly (iteration), then the true anomaly and radius vector.
**Applications.** The heart of every elliptical-orbit position calculation.
**Coverage.** Not implemented (general iterative solvers exist in the interpolation engine but Kepler's equation is not wired up).

### 23 — Elements of the Planetary Orbits · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Mean orbital elements of the planets as polynomials in time.
**Applications.** Starting point for any planetary position.
**Coverage.** Not implemented.

### 24 — Planets: Principal Perturbations · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** Periodic perturbation series correcting the two-body elements of the major planets.
**Applications.** Bringing planetary positions to arc-minute (and better) accuracy.
**Coverage.** Not implemented.

### 25 — Elliptic Motion · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** From elements → heliocentric → geocentric equatorial coordinates of a body in an elliptical orbit.
**Applications.** Positions of planets and elliptical-orbit minor planets.
**Coverage.** Not implemented.

### 26 — Parabolic Motion · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Position of a body on a parabolic orbit (Barker's equation).
**Applications.** Comet ephemerides.
**Coverage.** Not implemented.

### 27 — Planets in Perihelion and Aphelion · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Times and distances of perihelion/aphelion passage.
**Applications.** Apsides predictions; brightness timing.
**Coverage.** Not implemented.

### 28 — Passages Through the Nodes · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Times a body crosses the ecliptic (ascending/descending node).
**Applications.** Eclipse seasons; latitude-zero crossings.
**Coverage.** Not implemented.

### 29 — Correction for Parallax · Complexity: MEDIUM · `█▌░░░░░░░░` 15%
**Formulae.** Convert geocentric to topocentric coordinates using the observer's ρ·sin φ′ / ρ·cos φ′ and the body's parallax; Earth–body distance from horizontal parallax.
**Applications.** Moon and near-body positions as seen from a specific site; occultation/eclipse timing.
**Coverage.** A helper is present — `earthDistanceToMoonInKilometers(parallax)` plus the Earth-radius constant — and the observer's ρ·sin φ′ / ρ·cos φ′ are now available from Chapter 6. The topocentric coordinate correction itself is still not implemented (a Phase 3 harvest item).

### 30 — Position of the Moon · Complexity: HIGH · `██░░░░░░░░` 20%
**Formulae.** Moon's mean elements (L′, M′, F, D, Ω) followed by long periodic series for longitude, latitude, and parallax → geocentric position and distance.
**Applications.** Phases, eclipses, occultations, the Moon's place in the sky.
**Coverage.** The library supplies all the mean elements and an Earth–Moon distance helper, but none of the periodic terms or the finished position — so only the easy front end of a hard chapter is done. This is the Phase 2 Moon keystone.

### 31 — Illuminated Fraction of the Moon's Disk · Complexity: LOW · `░░░░░░░░░░` 0%
**Formulae.** Phase angle and illuminated fraction from Sun–Moon geometry.
**Applications.** Phase displays; observation planning around moonlight.
**Coverage.** Not implemented (waiting on the Moon's position; the Sun's is ready).

### 32 — Phases of the Moon · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Times of new/first-quarter/full/last-quarter from mean phase plus periodic corrections.
**Applications.** Calendars, almanacs, tide context.
**Coverage.** Not implemented.

### 33 — Eclipses · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** Whether and when solar/lunar eclipses occur, and their basic circumstances.
**Applications.** Eclipse prediction.
**Coverage.** Not implemented.

### 34 — Illuminated Fraction of the Disk of a Planet · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Phase angle and illuminated fraction of a planet from heliocentric/geocentric distances.
**Applications.** Appearance of Venus/Mars; magnitude inputs.
**Coverage.** Not implemented.

### 35 — Central Meridian of Jupiter · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Longitude of Jupiter's central meridian (Systems I/II).
**Applications.** Timing transits of the Great Red Spot and other features.
**Coverage.** Not implemented.

### 36 — Positions of the Satellites of Jupiter · Complexity: HIGH · `░░░░░░░░░░` 0%
**Formulae.** Apparent positions of the four Galilean moons.
**Applications.** Identifying moons; predicting transits, eclipses, occultations of the satellites.
**Coverage.** Not implemented.

### 37 — Semidiameters of Sun, Moon and Planets · Complexity: LOW · `░░░░░░░░░░` 0%
**Formulae.** Apparent angular radius from distance.
**Applications.** Rise/set refinement, eclipse and occultation geometry, sizing for drawings.
**Coverage.** Not implemented.

### 38 — Stellar Magnitudes · Complexity: LOW · `██████████` 100%
**Formulae.** Pogson's relation; combining magnitudes; brightness ratios.
**Applications.** Variable-star work; combined brightness of close pairs; limiting-magnitude estimates.
**Coverage.** *Implemented in 1.2.0.* `StellarMagnitudes` provides Pogson's ratio, the brightness ratio for a magnitude difference and its inverse, and the combined magnitude of two or more bodies (matches the double-star example, combined 1.96 + 2.89 → 1.58).

### 39 — Binary Stars · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Apparent orbit of a visual binary from its elements (a Kepler-equation problem) → separation and position angle at a date.
**Applications.** Ephemerides for double-star observers.
**Coverage.** Not implemented (needs a Kepler solver; the position-angle/separation output side can reuse Chapter 9).

### 40 — Linear Regression and Correlation · Complexity: LOW · `█░░░░░░░░░` 10%
**Formulae.** Least-squares straight-line fit and the correlation coefficient.
**Applications.** Reducing observation series; trend fitting.
**Coverage.** Not exposed directly; the Apache Commons Math dependency could supply it, and the interpolation module offers Lagrange/Laguerre fitting, but regression itself is not part of the public API.

### 41 — Atmospheric Refraction · Complexity: MEDIUM · `██████████` 100%
**Formulae.** True⇄apparent elevation, with optional temperature and pressure corrections.
**Applications.** Correcting observed altitudes; rise/set timing; sextant sight reduction.
**Coverage.** Fully implemented by `AtmosphericRefractionCalculator`, in both directions and with conditions (default 10 °C, 1013 mbar), guarding negative elevations.

### 42 — Rising, Transit and Setting · Complexity: MEDIUM · `████████░░` 80%
**Formulae.** Local hour angle, and the times a body rises, transits, and sets (iterative, with refraction at the horizon).
**Applications.** Sun/Moon/planet/star rise–set tables; observation windows.
**Coverage.** *Implemented in 1.2.0.* `RiseTransitSetCalculator` returns rise/transit/set times (UT), rise/set azimuths, transit altitude, and circumpolar / never-rises flags, with selectable standard altitudes for stars/planets and the Sun. It uses the first-approximation form (apparent RA/Dec held constant over the day): exact for stars and accurate to a few minutes for the Sun (validated against the Sun from Uccle on 1978-11-13). The remaining 20% is the iterative refinement that interpolates a fast body's position across the day — most relevant for the Moon.

### 43 — Heliocentric Position of Pluto · Complexity: MEDIUM · `░░░░░░░░░░` 0%
**Formulae.** Pluto's heliocentric coordinates from a dedicated periodic series (the chapter added in the 4th edition).
**Applications.** Locating Pluto.
**Coverage.** Not implemented.

---

## Reading the pattern

With Phase 1 complete, the picture has shifted. First, the **foundational arc is now
unbroken**: Chapters 1–9 are all strong, and the three companion chapters the foundation
leans on (14 Precession, 15 Nutation, 18 Solar Coordinates) are done, as are 38 and 41.
Second, the **two keystones are now de-risked**: Apparent Place of a Star (16) has three of
its four ingredients in place (precession, nutation, and the Sun for aberration), and
Position of the Moon (30) still needs its periodic series but sits on mean elements that are
ready. Third, the **next cheap wins are clustered around the Sun**: Equation of Time (21),
Equinoxes/Solstices (20) and Rectangular Coordinates of the Sun (19) are now short additions
because the Sun's apparent longitude, anomaly and radius vector are available — natural
candidates to fold into a later phase alongside the Moon and star keystones.

*Notes on sourcing: chapter titles and order are from the 4th-edition table of contents; coverage and complexity are assessed against the EphemerisEngine source and its reference card. A handful of percentages (e.g. the partial Moon/observer/star chapters and the 80% on rising/transit/setting) are judgement calls about how much of each chapter's finished output the library actually produces.*
