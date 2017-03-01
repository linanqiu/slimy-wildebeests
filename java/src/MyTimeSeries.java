import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MyTimeSeries implements TimeSeries {

	private TreeMap<LocalDateTime, Double> values;

	public MyTimeSeries(Map<LocalDateTime, Double> values) {
		this.values = new TreeMap<>(values);
	}

	public static TimeSeries fromDictionary(Map<LocalDateTime, Double> values) {
		// candidate fills in this part
		MyTimeSeries series = new MyTimeSeries(values);
		return series;
	}

	@Override
	public double get(LocalDateTime dateTime) {
		return values.containsKey(dateTime) ? values.get(dateTime) : Double.NaN;
	}

	@Override
	public void set(LocalDateTime dateTime, double value) {
		values.put(dateTime, value);
	}

	@Override
	public double getInterpolated(LocalDateTime dateTime) {
		if (values.containsKey(dateTime)) {
			return values.get(dateTime);
		} else {
			LocalDateTime minKey = values.firstKey();
			LocalDateTime maxKey = values.lastKey();
			if (dateTime.isBefore(minKey) || dateTime.isAfter(maxKey)) {
				return Double.NaN;
			}

			Entry<LocalDateTime, Double> ceilingEntry = values.ceilingEntry(dateTime);
			Entry<LocalDateTime, Double> floorEntry = values.floorEntry(dateTime);

			double dy = ceilingEntry.getValue() - floorEntry.getValue();
			long dx = ChronoUnit.MILLIS.between(ceilingEntry.getKey(), floorEntry.getKey());
			double slope = dy / dx;

			long deltaX = ChronoUnit.MILLIS.between(dateTime, floorEntry.getKey());
			double deltaY = slope * deltaX;

			double interpolatedValue = floorEntry.getValue() + deltaY;

			return interpolatedValue;
		}
	}
}
