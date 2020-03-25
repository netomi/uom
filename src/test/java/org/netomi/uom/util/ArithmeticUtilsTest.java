/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netomi.uom.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.math.BigInteger;
import java.util.Collections;

/**
 * Test cases for the {@link ArithmeticUtils} class.
 *
 */
public class ArithmeticUtilsTest {

    @Test
    public void testGcd() {
        int a = 30;
        int b = 50;
        int c = 77;

        Assertions.assertEquals(0, ArithmeticUtils.gcd(0, 0));

        Assertions.assertEquals(b, ArithmeticUtils.gcd(0, b));
        Assertions.assertEquals(a, ArithmeticUtils.gcd(a, 0));
        Assertions.assertEquals(b, ArithmeticUtils.gcd(0, -b));
        Assertions.assertEquals(a, ArithmeticUtils.gcd(-a, 0));

        Assertions.assertEquals(10, ArithmeticUtils.gcd(a, b));
        Assertions.assertEquals(10, ArithmeticUtils.gcd(-a, b));
        Assertions.assertEquals(10, ArithmeticUtils.gcd(a, -b));
        Assertions.assertEquals(10, ArithmeticUtils.gcd(-a, -b));

        Assertions.assertEquals(1, ArithmeticUtils.gcd(a, c));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(-a, c));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(a, -c));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(-a, -c));

        Assertions.assertEquals(3 * (1 << 15), ArithmeticUtils.gcd(3 * (1 << 20), 9 * (1 << 15)));

        Assertions.assertEquals(Integer.MAX_VALUE, ArithmeticUtils.gcd(Integer.MAX_VALUE, 0));
        Assertions.assertEquals(Integer.MAX_VALUE, ArithmeticUtils.gcd(-Integer.MAX_VALUE, 0));
        Assertions.assertEquals(1 << 30, ArithmeticUtils.gcd(1 << 30, -Integer.MIN_VALUE));
        try {
            // gcd(Integer.MIN_VALUE, 0) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(Integer.MIN_VALUE, 0);
            Assertions.fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(0, Integer.MIN_VALUE);
            Assertions.fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Integer.MIN_VALUE, Integer.MIN_VALUE) > Integer.MAX_VALUE
            ArithmeticUtils.gcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
            Assertions.fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }

    @Test
    public void testGcdConsistency() {
        // Use Integer to prevent varargs vs array issue with Arrays.asList
        Integer[] primeList = {19, 23, 53, 67, 73, 79, 101, 103, 111, 131};

        for (int i = 0; i < 20; i++) {
            Collections.shuffle(Arrays.asList(primeList));
            int p1 = primeList[0];
            int p2 = primeList[1];
            int p3 = primeList[2];
            int p4 = primeList[3];
            int i1 = p1 * p2 * p3;
            int i2 = p1 * p2 * p4;
            int gcd = p1 * p2;
            Assertions.assertEquals(gcd, ArithmeticUtils.gcd(i1, i2));
            long l1 = i1;
            long l2 = i2;
            Assertions.assertEquals(gcd, ArithmeticUtils.gcd(l1, l2));
        }
    }

    @Test
    public void  testGcdLong() {
        long a = 30;
        long b = 50;
        long c = 77;

        Assertions.assertEquals(0, ArithmeticUtils.gcd(0L, 0));

        Assertions.assertEquals(b, ArithmeticUtils.gcd(0, b));
        Assertions.assertEquals(a, ArithmeticUtils.gcd(a, 0));
        Assertions.assertEquals(b, ArithmeticUtils.gcd(0, -b));
        Assertions.assertEquals(a, ArithmeticUtils.gcd(-a, 0));

        Assertions.assertEquals(10, ArithmeticUtils.gcd(a, b));
        Assertions.assertEquals(10, ArithmeticUtils.gcd(-a, b));
        Assertions.assertEquals(10, ArithmeticUtils.gcd(a, -b));
        Assertions.assertEquals(10, ArithmeticUtils.gcd(-a, -b));

        Assertions.assertEquals(1, ArithmeticUtils.gcd(a, c));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(-a, c));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(a, -c));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(-a, -c));

        Assertions.assertEquals(3L * (1L << 45), ArithmeticUtils.gcd(3L * (1L << 50), 9L * (1L << 45)));

        Assertions.assertEquals(1L << 45, ArithmeticUtils.gcd(1L << 45, Long.MIN_VALUE));

        Assertions.assertEquals(Long.MAX_VALUE, ArithmeticUtils.gcd(Long.MAX_VALUE, 0L));
        Assertions.assertEquals(Long.MAX_VALUE, ArithmeticUtils.gcd(-Long.MAX_VALUE, 0L));
        Assertions.assertEquals(1, ArithmeticUtils.gcd(60247241209L, 153092023L));
        try {
            // gcd(Long.MIN_VALUE, 0) > Long.MAX_VALUE
            ArithmeticUtils.gcd(Long.MIN_VALUE, 0);
            Assertions.fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(0, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticUtils.gcd(0, Long.MIN_VALUE);
            Assertions.fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
        try {
            // gcd(Long.MIN_VALUE, Long.MIN_VALUE) > Long.MAX_VALUE
            ArithmeticUtils.gcd(Long.MIN_VALUE, Long.MIN_VALUE);
            Assertions.fail("expecting ArithmeticException");
        } catch (ArithmeticException expected) {
            // expected
        }
    }


    @Test
    public void testPow() {

        Assertions.assertEquals(1801088541, ArithmeticUtils.pow(21, 7));
        Assertions.assertEquals(1, ArithmeticUtils.pow(21, 0));
        try {
            ArithmeticUtils.pow(21, -7);
            Assertions.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        Assertions.assertEquals(1801088541, ArithmeticUtils.pow(21, 7));
        Assertions.assertEquals(1, ArithmeticUtils.pow(21, 0));
        try {
            ArithmeticUtils.pow(21, -7);
            Assertions.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        Assertions.assertEquals(1801088541L, ArithmeticUtils.pow(21L, 7));
        Assertions.assertEquals(1L, ArithmeticUtils.pow(21L, 0));
        try {
            ArithmeticUtils.pow(21L, -7);
            Assertions.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        BigInteger twentyOne = BigInteger.valueOf(21L);
        Assertions.assertEquals(BigInteger.valueOf(1801088541L), ArithmeticUtils.pow(twentyOne, 7));
        Assertions.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, 0));
        try {
            ArithmeticUtils.pow(twentyOne, -7);
            Assertions.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        Assertions.assertEquals(BigInteger.valueOf(1801088541L), ArithmeticUtils.pow(twentyOne, 7L));
        Assertions.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, 0L));
        try {
            ArithmeticUtils.pow(twentyOne, -7L);
            Assertions.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        Assertions.assertEquals(BigInteger.valueOf(1801088541L), ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(7L)));
        Assertions.assertEquals(BigInteger.ONE, ArithmeticUtils.pow(twentyOne, BigInteger.ZERO));
        try {
            ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(-7L));
            Assertions.fail("Expecting IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected behavior
        }

        BigInteger bigOne =
            new BigInteger("1543786922199448028351389769265814882661837148" +
                           "4763915343722775611762713982220306372888519211" +
                           "560905579993523402015636025177602059044911261");
        Assertions.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, 103));
        Assertions.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, 103L));
        Assertions.assertEquals(bigOne, ArithmeticUtils.pow(twentyOne, BigInteger.valueOf(103L)));

    }

    @Test
    public void testPowIntOverflow() {
        Assertions.assertThrows(ArithmeticException.class,
            () -> ArithmeticUtils.pow(21, 8)
        );
    }

    @Test
    public void testPowInt() {
        final int base = 21;

        Assertions.assertEquals(85766121L,
                            ArithmeticUtils.pow(base, 6));
        Assertions.assertEquals(1801088541L,
                            ArithmeticUtils.pow(base, 7));
    }

    @Test
    public void testPowNegativeIntOverflow() {
        Assertions.assertThrows(ArithmeticException.class,
            () -> ArithmeticUtils.pow(-21, 8)
        );
    }

    @Test
    public void testPowNegativeInt() {
        final int base = -21;

        Assertions.assertEquals(85766121,
                            ArithmeticUtils.pow(base, 6));
        Assertions.assertEquals(-1801088541,
                            ArithmeticUtils.pow(base, 7));
    }

    @Test
    public void testPowMinusOneInt() {
        final int base = -1;
        for (int i = 0; i < 100; i++) {
            final int pow = ArithmeticUtils.pow(base, i);
            Assertions.assertEquals(i % 2 == 0 ? 1 : -1, pow, "i: " + i);
        }
    }

    @Test
    public void testPowOneInt() {
        final int base = 1;
        for (int i = 0; i < 100; i++) {
            final int pow = ArithmeticUtils.pow(base, i);
            Assertions.assertEquals(1, pow, "i: " + i);
        }
    }

    @Test
    public void testPowLongOverflow() {
        Assertions.assertThrows(ArithmeticException.class,
            () -> ArithmeticUtils.pow(21, 15)
        );
    }

    @Test
    public void testPowLong() {
        final long base = 21;

        Assertions.assertEquals(154472377739119461L,
                            ArithmeticUtils.pow(base, 13));
        Assertions.assertEquals(3243919932521508681L,
                            ArithmeticUtils.pow(base, 14));
    }

    @Test
    public void testPowNegativeLongOverflow() {
        Assertions.assertThrows(ArithmeticException.class,
            () -> ArithmeticUtils.pow(-21L, 15)
        );
    }

    @Test
    public void testPowNegativeLong() {
        final long base = -21;

        Assertions.assertEquals(-154472377739119461L,
                            ArithmeticUtils.pow(base, 13));
        Assertions.assertEquals(3243919932521508681L,
                            ArithmeticUtils.pow(base, 14));
    }

    @Test
    public void testPowMinusOneLong() {
        final long base = -1;
        for (int i = 0; i < 100; i++) {
            final long pow = ArithmeticUtils.pow(base, i);
            Assertions.assertEquals(i % 2 == 0 ? 1 : -1, pow, "i: " + i);
        }
    }

    @Test
    public void testPowOneLong() {
        final long base = 1;
        for (int i = 0; i < 100; i++) {
            final long pow = ArithmeticUtils.pow(base, i);
            Assertions.assertEquals(1, pow, "i: " + i);
        }
    }

    @Test
    public void testIsPowerOfTwo() {
        final int n = 1025;
        final boolean[] expected = new boolean[n];
        Arrays.fill(expected, false);
        for (int i = 1; i < expected.length; i *= 2) {
            expected[i] = true;
        }
        for (int i = 0; i < expected.length; i++) {
            final boolean actual = ArithmeticUtils.isPowerOfTwo(i);
            Assertions.assertEquals(expected[i], actual, Integer.toString(i));
        }
    }

}
