package f1digitaltwin.car;

/**
 * Class representing the Fuel tank
 */
class Fuel {

    private double amount;

    /**
     * @param kg Amount of fuel in kilogramme
     */
    Fuel(double kg) {
        // Max fuel amount is 110 kg
        if (kg > 110) kg = 110;
        amount = kg;
    }

    /**
     * @param kg Amount of kg to decrease by
     * @return whether the tank is empty or not
     */
    boolean decreaseAmount(double kg) {
        if (amount <= 0) return true;
        amount -= kg;
        return false;
    }

    /**
     * @return Amount of fuel remaining
     */
    double getAmount() {
        return amount;
    }
}
