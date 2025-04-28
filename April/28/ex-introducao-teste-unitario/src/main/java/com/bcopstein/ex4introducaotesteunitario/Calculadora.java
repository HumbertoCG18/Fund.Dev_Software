package com.bcopstein.ex4introducaotesteunitario;

public class Calculadora {
    public static int evaluate(String expression) { 
        int sum = 0; 
        for (String summand: expression.split("\\+")) 
          sum += Integer.valueOf(summand); 
        return sum; 
      } 
}
