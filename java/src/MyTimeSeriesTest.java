import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

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

}
