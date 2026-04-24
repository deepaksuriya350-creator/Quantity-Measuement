public class QMAPP {

    // =========================================================
    // STANDALONE UNIT (UC8 REFACTOR)
    // Responsibility: ONLY conversion logic
    // =========================================================
    enum LengthUnit {

        INCH(1.0 / 12.0),
        FEET(1.0),
        YARD(3.0),
        CENTIMETER(1.0 / 30.48);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        // Convert value in THIS unit → BASE UNIT (FEET)
        public double convertToBaseUnit(double value) {
            return value * toFeetFactor;
        }

        // Convert value in BASE UNIT (FEET) → THIS unit
        public double convertFromBaseUnit(double baseValue) {
            return baseValue / toFeetFactor;
        }

        public double getFactor() {
            return toFeetFactor;
        }
    }

    // =========================================================
    // VALUE OBJECT (UC8 SIMPLIFIED)
    // Responsibility:
    // - arithmetic
    // - equality
    // - delegation to LengthUnit
    // =========================================================
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
        // Delegation to Unit
        // -------------------------
        private double toBase() {
            return unit.convertToBaseUnit(value);
        }

        // =========================================================
        // UC5: Conversion (delegates to LengthUnit)
        // =========================================================
        public Quantity convertTo(LengthUnit targetUnit) {
            validateTarget(targetUnit);

            double base = this.toBase();
            double converted = targetUnit.convertFromBaseUnit(base);

            return new Quantity(converted, targetUnit);
        }

        // =========================================================
        // UC6: Add (default = first operand unit)
        // =========================================================
        public Quantity add(Quantity other) {
            return add(other, this.unit);
        }

        // =========================================================
        // UC7: Add with explicit target unit
        // =========================================================
        public Quantity add(Quantity other, LengthUnit targetUnit) {

            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            validateTarget(targetUnit);

            double sumInBase =
                    this.unit.convertToBaseUnit(this.value)
                            + other.unit.convertToBaseUnit(other.value);

            double result = targetUnit.convertFromBaseUnit(sumInBase);

            return new Quantity(result, targetUnit);
        }

        // Static UC7 style API
        public static Quantity add(Quantity q1, Quantity q2, LengthUnit targetUnit) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Operands cannot be null");
            }
            return q1.add(q2, targetUnit);
        }

        // =========================================================
        // UC4/UC5/UC8: Equality using BASE UNIT
        // =========================================================
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

        // =========================================================
        // VALIDATION
        // =========================================================
        private static void validate(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
        }

        private static void validateTarget(LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }
        }
    }

    // =========================================================
    // DEMO (UC5 → UC8)
    // =========================================================
    public static void main(String[] args) {

        Quantity q1 = new Quantity(1.0, LengthUnit.FEET);
        Quantity q2 = new Quantity(12.0, LengthUnit.INCH);
        Quantity q3 = new Quantity(1.0, LengthUnit.YARD);

        // UC5: conversion
        System.out.println(q1.convertTo(LengthUnit.INCH));
        // 12 INCH

        // UC6: default addition
        System.out.println(q1.add(q2));
        // 2 FEET

        // UC7: explicit target unit
        System.out.println(q1.add(q2, LengthUnit.YARD));
        // ~0.667 YARD

        // UC8: refactored equality (same behavior, new architecture)
        System.out.println(
                new Quantity(36.0, LengthUnit.INCH)
                        .equals(new Quantity(1.0, LengthUnit.YARD))
        );
        // true

        // UC8: different target units
        System.out.println(q3.add(q1.add(q2), LengthUnit.FEET));
        // 6 FEET (1 yard + 2 feet)
    }
}