# EphemerisEngine — Reference Card

### The astronomical formulae of Jean Meeus, in Java

> **CONTENTS** &nbsp;·&nbsp; Conventions &nbsp;·&nbsp; Sexagesimal &nbsp;·&nbsp; Julian Day & Calendar &nbsp;·&nbsp; Sidereal Time &nbsp;·&nbsp; Delta-T / ET <-> UT &nbsp;·&nbsp; Sun & Moon Elements &nbsp;·&nbsp; Nutation &nbsp;·&nbsp; Easter &nbsp;·&nbsp; Coordinate Systems &nbsp;·&nbsp; Atmospheric Refraction &nbsp;·&nbsp; Interpolation &nbsp;·&nbsp; Constants &nbsp;·&nbsp; Build

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

```java
EphemerisEngine e = new EphemerisEngineImpl();
double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113, new Sexagesimal(4,34,0));
double dPsi = e.getNutationInLongitude(jd);   // ~ -3.378"
double dEps = e.getNutationInObliquity(jd);   // ~ -9.321"
double T = e.T(jd);
double sunL = e.sunMeanLongitude(T);
```

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

## 9. Constants — `Constants`

| Constant | Value |
|---|---|
| `BIG_DECIMAL_PRECISION` | `MathContext.DECIMAL64` |
| `EARTH_FLATTENING` | 1 / 298.257 |
| `GREGORIAN_CALENDAR_START_DATE` | 1582.1015 |
| `ECLIPTIC_OBLIQUITY_1950` | 23° 26' 44.84" (`Sexagesimal`) |
| `ECLIPTIC_OBLIQUITY_2000` | 23° 26' 21.45" (`Sexagesimal`) |
| `EARTH_EQUATORIAL_RADIUS_IN_KM` | 6378.140 |

---

## 10. BUILD & DEPENDENCIES

```
mvn clean verify            # compile + run the 43-test suite + build the jar
```

- **Java:** 17+   ·   **Build:** Maven 3.8+   ·   **Test:** JUnit 4.13.2
- **Runtime dependency:** Apache Commons Math 3.6.1 (interpolation only)
- **CI:** GitHub Actions, JDK 17 and 21 matrix (`.github/workflows/maven.yml`)
- **Artifact:** `com.nzv.astro:meeus-engine:1.1.0`

---

## 11. END-TO-END EXAMPLE

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

*EphemerisEngine 1.1.0 — formulae after J. Meeus, "Astronomical Formulae for
Calculators". Reference card generated June 2026.*
