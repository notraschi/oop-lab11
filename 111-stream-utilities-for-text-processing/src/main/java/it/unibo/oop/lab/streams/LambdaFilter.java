package it.unibo.oop.lab.streams;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.io.Serial;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Modify this small program adding new filters.
 * Realize this exercise using as much as possible the Stream library.
 * <br>
 * 1) Convert to lowercase
 * <br>
 * 2) Count the number of chars
 * <br>
 * 3) Count the number of lines
 * <br>
 * 4) List all the words in alphabetical order
 * <br>
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2"
 *
 */
public final class LambdaFilter extends JFrame {

    @Serial
    private static final long serialVersionUID = 1760990730218643730L;

    private enum Command {
        /**
         * Commands.
         */
        IDENTITY("No modifications", Function.identity()),
        TOLOWER("to lowercase", s ->
            s.chars()
                .map(Character::toLowerCase)
                .collect(StringBuilder::new,
                    StringBuilder::appendCodePoint,
                    StringBuilder::append)
                .toString()
        ),
        COUNTLINES("count lines", s -> String.valueOf(s.lines().count())),
        ALPHABETICAL("arrange in alphabetical order", s ->
            s.lines()
                .flatMap(l -> Arrays.stream(l.split(" ")))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .map(word -> word + " ")
                .collect(Collectors.joining())
        ),
        WORDCOUNT("get count of words", s -> {
            final Map<String, Integer> map = new LinkedHashMap<>();
            s.lines()
                .flatMap(l -> Arrays.stream(l.split(" ")))
                .forEach(word -> map.merge(word, 1, Integer::sum));
            return map.entrySet().stream()
                .map(entry -> entry.getKey() + " -> " + entry.getValue() + " ")
                .collect(Collectors.joining());
        }),
        COUNTCHARS("count chars", s -> String.valueOf(s.chars().count()));

        private final String commandName;
        private final Function<String, String> fun;

        Command(final String name, final Function<String, String> process) {
            commandName = name;
            fun = process;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return commandName;
        }

        /**
         * applies function to string.
         * 
         * @param s string to operate on
         * @return changed string
         */
        public String translate(final String s) {
            return fun.apply(s);
        }
    }

    private LambdaFilter() {
        super("Lambda filter GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel1 = new JPanel();
        final LayoutManager layout = new BorderLayout();
        panel1.setLayout(layout);
        final JComboBox<Command> combo = new JComboBox<>(Command.values());
        panel1.add(combo, BorderLayout.NORTH);
        final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
        final JTextArea left = new JTextArea();
        left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        final JTextArea right = new JTextArea();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setEditable(false);
        centralPanel.add(left);
        centralPanel.add(right);
        panel1.add(centralPanel, BorderLayout.CENTER);
        final JButton apply = new JButton("Apply");
        apply.addActionListener(ev ->
            right.setText(
                ((Command) Objects.requireNonNull(combo.getSelectedItem()))
                    .translate(left.getText())
            )
        );
        panel1.add(apply, BorderLayout.SOUTH);
        setContentPane(panel1);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        setSize(sw / 4, sh / 4);
        setLocationByPlatform(true);
    }

    /**
     * @param a unused
     */
    public static void main(final String... a) {
        final LambdaFilter gui = new LambdaFilter();
        gui.setVisible(true);
    }
}
