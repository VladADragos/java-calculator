package calc;

import java.util.*;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;

/*
 *   A calculator for rather simple arithmetic expressions
 *
 *   This is not the program, it's a class declaration (with methods) in it's
 *   own file (which must be named Calculator.java)
 *
 *   NOTE:
 *   - No negative numbers implemented
 */
public class Calculator {

    // Here are the only allowed instance variables!
    // Error messages (more on static later)
    final static String MISSING_OPERAND = "Missing or bad operand";
    final static String DIV_BY_ZERO = "Division with 0";
    final static String MISSING_OPERATOR = "Missing operator or parenthesis";
    final static String OP_NOT_FOUND = "Operator not found";

    // Definition of operators
    final static String OPERATORS = "+-*/^";

    // Method used in REPL
    double eval(String expr) {
        if (expr.length() == 0) {
            return NaN;
        }
        List<String> tokens = tokenize(expr);
        List<String> postfix = infix2Postfix(tokens);
        return evalPostfix(postfix);
    }

    // ------ Evaluate RPN expression -------------------

    double evalPostfix(List<String> postfix) {

        // TODO
        // create a stack
        Stack<Double> stack = new Stack<>();

        // Scan all characters one by one
        for (String s : postfix) {

            // If the scanned character is an operand (number here),
            // push it to the stack.
            if (!isSpecialChar(s)) {
                // double l = Double.parseDouble(s);
                stack.push(Double.parseDouble(s));
            }

            // If the scanned character is an operator, pop two
            // elements from stack apply the operator
            else {
                double val1 = stack.pop();
                double val2 = stack.pop();

                switch (s) {
                case "+":
                    stack.push(val2 + val1);
                    break;

                case "-":
                    stack.push(val2 - val1);
                    break;

                case "/":
                    stack.push(val2 / val1);
                    break;

                case "*":
                    stack.push(val2 * val1);
                    break;
                case "^":
                    stack.push(Math.pow(val2, val1));
                    break;
                }
            }
        }
        return Double.valueOf(stack.pop());
    }

    double applyOperator(String op, double d1, double d2) {
        switch (op) {
        case "+":
            return d1 + d2;
        case "-":
            return d2 - d1;
        case "*":
            return d1 * d2;
        case "/":
            if (d1 == 0) {
                throw new IllegalArgumentException(DIV_BY_ZERO);
            }
            return d2 / d1;
        case "^":
            return pow(d2, d1);
        }
        throw new RuntimeException(OP_NOT_FOUND);
    }

    // ------- Infix 2 Postfix ------------------------

    List<String> infix2Postfix(List<String> infix) {
        // initializing empty String for result
        List<String> result = new ArrayList<String>();

        // initializing empty stack
        Stack<String> stack = new Stack<>();

        for (String c : infix) {

            // System.out.println(c.equals("(") || c.equals(")"));
            // If the scanned character is an operand, add it to output.(number)
            if (!isSpecialChar(c)) {
                result.add(c);
            }

            // If the scanned character is an '(', push it to the stack.
            else if (c.equals("(")) {
                stack.push(c);
            }

            // If the scanned character is an ')', pop and output from the stack
            // until an '(' is encountered.
            else if (c.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    result.add(stack.pop());

                if (!stack.isEmpty() && !stack.peek().equals(")"))
                    new RuntimeException("invalid expression");
                // return null; // invalid expression
                else
                    stack.pop();
            } else // an operator is encountered
            {
                while (!stack.isEmpty() && !stack.peek().equals("(") && getPrecedence(c) <= getPrecedence(stack.peek())
                        && !c.equals("^")) {
                    result.add(stack.pop());
                }
                stack.push(c);
            }

        }

        // pop all the operators from the stack
        while (!stack.isEmpty()) {
            String popepd = stack.pop();
            if (popepd.equals("(")) {
                continue;
            }
            result.add(popepd);

        }
        return result;
    }

    int getPrecedence(String op) {
        if ("+-".contains(op)) {
            return 2;
        } else if ("*/".contains(op)) {
            return 3;
        } else if ("^".contains(op)) {
            return 4;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    Assoc getAssociativity(String op) {
        if ("+-*/".contains(op)) {
            return Assoc.LEFT;
        } else if ("^".contains(op)) {
            return Assoc.RIGHT;
        } else {
            throw new RuntimeException(OP_NOT_FOUND);
        }
    }

    enum Assoc {
        LEFT, RIGHT
    }

    // ---------- Tokenize -----------------------

    boolean isSpecialChar(String expr) {
        return (expr.equals("+") || expr.equals("-") || expr.equals("/") || expr.equals("*") || expr.equals("(")
                || expr.equals(")") || expr.equals("^"));
    }

    // List String (not char) because numbers (with many chars)
    List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<String>();

        String[] exprArr = expr.replaceAll("\\s", "").split("(?!^)");

        String number = "";

        for (String s : exprArr) {

            if (isSpecialChar(s)) {
                if (!number.isEmpty()) {
                    tokens.add(number);
                }
                tokens.add(s);
                number = "";
            } else {
                number += s;
            }
        }
        if (!number.isEmpty())
            tokens.add(number);
        return tokens;
    }

}
