package com.springuni.forgetme.core.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springuni.forgetme.core.orm.AbstractEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractResource extends ResourceSupport {

  @JsonProperty("id")
  private String resourceId;

  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;
  private Integer version;

  public AbstractResource(AbstractEntity entity) {
    Assert.notNull(entity, "entity cannot be null");

    this.resourceId = Optional.ofNullable(entity.getId()).map(String::valueOf).orElse(null);
    this.createdDate = entity.getCreatedDate();
    this.lastModifiedDate = entity.getLastModifiedDate();
    this.version = entity.getVersion();
  }

}
