package com.springuni.forgetme.datahandler;

import com.springuni.forgetme.core.orm.AbstractEntity;
import java.util.UUID;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class DataHandler extends AbstractEntity {

  private String name;
  private UUID key;

  public DataHandler(String name) {
    this.name = name;
    resetKey();
  }

  public UUID resetKey() {
    key = UUID.randomUUID();
    return key;
  }

}
