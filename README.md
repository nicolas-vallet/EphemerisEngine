# EphemerisEngine

A Java implementation of the astronomical formulae of **Jean Meeus**, as described
in his book *Astronomical Formulae for Calculators*.

The library is a pure calculation engine (no UI). It covers date and time handling,
sidereal time, nutation, Delta-T, conversions between the main astronomical
coordinate systems, atmospheric refraction and numerical interpolation.

## Features

- **Dates & time** — Julian Day <-> calendar conversions, day of week, day of year,
  leap-year detection, date of Easter, mean & apparent sidereal time, Delta-T
  (Ephemeris Time vs Universal Time) and UT <-> ET conversion.
- **Celestial mechanics** — mean longitude/anomaly of the Sun and Moon, Moon's node
  longitude, elongation, nutation in longitude and obliquity, Earth-Moon distance.
- **Coordinate systems** — Equatorial, Ecliptic, Galactic, Horizontal, Geographic and
  Geocentric coordinates, with adapters that convert between them.
- **Atmospheric refraction** — true <-> apparent elevation, with optional
  temperature/pressure corrections.
- **Interpolation** — 3- and 5-point interpolation, extremum finding and zero finding.

See `REFCARD.md` for a one-page reference covering the whole API.

## Requirements

- Java 17 or later
- Maven 3.8+
- Apache Commons Math 3 (pulled in automatically by Maven)

## Build

```
mvn clean verify
```

This compiles the sources, runs the JUnit test suite and produces
`target/meeus-engine-1.1.0.jar`.

## Quick start

```java
// Julian Day for 4.81 October 1957 (dates are encoded as yyyy.MMdd)
double jd = JulianDay.getJulianDayFromDateAsDouble(1957.100481d);

// Apparent sidereal time at Greenwich for 13 Nov 1978, 4h34m UT
MeeusEphemeris meeus = new MeeusEphemerisImpl();
double gast = meeus.getApparentSiderealTimeAsHoursFromJulianDay(
        JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));

// Equatorial -> ecliptic conversion for Pollux
EquatorialCoordinatesAdapter pollux =
        new EquatorialCoordinatesAdapter(new EquatorialCoordinates(115.564688d, 28.148642d));
double lambda = pollux.getEcliptiqueLongitude();
double beta   = pollux.getEcliptiqueLatitude();
```

> **Date encoding.** Throughout the API a calendar date is passed as a `double`
> encoded as `yyyy.MMdd` (e.g. `1978.1113` = 13 November 1978). Times of day are
> passed separately as a `Sexagesimal`.

## License

Released under the MIT License — see `LICENSE`.

## Changes

See `CHANGELOG.md` for the list of fixes and improvements in this release.
