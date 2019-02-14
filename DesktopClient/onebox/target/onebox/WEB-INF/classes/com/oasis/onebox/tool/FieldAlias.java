package com.oasis.onebox.tool;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldAlias {
    String alias() default "";
}
