public class QMAPP {

    // =========================================================
    // LENGTH SYSTEM (UC1–UC8 preserved)
    // =========================================================
    enum LengthUnit {
        INCH(1.0 / 12.0),
        FEET(1.0),
        YARD(3.0),
        CENTIMETER(1.0 / 30.48);

        private final double toFeet;

        LengthUnit(double toFeet) {
            this.toFeet = toFeet;
        }

        public double convertToBaseUnit(double value) {
            return value * toFeet;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / toFeet;
        }
    }

    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;
        private static final double EPSILON = 1e-6;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid input");
            }
            this.value = value;
            this.unit = unit;
        }

        private double toBase() {
            return unit.convertToBaseUnit(value);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof QuantityLength other)) return false;
            return Math.abs(this.toBase() - other.toBase()) < EPSILON;
        }
    }

    // =========================================================
    // WEIGHT SYSTEM (UC9 NEW CATEGORY)
    // =========================================================
    enum WeightUnit {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double toKg;

        WeightUnit(double toKg) {
            this.toKg = toKg;
        }

        // convert to base unit (KG)
        public double convertToBaseUnit(double value) {
            return value * toKg;
        }

        // convert from base unit (KG)
        public double convertFromBaseUnit(double baseValue) {
            return baseValue / toKg;
        }
    }

    static class QuantityWeight {

        private final double value;
        private final WeightUnit unit;
        private static final double EPSILON = 1e-6;

        public QuantityWeight(double value, WeightUnit unit) {
            if (unit == null || !Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid weight input");
            }
            this.value = value;
            this.unit = unit;
        }

        private double toBase() {
            return unit.convertToBaseUnit(value);
        }

        // =========================
        // UC9: Equality
        // =========================
        public boolean equals(Object obj) {

            if (!(obj instanceof QuantityWeight other)) return false;

            return Math.abs(this.toBase() - other.toBase()) < EPSILON;
        }

        // =========================
        // UC9: Conversion
        // =========================
        public QuantityWeight convertTo(WeightUnit target) {

            if (target == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double base = this.toBase();
            double result = target.convertFromBaseUnit(base);

            return new QuantityWeight(result, target);
        }

        // =========================
        // UC9: ADD (default unit)
        // =========================
        public QuantityWeight add(QuantityWeight other) {
            return add(other, this.unit);
        }

        // =========================
        // UC9: ADD with target unit
        // =========================
        public QuantityWeight add(QuantityWeight other, WeightUnit target) {

            if (other == null || target == null) {
                throw new IllegalArgumentException("Invalid input");
            }

            double sumBase =
                    this.unit.convertToBaseUnit(this.value)
                            + other.unit.convertToBaseUnit(other.value);

            double result = target.convertFromBaseUnit(sumBase);

            return new QuantityWeight(result, target);
        }

        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    // =========================================================
    // DEMO (UC1–UC9)
    // =========================================================
    public static void main(String[] args) {

        // =========================
        // LENGTH (UC1–UC8)
        // =========================
        QuantityLength l1 = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength l2 = new QuantityLength(12.0, LengthUnit.INCH);

        System.out.println("Length Equal: " +
                l1.equals(new QuantityLength(12.0, LengthUnit.INCH)));

        // =========================
        // WEIGHT (UC9)
        // =========================
        QuantityWeight w1 = new QuantityWeight(1.0, WeightUnit.KILOGRAM);
        QuantityWeight w2 = new QuantityWeight(1000.0, WeightUnit.GRAM);

        // Equality
        System.out.println("Weight Equal: " + w1.equals(w2)); // true

        // Conversion
        System.out.println(w1.convertTo(WeightUnit.GRAM)); // 1000 g

        // Addition (same unit)
        System.out.println(w1.add(new QuantityWeight(2.0, WeightUnit.KILOGRAM)));

        // Cross unit addition
        System.out.println(
                w1.add(new QuantityWeight(500.0, WeightUnit.GRAM))
        );

        // Explicit target unit
        System.out.println(
                w1.add(new QuantityWeight(1000.0, WeightUnit.GRAM), WeightUnit.GRAM)
        );

        // Pound conversion
        System.out.println(
                new QuantityWeight(1.0, WeightUnit.KILOGRAM)
                        .convertTo(WeightUnit.POUND)
        );
    }
}