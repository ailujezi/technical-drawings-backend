package com.ritzjucy.technicaldrawingsbackend.exception;


public class Validation
{
    public static void validateNonNull(Object value, String name)
    {
        if (value == null)  {
            throw new ValidationException(name + " must not be null");
        }
    }
}
