public class QMAPP {


        // Enum representing supported length units
        enum LengthUnit {
            FEET(1.0),
            INCH(1.0 / 12.0); // 1 inch = 1/12 feet

            private final double toFeetFactor;

            LengthUnit(double toFeetFactor) {
                this.toFeetFactor = toFeetFactor;
            }

            public double toBase(double value) {
                return value * toFeetFactor;
            }
        }

        // Unified Quantity class (DRY applied)
        static class Quantity {
            private final double value;
            private final LengthUnit unit;

            public Quantity(double value, LengthUnit unit) {
                if (unit == null) {
                    throw new IllegalArgumentException("Unit cannot be null");
                }
                this.value = value;
                this.unit = unit;
            }

            private double toBaseUnit() {
                return unit.toBase(value); // convert to feet
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

            @Override
            public String toString() {
                return value + " " + unit;
            }
        }

        // Demo (replaces earlier UC1 + UC2 logic)
        public static void main(String[] args) {

            Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
            Quantity q2 = new Quantity(12.0, LengthUnit.INCH);
            Quantity q3 = new Quantity(2.0, LengthUnit.FEET);

            System.out.println(q1 + " == " + q2 + " → " + q1.equals(q2)); // true
            System.out.println(q1 + " == " + q3 + " → " + q1.equals(q3)); // false
        }
    }