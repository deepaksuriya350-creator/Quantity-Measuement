public class QMAPP {

        // Enum to represent units and conversion logic
        enum Unit {
            FEET(12.0),
            INCH(1.0);

            private final double toInchFactor;

            Unit(double toInchFactor) {
                this.toInchFactor = toInchFactor;
            }

            public double toBase(double value) {
                return value * toInchFactor;
            }
        }

        // Quantity class representing value + unit
        static class Quantity {
            private final double value;
            private final Unit unit;

            public Quantity(double value, Unit unit) {
                this.value = value;
                this.unit = unit;
            }

            // Convert everything to base unit (inches)
            private double toBaseUnit() {
                return unit.toBase(value);
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;

                if (obj == null || getClass() != obj.getClass()) return false;

                Quantity other = (Quantity) obj;

                return Double.compare(this.toBaseUnit(), other.toBaseUnit()) == 0;
            }

            @Override
            public int hashCode() {
                return Double.hashCode(toBaseUnit());
            }
        }

        // Utility methods (reduces dependency on main)
        public static boolean compareFeet(double a, double b) {
            Quantity q1 = new Quantity(a, Unit.FEET);
            Quantity q2 = new Quantity(b, Unit.FEET);
            return q1.equals(q2);
        }

        public static boolean compareInches(double a, double b) {
            Quantity q1 = new Quantity(a, Unit.INCH);
            Quantity q2 = new Quantity(b, Unit.INCH);
            return q1.equals(q2);
        }

        public static boolean compareMixed(double feet, double inches) {
            Quantity q1 = new Quantity(feet, Unit.FEET);
            Quantity q2 = new Quantity(inches, Unit.INCH);
            return q1.equals(q2);
        }

        // Main method (demo)
        public static void main(String[] args) {

            System.out.println("Feet vs Feet (1.0, 1.0): " + compareFeet(1.0, 1.0));
            System.out.println("Inch vs Inch (1.0, 1.0): " + compareInches(1.0, 1.0));
            System.out.println("Feet vs Inches (1 ft, 12 in): " + compareMixed(1.0, 12.0));
            System.out.println("Feet vs Inches (1 ft, 11 in): " + compareMixed(1.0, 11.0));
        }
    }

