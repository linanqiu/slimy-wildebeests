import java.time.LocalDateTime;
import java.util.Map;

public interface TimeSeries {
	
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
	 * Returns a value if it exists, or an interpolated values if bounding values exist.
	 * Otherwise, return NaN
	 * Millisecond accuracy is sufficient
	 * 
	 * @param dateTime
	 * @return
	 */
	double getInterpolated(LocalDateTime dateTime);
}