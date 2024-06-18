 package util;
 
 public class Calc {
   private static final double y_min = Math.pow(Math.E, -4.5D);
   private static final double y_mul = 1.0D / (1.0D - y_min);
   
   public static double f(double x) {
     x = (1.0D - x) * 3.0D;
     return (Math.pow(Math.E, -1.0D * x * x / 2.0D) - y_min) * y_mul;
   }
 }
