package view;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * The single window of the simulator, holding every screen in a card layout.
 *
 * <p>The frame creates the panels and swaps between them on request. It makes
 * no decisions about when a swap should happen; the controllers do that.</p>
 */
public class MainFrame extends JFrame {

    /** Version marker required of serializable Swing components. */
    private static final long serialVersionUID = 1L;

    /** Card name of the factory menu. */
    public static final String FACTORY = "FACTORY";
    /** Card name of the test menu. */
    public static final String TEST = "TEST";
    /** Card name of the vending features screen. */
    public static final String VENDING = "VENDING";
    /** Card name of the ramen builder screen. */
    public static final String RAMEN = "RAMEN";
    /** Card name of the maintenance features screen. */
    public static final String MAINTENANCE = "MAINTENANCE";

    private final CardLayout cards;
    private final JPanel deck;

    private final FactoryPanel factoryPanel;
    private final TestPanel testPanel;
    private final VendingPanel vendingPanel;
    private final RamenPanel ramenPanel;
    private final MaintenancePanel maintenancePanel;

    /**
     * Builds the window and every screen it contains.
     *
     * @param denominations     the face values accepted, smallest first
     * @param denominationNames the display labels for those face values
     * @param ingredientNames   the selectable ingredient roles
     */
    public MainFrame(int[] denominations, String[] denominationNames, String[] ingredientNames) {
        super("Vending Machine Factory Simulator");

        this.cards = new CardLayout();
        this.deck = new JPanel(this.cards);

        this.factoryPanel = new FactoryPanel();
        this.testPanel = new TestPanel();
        this.vendingPanel = new VendingPanel(denominations);
        this.ramenPanel = new RamenPanel();
        this.maintenancePanel = new MaintenancePanel(denominationNames, ingredientNames);

        this.deck.add(this.factoryPanel, FACTORY);
        this.deck.add(this.testPanel, TEST);
        this.deck.add(this.vendingPanel, VENDING);
        this.deck.add(this.ramenPanel, RAMEN);
        this.deck.add(this.maintenancePanel, MAINTENANCE);

        setContentPane(this.deck);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(940, 660));
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Brings one screen to the front.
     *
     * @param cardName one of the card name constants declared by this class
     */
    public void showCard(String cardName) {
        this.cards.show(this.deck, cardName);
    }

    /**
     * Shows a modal message to the user.
     *
     * @param message the text to display
     */
    public void showDialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Returns the factory menu screen.
     *
     * @return the factory panel
     */
    public FactoryPanel getFactoryPanel() {
        return this.factoryPanel;
    }

    /**
     * Returns the test menu screen.
     *
     * @return the test panel
     */
    public TestPanel getTestPanel() {
        return this.testPanel;
    }

    /**
     * Returns the vending features screen.
     *
     * @return the vending panel
     */
    public VendingPanel getVendingPanel() {
        return this.vendingPanel;
    }

    /**
     * Returns the ramen builder screen.
     *
     * @return the ramen panel
     */
    public RamenPanel getRamenPanel() {
        return this.ramenPanel;
    }

    /**
     * Returns the maintenance features screen.
     *
     * @return the maintenance panel
     */
    public MaintenancePanel getMaintenancePanel() {
        return this.maintenancePanel;
    }
}
