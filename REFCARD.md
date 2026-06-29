# EphemerisEngine — Reference Card

### The astronomical formulae of Jean Meeus, in Java

> **CONTENTS** &nbsp;·&nbsp; Conventions &nbsp;·&nbsp; Sexagesimal &nbsp;·&nbsp; Julian Day & Calendar &nbsp;·&nbsp; Sidereal Time &nbsp;·&nbsp; Delta-T / ET <-> UT &nbsp;·&nbsp; Sun & Moon Elements &nbsp;·&nbsp; Solar Coordinates &nbsp;·&nbsp; Nutation &nbsp;·&nbsp; Easter &nbsp;·&nbsp; Coordinate Systems &nbsp;·&nbsp; Precession &nbsp;·&nbsp; Angular Separation &nbsp;·&nbsp; Stellar Magnitudes &nbsp;·&nbsp; Rise / Transit / Set &nbsp;·&nbsp; Apparent Place of a Star &nbsp;·&nbsp; Position of the Moon &nbsp;·&nbsp; Atmospheric Refraction &nbsp;·&nbsp; Interpolation &nbsp;·&nbsp; Equation of Kepler &nbsp;·&nbsp; Constants &nbsp;·&nbsp; Build

---

## ABOUT THIS REFCARD

EphemerisEngine is a pure-Java calculation library implementing the formulae from
Jean Meeus' *Astronomical Formulae for Calculators*. It has no UI and no runtime
dependencies beyond Apache Commons Math. This card summarises every public type and
the methods you will actually call, with runnable snippets. Package root:
`com.nzv.astro.ephemeris`.

---

## 1. CONVENTIONS YOU MUST KNOW

| Convention | Rule | Example |
|---|---|---|
| **Date encoding** | A calendar date is a `double` shaped `yyyy.MMdd`. | `1978.1113` = 13 Nov 1978 |
| **Time of day** | Passed separately as a `Sexagesimal` (h, m, s). | `new Sexagesimal(4, 34, 0)` |
| **Angles** | Degrees unless a method name says hours. RA is often hours. | longitude in degrees |
| **Sidereal time** | Returned in **hours** (decimal). | `8.0294...` |
| **Refraction** | Computed internally in arcseconds, returned as degrees. | — |
| **Julian Day** | `double`; `.5` fraction = midnight (Meeus convention). | `2443825.5` |
| **Dates are UTC** | All `java.util.Date` results are anchored in UTC. Format with a UTC `SimpleDateFormat`. | — |

> **HOT TIP:** dates use `yyyy.MMdd`, *not* a fraction of a year. `1957.100481`
> means "4.81 October 1957" (month 10, day 04.81), not "year 1957.10".

---

## 2. Sexagesimal — degrees/hours, minutes, seconds

`com.nzv.astro.ephemeris.Sexagesimal`

| Member | Purpose |
|---|---|
| `Sexagesimal(int unit, int minute, double second)` | Build from components. |
| `Sexagesimal(double decimal)` | Build from a decimal value. |
| `double getValueAsUnits()` | Decimal value (degrees or hours). |
| `boolean isNegative()` | Correct even for magnitudes below one unit. |
| `static double sexagesimalToDecimal(Sexagesimal)` | Components -> decimal. |
| `static BigDecimal sexagesimalToBigDecimal(Sexagesimal)` | High-precision variant. |
| `static Sexagesimal decimalToSexagesimal(double)` | Decimal -> components. |
| `String toString(SexagesimalType type)` | `DEGREES` -> `12° 3' 4.5"`, `HOURS` -> `7H 42m 15.5s`. |

```java
Sexagesimal lat = new Sexagesimal(50, 47, 55);
double deg = lat.getValueAsUnits();                 // 50.79861...
Sexagesimal s = Sexagesimal.decimalToSexagesimal(-0.5930);
s.isNegative();                                     // true (sign preserved)
s.toString(SexagesimalType.DEGREES);                // "-0° 35' 34.8\""
```

---

## 3. Julian Day & Calendar — `JulianDay` (static)

| Method | Returns |
|---|---|
| `getJulianDayFromDateAsDouble(double date)` | JD at 0h for a `yyyy.MMdd` date. |
| `getJulianDayFromDateAsDouble(double date, Sexagesimal time)` | JD including time of day. |
| `getDateFromJulianDay(double jd)` | `java.util.Date` (UTC) for a JD. |
| `getDayOfWeekFromDayAsDouble(double date)` | `DayOfWeek` enum. |
| `getDayOfYearFromDateAsDouble(double date)` | Day number 1..366. |
| `isLeapYear(int year)` | Gregorian leap-year test. |
| `getJulianDateFromDate(Date)` | *Not implemented* — throws `UnsupportedOperationException`. |

```java
double jd = JulianDay.getJulianDayFromDateAsDouble(1957.100481d);   // 2436116.31
Date d    = JulianDay.getDateFromJulianDay(2436116.31);            // 1957-10-04 19:26:24 UTC
DayOfWeek dow = JulianDay.getDayOfWeekFromDayAsDouble(1954.0630d); // WEDNESDAY

SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
f.setTimeZone(TimeZone.getTimeZone("UTC"));                        // always format in UTC
```

> **HOT TIP:** the day-of-week and "date at midnight" helpers require the JD to land
> exactly on midnight; otherwise they raise `IllegalArgumentException`.

---

## 4. Sidereal Time & Time Scales — `MeeusEphemeris`

`com.nzv.astro.ephemeris.MeeusEphemeris` / `impl.MeeusEphemerisImpl`

| Method | Returns |
|---|---|
| `getMeanSiderealTimeAsHoursFromJulianDay(double jd)` | Mean sidereal time at 0h, in hours. |
| `getMeanSiderealTimeAsHoursFromJulianDay(double jd, Sexagesimal hourOfDay)` | Mean sidereal time at a given UT. |
| `getApparentSiderealTimeAsHoursFromJulianDay(double jd, Sexagesimal hourOfDay)` | Apparent sidereal time (mean + equation of the equinoxes). |
| `computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(double jd)` | Delta-T (ET - UT) in seconds. |
| `universalTimeToEphemerisTime(double date, Sexagesimal ut)` | UT -> ET. |
| `ephemerisTimeToUniversalTime(double date, Sexagesimal et)` | ET -> UT. |
| `getEasterDateForYear(int year)` | Gregorian date of Easter (year >= 1583). |

```java
MeeusEphemeris m = new MeeusEphemerisImpl();
double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
double gmst = m.getMeanSiderealTimeAsHoursFromJulianDay(jd, new Sexagesimal(4,34,0));
double gast = m.getApparentSiderealTimeAsHoursFromJulianDay(jd, new Sexagesimal(4,34,0));
double dT   = m.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(jd);  // seconds
```

> **HOT TIP:** Delta-T uses the NASA polynomial series for years -1999..+3000 and a
> Meeus fallback outside that window. `getEasterDateForYear` throws
> `InvalidParameterException` before 1583.

---

## 5. Sun & Moon Mean Elements + Nutation — `EphemerisEngine`

`com.nzv.astro.ephemeris.EphemerisEngine` / `impl.EphemerisEngineImpl`.
`T` is time in Julian centuries from 1900.0 (`T(jd)`); element methods take `T`.

| Method (argument) | Returns (degrees unless noted) |
|---|---|
| `T(double jd)` | Julian centuries since 1900.0. |
| `sunMeanLongitude(double T)` / `sunMeanAnomaly(double T)` | Sun L, M. |
| `moonMeanLongitude(double T)` / `moonMeanAnomaly(double T)` | Moon L', M'. |
| `moonAscendantNodeLongitude(double T)` | Node longitude Omega. |
| `moonMeanElongation(double T)` | Elongation D. |
| `moonMeanDistanceToAscendantNode(double T)` | Argument of latitude F. |
| `getNutationInLongitude(double jd)` | Delta-psi, arcseconds. |
| `getNutationInObliquity(double jd)` | Delta-epsilon, arcseconds. |
| `earthDistanceToMoonInKilometers(double parallax)` | Earth-Moon distance, km. |
| `H(double gst, double longitude, double rightAscension)` | Local hour angle, degrees. |
| `meanObliquityOfEcliptic(double T)` | Mean obliquity epsilon-0, degrees. |
| `sunEquationOfCenter(double T)` | Equation of centre C, degrees. |
| `sunTrueLongitude(double T)` / `sunTrueAnomaly(double T)` | Sun true L, v, degrees [0,360). |
| `sunApparentLongitude(double T)` | Apparent longitude (nutation + aberration), degrees. |
| `sunRadiusVector(double T)` | Earth-Sun distance, AU. |
| `sunApparentEquatorialCoordinates(double jd)` | Sun apparent `EquatorialCoordinates` (**RA in degrees**, Dec degrees). |

```java
EphemerisEngine e = new EphemerisEngineImpl();
double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113, new Sexagesimal(4,34,0));
double dPsi = e.getNutationInLongitude(jd);   // ~ -3.378"
double dEps = e.getNutationInObliquity(jd);   // ~ -9.321"
double T = e.T(jd);
double sunL = e.sunMeanLongitude(T);

// Chapter 18 — the Sun's apparent place (RA returned in DEGREES; /15 for hours)
double appLong = e.sunApparentLongitude(T);            // ~ 230.25 deg
EquatorialCoordinates sun = e.sunApparentEquatorialCoordinates(jd);
double sunRaHours = sun.getRightAscension() / 15.0;    // ~ 15h11m
double sunDec     = sun.getDeclinaison();              // ~ -17.81 deg
```

> **HOT TIP:** `sunApparentEquatorialCoordinates` returns right ascension in **degrees**
> (consistent with the ecliptic/galactic adapters and with `Precession`,
> `AngularSeparation` and `RiseTransitSetCalculator`). Divide by 15 for hours. The
> apparent RA/Dec use the *true* obliquity of date (mean obliquity plus the nutation term).

---

## 6. Coordinate Systems

**Value types** (`coordinate` / `coordinate.impl`): `EquatorialCoordinates(ra, dec)`,
`EclipticCoordinates(lambda, beta)`, `GalacticCoordinates(l, b)`,
`HorizontalCoordinates(azimuth, elevation)`, `GeographicCoordinates(latitude, longitude)`,
`GeocentricCoordinates(...)`.

**Adapters** convert *from* one system by wrapping its value type:

| Adapter | Key methods |
|---|---|
| `EquatorialCoordinatesAdapter` | `getEcliptiqueLongitude()/Latitude()`, `getGalacticLongitude()/Latitude()`, `getAzimuth(site, gst)`, `getElevation(site, gst)` |
| `EclipticCoordinatesAdapter` | `getRightAscension()`, `getDeclinaison()` |
| `GalacticCoordinatesAdapter` | `getRightAscension()`, `getDeclinaison()` |
| `HorizontalCoordinatesAdapter` | `getRightAscension(site, gst)`, `getDeclinaison(site, gst)` |

```java
// Equatorial -> Ecliptic (Pollux)
EquatorialCoordinatesAdapter eq =
    new EquatorialCoordinatesAdapter(new EquatorialCoordinates(115.564688, 28.148642));
double lambda = eq.getEcliptiqueLongitude();   // 112.5257
double beta   = eq.getEcliptiqueLatitude();    // 6.6866

// Equatorial -> Horizontal (needs observer site + apparent sidereal time)
GeographicCoordinates uccle = new GeographicCoordinates(
    Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50,47,55)),
    -(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0,17,25.94)) * 15));
double gast = new MeeusEphemerisImpl().getApparentSiderealTimeAsHoursFromJulianDay(
    JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4,34,0));
double az = eq.getAzimuth(uccle, gast);        // 128° 18' 03"
double h  = eq.getElevation(uccle, gast);      // 36° 32' 25.7"
```

> **HOT TIP:** `getAzimuth()`/`getElevation()` with no arguments throw on purpose —
> horizontal coordinates depend on the observer and the instant, so use the
> `(GeographicCoordinates, double siderealTime)` overloads. Geographic longitude is
> measured positive **west**; multiply an east-of-Greenwich hour value by 15 and negate
> as shown.

---

## 7. Atmospheric Refraction — `AtmosphericRefractionCalculator`

`impl.AtmosphericRefractionCalculatorImpl`. Elevations in degrees; default conditions
are 10 °C and 1013 mbar.

| Method | Returns |
|---|---|
| `getTrueElevation(double apparent)` | True elevation from apparent. |
| `getApparentElevation(double trueElev)` | Apparent elevation from true. |
| `getTrueElevation(double apparent, double tempC, double pressureMbar)` | With conditions. |
| `getApparentElevation(double trueElev, double tempC, double pressureMbar)` | With conditions. |

```java
AtmosphericRefractionCalculator arc = new AtmosphericRefractionCalculatorImpl();
double trueEl = arc.getTrueElevation(
    Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32,4,17)));   // 32.0456°
double withTP = arc.getTrueElevation(/*apparent*/ 2.2387, 38, 1023);
```

> **HOT TIP:** elevation must be >= 0; a negative value throws `InvalidParameterException`.
> Pressure correction is ~1% per 10 mbar; temperature ~1% per 2.8 °C.

---

## 8. Interpolation — `InterpolationEngine`

`interpolation.InterpolationEngineImpl`. Samples are `InterpolationData(x, y)` pairs at
**equal x-spacing**. Methods declare `throws InterpolationException`.

| Method | Purpose |
|---|---|
| `interpolate(d1, d2, d3, x)` | 3-point interpolation at `x`. |
| `interpolate(d1..d5, x)` | 5-point interpolation. |
| `findExtremum(d1, d2, d3)` | Returns the `(x, y)` of the local extremum. |
| `findExtremum(d1..d5)` | 5-point extremum (iterative). |
| `findZero(d1, d2, d3)` | x where y = 0 (iterative). |
| `findZero(d1..d5)` | 5-point root (iterative). |
| `interpolate(List, x)` / `findZero(List)` | Lagrange / Laguerre via Commons Math. |
| `findExtremum(List)` | *Guarded* — throws `UnsupportedOperationException`. |

```java
InterpolationEngine iei = new InterpolationEngineImpl();
InterpolationData a = new InterpolationData(jd1, 0.664531);
InterpolationData b = new InterpolationData(jd2, 0.669651);
InterpolationData c = new InterpolationData(jd3, 0.674800);
double marsDist = iei.interpolate(a, b, c, searchJd);     // 0.67574 AU
InterpolationData perihelion = iei.findExtremum(a, b, c); // (x = JD, y = min distance)
```

> **HOT TIP:** the iterative solvers now stop at a tolerance of 1e-12 with a 100-iteration
> cap and throw `InterpolationException` if they do not converge — they can no longer
> spin forever.

---

## 9. Precession — `Precession`

`com.nzv.astro.ephemeris.Precession` / `impl.PrecessionImpl`. Rigorous reduction of
equatorial coordinates between two equinoxes (Chapter 14). RA and Dec in **degrees**;
epochs are decimal years.

| Method | Returns |
|---|---|
| `precess(EquatorialCoordinates c, double fromEpoch, double toEpoch)` | Coordinates referred to `toEpoch` (RA normalised to [0,360)). |
| `precessionalAngles(double fromEpoch, double toEpoch)` | `{zeta, z, theta}` in degrees. |

```java
Precession p = new PrecessionImpl();
// Regulus from B1950.0 to J2000.0
EquatorialCoordinates b1950 = new EquatorialCoordinates(151.42875, 12.2125);
EquatorialCoordinates j2000 = p.precess(b1950, 1950.0, 2000.0);  // RA 152.0973°, Dec 11.9673°
double[] angles = p.precessionalAngles(1950.0, 2000.0);          // {0.32015, 0.32021, 0.27834}°
```

> **HOT TIP:** this is precession only. To reproduce a catalogue position you also need
> proper motion, nutation and aberration (Chapter 16) — precession alone leaves the star's
> own motion uncorrected.

---

## 10. Angular Separation — `AngularSeparation` (static)

`com.nzv.astro.ephemeris.coordinate.AngularSeparation`. Great-circle separation and
relative position angle (Chapter 9). Inputs in **degrees**; works with any consistent
spherical pair (equatorial or ecliptic).

| Method | Returns |
|---|---|
| `between(EquatorialCoordinates a, EquatorialCoordinates b)` | Separation, degrees [0,180]. |
| `between(double lon1, double lat1, double lon2, double lat2)` | Separation, degrees [0,180]. |
| `positionAngle(EquatorialCoordinates a, EquatorialCoordinates b)` | Position angle of `b` w.r.t. `a`, degrees [0,360). |

```java
EquatorialCoordinates arcturus = new EquatorialCoordinates(213.9154, 19.1825);
EquatorialCoordinates spica    = new EquatorialCoordinates(201.2983, -11.1614);
double sep = AngularSeparation.between(arcturus, spica);          // 32.793°
double pa  = AngularSeparation.positionAngle(arcturus, spica);    // 203.308°
```

---

## 11. Stellar Magnitudes — `StellarMagnitudes` (static)

`com.nzv.astro.ephemeris.StellarMagnitudes`. Pogson's relation and combined magnitudes
(Chapter 37).

| Member | Returns |
|---|---|
| `POGSON_RATIO` | Brightness ratio for one magnitude (~2.512). |
| `brightnessRatio(double m1, double m2)` | Ratio b1/b2 = 10^(0.4 (m2 - m1)). |
| `magnitudeDifference(double ratio)` | 2.5 log10(ratio); throws `IllegalArgumentException` if ratio <= 0. |
| `combinedMagnitude(double m1, double m2)` / `combinedMagnitude(double... ms)` | Combined magnitude of a blended source. |

```java
double combined = StellarMagnitudes.combinedMagnitude(1.96, 2.89);  // 1.576
double ratio    = StellarMagnitudes.brightnessRatio(0.0, 1.0);      // 2.5119
double dm       = StellarMagnitudes.magnitudeDifference(100.0);     // 5.0
```

---

## 12. Rising, Transit & Setting — `RiseTransitSetCalculator`

`com.nzv.astro.ephemeris.RiseTransitSetCalculator` → returns `RiseTransitSet` (supplementary utility — not a chapter of the reference edition).
First-approximation method: the body's apparent RA/Dec are treated as constant over the
day (exact for stars, a good estimate for Sun/planets). Body RA in **degrees**, observer
longitude positive **west**, Greenwich apparent sidereal time in **hours**. Times are UT
decimal hours in [0,24).

| Member | Returns |
|---|---|
| `STANDARD_ALTITUDE_STAR_PLANET` / `STANDARD_ALTITUDE_SUN` | Horizon altitudes -0.5667 / -0.8333 deg. |
| `compute(body, observer, ast0h)` | `RiseTransitSet` using the star/planet horizon. |
| `compute(body, observer, ast0h, standardAltitude)` | `RiseTransitSet` for a chosen horizon. |
| `RiseTransitSet`: `getRiseTimeUT()`, `getTransitTimeUT()`, `getSetTimeUT()` | UT hours (NaN if no rise/set). |
| `RiseTransitSet`: `getRiseAzimuth()`, `getSetAzimuth()`, `getTransitAltitude()` | Degrees. |
| `RiseTransitSet`: `isCircumpolar()`, `isAlwaysBelowHorizon()` | Horizon-crossing flags. |

```java
// Sunrise / transit / sunset of the Sun from Uccle on 1978-11-13
EphemerisEngine e = new EphemerisEngineImpl();
MeeusEphemeris  m = new MeeusEphemerisImpl();
double jd    = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
double ast0h = m.getApparentSiderealTimeAsHoursFromJulianDay(jd, new Sexagesimal(0,0,0));
EquatorialCoordinates sun = e.sunApparentEquatorialCoordinates(jd);
GeographicCoordinates uccle = new GeographicCoordinates(
        Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50,47,55)),
        -(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0,17,25.94)) * 15));

RiseTransitSet rts = new RiseTransitSetCalculator()
        .compute(sun, uccle, ast0h, RiseTransitSetCalculator.STANDARD_ALTITUDE_SUN);
double sunrise = rts.getRiseTimeUT();      // ~ 06:52 UT
double noon    = rts.getTransitTimeUT();   // ~ 11:25 UT
double sunset  = rts.getSetTimeUT();       // ~ 15:57 UT
```

> **HOT TIP:** feed `sunApparentEquatorialCoordinates` straight in — both use RA in degrees.
> For circumpolar or never-rising bodies the rise/set times are `NaN` and the matching flag
> is set; the transit time is always defined.

---

## 13. Apparent Place of a Star — `ApparentPlace`

`com.nzv.astro.ephemeris.ApparentPlace` / `impl.ApparentPlaceImpl`. Composes proper
motion, precession (Ch 14), nutation (Ch 15) and annual aberration into the apparent
place of a star. Construct the impl with an `EphemerisEngine` and a `Precession`.
RA/Dec in **degrees**; proper motions in **arcsec/year** as plain coordinate rates
(multiply a Meeus seconds-of-time/year value by 15). The equinox of date is derived
from the JD, so you pass only the *catalogue* epoch.

| Method | Returns |
|---|---|
| `apparentPlace(mean, catalogueEpoch, muAlpha, muDelta, jd)` | Apparent `EquatorialCoordinates` at the instant. |
| `applyProperMotion(mean, muAlpha, muDelta, years)` | Mean place carried forward by proper motion. |
| `nutationCorrection(mean, jd)` | `{Δα, Δδ}` nutation terms, degrees. |
| `aberrationCorrection(mean, jd)` | `{Δα, Δδ}` annual-aberration terms (incl. eccentricity), degrees. |
| `ABERRATION_CONSTANT` | Constant of aberration, 20.49552″ (static field on impl). |

```java
EphemerisEngine engine = new EphemerisEngineImpl();
ApparentPlace ap = new ApparentPlaceImpl(engine, new PrecessionImpl());

// Theta Persei (mean J2000) reduced to 1978-11-13:
EquatorialCoordinates mean = new EquatorialCoordinates(
    (2 + 44 / 60.0 + 11.986 / 3600.0) * 15, 49 + 13 / 60.0 + 42.48 / 3600.0);
double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
EquatorialCoordinates apparent =
    ap.apparentPlace(mean, 2000.0, 0.03425 * 15, -0.0895, jd);  // 40.6964°, +49.1398°
```

> **HOT TIP:** aberration uses the Sun's **true** longitude (`sunTrueLongitude`), not the
> apparent one — the apparent longitude already folds in nutation + aberration and would
> double-count.

---

## 14. Position of the Moon — `EphemerisEngine` (Ch. 30)

Geocentric longitude, latitude and equatorial horizontal parallax of the Moon, via the abridged
AFFC chapter-30 theory. Accuracy ~10″ longitude, 3″ latitude, 0.2″ parallax. All angles in degrees.

| Method | Returns |
|---|---|
| `moonGeocentricLongitude(T)` | true longitude λ of date (deg, normalised). |
| `moonGeocentricLatitude(T)` | latitude β of date (deg). |
| `moonEquatorialHorizontalParallaxe(T)` | horizontal parallax π (deg). |
| `moonApparentEquatorialCoordinates(jd)` | apparent RA/Dec (deg): adds Δψ, rotates with true obliquity of date. |
| `moonGeocentricEclipticCoordinates(jd)` | true ecliptic λ, β of date. |
| `moonDistanceToEarthInKilometers(jd)` | Earth–Moon distance (km), from π. |

The series live in **external CSV tables** under `resources/.../lunar/affc-1900/`, parsed by a
table-driven evaluator (`com.nzv.astro.ephemeris.lunar`). Selecting a model is constructor-level:

```java
EphemerisEngine engine = new EphemerisEngineImpl();              // default: AFFC_1900
// or: new EphemerisEngineImpl(MoonModels.AFFC_1900.getModel());
double T = engine.T(2444214.5);                                 // 1979 Dec 7.0 ET
engine.moonGeocentricLongitude(T);          //  113.6603°   (book 113.6604)
engine.moonGeocentricLatitude(T);           //   -3.16367°  (AE -3°09'49".22)
engine.moonEquatorialHorizontalParallaxe(T);//    0.930249° (book 0.930249)
```

> **HOT TIP:** the `T`-based methods give the **true** position of date; only the `jd`-based
> `moonApparentEquatorialCoordinates` adds nutation + true obliquity. Validate β against the
> Astronomical Ephemeris value, not the book's printed example B (which has a ~2″ slip).

---

## 15. Moon & Sun Phenomena — `EphemerisEngine` (Ch. 31, 13, 32, 20, 19, 21, 29)

Derived phenomena built on the Sun (Ch. 18) and Moon (Ch. 30) positions. All angles in degrees;
phase/equinox methods return a Julian Ephemeris Day.

| Method | Returns |
|---|---|
| `moonPhaseAngle(jd)` | phase angle *i* of the Moon (deg), high accuracy (Ch. 31). |
| `moonIlluminatedFraction(jd)` | illuminated fraction *k* of the disk, in [0, 1] (Ch. 31). |
| `moonPhaseAngleApproximate(jd)` | low-accuracy *i* from mean elements, no latitude (formula 31.4). |
| `moonBrightLimbPositionAngle(jd)` | position angle χ of the bright limb (deg, [0, 360)) (Ch. 13). |
| `equinoxSolsticeJulianDay(year, Season)` | JDE of the equinox/solstice (Ch. 20). |
| `moonPhaseJulianDay(year, MoonPhase)` | JDE of the New/First/Full/Last phase nearest `year` (Ch. 32). |
| `sunRectangularEquatorialCoordinates(jd)` | `double[]{X,Y,Z}` (AU), mean equinox of date (Ch. 19). |
| `sunRectangularEquatorialCoordinates(jd, equinox)` | `double[]{X,Y,Z}` (AU), reduced to `equinox` (e.g. 1950.0). |
| `equationOfTime(jd)` | equation of time in **minutes of time** (Ch. 21). |
| `moonTopocentricEquatorialCoordinates(jd, observer, height, θ₀)` | Moon topocentric α/δ (deg) for an observer (Ch. 29). |

```java
EphemerisEngine e = new EphemerisEngineImpl();
e.moonIlluminatedFraction(2444232.5);                       // 0.3639  (book 0.36, 1979 Dec 25.0)
e.moonBrightLimbPositionAngle(jd);                          // χ ≈ 250.38° (book Example 13.a)
e.equinoxSolsticeJulianDay(1979, Season.SEPTEMBER_EQUINOX); // 2444140.137  (book Example 20.a)
e.moonPhaseJulianDay(1977.13, MoonPhase.NEW_MOON);          // 2443192.6525 (book Example 32.a)
double[] xyz = e.sunRectangularEquatorialCoordinates(2443824.5, 1950.0); // {-0.65138, -0.68379, -0.29650}
e.equationOfTime(2443529.5);                                // -11.171 min  (book -11m10.3s, Example 21.b)
```

Parallax reduction lives in the static `ParallaxCorrection` (Ch. 29):

```java
// rigorous topocentric reduction (use this form for the Moon)
EquatorialCoordinates topo = ParallaxCorrection.topocentric(alphaDeg, deltaDeg, parallaxDeg,
        rhoSinPhiPrime, rhoCosPhiPrime, hourAngleDeg);
ParallaxCorrection.topocentricApproximate(...);             // non-rigorous (Sun/planets/comets)
ParallaxCorrection.parallaxFromDistanceInDegrees(0.3757);   // π from distance (AU), formula 29.1
```

> **HOT TIP:** the elongation chapters (31, 13) use the Sun's **true** longitude
> (`sunTrueLongitude`), while equinoxes/solstices (20) use the **apparent** longitude
> (`sunApparentLongitude`) — the apparent place is what defines the season. Two static helpers,
> `EphemerisEngineImpl.phaseAngleFromCoordinates(...)` and `.brightLimbPositionAngle(...)`, expose
> the pure geometry for validating against externally supplied (A.E.) coordinates.
>
> **HOT TIP (Ch. 29):** for the **Moon** always use the rigorous `topocentric` form — the
> non-rigorous one can be off by tens of arcseconds. For the Sun, planets and comets either form is
> fine. The geocentric hour angle is H = θ₀ − longitude(west) − α.

---

## 16. Equation of Kepler — `KeplerEquation` (static, Ch. 22)

`com.nzv.astro.ephemeris.KeplerEquation`. Solves `E = M + e sin E` for the eccentric anomaly *E*
given the mean anomaly *M* and eccentricity *e*. All angles in degrees.

| Method | Returns |
|---|---|
| `solveEccentricAnomaly(M, e)` | *E* (deg) by Newton's correction (22.3) — **recommended**, tol 1e-9°. |
| `solveEccentricAnomaly(M, e, tol)` | as above, caller-supplied tolerance. |
| `solveEccentricAnomalyByIteration(M, e, tol)` | *E* (deg) by fixed-point iteration (first method); slow for large *e*. |
| `approximateEccentricAnomaly(M, e)` | *E* (deg) by the closed form (22.4); small *e* only. |

```java
KeplerEquation.solveEccentricAnomaly(5.0, 0.100);              // 5.554589253  (book Example 22.b)
KeplerEquation.solveEccentricAnomalyByIteration(5.0, 0.100, 1e-6); // 5.554589 (book Example 22.a)
KeplerEquation.solveEccentricAnomaly(2.0, 0.990);             // 32.361007    (hard case, Newton)
KeplerEquation.approximateEccentricAnomaly(5.0, 0.100);      // 5.554599     (formula 22.4)
```

> **HOT TIP:** internally the chapter uses the *modified* eccentricity `e0 = e × 180/π` so the work
> can be done in degrees. In Newton's correction the **numerator** carries `e0` while the
> **denominator** carries the ordinary `e` — mixing them up is the easy transcription slip. Prefer
> `solveEccentricAnomaly` (Newton) everywhere; the simple iteration can need 50+ steps near e = 1.

---

## 17. Constants — `Constants`

| Constant | Value |
|---|---|
| `BIG_DECIMAL_PRECISION` | `MathContext.DECIMAL64` |
| `EARTH_FLATTENING` | 1 / 298.257 |
| `GREGORIAN_CALENDAR_START_DATE` | 1582.1015 |
| `ECLIPTIC_OBLIQUITY_1950` | 23° 26' 44.84" (`Sexagesimal`) |
| `ECLIPTIC_OBLIQUITY_2000` | 23° 26' 21.45" (`Sexagesimal`) |
| `EARTH_EQUATORIAL_RADIUS_IN_KM` | 6378.140 |

---

## 18. BUILD & DEPENDENCIES

```
mvn clean verify            # compile + run the 82-test suite + build the jar
```

- **Java:** 17+   ·   **Build:** Maven 3.8+   ·   **Test:** JUnit 4.13.2
- **Runtime dependency:** Apache Commons Math 3.6.1 (interpolation only)
- **CI:** GitHub Actions, JDK 17 and 21 matrix (`.github/workflows/maven.yml`)
- **Artifact:** `com.nzv.astro:meeus-engine:1.4.0`

---

## 19. END-TO-END EXAMPLE

```java
// Where is Saturn in the sky from Uccle on 1978-11-13 at 04:34 UT?
MeeusEphemeris meeus = new MeeusEphemerisImpl();
double gast = meeus.getApparentSiderealTimeAsHoursFromJulianDay(
        JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));

EquatorialCoordinatesAdapter saturn = new EquatorialCoordinatesAdapter(
        new EquatorialCoordinates(
            Sexagesimal.sexagesimalToDecimal(new Sexagesimal(10, 57, 35.681)),
            Sexagesimal.sexagesimalToDecimal(new Sexagesimal(8, 25, 58.10))));

GeographicCoordinates site = new GeographicCoordinates(
        Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
        -(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));

double azimuth   = saturn.getAzimuth(site, gast);    // ~128° 18' 03"
double elevation = saturn.getElevation(site, gast);  // ~ 36° 32' 25.7"

// Refract the true elevation to what an observer actually sees:
double apparent = new AtmosphericRefractionCalculatorImpl().getApparentElevation(elevation);
```

---

*EphemerisEngine 1.4.0 — formulae after J. Meeus, "Astronomical Formulae for
Calculators". Reference card generated June 2026.*
