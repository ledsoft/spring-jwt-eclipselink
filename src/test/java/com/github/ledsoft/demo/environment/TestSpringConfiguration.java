package com.github.ledsoft.demo.environment;

import com.github.ledsoft.demo.SpringJwtEclipselinkDemo;
import com.github.ledsoft.demo.service.SystemInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Exclude {@link SystemInitializer} from test configurations.
 */
@ComponentScan(basePackageClasses = SpringJwtEclipselinkDemo.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SystemInitializer.class)})
public class TestSpringConfiguration {
}
