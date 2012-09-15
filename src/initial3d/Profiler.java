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
		if (do_autoreset && timenanos() - t_lastreset > dt_autoreset) reset();
		current.put(id, timenanos());
	}

	public long endSection(String id) {
		Long t;
		t = current.get(id);
		if (t == null) return -1L;
		t = timenanos() - t;
		Long tc;
		if ((tc = cumulative.get(id)) == null) tc = 0L;
		cumulative.put(id, t + tc);
		return t;
	}

	public void reset() {
		current.clear();
		Map<String, Long> mtemp = last;
		last = cumulative;
		cumulative = mtemp;
		cumulative.clear();
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
