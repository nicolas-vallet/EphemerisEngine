# EphemerisEngine — Phased Implementation Plan

*A roadmap for closing the gap against Jean Meeus' *Astronomical Formulae for Calculators*, prioritising Moon-relative and star-relative computations, framed on an Effort vs. Value matrix.*

---

## Progress tracker

| Phase | Status | Chapters | Notes |
|---|---|---|---|
| **1 — Enablers & quick wins** | ✅ **DONE (v1.2.0)** | 6, 9, 14, 18, 38, 42 | All shipped with regression tests; 43 → 63 tests. |
| **2 — The two keystones** | ✅ **Done (v1.4.0)** | 16 ✅, 30 ✅ | Star keystone (16) and Moon keystone (30, table-driven series) both shipped. |
| **3 — Harvest** | 🟦 In progress — steps 1–2 ✅, 3a ✅ (v1.5.0–v1.7.0) | 13 ✅, 19 ✅, 20 ✅, 21 ✅, 22 ✅, 29 ✅, 31 ✅, 32 ✅ · then 38 | Steps 1–2 and 3a (equation of Kepler) shipped, 82 → 108 tests. Step 3b (binary stars, Ch 38) is the last Harvest item. |
| **4 — Planets & minor bodies** | 🟦 Committed (Option C); data-acquisition track started | 23–28, 34–36 | Parallel **data-acquisition side-track** live on `feat/phase-4-data-acquisition` (docs/masters only): Ch 23 elements ✅ transcribed & self-checked, Ch 24 perturbations next. See the proposal + data-acquisition tracker. |
| **4 — Orbital-motion engine & minor bodies** | 🟦 In progress — steps 1–2 ✅ (v1.8.0–v1.9.0) | 25 ✅, 26 ✅ · then 27, 28, 36 | Elliptic (Ch 25) and parabolic/comet (Ch 26) engines shipped, 108 → 131 tests. Algorithm-bound, table-free; runs in parallel with the Ch 23/24 data-acquisition track. |
| *Deferred* | ⬜ Out of scope (data-bound) | Major-planet data (elements + perturbations) and Phase-5 derived phenomena. |

**What changed in v1.2.0 (Phase 1):**
- **Ch 18 Solar Coordinates** 25% → **100%** — equation of centre, true/apparent longitude, radius vector, mean obliquity, apparent RA/Dec.
- **Ch 14 Precession** 0% → **100%** — rigorous ζ/z/θ reduction between equinoxes.
- **Ch 9 Angular Separation** 0% → **90%** — separation + position angle.
- **Ch 38 Stellar Magnitudes** 10% → **100%** — Pogson, ratios, combined magnitude.
- **Ch 6 Observer coordinates** 20% → **100%** — ρ·sin φ′ / ρ·cos φ′ accessors.
- **Ch 42 Rising/Transit/Setting** 30% → **80%** — first-approximation rise/transit/set, azimuths, circumpolar flags.

---

## The strategy in one line

Two keystones carry almost all the value: **Chapter 30 (Position of the Moon)** for the Moon track and **Chapter 16 (Apparent Place of a Star)** for the star track. Both are high-effort *and* both depend on prerequisites the library didn't have. The plan front-loaded the cheap shared enablers (Phase 1 — **now done**), builds the two keystones (Phase 2), then harvests the many low-effort chapters that only become trivial once the keystones exist (Phase 3).

Moon work and star work are weighted up on the value axis throughout, per the stated priority. Enablers and the planetary suite are treated as neutral/deferred.

---

## Effort vs. Value matrix

Chapters positioned by implementation effort against value (with the Moon and star tracks weighted highest). ✅ marks items completed in Phase 1.

| | **Low effort** | **High effort** |
|---|---|---|
| **High value** | **Quick wins** — 18 Solar coordinates ✅ · 9 Angular separation ✅ · 42 Rising/transit/setting ✅ · 38 Stellar magnitudes ✅ · 6 Observer coordinates ✅ · 14 Precession ✅ | **Big bets** — 30 Position of the Moon ★★ ✅ · 16 Apparent place of a star ★★ ✅ · 33 Eclipses |
| **Low value** | **Fill-ins** — 31 Illuminated fraction ✅ · 32 Phases of the Moon ✅ · 13 Bright-limb angle ✅ · 29 Parallax ✅ · 21 Equation of time ✅ | **Defer** — 38 Binary stars · 22 Equation of Kepler · 23+ Planetary suite (Ch 23–28, 34–36) |

★★ = the two keystones.

Track legend: Moon track = Ch 13 ✅, 29 ✅, 30 ✅, 31 ✅, 32 ✅ · Star track = Ch 9 ✅, 14 ✅, 16 ✅, 37 ✅ (stellar magnitudes), 38 (binary stars) · Shared/Sun = Ch 18 ✅, 19 ✅, 20 ✅, 21 ✅, 22, 23+, 33, 6 ✅ · rising/transit/setting available as a supplementary utility ✅.

---

## Phase 1 — Enablers and quick wins ✅ COMPLETE (v1.2.0)
*Low effort; clears the runway.*

This phase shipped immediately useful features while quietly building the substrate the keystones need. All items are implemented, validated against worked examples, and covered by regression tests.

- ✅ **Ch 18 — full Solar Coordinates.** The single highest-leverage item, and a hard prerequisite for *both* tracks (star aberration in Ch 16, Moon illumination/phase in Ch 31, 13). Implemented in `EphemerisEngine`/`EphemerisEngineImpl`; cross-checked against the 1992-10-13 example to <0.01°.
- ✅ **Ch 14 — Precession.** The star track's cornerstone. Implemented as `Precession`/`PrecessionImpl` with the rigorous ζ/z/θ angles; annual rates reproduce m and n exactly.
- ✅ **Ch 9 — Angular Separation** and ✅ **Ch 38 — Stellar Magnitudes.** Self-contained star-relative utilities (`AngularSeparation`, `StellarMagnitudes`); both match their Meeus worked examples.
- ✅ **Ch 6 — Observer geocentric coordinates** (ρ·sin φ′, ρ·cos φ′). The prerequisite for topocentric Moon parallax in Phase 3; exposed on `GeocentricCoordinates` and validated against the Palomar example.
- ✅ **Ch 42 — Rising / Transit / Setting.** Finished from its ~30% starting point to a working first-approximation calculator (`RiseTransitSetCalculator`/`RiseTransitSet`) with azimuths, transit altitude and circumpolar handling.

**Exit state — achieved:** the Sun is fully solved, precession works, and the angular and observer-geometry primitives exist. Everything in Phase 2 now has what it needs.

---

## Phase 2 — The two keystones ✅ DONE (v1.4.0)
*High effort, high value — the stated priority.*

These are the marquee deliverables. Each is genuinely laborious, but Phase 1 has removed the blockers.

- ✅ **Ch 30 — Position of the Moon (DONE, v1.4.0).** Geocentric longitude, latitude and parallax built on the mean elements via a **table-driven evaluator** (`com.nzv.astro.ephemeris.lunar`) reading external CSV coefficient tables (AFFC-1900 model), plus `jd`-based apparent RA/Dec, ecliptic coordinates and Earth–Moon distance. Validated on Example 30.a. The table-driven design lets a higher-precision model be added later as data, not code. *Note:* the rise/transit/set iterative Moon refinement was deliberately deferred to Phase 3.
- ✅ **Ch 16 — Apparent Place of a Star (DONE, v1.3.0).** Implemented as `ApparentPlace`/`ApparentPlaceImpl`, composing precession (Ch 14) + nutation (Ch 15) + annual aberration with the Earth-orbit eccentricity terms (using the Sun's true longitude from Ch 18) + proper motion (a plain coordinate rate in arcsec/year). Validated on Theta Persei reduced to the house date 1978-11-13, with the nutation and aberration components cross-checked term by term.

**Exit state — achieved:** both priority tracks' hard cores are done. The Moon and star positions are solved, so the remaining harvest chapters (Phase 3) collapse to short geometric additions.

---

## Phase 3 — Harvest 🟦 IN PROGRESS
*Low effort once the keystones exist. Delivered as three small PRs, one minor version each.*

Each of these was "high effort" only because it presupposed a Moon or Sun position. With Phase 2 done they collapse to short, mostly geometric additions. Eclipses (Ch 33) are explicitly **out** of Phase 3; the planetary suite stays deferred.

- ✅ **Step 1 — Moon phenomena + Sun bonuses (DONE, v1.5.0).** Ch 31 (illuminated fraction), Ch 13 (bright-limb position angle), Ch 32 (phases), plus the Sun-adjacent bonuses Ch 20 (equinoxes & solstices) and Ch 19 (rectangular coordinates of the Sun, of-date and reduced to a chosen equinox). All five validated on their Meeus worked examples; suite 82 → 94 tests.
- ✅ **Step 2 — Parallax + Equation of Time (DONE, v1.6.0).** Ch 29 (topocentric parallax — rigorous and non-rigorous reduction plus a Moon convenience, using Ch 6 ✅) and the Sun bonus Ch 21 (Equation of Time). The Moon rise/transit/set refinement once planned here is **dropped**: re-anchoring to the working 39-chapter edition showed it has no Rising/Transit/Setting chapter, so there is no text to transcribe or worked example to validate against; the first-approximation calculator stays as a supplementary utility. This step also **re-anchored the project's chapter numbering** to the working edition (Stellar Magnitudes 38→37, Binary Stars 39→38, Linear Regression 40→39; the phantom Central Meridian of Jupiter removed; Refraction/RTS reclassified as supplementary, Pluto dropped).
- ✅ **Step 3a — Equation of Kepler (DONE, v1.7.0).** Ch 22 (Equation of Kepler) implemented as the `KeplerEquation` utility with all three methods of the chapter (Newton's correction, fixed-point iteration, closed-form approximation), hand-iterated with no external solver. Validated on Examples 22.a/22.b and the high-eccentricity case.
- ⬜ **Step 3b — Binary stars (a later release).** Ch 38 (binary stars), whose apparent-orbit solution reuses the Kepler solver just shipped and whose separation/position-angle output reuses Ch 9 ✅. The last Harvest item; independent of the Phase 4 engine track now under way in parallel.

**Note on the true-vs-apparent longitude split:** within step 1, the elongation chapters (31, 13) use the Sun's *true* longitude, while equinoxes/solstices (20) use the *apparent* longitude — the apparent place is what defines the season. Mixing these up is a silent error, so it is called out in the findings log.

---

## Phases 4 & 5 — Planets & Minor Bodies 🟦 COMMITTED (Option C)

With Phase 3 effectively complete (only Ch 38 remains) and the Kepler solver (Ch 22) shipped, the
planetary suite is no longer deferred: it proceeds along **Option C** of the *Planets & Minor
Bodies Plan Proposal* — two lanes sharing one engine:

- **Phase 4 — Orbital-motion engine & minor bodies** *(algorithm-bound; needs no tables)*. Ch 25
  elliptic engine + Ch 26 parabolic/comets, driven by **caller-supplied elements** and validated on
  the book's own worked intermediates; plus light derived chapters (Ch 27/28/36). Ships fast,
  independent of any data.
- **Phase 5 — Major planets & planet-derived phenomena** *(data-bound)*. Ch 23 built-in elements →
  Ch 24 perturbations (on a `planetary` table package mirroring `lunar`) → Ch 34 + conjunction
  capstone; Ch 35 Galilean satellites independent.

**Parallel data-acquisition side-track (live).** Because Phase 4 needs no tables and Phase 5 is
table-bound, the data work runs as a decoupled side-track on `feat/phase-4-data-acquisition`
(docs, master spreadsheets, and eventually `resources/.../planetary/**` CSVs — **no Java, no
version bump until code consumes the data**). Kick-off delivered: the **Ch 23 element master**
(Tables 23.A/B/C — of date / 1950.0 / 2000.0, all selectable, default of date), transcribed by
visual reading and self-checked against Example 23.a to sub-arcsecond; the **`planetary` table
contract** (External-Tables guide §7); and the **data-acquisition tracker**. **Ch 24 perturbations
are now complete for all five bodies** — Mercury, Venus, Mars (flat series) and Jupiter, Saturn
(procedural, with auxiliary angles and A − B/e), each pinned by a self-generated regression anchor.
The full Ch 23 + Ch 24 planetary coefficient set is acquired and verified. Engine code (Ch 25) has not started.

---

## Phase 4 — Orbital-motion engine & minor bodies 🟦 IN PROGRESS
*Algorithm-bound, table-free. Consumes caller-supplied elements, so it ships independently of the Ch 23/24 planetary-data track and runs in parallel with it (per the Planets & Minor Bodies proposal, Option C).*

The keystone here is **Chapter 25 (Elliptic Motion)**: it turns orbital elements into a geocentric position and is reused by every later planet and minor-body chapter. It needs no perturbation tables — only the Chapter-22 Kepler solver ✅ and the Chapter-19 Sun ✅, both already in the library.

- ✅ **Step 1 — Elliptic-motion engine (DONE, v1.8.0).** Ch 25, both methods, in the new `com.nzv.astro.ephemeris.orbit` package (`EllipticMotion`, `OrbitalElements`, `OrbitPosition`). The first method (major planets, mean equinox of date) forms heliocentric → geocentric ecliptic → equatorial of date; the second method (minor planets/comets, standard-equinox elements) forms heliocentric rectangular equatorial coordinates via the per-orbit Gaussian constants and reads RA/Dec directly off the Chapter-19 Sun, with a light-time correction. Geometric positions plus elongation, phase angle and the magnitude relations. Validated on Examples 25.a (Mercury) and 25.b (433 Eros) and the 234 Barbara published-ephemeris exercise; suite 108 → 121 tests.
- ✅ **Step 2 — Parabolic motion / comets (DONE, v1.9.0).** Ch 26: `BarkerEquation` solves `s³ + 3s − W = 0` for `s = tan(v/2)` (both the iteration 26.4 and the closed-form 26.5), feeding the **same** geocentric reduction as Ch 25 — now factored into a shared `GeocentricReduction` helper. `ParabolicMotion` + `ParabolicElements`. Validated on Example 26.a (comet Kohler 1977m); suite 121 → 131.
- ⬜ **Step 3 — Light derived chapters (Ch 27, 28, 36).** Perihelion/aphelion instants, nodal passages and semidiameter, for caller-supplied elements/distances.

**End-of-phase deliverable:** *"give me the apparent position of any comet or minor planet from its orbital elements,"* plus apsides, nodes and semidiameter — entirely algorithm-bound. The data-bound major-planet work (Ch 23 elements, Ch 24 perturbations on a `planetary` table package, Ch 34, Ch 10 capstone) is **Phase 5**, gated on the data-acquisition track now running in parallel.

---

## Two notes on realism

**Effort is dominated by data entry and verification, not algorithm design.** The lunar and solar series are long coefficient tables that must be transcribed exactly. Budget the time accordingly, and lean on the fact that Meeus prints a fully worked numerical example for nearly every chapter.

**Make each chapter regression-proof before moving on.** The library ships a 108-test suite built around canonical dates like `1978.1113`. The discipline that makes this plan low-risk is adding each chapter's book example as a regression test *before* moving to the next, so a transcription slip in one coefficient surfaces immediately rather than three chapters later. Phase 1 followed this discipline: every new chapter landed with its worked example (or an independent physical/round-trip check) as a test.

---

## Phase summary at a glance

| Phase | Theme | Chapters | Effort | Status | Unlocks |
|---|---|---|---|---|---|
| **1** | Enablers & quick wins | 6, 9, 14, 18, 38, 42 | Low–medium | ✅ Done (v1.2.0) | The substrate for both keystones |
| **2** | The two keystones | 16 ✅, 30 ✅ | High | ✅ Done (v1.4.0) | Both priority tracks' hard core |
| **3** | Harvest | 13 ✅ 19 ✅ 20 ✅ 21 ✅ 22 ✅ 29 ✅ 31 ✅ 32 ✅ · then 38 | Low (post-keystone) | 🟦 Steps 1–2, 3a done (v1.5.0–v1.7.0) | The derived Moon and star phenomena |
| **4** | Data acquisition | Planets & minor bodies (Option C) | 23–28, 34–36 | L (engine) + XL (Ch 24 data) | 🟦 Committed; data track started | Positions of comets, minor planets, and the major planets |
| **4** | Orbital-motion engine | 25 ✅ 26 ✅ · then 27, 28, 36 | Medium (algorithm-bound) | 🟦 Steps 1–2 done (v1.8.0–v1.9.0) | Positions of comets & minor planets from elements |
| *Phase 5* | Major planets (data-bound) | 23, 24, 34, 10 | High | ⬜ | Built-in major-planet positions; gated on data acquisition |
