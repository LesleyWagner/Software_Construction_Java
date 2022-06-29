package warmup;

import java.util.HashSet;
import java.util.Set;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    public static Set<Integer> roots(int a, int b, int c) {
        if (a==0 && b==0 && c==0) {
            throw new IllegalArgumentException("all zeros");
        }
        
        Set<Integer> rootNumbers = new HashSet<>();
        
        if (a==0) {
            double xDouble = (double)-c/b;
            
            // Only add to result if x is an integer
            addIntRoot (xDouble, rootNumbers);
            return rootNumbers;
        }
        
        double discriminant = Math.pow((double)b, 2.0)-4*a*(long)c;
        if (discriminant == 0) {
            double xDouble = (double)-b/(2*a);
            
            addIntRoot (xDouble, rootNumbers);
        }
        else if (discriminant > 0) {
            // b +/- the root 
            double x1Double =((double)-b+Math.sqrt(discriminant))/(2*a);
            double x2Double =((double)-b-Math.sqrt(discriminant))/(2*a);
            //System.out.println(x1Double);
            //System.out.println(x2Double);
            addIntRoot (x1Double, rootNumbers);
            addIntRoot (x2Double, rootNumbers);            
        }
        
        return rootNumbers;
    }
    
    /**
     * Helper function to roots. Adds number to result if the number is an integer.
     * @param xDouble result of the quadratic formula
     * @param rootNumbers set of integer roots
     */
    private static void addIntRoot (double xDouble, Set rootNumbers) {
        if ((xDouble%1)==0) {
            int x = (int)xDouble;
            rootNumbers.add(x);  
        }
    }
    
    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
