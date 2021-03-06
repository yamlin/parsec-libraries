// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.

package com.yahoo.parsec.constraint.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The javax.validation MsisdnComm annotation.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = MsisdnCommValidator.class)
public @interface MsisdnComm {

    /**
     * the default error message.
     */
    String message() default "invalid MsisdnComm";

    /**
     * the class groups to apply for validation.
     */
    Class<?>[] groups() default {
    };

    /**
     * the annotation payload.
     */
    Class<? extends Payload>[] payload() default {
    };
}

