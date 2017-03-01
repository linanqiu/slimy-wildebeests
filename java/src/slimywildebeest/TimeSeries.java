package slimywildebeest;
import java.time.LocalDateTime;
import java.util.Map;

public interface TimeSeries {

	public enum FrequencyType {
		DAY, MONTH
	}

	/**
	 * Returns the value corresponding to the dateTime or NaN
	 * 
	 * @param dateTime
	 * @return
	 */
	double get(LocalDateTime dateTime);

	/**
	 * Sets a value at a specific dateTime
	 * 
	 * @param dateTime
	 * @param value
	 */
	void set(LocalDateTime dateTime, double value);

	/**
	 * Returns a value if it exists, or an interpolated values if bounding
	 * values exist. Otherwise, return NaN Millisecond accuracy is sufficient
	 * 
	 * @param dateTime
	 * @return
	 */
	double getInterpolated(LocalDateTime dateTime);

	/**
	 * Returns a resampled time series starting at startTime. e.g. getResampledTimeSeries(2016 April 03, DAY, 2)
	 * would return a resampled time series every 2 days starting at 2016 April 03.
	 * 
	 * This series should be bounded by the original series' earliest and latest value.
	 * If dates for the resample do exist in the original series, the original's value should be used.
	 * Otherwise, it should be the same result as calling getInterpolated on the resample date
	 * (you don't need to explicitly call getInterpolated to calculate these values)
	 * 
	 * @param startTime
	 * @param frequencyType
	 * @param window
	 * @param intervals
	 * @return
	 */
	TimeSeries getResampledTimeSeries(LocalDateTime startTime, FrequencyType frequencyType, int window, int intervals);
}