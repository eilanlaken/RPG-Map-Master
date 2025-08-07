package com.heavybox.jtix.application;

import java.lang.reflect.Field;

// TODO: make a robust logger that supports reflection etc.
public class AppLogger {

    public static void print(Object variable) {
        try {
            // Get the stack trace of the current thread
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

            // Get the caller of the print method (2nd element in the stack trace)
            StackTraceElement caller = stackTrace[2];
            String className = caller.getClassName();
            String methodName = caller.getMethodName();

            // Load the caller's class
            Class<?> clazz = Class.forName(className);

            // Find the variable's field in the caller's class
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true); // Access private fields
                if (field.get(null) == variable) { // Check if this is the same variable
                    System.out.println(field.getName() + ": " + variable);
                    return;
                }
            }
            System.out.println(variable);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to retrieve variable name");
        }
    }

}
