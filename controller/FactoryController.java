package controller;

import model.RegularVendingMachine;
import model.SpecialVendingMachine;
import model.VendingMachine;
import view.FactoryPanel;
import view.MainFrame;
import view.TestPanel;

/** The top-level controller of the simulator. */
public class FactoryController {

    private final MainFrame frame;
    private final FactoryPanel factoryPanel;
    private final TestPanel testPanel;
    private final VendingController vendingController;
    private final MaintenanceController maintenanceController;

    private VendingMachine currentMachine;
    private int machinesBuilt;

    /**
     * Wires the factory and test menus and creates the feature controllers.
     *
     * @param frame the application window
     */
    public FactoryController(MainFrame frame) {
        this.frame = frame;
        this.factoryPanel = frame.getFactoryPanel();
        this.testPanel = frame.getTestPanel();
        this.vendingController = new VendingController(frame);
        this.maintenanceController = new MaintenanceController(frame);
        this.currentMachine = null;
        this.machinesBuilt = 0;

        this.factoryPanel.addRegularListener(event ->
                createRegularMachine(this.factoryPanel.isLoadDefaultsSelected()));
        this.factoryPanel.addSpecialListener(event ->
                createSpecialMachine(this.factoryPanel.isLoadDefaultsSelected()));
        this.factoryPanel.addTestListener(event -> openTestMenu());
        this.factoryPanel.addExitListener(event -> System.exit(0));

        this.testPanel.addVendingListener(event -> openVending());
        this.testPanel.addMaintenanceListener(event -> openMaintenance());
        this.testPanel.addBackListener(event -> frame.showCard(MainFrame.FACTORY));

        this.factoryPanel.setTestEnabled(false);
        frame.showCard(MainFrame.FACTORY);
    }

    /** Creates an empty regular machine. */
    public void createRegularMachine() {
        createRegularMachine(false);
    }

    /**
     * Creates a regular machine, optionally preloaded with sample items.
     *
     * @param loadDefaults true to stock the machine with its sample items
     */
    public void createRegularMachine(boolean loadDefaults) {
        this.currentMachine = new RegularVendingMachine();
        finishCreation(loadDefaults);
    }

    /** Creates an empty special machine. */
    public void createSpecialMachine() {
        createSpecialMachine(false);
    }

    /**
     * Creates a special machine, optionally preloaded with sample items.
     *
     * @param loadDefaults true to stock the machine with its sample items
     */
    public void createSpecialMachine(boolean loadDefaults) {
        this.currentMachine = new SpecialVendingMachine();
        finishCreation(loadDefaults);
    }

    /**
     * Returns the machine the factory is currently holding.
     *
     * @return the current machine, or null if none has been built
     */
    public VendingMachine getCurrentMachine() {
        return this.currentMachine;
    }

    /**
     * Reports whether a machine has been built yet.
     *
     * @return true if a machine exists to be tested
     */
    public boolean hasCurrentMachine() {
        return this.currentMachine != null;
    }

    /**
     * Stocks the new machine if asked and updates the factory screen.
     *
     * @param loadDefaults true to stock the machine with its sample items
     */
    private void finishCreation(boolean loadDefaults) {
        this.machinesBuilt++;

        if (loadDefaults) {
            this.currentMachine.loadDefaultItems();
        }

        this.factoryPanel.setStatus("Current machine: " + this.currentMachine.getMachineName()
                + " (#" + this.machinesBuilt + ") - "
                + this.currentMachine.getSlots().size() + " slot(s) stocked, change fund empty.");
        this.factoryPanel.setTestEnabled(true);

        String message = "A new " + this.currentMachine.getMachineName() + " has been created.";
        if (!loadDefaults) {
            message += System.lineSeparator()
                    + "It has no items yet. Add some under Maintenance, Stock.";
        }
        message += System.lineSeparator()
                + "Replenish its money under Maintenance, Money so it can give change.";
        this.frame.showDialog(message);
    }

    /** Opens the test menu for the current machine. */
    private void openTestMenu() {
        this.testPanel.setMachineLabel("Now testing: " + this.currentMachine.getMachineName()
                + " with " + this.currentMachine.getSlots().size() + " slot(s)");
        this.frame.showCard(MainFrame.TEST);
    }

    /** Hands the current machine to the vending controller and shows its screen. */
    private void openVending() {
        this.vendingController.setMachine(this.currentMachine);
        this.frame.showCard(MainFrame.VENDING);
    }

    /** Hands the current machine to the maintenance controller and shows its screen. */
    private void openMaintenance() {
        this.maintenanceController.setMachine(this.currentMachine);
        this.frame.showCard(MainFrame.MAINTENANCE);
    }
}
