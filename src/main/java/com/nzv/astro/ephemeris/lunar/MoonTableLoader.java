package com.nzv.astro.ephemeris.lunar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.DoubleUnaryOperator;

import com.nzv.astro.ephemeris.lunar.TableDrivenMoonModel.AdditiveKind;
import com.nzv.astro.ephemeris.lunar.TableDrivenMoonModel.AdditiveTerm;
import com.nzv.astro.ephemeris.lunar.TableDrivenMoonModel.Series;

/**
 * Loads a {@link TableDrivenMoonModel} from a model folder on the classpath. The folder must
 * contain {@code model.properties} plus {@code fundamental.csv}, {@code additive.csv},
 * {@code longitude.csv}, {@code latitude.csv} and {@code parallax.csv}, in the formats described
 * by the project's external-tables integration guide.
 */
public final class MoonTableLoader {

	private MoonTableLoader() {
	}

	/**
	 * @param resourceBase absolute classpath folder of the model, e.g.
	 *            {@code /com/nzv/astro/ephemeris/lunar/affc-1900}.
	 * @return the parsed, ready-to-evaluate model.
	 */
	public static TableDrivenMoonModel load(String resourceBase) {
		Properties p = loadProperties(resourceBase + "/model.properties");
		String id = p.getProperty("id", "unknown").trim();
		double epochYear = Double.parseDouble(p.getProperty("epoch.year", "1900.0").trim());
		double[] eCoef = { d(p, "e.c0"), d(p, "e.c1"), d(p, "e.c2") };

		Map<String, double[]> fundamental = loadFundamental(resourceBase + "/fundamental.csv");
		List<AdditiveTerm> additive = loadAdditive(resourceBase + "/additive.csv");

		Series longitude = loadSeries(resourceBase + "/longitude.csv", p, "longitude");
		Series latitude = loadSeries(resourceBase + "/latitude.csv", p, "latitude");
		Series parallax = loadSeries(resourceBase + "/parallax.csv", p, "parallax");

		double omega1 = d(p, "latitude.omega1", 0.0);
		double omega2 = d(p, "latitude.omega2", 0.0);
		double omega2ArgConst = d(p, "latitude.omega2.argConst", 0.0);
		double omega2ArgT = d(p, "latitude.omega2.argT", 0.0);

		return new TableDrivenMoonModel(id, epochYear, eCoef, fundamental, additive, longitude,
				latitude, parallax, omega1, omega2, omega2ArgConst, omega2ArgT);
	}

	private static Map<String, double[]> loadFundamental(String resource) {
		Map<String, double[]> map = new HashMap<>();
		for (String[] f : rows(resource)) {
			map.put(f[0].trim(), new double[] { parse(f[1]), parse(f[2]), parse(f[3]), parse(f[4]) });
		}
		return map;
	}

	private static List<AdditiveTerm> loadAdditive(String resource) {
		List<AdditiveTerm> list = new ArrayList<>();
		for (String[] f : rows(resource)) {
			String[] targets = f[0].trim().split("\\|");
			double coefficient = parse(f[1]);
			AdditiveKind kind = AdditiveKind.valueOf(toEnum(f[5]));
			double argConst = parse(f[2]);
			double argT = parse(f[3]);
			double argT2 = parse(f[4]);
			list.add(new AdditiveTerm(targets, coefficient, argConst, argT, argT2, kind));
		}
		return list;
	}

	private static Series loadSeries(String resource, Properties p, String name) {
		List<PeriodicTerm> terms = new ArrayList<>();
		for (String[] f : rows(resource)) {
			terms.add(new PeriodicTerm(Integer.parseInt(f[0].trim()), parse(f[1]),
					Integer.parseInt(f[2].trim()), Integer.parseInt(f[3].trim()),
					Integer.parseInt(f[4].trim()), Integer.parseInt(f[5].trim())));
		}
		String baseSpec = p.getProperty(name + ".base", "0").trim();
		boolean addLprime = baseSpec.equalsIgnoreCase("Lprime");
		double baseConst = addLprime ? 0.0 : Double.parseDouble(baseSpec);
		DoubleUnaryOperator trig = "cos".equalsIgnoreCase(p.getProperty(name + ".trig", "sin").trim())
				? Math::cos
				: Math::sin;
		return new Series(baseConst, addLprime, trig, terms);
	}

	/** Reads a CSV, skipping the header, blank lines and comment lines. */
	private static List<String[]> rows(String resource) {
		List<String[]> out = new ArrayList<>();
		try (BufferedReader r = reader(resource)) {
			String line;
			boolean header = true;
			while ((line = r.readLine()) != null) {
				String trimmed = line.trim();
				if (trimmed.isEmpty() || trimmed.startsWith("#")) {
					continue;
				}
				if (header) {
					header = false;
					continue;
				}
				out.add(line.split(",", -1));
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Failed reading " + resource, e);
		}
		return out;
	}

	private static Properties loadProperties(String resource) {
		Properties p = new Properties();
		try (InputStream in = open(resource)) {
			p.load(in);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed reading " + resource, e);
		}
		return p;
	}

	private static BufferedReader reader(String resource) {
		return new BufferedReader(new InputStreamReader(open(resource), StandardCharsets.UTF_8));
	}

	private static InputStream open(String resource) {
		InputStream in = MoonTableLoader.class.getResourceAsStream(resource);
		if (in == null) {
			throw new IllegalStateException("Resource not found on classpath: " + resource);
		}
		return in;
	}

	private static double parse(String s) {
		String t = s.trim();
		return t.isEmpty() ? 0.0 : Double.parseDouble(t);
	}

	private static double d(Properties p, String key) {
		return Double.parseDouble(p.getProperty(key).trim());
	}

	private static double d(Properties p, String key, double dflt) {
		String v = p.getProperty(key);
		return v == null ? dflt : Double.parseDouble(v.trim());
	}

	private static String toEnum(String kind) {
		return switch (kind.trim()) {
			case "linear" -> "LINEAR";
			case "sinOmega" -> "SIN_OMEGA";
			case "sinOmegaPlus" -> "SIN_OMEGA_PLUS";
			default -> throw new IllegalStateException("Unknown additive kind: " + kind);
		};
	}
}
