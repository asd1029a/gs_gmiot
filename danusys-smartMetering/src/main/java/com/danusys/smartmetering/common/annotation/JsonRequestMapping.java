package com.danusys.smartmetering.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping
@ResponseBody
public @interface JsonRequestMapping {
	
	@AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] value() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "method")
    RequestMethod[] method() default {
    	RequestMethod.POST
    };

    @AliasFor(annotation = RequestMapping.class, attribute = "params")
    String[] params() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "headers")
    String[] headers() default {};

    @AliasFor(annotation = RequestMapping.class, attribute = "consumes")
    String[] consumes() default {
    	"application/json;charset=utf8"
    };

    @AliasFor(annotation = RequestMapping.class, attribute = "produces")
    String[] produces() default {
    	"application/json;charset=utf8"
    };
}