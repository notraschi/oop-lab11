package it.unibo.oop.reactivegui02;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentGUI.class);
    private final JLabel display = new JLabel("0");

    /**
     * enum to represent the counter behavior.
     */
    private enum CounterStatus {
        UP, DOWN, STOP
    }

    /**
     * builds a concurrent GUI.
     */
    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        //
        final JPanel box = new JPanel();
        box.add(display);
        final JButton up = new JButton("up");
        box.add(up);
        final JButton down = new JButton("down");
        box.add(down);
        final JButton stop = new JButton("stop");
        box.add(stop);
        //
        setContentPane(box);
        setVisible(true);
        //
        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * action listeners
         */
        up.addActionListener(e -> agent.setStatus(CounterStatus.UP));
        down.addActionListener(e -> agent.setStatus(CounterStatus.DOWN));
        stop.addActionListener(e -> {
            agent.setStatus(CounterStatus.STOP);
            up.setEnabled(false);
            down.setEnabled(false);
            stop.setEnabled(false);
        });
    }

    private final class Agent implements Runnable {
        private volatile CounterStatus status = CounterStatus.UP; 
        private int counter;

        @Override
        public void run() {
            while (status != CounterStatus.STOP) {
                try {
                    final String text = Integer.toString(counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(text));
                    switch (status) {
                        case CounterStatus.UP:
                            counter++;
                            break;
                        case CounterStatus.DOWN:
                            counter--;
                            break;
                        case CounterStatus.STOP:
                            break;
                    }
                    Thread.sleep(100);
                } catch (final InterruptedException | InvocationTargetException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        /**
         * sets counter behavior.
         * 
         * @param cs the new counter status
         */
        public void setStatus(final CounterStatus cs) {
            status = cs;
        }
    }
}
