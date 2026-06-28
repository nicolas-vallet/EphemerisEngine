# EphemerisEngine — Phased Implementation Plan

*A roadmap for closing the gap against Jean Meeus' *Astronomical Formulae for Calculators*, prioritising Moon-relative and star-relative computations, framed on an Effort vs. Value matrix.*

---

## Progress tracker

| Phase | Status | Chapters | Notes |
|---|---|---|---|
| **1 — Enablers & quick wins** | ✅ **DONE (v1.2.0)** | 6, 9, 14, 18, 38, 42 | All shipped with regression tests; 43 → 63 tests. |
| **2 — The two keystones** | 🟦 **In progress (v1.3.0)** | 16 ✅, 30 | Star keystone (16) shipped: proper motion + precession + nutation + aberration composed. Moon keystone (30) remaining. |
| **3 — Harvest** | ⬜ Not started | 13, 22, 29, 31, 32, 39 (+33 optional) | Becomes cheap once Phase 2 lands. |
| *Deferred* | ⬜ Out of scope | 23–28, 34–36, 43 | Planetary suite. |

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
| **High value** | **Quick wins** — 18 Solar coordinates ✅ · 9 Angular separation ✅ · 42 Rising/transit/setting ✅ · 38 Stellar magnitudes ✅ · 6 Observer coordinates ✅ · 14 Precession ✅ | **Big bets** — 30 Position of the Moon ★★ · 16 Apparent place of a star ★★ ✅ · 33 Eclipses |
| **Low value** | **Fill-ins** — 31 Illuminated fraction (Moon) · 32 Phases of the Moon · 13 Bright-limb angle · 29 Parallax (topocentric Moon) · 21 Equation of time | **Defer** — 39 Binary stars · 22 Equation of Kepler · 23+ Planetary suite (Ch 23–28, 34–36, 43) |

★★ = the two keystones.

Track legend: Moon track = Ch 13, 29, 30, 31, 32 · Star track = Ch 9 ✅, 14 ✅, 16 ✅, 38 ✅, 39 · Shared/deferred = Ch 18 ✅, 21, 22, 23+, 33, 42 ✅, 6 ✅.

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

## Phase 2 — The two keystones 🟦 IN PROGRESS (step 1 done in v1.3.0)
*High effort, high value — the stated priority.*

These are the marquee deliverables. Each is genuinely laborious, but Phase 1 has removed the blockers.

- **Ch 30 — Position of the Moon.** Transcribe and validate the periodic series for longitude, latitude and parallax on top of the mean elements already present, yielding the Moon's geocentric position and distance. This is the keystone that unlocks the entire Moon track.
- ✅ **Ch 16 — Apparent Place of a Star (DONE, v1.3.0).** Implemented as `ApparentPlace`/`ApparentPlaceImpl`, composing precession (Ch 14) + nutation (Ch 15) + annual aberration with the Earth-orbit eccentricity terms (using the Sun's true longitude from Ch 18) + proper motion (a plain coordinate rate in arcsec/year). Validated on Theta Persei reduced to the house date 1978-11-13, with the nutation and aberration components cross-checked term by term.

**Exit state (step 1):** the star track's hard core is done. Once Ch 30 (Moon) lands in step 2, both priority tracks are complete and the remaining harvest chapters become cheap.

---

## Phase 3 — Harvest ⬜
*Low effort once the keystones exist.*

Each of these was "high effort" only because it presupposed a Moon or Sun position. With Phase 2 done they collapse to short, mostly geometric additions.

- **Moon track:** Ch 31 (illuminated fraction), Ch 13 (bright-limb position angle), Ch 32 (phases), Ch 29 (topocentric parallax, using Ch 6 ✅).
- **Star track:** Ch 39 (binary stars) — needs a small Kepler-equation solver (Ch 22), which is itself a modest add and also opens the door to planets later; the separation/position-angle output can reuse Ch 9 ✅.
- **Optional capstone:** Ch 33 (Eclipses) becomes feasible here since it needs accurate Sun *and* Moon — high value, but still high effort, so treat it as a stretch goal rather than a commitment.

**Sun-adjacent bonus (now cheap after Phase 1):** Ch 21 (Equation of Time), Ch 20 (Equinoxes & Solstices) and Ch 19 (Rectangular Coordinates of the Sun) all follow directly from the solar coordinates shipped in Phase 1 and could be folded in opportunistically.

**Deliberately deferred:** the planetary suite (Ch 23–28, 34–36, 43), shown as the `23+` cluster in the matrix's bottom-right. It's the largest body of work in the book and the least aligned with a Moon/star focus, so it sits in the "defer" quadrant until those tracks are complete.

---

## Two notes on realism

**Effort is dominated by data entry and verification, not algorithm design.** The lunar and solar series are long coefficient tables that must be transcribed exactly. Budget the time accordingly, and lean on the fact that Meeus prints a fully worked numerical example for nearly every chapter.

**Make each chapter regression-proof before moving on.** The library ships a 68-test suite built around canonical dates like `1978.1113`. The discipline that makes this plan low-risk is adding each chapter's book example as a regression test *before* moving to the next, so a transcription slip in one coefficient surfaces immediately rather than three chapters later. Phase 1 followed this discipline: every new chapter landed with its worked example (or an independent physical/round-trip check) as a test.

---

## Phase summary at a glance

| Phase | Theme | Chapters | Effort | Status | Unlocks |
|---|---|---|---|---|---|
| **1** | Enablers & quick wins | 6, 9, 14, 18, 38, 42 | Low–medium | ✅ Done (v1.2.0) | The substrate for both keystones |
| **2** | The two keystones | 16 ✅, 30 | High | 🟦 In progress (v1.3.0) | Both priority tracks' hard core |
| **3** | Harvest | 13, 22, 29, 31, 32, 39 (+33 optional) | Low (post-keystone) | ⬜ | The derived Moon and star phenomena |
| *Deferred* | Planetary suite | 23–28, 34–36, 43 | High | ⬜ | (Out of scope for a Moon/star focus) |
