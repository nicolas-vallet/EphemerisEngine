package com.nzv.astro.ephemeris;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nzv.astro.ephemeris.Sexagesimal.SexagesimalType;
import com.nzv.astro.ephemeris.coordinate.GeocentricCoordinates;
import com.nzv.astro.ephemeris.coordinate.GeographicCoordinates;
import com.nzv.astro.ephemeris.coordinate.adapter.EclipticCoordinatesAdapter;
import com.nzv.astro.ephemeris.coordinate.adapter.EquatorialCoordinatesAdapter;
import com.nzv.astro.ephemeris.coordinate.adapter.GalacticCoordinatesAdapter;
import com.nzv.astro.ephemeris.coordinate.adapter.HorizontalCoordinatesAdapter;
import com.nzv.astro.ephemeris.coordinate.impl.EclipticCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.EquatorialCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.GalacticCoordinates;
import com.nzv.astro.ephemeris.coordinate.impl.HorizontalCoordinates;
import com.nzv.astro.ephemeris.impl.AtmosphericRefractionCalculatorImpl;
import com.nzv.astro.ephemeris.impl.EphemerisEngineImpl;
import com.nzv.astro.ephemeris.impl.MeeusEphemerisImpl;
import com.nzv.astro.ephemeris.interpolation.InterpolationData;
import com.nzv.astro.ephemeris.interpolation.InterpolationEngine;
import com.nzv.astro.ephemeris.interpolation.InterpolationException;
import com.nzv.astro.ephemeris.interpolation.impl.InterpolationEngineImpl;

public class JeanMeeusTest {

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyy GG, HH:mm:ss");

		MeeusEphemeris meeusEngine = new MeeusEphemerisImpl();
		EphemerisEngine engine = new EphemerisEngineImpl();

		long tEnd;
		long tStart = System.currentTimeMillis();

//		System.out.println("Jour julien pour le 4.81 octobre 1957 = "
//				+ JulianDay.getJulianDayFromDateAsDouble(1957.100481d));
//		System.out.println("Jour julien pour le 27.5 janvier 333 = "
//				+ JulianDay.getJulianDayFromDateAsDouble(333.01275d));
//		System.out.println("Jour julien pour le 28.63 mai -584 = "
//				+ JulianDay.getJulianDayFromDateAsDouble(-584.052863d));

//		System.out.println("Date pour le jour julien 2436116.31 = "
//				+ sdf.format(JulianDay.getDateFromJulianDay(2436116.31d)));
//		System.out.println("Date pour le jour julien 1842713.0 = "
//				+ sdf.format(JulianDay.getDateFromJulianDay(1842713.0d)));
//		System.out.println("Date pour le jour julien 1507900.13 = "
//				+ sdf.format(JulianDay.getDateFromJulianDay(1507900.13d)));

//		System.out
//				.println("Intervalle de temps entre les passages de la comète de Halley du 16 novembre 1835 et du 20 avril 1910 = "
//						+ (JulianDay.getJulianDayFromDateAsDouble(1910.0420d) - JulianDay
//								.getJulianDayFromDateAsDouble(1835.1116d)) + " jours");

//		System.out.println("Date se trouvant 10 000 jours après le 30 juin 1954 = "
//				+ sdf.format(JulianDay.getDateFromJulianDay(JulianDay
//						.getJulianDayFromDateAsDouble(1954.0630d) + 10000)));

//		System.out.println("Jour de la semaine correspondant au 30 juin 1954 = "
//				+ JulianDay.getDayOfWeekFromDayAsDouble(1954.0630d));

//		System.out.println("Jour de l'année correspondant au 14 novembre 1978 = "
//				+ JulianDay.getDayOfYearFromDateAsDouble(1978.1114d));
//		System.out.println("Jour de l'année correspondant au 22 avril 1980 = "
//				+ JulianDay.getDayOfYearFromDateAsDouble(1980.0422d));

//		sdf = new SimpleDateFormat("EEEE dd MMMM");
//		System.out.println("Jour de Pâques pour l'année 1978 = "
//				+ sdf.format(meeusEngine.getEasterDateForYear(1978)));
//		System.out.println("Jour de Pâques pour l'année 1979 = "
//				+ sdf.format(meeusEngine.getEasterDateForYear(1979)));
//		System.out.println("Jour de Pâques pour l'année 1980 = "
//				+ sdf.format(meeusEngine.getEasterDateForYear(1980)));
//		System.out.println("Jour de Pâques pour l'année 1954 = "
//				+ sdf.format(meeusEngine.getEasterDateForYear(1954)));
//		System.out.println("Jour de Pâques pour l'année 2000 = "
//				+ sdf.format(meeusEngine.getEasterDateForYear(2000)));
//		System.out.println("Jour de Pâques pour l'année 1000...");
//		try {
//			meeusEngine.getEasterDateForYear(1000);
//		} catch (InvalidParameterException ex) {
//			System.out.println(ex.getMessage());
//		}

		Sexagesimal phi1 = new Sexagesimal(50, 47, 55);
		Sexagesimal phi2 = Sexagesimal
				.decimalToSexagesimal((double) (50 * 3600 + 47 * 60 + 55) / 3600);
		System.out.println(phi1.toString(SexagesimalType.DEGREES));
		System.out.println(phi2.toString(SexagesimalType.DEGREES));
		GeocentricCoordinates gc = new GeocentricCoordinates(50, 47, 55, 105);
		System.out.println("Coordonnées geocentriques pour la position +50° 47' 55\" : abscisse="
				+ gc.getAbscissa() + "; ordonnées=" + gc.getOrdinate());
		GeocentricCoordinates gc2 = new GeocentricCoordinates((50 + ((double)47 / 60) + ((double)55 / 3600)), 105);
		System.out.println(gc.getAbscissa() + " / " + gc.getOrdinate());
		System.out.println(gc2.getAbscissa() + " / " + gc2.getOrdinate());
		if (gc.getAbscissa() != gc2.getAbscissa() || gc.getOrdinate() != gc2.getOrdinate()) {
			throw new RuntimeException("Erreur!");
		}

		System.out.println("Temps sideral moyen a Greenwich pour le 13 novembre 1978 à 0h");
		double jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		Sexagesimal s = Sexagesimal.decimalToSexagesimal(meeusEngine
				.getMeanSiderealTimeAsHoursFromJulianDay(jd));
		System.out.println(s.getUnit() + "h " + s.getMinute() + "m " + s.getSecond() + "s");

		System.out.println("Temps sideral moyen a Greenwich pour le 13 novembre 1978 à 4h 34m 0s");
		jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113);
		s = Sexagesimal.decimalToSexagesimal(meeusEngine.getMeanSiderealTimeAsHoursFromJulianDay(
				jd, new Sexagesimal(4, 34, 0)));
		System.out.println(s.getUnit() + "h " + s.getMinute() + "m " + s.getSecond() + "s");

		jd = JulianDay.getJulianDayFromDateAsDouble(1978.1113, new Sexagesimal(4, 34, 0));
		System.out.println("Pour le 13 novembre 1978 à 4h 34m TU...");
		System.out.println("Nutation en longitude = " + engine.getNutationInLongitude(jd));
		System.out.println("Nutation en inclinaison = " + engine.getNutationInObliquity(jd));

		Sexagesimal apparentSiderealTime = Sexagesimal.decimalToSexagesimal(meeusEngine
				.getApparentSiderealTimeAsHoursFromJulianDay(
						JulianDay.getJulianDayFromDateAsDouble(1978.1113),
						new Sexagesimal(4, 34, 0)));
		System.out.println("Temps sidéral apparent � Greenwich le 13 novembre 1978 à 4h 34m TU = "
				+ apparentSiderealTime.toString(SexagesimalType.HOURS));

		System.out.println("Temps des ephemerides pour le 6 fevrier -555 à 6h TU = "
				+ meeusEngine.universalTimeToEphemerisTime(-555.0206, new Sexagesimal(6, 0, 0))
						.toString(SexagesimalType.HOURS));

		System.out.println("Temps universel pour le 4 avril 1977 à 4h 19m = "
				+ meeusEngine.ephemerisTimeToUniversalTime(1977.0404, new Sexagesimal(4, 19, 0))
						.toString(SexagesimalType.HOURS));

		System.out.println("dT en seconde pour -2000 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(-2000.0101)));
		System.out.println("dT en seconde pour 200 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(200.0101)));
		System.out.println("dT en seconde pour 600 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(600.0101)));
		System.out.println("dT en seconde pour 1673 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1673.0101)));
		System.out.println("dT en seconde pour 1750 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1750.0101)));
		System.out.println("dT en seconde pour 1850 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1850.0101)));
		System.out.println("dT en seconde pour 1870 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1870.0101)));
		System.out.println("dT en seconde pour 1910 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1910.0101)));
		System.out.println("dT en seconde pour 1930 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1930.0101)));
		System.out.println("dT en seconde pour 1950 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1950.0101)));
		System.out.println("dT en seconde pour 1990 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(1990.0101)));
		System.out.println("dT en seconde pour 2014 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(2014.0101)));
		System.out.println("dT en seconde pour 2100 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(2100.0101)));
		System.out.println("dT en seconde pour 2200 = "
				+ meeusEngine.computeDeltaBetweenEphemerisTimeAndUniveralTimeInSecond(JulianDay
						.getJulianDayFromDateAsDouble(2200.0101)));

		System.out.println("Inclinaison de l'ecliptique pour l'année 1950 = "
				+ Constants.ECLIPTIC_OBLIQUITY_1950.toString(SexagesimalType.DEGREES));
		System.out.println("Inclinaison de l'ecliptique pour l'année 2000 = "
				+ Constants.ECLIPTIC_OBLIQUITY_2000.toString(SexagesimalType.DEGREES));
		System.out.println("Aplatissement de la Terre = " + Constants.EARTH_FLATTENING);

		System.out
				.println("Coordonnées ecliptiques de Pollux dont les coordonnées équatoriaux sont RA=7h 42m 15.525s / DEC=+28° 08' 55.11\" : ");
		EquatorialCoordinatesAdapter equatorialCoordinateAdapter = new EquatorialCoordinatesAdapter(
				new EquatorialCoordinates(115.564688d, 28.148642d));
		System.out.println("LAMBDA=" + equatorialCoordinateAdapter.getEcliptiqueLongitude());
		System.out.println("BETA=" + equatorialCoordinateAdapter.getEcliptiqueLatitude());

		System.out
				.println("Coordonnées équatoriaux de Pollux dont les coordonnées ecliptiques sont LAMBDA=112.52538° / BETA=6.68058°");
		EclipticCoordinatesAdapter eclipticCoordinateAdapter = new EclipticCoordinatesAdapter(
				new EclipticCoordinates(112.52537339688136d, 6.680581550926902d));
		Sexagesimal RA = new Sexagesimal(eclipticCoordinateAdapter.getRightAscension() / 15);
		Sexagesimal DEC = new Sexagesimal(eclipticCoordinateAdapter.getDeclinaison());
		System.out.println("RA=" + RA.toString(SexagesimalType.HOURS));
		System.out.println("DEC=" + DEC.toString(SexagesimalType.DEGREES));

		Sexagesimal rightAscension = new Sexagesimal(7, 42, 15.525);
		System.out.println("Valeur en degrées de RA(7h 42m 15.525s) = "
				+ rightAscension.getValueAsUnits() * 15);

		System.out
				.println("Coordonnées galactiques de Nova Serpentis 1978 (RA=17h 48m 59.74s / DEC=-14° 43' 08.2\")");
		Sexagesimal novaSerpentisRA = new Sexagesimal(17, 48, 59.74);
		Sexagesimal novaSerpentisDEC = new Sexagesimal(-14, 43, 8.2);
		equatorialCoordinateAdapter.setEquatorialCoordinates(new EquatorialCoordinates(
				novaSerpentisRA.getValueAsUnits() * 15, novaSerpentisDEC.getValueAsUnits()));
		System.out.println("l=" + equatorialCoordinateAdapter.getGalacticLongitude());
		System.out.println("b=" + equatorialCoordinateAdapter.getGalacticLatitude());

		System.out
				.println("Coordonnées équatoriaux de Nova Serpentis 1978 (l=12.9593° / b=6.0463°)");
		GalacticCoordinatesAdapter galacticCoordinatesAdapter = new GalacticCoordinatesAdapter(
				new GalacticCoordinates(12.9593d, 6.0463d));
		Sexagesimal ra = new Sexagesimal(galacticCoordinatesAdapter.getRightAscension() / 15);
		Sexagesimal dec = new Sexagesimal(galacticCoordinatesAdapter.getDeclinaison());
		System.out.println("RA=" + ra.toString(SexagesimalType.HOURS));
		System.out.println("DEC=" + dec.toString(SexagesimalType.DEGREES));

		System.out
				.println("Coordonnées Alt/Az de Saturne le 13 novembre 1978 à 4h 34m 00s UT (RA=10h 57' 35.681\" / DEC=8° 25' 58.10\") "
						+ "à l'Observatoire d'Uccle (longitude -0h 17m 25.94s / latitude +50° 47' 55\")");
		equatorialCoordinateAdapter.setEquatorialCoordinates(new EquatorialCoordinates(Sexagesimal
				.sexagesimalToDecimal(new Sexagesimal(10, 57, 35.681)), Sexagesimal
				.sexagesimalToDecimal(new Sexagesimal(8, 25, 58.10))));
		GeographicCoordinates observerSite = new GeographicCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(50, 47, 55)),
				-(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 17, 25.94)) * 15));
		double siderealTime = meeusEngine.getApparentSiderealTimeAsHoursFromJulianDay(
				JulianDay.getJulianDayFromDateAsDouble(1978.1113), new Sexagesimal(4, 34, 0));
		Sexagesimal azimut = new Sexagesimal(equatorialCoordinateAdapter.getAzimuth(observerSite,
				siderealTime));
		Sexagesimal elevation = new Sexagesimal(equatorialCoordinateAdapter.getElevation(
				observerSite, siderealTime));
		System.out.println("Azimuth   A= " + azimut.toString(SexagesimalType.DEGREES));
		System.out.println("Elevation h= " + elevation.toString(SexagesimalType.DEGREES));

		HorizontalCoordinates hc = new HorizontalCoordinates(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(128, 18, 3.0128d)),
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(36, 32, 25.7272d)));
		System.out.println("Coordonnées équatoriales du point situé à "
				+ "Azimut=128° 18' 3.013\" / Altitude=36° 32' 25.7272\", "
				+ "le 13/11/1978 à 4h 34m 00s TU à l'Observatoire d'Uccle : ");
		HorizontalCoordinatesAdapter horizontalCoordinatesAdapter = new HorizontalCoordinatesAdapter(
				hc);
		System.out.println("RA = "
				+ Sexagesimal
						.decimalToSexagesimal(
								horizontalCoordinatesAdapter.getRightAscension(observerSite,
										siderealTime) / 15).toString(SexagesimalType.HOURS));
		System.out.println("DEC = "
				+ Sexagesimal.decimalToSexagesimal(
						horizontalCoordinatesAdapter.getDeclinaison(observerSite, siderealTime))
						.toString(SexagesimalType.DEGREES));

		// Refraction atmospherique...
		AtmosphericRefractionCalculator arc = new AtmosphericRefractionCalculatorImpl();
		Sexagesimal trueElevation = Sexagesimal.decimalToSexagesimal(arc
				.getTrueElevation(Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32, 04, 17))));
		System.out
				.println("Hauteur vraie d'une étoile dont la hauteur est mesurée à 32° 04' 17\" au dessus de l'horizon : "
						+ trueElevation.toString(SexagesimalType.DEGREES));
		trueElevation = Sexagesimal.decimalToSexagesimal(arc.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(32, 04, 17)), 10, 1013));
		System.out.println("Influence de T(10°C) et P(1013 hPa) :  : "
				+ trueElevation.toString(SexagesimalType.DEGREES));

		System.out
				.println("Hauteur apparente d'une étoile dont la hauteur vraie est de 32° 2' 44.24\" : ");
		Sexagesimal apparentElevation = Sexagesimal.decimalToSexagesimal(arc
				.getApparentElevation(Sexagesimal
						.sexagesimalToDecimal(new Sexagesimal(32, 2, 44.24))));
		System.out.println(apparentElevation.toString(SexagesimalType.DEGREES));

		System.out.println("Hauteur apparente d'une étoile dont la hauteur vraie est de 1° 57' : ");
		apparentElevation = Sexagesimal.decimalToSexagesimal(arc.getApparentElevation(Sexagesimal
				.sexagesimalToDecimal(new Sexagesimal(1, 57, 0))));
		System.out.println(apparentElevation.toString(SexagesimalType.DEGREES));

		trueElevation = Sexagesimal.decimalToSexagesimal(arc.getTrueElevation(Sexagesimal
				.sexagesimalToDecimal(new Sexagesimal(2, 14, 19.34))));
		System.out
				.println("Hauteur vrai d'une étoile dont la hauteur apparente est de 2° 14' 19.34\" : "
						+ trueElevation.toString(SexagesimalType.DEGREES));

		trueElevation = Sexagesimal.decimalToSexagesimal(arc.getTrueElevation(
				Sexagesimal.sexagesimalToDecimal(new Sexagesimal(2, 14, 19.34)), 38, 1023));
		System.out.println("Influence de T(38° C) et P(1023 hPa) : "
				+ trueElevation.toString(SexagesimalType.DEGREES));

		System.out.println("Hauteur apparente d'une étoile dont la hauteur vrai est de 90° : ");
		apparentElevation = Sexagesimal.decimalToSexagesimal(arc.getApparentElevation(Sexagesimal
				.sexagesimalToDecimal(new Sexagesimal(90, 0, 0))));
		System.out.println(apparentElevation.toString(SexagesimalType.DEGREES));

		System.out.println("Hauteur vrai d'une étoile dont la hauteur apparente est de 90° : ");
		trueElevation = Sexagesimal.decimalToSexagesimal(arc.getTrueElevation(Sexagesimal
				.sexagesimalToDecimal(new Sexagesimal(90, 0, 0))));
		System.out.println(trueElevation.toString(SexagesimalType.DEGREES));

		InterpolationEngine iei = new InterpolationEngineImpl();
		long sNano, eNano;

		{
			InterpolationData input1 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1969.0805), 0.664531);
			InterpolationData input2 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1969.0806), 0.669651);
			InterpolationData input3 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1969.0807), 0.674800);
			try {
				sNano = System.nanoTime();
				Double distanceMarsTerre = iei.interpolate(input1, input2, input3, JulianDay
						.getJulianDayFromDateAsDouble(1969.0807, new Sexagesimal(4, 21, 0)));
				eNano = System.nanoTime();
				System.out
						.println("METHODE 1 => Distance Terre-Mars en le 06 aout 1969 à 22h14 TE = "
								+ distanceMarsTerre + " UA");
				System.out.println("Computation took " + (eNano - sNano) + " nanoseconds");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}

			List<InterpolationData> samples = Arrays.asList(input1, input2, input3);
			try {
				sNano = System.nanoTime();
				Double distanceMarsTerre = iei.interpolate(samples, JulianDay
						.getJulianDayFromDateAsDouble(1969.0807, new Sexagesimal(4, 21, 0)));
				eNano = System.nanoTime();
				System.out
						.println("METHODE 2 => Distance Terre-Mars en le 06 aout 1969 à 22h14 TE = "
								+ distanceMarsTerre + " UA");
				System.out.println("Computation took " + (eNano - sNano) + " nanoseconds");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}
		}

		{
			InterpolationData input1 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1966.0111), 1.381701);
			InterpolationData input2 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1966.0115), 1.381502);
			InterpolationData input3 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1966.0119), 1.381535);
			try {
				sNano = System.nanoTime();
				InterpolationData marsPerihelie = iei.findExtremum(input1, input2, input3);
				eNano = System.nanoTime();
				System.out.println("Mars atteint son perihelie le "
						+ sdf.format(JulianDay.getDateFromJulianDay(marsPerihelie.getX()))
						+ " avec une distance Mars-Soleil de " + marsPerihelie.getY() + " UA.");
				System.out.println("Computation took " + (eNano - sNano) + " nanoseconds");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}
		}

		{
			InterpolationData input1 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1973.0226),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 28, 13.4)) * -3600);
			InterpolationData input2 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1973.0227),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 6, 46.3)) * 3600);
			InterpolationData input3 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1973.0228),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 38, 23.2)) * 3600);
			try {
				sNano = System.nanoTime();
				double tmp = iei.findZero(input1, input2, input3);
				eNano = System.nanoTime();
				Date zeroDeclinaisonOfMercure = JulianDay.getDateFromJulianDay(tmp);
				System.out.println("La declinaison de Mercure est égale à zero le "
						+ sdf.format(zeroDeclinaisonOfMercure));
				System.out.println("Computation took " + (eNano - sNano) + " nanoseconds");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}
		}

		{
			InterpolationData input1 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.1209), Sexagesimal
							.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 45.5099)).doubleValue());
			InterpolationData input2 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.12095), Sexagesimal
							.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 34.4060)).doubleValue());
			InterpolationData input3 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.1210), Sexagesimal
							.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 25.6303)).doubleValue());
			InterpolationData input4 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.12105), Sexagesimal
							.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 19.3253)).doubleValue());
			InterpolationData input5 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.1211), Sexagesimal
							.sexagesimalToBigDecimal(new Sexagesimal(0, 54, 15.5940)).doubleValue());
			double searchValueFor = JulianDay.getJulianDayFromDateAsDouble(1979.1210,
					new Sexagesimal(3, 20, 0));
			try {
				sNano = System.nanoTime();
				double moonParallaxe = iei.interpolate(input1, input2, input3, input4, input5,
						searchValueFor);
				eNano = System.nanoTime();
				System.out
						.println("METHODE 1 => Parallaxe de la Lune le 10 décembre 1979 à 3h20 TE = "
								+ Sexagesimal.decimalToSexagesimal(moonParallaxe).toString(
										SexagesimalType.DEGREES));
				System.out.println("Computation took " + (eNano - sNano) + " nanoseconds");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}

			try {
				List<InterpolationData> samples = Arrays.asList(input1, input2, input3, input4,
						input5);
				sNano = System.nanoTime();
				double moonParallaxe = iei.interpolate(samples, searchValueFor);
				eNano = System.nanoTime();
				System.out
						.println("METHODE 2 => Parallaxe de la Lune le 10 décembre 1979 à 3h20 TE = "
								+ Sexagesimal.decimalToSexagesimal(moonParallaxe).toString(
										SexagesimalType.DEGREES));
				System.out.println("Computation took " + (eNano - sNano) + " nanoseconds");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}
		}

		{
			InterpolationData input1 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(2014.0701),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(20, 21, 0)));
			InterpolationData input2 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(2014.0715),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(22, 30, 0)));
			InterpolationData input3 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(2014.0731),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(22, 29, 0)));
			InterpolationData input4 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(2014.0815),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(19, 58, 0)));
			InterpolationData input5 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(2014.0831),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(14, 56, 0)));
			try {
				sNano = System.nanoTime();
				InterpolationData maxDeclinaisonVenus = iei.findExtremum(input1, input2, input3,
						input4, input5);
				eNano = System.nanoTime();
				System.out.println("Venus atteint sa déclinaison maximale le "
						+ sdf.format(JulianDay.getDateFromJulianDay(maxDeclinaisonVenus.getX()))
						+ " � une valeur de "
						+ Sexagesimal.decimalToSexagesimal(maxDeclinaisonVenus.getY()).toString(
								SexagesimalType.DEGREES));
				System.out.println("Computation tooks " + (eNano - sNano) + " nanoseconds.");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}
		}

		{
			InterpolationData input1 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.0525),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(-1, 16, 0.5)));
			InterpolationData input2 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.0526),
					-Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 33, 1.3)));
			InterpolationData input3 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.0527),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 11, 12.0)));
			InterpolationData input4 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.0528),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(0, 56, 3.3)));
			InterpolationData input5 = new InterpolationData(
					JulianDay.getJulianDayFromDateAsDouble(1979.0529),
					Sexagesimal.sexagesimalToDecimal(new Sexagesimal(1, 40, 52.2)));
			try {
				sNano = System.nanoTime();
				double nullLatitudeOfMercure = iei.findZero(input1, input2, input3, input4, input5);
				eNano = System.nanoTime();
				System.out
						.println("METHODE 1 => Mercure atteint le noeud ascendant de son orbite le "
								+ sdf.format(JulianDay.getDateFromJulianDay(nullLatitudeOfMercure)));
				System.out.println("Computation tooks " + (eNano - sNano) + " nanoseconds.");
			} catch (InterpolationException ex) {
				System.out.println(ex.getMessage());
			}

			// List<InterpolationData> samples = Arrays.asList(input1, input2, input3, input4,
			// input5);
			// try {
			// s = System.nanoTime();
			// double nullLatitudeOfMercure = iei.findZero(samples);
			// e = System.nanoTime();
			// System.out
			// .println("METHODE 2 => Mercure atteint le noeud ascendant de son orbite le "
			// + sdf.format(JulianDay.getDateFromJulianDay(nullLatitudeOfMercure)));
			// System.out.println("Computation tooks " + (e - s) + " nanoseconds.");
			// } catch (InterpolationException ex) {
			// System.out.println(ex.getMessage());
			// }
		}
		
		System.out.println("Jour de la semaine pour le 23 decembre 2015: "+JulianDay.getDayOfWeekFromDayAsDouble(2015.1223));
		System.out.println("Jour de la semaine pour le 14 fevrier 2015: "+JulianDay.getDayOfWeekFromDayAsDouble(2015.0214));
		System.out.println("Jour de la semaine pour le 2 octobre 2015: "+JulianDay.getDayOfWeekFromDayAsDouble(2015.1002));
		System.out.println("Jour de la semaine pour le 20 avril 2015: "+JulianDay.getDayOfWeekFromDayAsDouble(2015.0420));

		tEnd = System.currentTimeMillis();
		System.out.println("Computation took " + (tEnd - tStart) + "ms");
	}
}
