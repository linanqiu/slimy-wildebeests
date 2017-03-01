package slimywildebeest;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import slimywildebeest.TimeSeries.FrequencyType;

public class MyTimeSeriesTest {

	@Test
	public void testInterpolate() {
		// Arrange
		Map<LocalDateTime, Double> values = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();
		values.put(now, 0.0);
		values.put(now.plusDays(10), 1.0);

		TimeSeries ts = MyTimeSeries.fromDictionary(values);

		// Act
		LocalDateTime nowPlus7 = now.plusDays(7);
		double interpolated = ts.getInterpolated(nowPlus7);

		// Assert
		assertEquals(0.7, interpolated, 0.0001);
	}

	@Test
	public void testResampleDays() {
		// Arrange
		Map<LocalDateTime, Double> values = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();
		values.put(now, 0.0);
		values.put(now.plusDays(10), 1.0);
		values.put(now.plusDays(30), 3.0);

		TimeSeries ts = MyTimeSeries.fromDictionary(values);

		// Act
		TimeSeries tsResampled = ts.getResampledTimeSeries(now, FrequencyType.DAY, 1, 30);

		// Assert
		for (int i = 0; i < 31; i++) {
			double resampledValue = tsResampled.get(now.plusDays(i));
			double expectedValue = i * 0.1;
			assertEquals(expectedValue, resampledValue, 0.0001);
		}
	}

	@Test
	public void testResampleMonths() {
		// Arrange
		Map<LocalDateTime, Double> values = new HashMap<>();
		LocalDateTime now = LocalDateTime.now();
		values.put(now, 0.0);
		values.put(now.plusMonths(10), 1.0);
		values.put(now.plusMonths(30), 3.0);

		TimeSeries ts = MyTimeSeries.fromDictionary(values);

		// Act
		TimeSeries tsResampled = ts.getResampledTimeSeries(now, FrequencyType.MONTH, 1, 30);

		// Assert
		for (int i = 0; i < 31; i++) {
			double resampledValue = tsResampled.get(now.plusMonths(i));
			double expectedValue = i * 0.1;
			// oops I fucked myself if we sample by irregular intervals such as
			// months.
			// fuckkkkk
			assertEquals(expectedValue, resampledValue, 0.01);
		}
	}

}
