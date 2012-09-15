package initial3d;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import static initial3d.renderer.Util.*;

/**
 * Basic profiling helper. Only for single-threaded use. All times are in nanoseconds. Automatic reset is not
 * particularly precise as it is only triggered on <code>startSection()</code>.
 * 
 * @author Ben Allen
 * 
 */
public class Profiler {

	private Map<String, Long> last = new HashMap<String, Long>();
	private Map<String, Long> current = new HashMap<String, Long>();
	private Map<String, Long> cumulative = new HashMap<String, Long>();
	private long last_prof = 0;
	private long cumulative_prof = 0;
	private final long t_start;
	private long t_lastreset = 0;
	private long dt_lastreset = 0;
	private long dt_autoreset = 10000000000L;
	private boolean do_autoreset = false;
	private PrintStream reset_output = null;

	public Profiler() {
		t_lastreset = timenanos();
		t_start = timenanos();
	}

	public void startSection(String id) {
		long tp = timenanos();
		if (do_autoreset && timenanos() - t_lastreset > dt_autoreset) reset();
		current.put(id, timenanos());
		cumulative_prof += timenanos() - tp;
	}

	public long endSection(String id) {
		long tp = timenanos();
		Long t;
		t = current.get(id);
		if (t == null) return -1L;
		t = timenanos() - t;
		Long tc;
		if ((tc = cumulative.get(id)) == null) tc = 0L;
		cumulative.put(id, t + tc);
		cumulative_prof += timenanos() - tp;
		return t;
	}

	public void reset() {
		current.clear();
		Map<String, Long> mtemp = last;
		last = cumulative;
		last_prof = cumulative_prof;
		cumulative = mtemp;
		cumulative.clear();
		cumulative_prof = 0;
		dt_lastreset = timenanos() - t_lastreset;
		t_lastreset = timenanos();
		if (reset_output != null) {
			reset_output.print(toString());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(sprintf(" -- Profile @ %.4fs (%.4fs) -- \n", (t_lastreset - t_start) / 1000000000d,
				dt_lastreset / 1000000000d));
		sb.append(sprintf("$Profiler : %.4f (%.4fs)\n", last_prof / (double) dt_lastreset, last_prof / 1000000000d));
		for (String id : last.keySet()) {
			sb.append(sprintf("%s : %.4f (%.4fs)\n", id, getLastRatio(id), getLast(id) / 1000000000d));
		}
		sb.append(sprintf(" -- End Profile (%d items) -- \n", last.keySet().size()));
		return sb.toString();
	}

	public long getLast(String id) {
		Long t;
		if ((t = last.get(id)) == null) t = -1L;
		return t;
	}

	public double getLastRatio(String id) {
		Long t;
		if ((t = last.get(id)) == null) t = -1L;
		return t / (double) dt_lastreset;
	}

	public void setAutoResetEnabled(boolean b) {
		do_autoreset = b;
	}

	public boolean isAutoResetEnabled() {
		return do_autoreset;
	}

	public long getAutoReset() {
		return dt_autoreset;
	}

	public void setResetOutput(PrintStream ps) {
		reset_output = ps;
	}

}
