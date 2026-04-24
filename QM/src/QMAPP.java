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
        }

        // Unified Quantity class (unchanged from UC3 – proves scalability)
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
                return unit.toBase(value); // convert to inches
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

        // Demo
        public static void main(String[] args) {

            Quantity yard = new Quantity(1.0, LengthUnit.YARD);
            Quantity feet = new Quantity(3.0, LengthUnit.FEET);
            Quantity inches = new Quantity(36.0, LengthUnit.INCH);
            Quantity cm = new Quantity(1.0, LengthUnit.CENTIMETER);
            Quantity inchEquivalent = new Quantity(0.393701, LengthUnit.INCH);

            System.out.println(yard + " == " + feet + " → " + yard.equals(feet));     // true
            System.out.println(yard + " == " + inches + " → " + yard.equals(inches)); // true
            System.out.println(cm + " == " + inchEquivalent + " → " + cm.equals(inchEquivalent)); // true
        }
    }