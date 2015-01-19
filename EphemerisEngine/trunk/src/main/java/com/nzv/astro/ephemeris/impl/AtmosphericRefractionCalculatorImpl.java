package com.nzv.astro.ephemeris.impl;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

import com.nzv.astro.ephemeris.AtmosphericRefractionCalculator;

public class AtmosphericRefractionCalculatorImpl implements AtmosphericRefractionCalculator {

	// In the following methods :
	// - R is computing refraction
	// - Ha is the apparent elevation of object.
	// - Ht is the true elevation of object.
	// R is expressed in arcseconds.
	// R depends of the considered elevation, the ambient temperature and the atmospheric pressure.
	// When it is not provided, we used the following default values :
	// - default temperature : 10°C
	// - default atmospheric pressure : 1 013 millibars.
	// We have the following formulas :
	// Ha = Ht + R
	// Ht = Ha - R

	// Under 15° of elevation, we will use extra parameters.
	// They are store in the following set.
	private static final Map<String, Double> Ha_LOW_PARAMETERS;
	private static final Map<String, Double> Ha_HIGH_PARAMETERS;
	private static final Map<String, Double> Ht_LOW_PARAMETERS;
	private static final Map<String, Double> Ht_HIGH_PARAMETERS;
	static {
		Map<String, Double> aMap = new HashMap<String, Double>();
		aMap.put("phi", Double.valueOf(+1.4));
		aMap.put("a", Double.valueOf(6.6176));
		aMap.put("b", Double.valueOf(-1.9037));
		Ha_LOW_PARAMETERS = Collections.unmodifiableMap(aMap);

		aMap = new HashMap<String, Double>();
		aMap.put("phi", Double.valueOf(-1.4));
		aMap.put("a", Double.valueOf(+4.3481));
		aMap.put("b", Double.valueOf(-0.9466));
		Ha_HIGH_PARAMETERS = Collections.unmodifiableMap(aMap);

		aMap = new HashMap<String, Double>();
		aMap.put("phi", Double.valueOf(+2.6));
		aMap.put("a", Double.valueOf(+7.5262));
		aMap.put("b", Double.valueOf(-2.2204));
		Ht_LOW_PARAMETERS = Collections.unmodifiableMap(aMap);

		aMap = new HashMap<String, Double>();
		aMap.put("phi", Double.valueOf(-1.1));
		aMap.put("a", Double.valueOf(+4.4010));
		aMap.put("b", Double.valueOf(-0.9603));
		Ht_HIGH_PARAMETERS = Collections.unmodifiableMap(aMap);
	}
	
	// TODO
	private double temperatureAndPressureCorrectionsOnRefraction(double R, double T, double P) {
		// R input parameter is computed for a temperature of 10° C and a pressure of 1013 millibars.
		// R increases when pressure increases. 1% for each 10 millibars
		double pressureInfluence = 
				((P - 1013) / 10) / 100;
		
		// R increases when temperature decreases. 1% for each 2.8°C
		double temperatureInfluence = 
				((T - 10) / 2.8) / 100;

		double result =  R + (R * pressureInfluence) - (R * temperatureInfluence);
		return result;
	}
	
	private double getRefractionValueFromApparentElevation(double apparentElevation) {
		double R; // The refraction expressed in arcseconds.
		if (apparentElevation >= 15) {
			double term1 = tan(toRadians(90 - apparentElevation));
			R = 58.294d * term1 - 0.0668 * pow(term1, 3);
		} else {
			double term1, phi, a, b;
			if (apparentElevation >= 4) {
				phi = Ha_HIGH_PARAMETERS.get("phi").doubleValue();
				a = Ha_HIGH_PARAMETERS.get("a").doubleValue();
				b = Ha_HIGH_PARAMETERS.get("b").doubleValue();
			} else if (apparentElevation >= 0) {
				phi = Ha_LOW_PARAMETERS.get("phi").doubleValue();
				a = Ha_LOW_PARAMETERS.get("a").doubleValue();
				b = Ha_LOW_PARAMETERS.get("b").doubleValue();
			} else {
				throw new InvalidParameterException(
						"The given elevation should be positive!");
			}
			term1 = a + b * log(apparentElevation + phi);
			R = pow(term1, 2) * 60;
		}
		return R;
	}
	
	private double getRefractionValueFromTrueElevation(double trueElevation) {
		double R;  // The refraction expressed in arcseconds.
		if (trueElevation >= 15) {
			double term1 = tan(toRadians(90 - trueElevation));
			R = 58.276d * term1 - 0.0824d * pow(term1, 3);
		} else {
			double term1, phi, a, b;
			if (trueElevation >= 4) {
				phi = Ht_HIGH_PARAMETERS.get("phi").doubleValue();
				a = Ht_HIGH_PARAMETERS.get("a").doubleValue();
				b = Ht_HIGH_PARAMETERS.get("b").doubleValue();
			} else if (trueElevation >= 0) {
				phi = Ht_LOW_PARAMETERS.get("phi").doubleValue();
				a = Ht_LOW_PARAMETERS.get("a").doubleValue();
				b = Ht_LOW_PARAMETERS.get("b").doubleValue();
			} else {
				throw new InvalidParameterException(
						"The given elevation should be positive!");
			}
			term1 = a + b * log(trueElevation + phi);
			R = pow(term1, 2) * 60;
		}
		return R;
	}

	@Override
	public double getTrueElevation(double apparentElevation) {
		double R = getRefractionValueFromApparentElevation(apparentElevation);
		return apparentElevation - (R / 3600);
	}

	@Override
	public double getApparentElevation(double trueElevation) {
		double R = getRefractionValueFromTrueElevation(trueElevation);
		return trueElevation + (R / 3600);
	}

	@Override
	public double getTrueElevation(double apparentElevation, double temperature, double pressure) {
		double R = getRefractionValueFromApparentElevation(apparentElevation);
		R = temperatureAndPressureCorrectionsOnRefraction(R, temperature, pressure);
		return apparentElevation - (R / 3600);
	}

	@Override
	public double getApparentElevation(double trueElevation, double temperature, double pressure) {
		double R = getRefractionValueFromTrueElevation(trueElevation);
		R = temperatureAndPressureCorrectionsOnRefraction(R, temperature, pressure);
		return trueElevation + (R / 3600);
	}

}
