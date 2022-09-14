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
     * @return Amount of fuel remaining
     */
    double getAmount() {
        return amount;
    }

    /**
     * @param amount of fuel
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
