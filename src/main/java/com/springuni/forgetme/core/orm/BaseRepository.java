package com.springuni.forgetme.core.orm;

import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface BaseRepository<E, K> extends Repository<E, K> {

  Optional<E> findById(K id);

  E save(E entity);

}
