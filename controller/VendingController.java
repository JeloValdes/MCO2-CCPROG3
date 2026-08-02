package controller;

import model.SpecialVendingMachine;
import model.VendingMachine;

/**
 * Controller for customer/vending features.
 * It keeps track of the current customer's inserted money.
 */
public class VendingController {
    private VendingMachine machine;
    private int[] insertedCash;

    public VendingController(VendingMachine machine) {
        this.machine = machine;
        insertedCash = new int[VendingMachine.DENOMINATIONS.length];
    }

    public VendingMachine getMachine() {
        return machine;
    }

    public boolean insertMoney(int denomination) {
        int index = findDenominationIndex(denomination);
        if (index == -1) {
            return false;
        }

        insertedCash[index]++;
        return true;
    }

    public int getInsertedTotal() {
        int total = 0;

        for (int i = 0; i < insertedCash.length; i++) {
            total += insertedCash[i] * VendingMachine.DENOMINATIONS[i];
        }

        return total;
    }

    /** Returns the customer's money and clears the current payment. */
    public int[] refundMoney() {
        int[] refund = new int[insertedCash.length];

        for (int i = 0; i < insertedCash.length; i++) {
            refund[i] = insertedCash[i];
            insertedCash[i] = 0;
        }

        return refund;
    }

    public boolean buyItem(int slotNumber) {
        boolean success = machine.purchaseItem(slotNumber, insertedCash);

        if (success) {
            clearInsertedCash();
        }
        return success;
    }

    /**
     * Only works when the current machine is actually Special.
     */
    public boolean buyCustomRamen(int[] slotNumbers, int[] quantities) {
        if (!(machine instanceof SpecialVendingMachine)) {
            return false;
        }

        SpecialVendingMachine specialMachine =
                (SpecialVendingMachine) machine;

        boolean success = specialMachine.prepareRamen(
                slotNumbers, quantities, insertedCash);

        if (success) {
            clearInsertedCash();
        }
        return success;
    }

    public String getLastMessage() {
        return machine.getLastMessage();
    }

    public int getLastCalories() {
        return machine.getLastCalories();
    }

    public int getLastPrice() {
        return machine.getLastPrice();
    }

    public int[] getLastChange() {
        return machine.getLastChange();
    }

    public String getLastPreparation() {
        if (machine instanceof SpecialVendingMachine) {
            SpecialVendingMachine special =
                    (SpecialVendingMachine) machine;
            return special.getLastPreparation();
        }
        return "";
    }

    private int findDenominationIndex(int denomination) {
        for (int i = 0; i < VendingMachine.DENOMINATIONS.length; i++) {
            if (VendingMachine.DENOMINATIONS[i] == denomination) {
                return i;
            }
        }
        return -1;
    }

    private void clearInsertedCash() {
        for (int i = 0; i < insertedCash.length; i++) {
            insertedCash[i] = 0;
        }
    }
}
