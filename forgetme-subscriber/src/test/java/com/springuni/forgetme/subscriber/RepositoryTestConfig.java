package com.springuni.forgetme.subscriber;

import com.springuni.forgetme.core.orm.BaseRepositoryTestConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Dummy configuration required by {@code @DataJpaTest}. It needs to be one level higher than
 * {@code @Entity} classes and/or {@code Repository} interfaces.
 */
@SpringBootApplication
public class RepositoryTestConfig extends BaseRepositoryTestConfig {

}
