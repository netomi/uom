/*
 * Copyright (c) 2020 Thomas Neidhart
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.neidhart.uom.unit;

import tech.neidhart.uom.Dimension;
import tech.neidhart.uom.SystemOfUnits;
import tech.neidhart.uom.Unit;
import tech.neidhart.uom.math.Fraction;

import java.util.*;

/**
 * A utility class to access the supported set of {@link Dimension} instances.
 * <p>
 * {@link SystemOfUnits} implementations may use a distinct subset of
 * these dimensions, but may not define their own set of dimensions. If a new dimension
 * is needed, this class should be extended accordingly.
 *
 * @author Thomas Neidhart
 */
public final class Dimensions {

    private static final Map<PhysicalDimension.Base, Dimension> physicalBaseDimensions =
            new EnumMap<>(PhysicalDimension.Base.class);

    /**
     * A {@link Dimension} to represent dimensionless quantities / units.
     */
    public static final Dimension NONE = PhysicalDimension.empty();

    /**
     * The {@link Dimension} to represent quantities of type length.
     */
    public static final Dimension LENGTH = addPhysicalBaseDimension(PhysicalDimension.Base.LENGTH);

    /**
     * The {@link Dimension} to represent quantities of type time.
     */
    public static final Dimension TIME = addPhysicalBaseDimension(PhysicalDimension.Base.TIME);

    /**
     * The {@link Dimension} to represent quantities of type temperature.
     */
    public static final Dimension TEMPERATURE = addPhysicalBaseDimension(PhysicalDimension.Base.TEMPERATURE);

    /**
     * The {@link Dimension} to represent quantities of type electric current.
     */
    public static final Dimension ELECTRIC_CURRENT = addPhysicalBaseDimension(PhysicalDimension.Base.ELECTRIC_CURRENT);

    /**
     * The {@link Dimension} to represent quantities of type mass.
     */
    public static final Dimension MASS = addPhysicalBaseDimension(PhysicalDimension.Base.MASS);

    /**
     * The {@link Dimension} to represent quantities of type amount of substance.
     */
    public static final Dimension AMOUNT_OF_SUBSTANCE = addPhysicalBaseDimension(PhysicalDimension.Base.AMOUNT_OF_SUBSTANCE);

    /**
     * The {@link Dimension} to represent quantities of type luminous intensity.
     */
    public static final Dimension LUMINOUS_INTENSITY = addPhysicalBaseDimension(PhysicalDimension.Base.LUMINOUS_INTENSITY);


    // Hide utility class constructor.
    private Dimensions() {}

    private static Dimension addPhysicalBaseDimension(PhysicalDimension.Base baseDimension) {
        Dimension dimension = PhysicalDimension.of(baseDimension);
        physicalBaseDimensions.put(baseDimension, dimension);
        return dimension;
    }

    static Dimension getPhysicalBaseDimension(PhysicalDimension.Base baseDimension) {
        return physicalBaseDimensions.get(baseDimension);
    }

    /**
     * Returns an unmodifiable {@link Collection} containing all supported base dimensions.
     */
    public static Collection<Dimension> getPhysicalBaseDimensions() {
        return Collections.unmodifiableCollection(physicalBaseDimensions.values());
    }

    // Internal methods with public scope.

    /**
     * Returns a {@link Dimension} which is the product of the 2 given dimensions and
     * their associated fractions.
     * <p>
     * Note: this method is only used for internal purposes and should not be called
     * otherwise.
     */
    public static Dimension productOf(Dimension left,  Fraction leftFraction,
                                      Dimension right, Fraction rightFraction) {
        return ProductDimension.ofProduct(left, leftFraction, right, rightFraction);
    }

    /**
     * Returns a new {@link Dimension} that represents the nth power of this dimension.
     * <p>
     * Note: this method is only used for internal purposes and should not be called
     * otherwise.
     */
    public static Dimension powOf(Dimension dimension, Fraction fraction) {
        return ProductDimension.ofProduct(dimension, fraction);
    }

    /**
     * Returns a new {@link Dimension}, identified by the given name that can be used
     * to create distinct {@link Unit} instances for non-physical quantities.
     *
     * @param name the name of the dimension.
     * @return a new {@link Dimension} instance with the given name.
     */
    public static Dimension ofName(String name) {
        return new NamedDimension(name);
    }

    /**
     * A simple {@link Dimension} identified by a name.
     */
    static class NamedDimension extends Dimension {

        private final String name;

        NamedDimension(String name) {
            this.name = name;
        }

        @Override
        public Map<Dimension, Fraction> getBaseDimensions() {
            return Collections.singletonMap(this, Fraction.ONE);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NamedDimension that = (NamedDimension) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
