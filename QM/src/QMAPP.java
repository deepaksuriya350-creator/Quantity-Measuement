public class QMAPP {

    // Enum with ALL units using a single base unit (INCHES)
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

    // Unified Quantity class (UC5 + UC6)
    static class Quantity {
        private final double value;
        private final LengthUnit unit;
        private static final double EPSILON = 1e-6;

        public Quantity(double value, LengthUnit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        // Convert to base unit (INCHES)
        private double toBaseUnit() {
            return unit.toBase(value);
        }

        // =========================
        // UC5: Conversion Methods
        // =========================

        public Quantity convertTo(LengthUnit targetUnit) {
            double convertedValue = convert(this.value, this.unit, targetUnit);
            return new Quantity(convertedValue, targetUnit);
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
        // UC6: ADDITION (NEW)
        // =========================

        public Quantity add(Quantity other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            // Convert both to base unit (INCHES)
            double thisBase = this.toBaseUnit();
            double otherBase = other.toBaseUnit();

            double sumBase = thisBase + otherBase;

            if (!Double.isFinite(sumBase)) {
                throw new ArithmeticException("Invalid addition result");
            }

            // Convert back to FIRST operand unit
            double resultValue = this.unit.fromBase(sumBase);

            return new Quantity(resultValue, this.unit);
        }

        public static Quantity add(Quantity q1, Quantity q2) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Operands cannot be null");
            }
            return q1.add(q2);
        }

        // =========================
        // Equality (UC4/UC5)
        // =========================

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Quantity)) return false;

            Quantity other = (Quantity) obj;
            return Math.abs(this.toBaseUnit() - other.toBaseUnit()) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(Math.round(toBaseUnit() / EPSILON));
        }

        @Override
        public String toString() {
            return value + " " + unit;
        }

        // =========================
        // Validation
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
    // Demo (UC5 + UC6)
    // =========================

    public static void main(String[] args) {

        // UC4: Equality
        Quantity yard = new Quantity(1.0, LengthUnit.YARD);
        Quantity feet = new Quantity(3.0, LengthUnit.FEET);
        System.out.println(yard + " == " + feet + " → " + yard.equals(feet));

        // UC5: Conversion
        System.out.println(Quantity.convert(1.0, LengthUnit.FEET, LengthUnit.INCH));
        System.out.println(Quantity.convert(3.0, LengthUnit.YARD, LengthUnit.FEET));
        System.out.println(Quantity.convert(36.0, LengthUnit.INCH, LengthUnit.YARD));

        Quantity cm = new Quantity(2.54, LengthUnit.CENTIMETER);
        System.out.println(cm.convertTo(LengthUnit.INCH));

        // =========================
        // UC6: ADDITION TESTS
        // =========================

        System.out.println(
                new Quantity(1.0, LengthUnit.FEET)
                        .add(new Quantity(2.0, LengthUnit.FEET))
        ); // 3 FEET

        System.out.println(
                new Quantity(1.0, LengthUnit.FEET)
                        .add(new Quantity(12.0, LengthUnit.INCH))
        ); // 2 FEET

        System.out.println(
                new Quantity(12.0, LengthUnit.INCH)
                        .add(new Quantity(1.0, LengthUnit.FEET))
        ); // 24 INCH

        System.out.println(
                new Quantity(1.0, LengthUnit.YARD)
                        .add(new Quantity(3.0, LengthUnit.FEET))
        ); // 2 YARD

        System.out.println(
                new Quantity(5.0, LengthUnit.FEET)
                        .add(new Quantity(0.0, LengthUnit.INCH))
        ); // 5 FEET

        System.out.println(
                new Quantity(5.0, LengthUnit.FEET)
                        .add(new Quantity(-2.0, LengthUnit.FEET))
        ); // 3 FEET
    }
}