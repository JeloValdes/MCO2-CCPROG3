package controller;

import model.VendingMachine;

/** Simple controller for maintenance features. */
public class MaintenanceController {
    private VendingMachine machine;

    public MaintenanceController(VendingMachine machine) {
        this.machine = machine;
    }

    public boolean restock(int slotNumber, int quantity) {
        return machine.restock(slotNumber, quantity);
    }

    public boolean setPrice(int slotNumber, int newPrice) {
        return machine.setItemPrice(slotNumber, newPrice);
    }

    public boolean replenishMoney(int denomination, int quantity) {
        return machine.replenishMoney(denomination, quantity);
    }

    public boolean collectMoney(int denomination, int quantity) {
        return machine.collectMoney(denomination, quantity);
    }

    public String getTransactionSummary() {
        return machine.getTransactionSummary();
    }
}
