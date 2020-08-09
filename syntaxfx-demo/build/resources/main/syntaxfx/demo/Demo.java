package com.example;

import java.util.*;

/**
 * This is an example Java class.
 * @author John Doe
 */
public class Foo extends Bar implements Baz {

    /*
     * Multi-line comment
     */
    public static void main(String[] args) {
        //Single-line comment
        for (String arg : args) {
            if (arg.length() != 0)
                System.out.println(arg);
            else
                System.err.println("Warning: empty string as argument");
        }
    }

}
