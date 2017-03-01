package slimywildebeest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyTimeSeries implements TimeSeries {

	private TreeMap<LocalDateTime, Double> values;

	public MyTimeSeries() {
		values = new TreeMap<>();
	}

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

			return calculateInterpolation(floorEntry, ceilingEntry, dateTime);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TimeSeries#getResampledTimeSeries(java.time.LocalDateTime,
	 * TimeSeries.FrequencyType, int, int)
	 */
	@Override
	public TimeSeries getResampledTimeSeries(LocalDateTime startTime, FrequencyType frequencyType, int window,
			int intervals) {
		// check size
		if (values.size() < 2) {
			return new MyTimeSeries();
		}
		// check bounded
		if (startTime.isBefore(values.firstKey()) || startTime.isAfter(values.lastKey())) {
			return new MyTimeSeries();
		}

		// create map from one before startTime to end
		SortedMap<LocalDateTime, Double> tailMap = new TreeMap<>(values.tailMap(startTime, false));
		Entry<LocalDateTime, Double> entryBeforeStartTime = values.floorEntry(startTime);
		tailMap.put(entryBeforeStartTime.getKey(), entryBeforeStartTime.getValue());

		Iterator<Entry<LocalDateTime, Double>> tailIterator = tailMap.entrySet().iterator();
		Entry<LocalDateTime, Double> floorEntry = tailIterator.hasNext() ? tailIterator.next() : null;
		Entry<LocalDateTime, Double> ceilingEntry = tailIterator.hasNext() ? tailIterator.next() : null;

		LocalDateTime lastKey = values.lastKey();

		Map<LocalDateTime, Double> resampledValues = new HashMap<>();
		TimeSeriesIterator intervalIterator = new TimeSeriesIterator(startTime, frequencyType, window, intervals);

		LocalDateTime currentTime;
		while (intervalIterator.hasNext() && !(currentTime = intervalIterator.next()).isAfter(lastKey)
				&& floorEntry != null && ceilingEntry != null) {
			if (currentTime.isAfter(ceilingEntry.getKey())) {
				floorEntry = ceilingEntry;
				ceilingEntry = tailIterator.hasNext() ? tailIterator.next() : null;
			}
			double value = calculateInterpolation(floorEntry, ceilingEntry, currentTime);
			resampledValues.put(currentTime, value);
		}

		return MyTimeSeries.fromDictionary(resampledValues);
	}

	public String toString() {
		return values.toString();
	}

	private static double calculateInterpolation(Entry<LocalDateTime, Double> floorEntry,
			Entry<LocalDateTime, Double> ceilingEntry, LocalDateTime dateTime) {
		if (floorEntry.getKey().equals(dateTime)) {
			return floorEntry.getValue();
		}
		if (ceilingEntry.getKey().equals(dateTime)) {
			return ceilingEntry.getValue();
		}
		double dy = ceilingEntry.getValue() - floorEntry.getValue();
		long dx = ChronoUnit.MILLIS.between(ceilingEntry.getKey(), floorEntry.getKey());
		double slope = dy / dx;

		long deltaX = ChronoUnit.MILLIS.between(dateTime, floorEntry.getKey());
		double deltaY = slope * deltaX;

		double interpolatedValue = floorEntry.getValue() + deltaY;

		return interpolatedValue;
	}

	public class TimeSeriesIterator implements Iterator<LocalDateTime> {

		private LocalDateTime currentTime;
		private FrequencyType frequencyType;
		private int intervals;
		private int intervalsCurrent;
		private int window;

		public TimeSeriesIterator(LocalDateTime startTime, FrequencyType frequencyType, int window, int intervals) {
			this.currentTime = startTime;
			this.frequencyType = frequencyType;
			this.intervals = intervals;
			intervalsCurrent = 0;
			this.window = window;
		}

		@Override
		public boolean hasNext() {
			return intervalsCurrent <= intervals;
		}

		@Override
		public LocalDateTime next() {
			if (intervalsCurrent > intervals) {
				throw new NoSuchElementException();
			}
			LocalDateTime existingCurrentTime = currentTime;

			if (frequencyType == FrequencyType.DAY) {
				currentTime = currentTime.plusDays(window);
			} else {
				currentTime = currentTime.plusMonths(window);
			}
			intervalsCurrent++;
			return existingCurrentTime;
		}
	}
}
