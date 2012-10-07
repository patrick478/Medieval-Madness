package initial3d.engine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

public class ActionSource {

	private Set<ActionListener> actionlisteners = new HashSet<ActionListener>();

	public ActionSource() {

	}

	protected void dispatchActionEvent(ActionEvent e) {
		for (ActionListener l : actionlisteners) {
			l.actionPerformed(e);
		}
	}

	public void addActionListener(ActionListener l) {
		actionlisteners.add(l);
	}

	public void removeActionListener(ActionListener l) {
		actionlisteners.remove(l);
	}

}
