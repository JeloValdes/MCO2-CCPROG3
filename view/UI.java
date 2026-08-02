package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Shared styling helpers for the view layer.
 *
 * <p>Its only job is to keep fonts, spacing, and colours consistent without
 * repeating the same Swing configuration in five different panels. It holds no
 * state and knows nothing about vending machines.</p>
 */
public final class UI {

    /** Accent colour used for headings and titles. */
    public static final Color ACCENT = new Color(0x8C, 0x2F, 0x2F);

    /** Background colour shared by every panel. */
    public static final Color BACKGROUND = new Color(0xF6, 0xF4, 0xEE);

    /** Font used for screen titles. */
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);

    /** Font used for section headings and running totals. */
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 14);

    /** Font used for the simulated machine output. */
    public static final Font OUTPUT_FONT = new Font("Monospaced", Font.PLAIN, 13);

    /**
     * Prevents instantiation of this utility class.
     */
    private UI() {
    }

    /**
     * Creates a bold screen title.
     *
     * @param text the title text
     * @return a configured label
     */
    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(ACCENT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Creates a bold section heading.
     *
     * @param text the heading text
     * @return a configured label
     */
    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING_FONT);
        return label;
    }

    /**
     * Creates a wide button sized for the menu screens.
     *
     * @param text the button caption
     * @return a configured button
     */
    public static JButton menuButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(340, 44));
        button.setPreferredSize(new Dimension(340, 44));
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Creates a compact button for toolbars and forms.
     *
     * @param text the button caption
     * @return a configured button
     */
    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        return button;
    }

    /**
     * Creates a panel with the shared background and a padding border.
     *
     * @param padding the padding in pixels applied on all four sides
     * @return a configured panel using a border layout
     */
    public static JPanel panel(int padding) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        return panel;
    }

    /**
     * Creates a read-only, single-selection table with the given headings.
     *
     * @param columns the column headings
     * @return a configured table backed by an empty model
     */
    public static JTable table(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(new Object[0][columns.length], columns) {
            /** Version marker required of serializable Swing models. */
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    /**
     * Replaces every row of a table created by {@link #table(String[])}.
     *
     * @param table the table to refresh
     * @param rows  the new rows, in display order
     */
    public static void setRows(JTable table, Object[][] rows) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Object[] row : rows) {
            model.addRow(row);
        }
    }

    /**
     * Configures the read-only area used to simulate machine feedback.
     *
     * @param area the text area to configure and wrap
     * @param rows the visible height of the area in text rows
     * @return a scroll pane wrapping the configured text area
     */
    public static JScrollPane output(JTextArea area, int rows) {
        area.setEditable(false);
        area.setFont(OUTPUT_FONT);
        area.setRows(rows);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createTitledBorder("Machine output"));
        return scroll;
    }

    /**
     * Builds the display label for a denomination, distinguishing coins from
     * bills the way the physical currency does.
     *
     * @param value the face value in pesos
     * @return a label such as "20 Peso Coin" or "100 Peso Bill"
     */
    public static String denominationLabel(int value) {
        return value + " Peso " + (value > 20 ? "Bill" : "Coin");
    }

    /**
     * Creates a fixed vertical gap for use inside a box layout.
     *
     * @param height the gap height in pixels
     * @return a rigid area component
     */
    public static Component gap(int height) {
        return Box.createRigidArea(new Dimension(1, height));
    }
}
