public class QMAPP {

    // =========================
    // ENUM: LENGTH UNITS
    // Base Unit = INCH
    // =========================
    enum LengthUnit {
        INCH(1.0),
        FEET(12.0),
        YARD(36.0),
        CENTIMETER(0.393701);

        private final double toInchFactor;

        LengthUnit(double toInchFactor) {
            this.toInchFactor = toInchFactor;
        }

        public double toBase(double value) {
            return value * toInchFactor;
        }

        public double fromBase(double baseValue) {
            return baseValue / toInchFactor;
        }

        public double getFactor() {
            return toInchFactor;
        }
    }

    // =========================
    // VALUE OBJECT: QUANTITY
    // UC5 + UC6 + UC7
    // =========================
    static class Quantity {

        private final double value;
        private final LengthUnit unit;
        private static final double EPSILON = 1e-6;

        public Quantity(double value, LengthUnit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        // -------------------------
        // Base Conversion
        // -------------------------
        private double toBase() {
            return unit.toBase(value);
        }

        // =========================
        // UC5: Conversion API
        // =========================
        public Quantity convertTo(LengthUnit targetUnit) {
            double result = convert(value, unit, targetUnit);
            return new Quantity(result, targetUnit);
        }

        public static double convert(double value, LengthUnit source, LengthUnit target) {
            validate(value, source);

            if (target == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double result = value * (source.getFactor() / target.getFactor());

            if (!Double.isFinite(result)) {
                throw new ArithmeticException("Conversion overflow");
            }

            return result;
        }

        // =========================
        // UC6: ADD (default unit = first operand)
        // =========================
        public Quantity add(Quantity other) {
            return add(other, this.unit);
        }

        // =========================
        // UC7: ADD with explicit target unit
        // =========================
        public Quantity add(Quantity other, LengthUnit targetUnit) {

            if (other == null) {
                throw new IllegalArgumentException("Second operand cannot be null");
            }

            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            // Convert both operands to base (INCH)
            double sumBase = this.toBase() + other.toBase();

            if (!Double.isFinite(sumBase)) {
                throw new ArithmeticException("Invalid arithmetic result");
            }

            // Convert to explicitly requested unit
            double resultValue = targetUnit.fromBase(sumBase);

            return new Quantity(resultValue, targetUnit);
        }

        // Static overload (UC7 style API)
        public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Operands cannot be null");
            }
            return q1.add(q2, targetUnit);
        }

        // =========================
        // EQUALITY (UC4/UC5)
        // =========================
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity)) return false;

            Quantity other = (Quantity) obj;
            return Math.abs(this.toBase() - other.toBase()) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(Math.round(toBase() / EPSILON));
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }

        // =========================
        // VALIDATION
        // =========================
        private static void validate(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
        }
    }

    // =========================
    // DEMO (UC5 + UC6 + UC7)
    // =========================
    public static void main(String[] args) {

        Quantity oneFoot = new Quantity(1.0, LengthUnit.FEET);
        Quantity twelveInch = new Quantity(12.0, LengthUnit.INCH);
        Quantity threeFeet = new Quantity(3.0, LengthUnit.FEET);

        // UC6 (default unit = first operand)
        System.out.println(oneFoot.add(twelveInch));
        // Quantity(2.0, FEET)

        // UC7: explicit FEET
        System.out.println(oneFoot.add(twelveInch, LengthUnit.FEET));
        // Quantity(2.0, FEET)

        // UC7: explicit INCH
        System.out.println(oneFoot.add(twelveInch, LengthUnit.INCH));
        // Quantity(24.0, INCH)

        // UC7: explicit YARD
        System.out.println(oneFoot.add(twelveInch, LengthUnit.YARD));
        // ~0.667 YARD

        // UC7: YARD + FEET → YARD
        System.out.println(
                new Quantity(1.0, LengthUnit.YARD)
                        .add(threeFeet, LengthUnit.YARD)
        );
        // Quantity(2.0, YARD)

        // UC7: zero case
        System.out.println(
                new Quantity(5.0, LengthUnit.FEET)
                        .add(new Quantity(0.0, LengthUnit.INCH), LengthUnit.YARD)
        );
        // ~1.667 YARD

        // UC7: negative values
        System.out.println(
                new Quantity(5.0, LengthUnit.FEET)
                        .add(new Quantity(-2.0, LengthUnit.FEET), LengthUnit.INCH)
        );
        // 36 INCH
    }
}