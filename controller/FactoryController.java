package controller;

import model.RegularVendingMachine;
import model.SpecialVendingMachine;
import model.VendingMachine;

public class FactoryController {
    // Polymorphism: this can hold either Regular or Special.
    private VendingMachine currentMachine;

    public FactoryController() {
        currentMachine = null;
    }

    /*create empty Regular machine. */
    public void createRegularMachine() {
        createRegularMachine(false);
    }

    /*optionally load the sample items. */
    public void createRegularMachine(boolean loadDefaults) {
        currentMachine = new RegularVendingMachine();

        if (loadDefaults) {
            currentMachine.loadDefaultItems();
        }
    }

    public void createSpecialMachine() {
        createSpecialMachine(false);
    }

    public void createSpecialMachine(boolean loadDefaults) {
        currentMachine = new SpecialVendingMachine();

        if (loadDefaults) {
            currentMachine.loadDefaultItems();
        }
    }

    public VendingMachine getCurrentMachine() {
        return currentMachine;
    }

    public boolean hasCurrentMachine() {
        return currentMachine != null;
    }
}
