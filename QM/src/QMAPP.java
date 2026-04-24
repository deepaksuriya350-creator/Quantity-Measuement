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

    // Unified Quantity class
    static class Quantity {
        private final double value;
        private final LengthUnit unit;
        private static final double EPSILON = 1e-6;

        public Quantity(double value, LengthUnit unit) {
            validate(value, unit);
            this.value = value;
            this.unit = unit;
        }

        private double toBaseUnit() {
            return unit.toBase(value); // convert to inches
        }

        // ✅ UC5: Instance conversion method
        public Quantity convertTo(LengthUnit targetUnit) {
            double convertedValue = convert(this.value, this.unit, targetUnit);
            return new Quantity(convertedValue, targetUnit);
        }

        // ✅ UC5: Static conversion API
        public static double convert(double value, LengthUnit source, LengthUnit target) {
            validate(value, source);
            if (target == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            // Direct formula (more stable)
            double result = value * (source.getFactor() / target.getFactor());

            if (!Double.isFinite(result)) {
                throw new ArithmeticException("Conversion overflow");
            }

            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;

            if (obj == null || getClass() != obj.getClass()) return false;

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

        private static void validate(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
        }
    }

    // ✅ UC5 Demonstration Methods (Method Overloading)

    public static void demonstrateLengthConversion(double value, LengthUnit from, LengthUnit to) {
        double result = Quantity.convert(value, from, to);
        System.out.println(value + " " + from + " → " + result + " " + to);
    }

    public static void demonstrateLengthConversion(Quantity quantity, LengthUnit to) {
        System.out.println(quantity + " → " + quantity.convertTo(to));
    }

    // Demo
    public static void main(String[] args) {

        // UC4 Equality
        Quantity yard = new Quantity(1.0, LengthUnit.YARD);
        Quantity feet = new Quantity(3.0, LengthUnit.FEET);
        System.out.println(yard + " == " + feet + " → " + yard.equals(feet));

        // UC5 Conversion
        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCH); // 12
        demonstrateLengthConversion(3.0, LengthUnit.YARD, LengthUnit.FEET); // 9
        demonstrateLengthConversion(36.0, LengthUnit.INCH, LengthUnit.YARD); // 1

        Quantity cm = new Quantity(2.54, LengthUnit.CENTIMETER);
        demonstrateLengthConversion(cm, LengthUnit.INCH); // ~1 inch

        // Edge cases
        demonstrateLengthConversion(0.0, LengthUnit.FEET, LengthUnit.INCH);
        demonstrateLengthConversion(-1.0, LengthUnit.FEET, LengthUnit.INCH);
    }
}