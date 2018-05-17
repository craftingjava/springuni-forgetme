package com.springuni.forgetme.datahandler;

import com.springuni.forgetme.core.orm.AbstractEntity;
import java.util.UUID;
import javax.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DataHandler extends AbstractEntity {

  private String name;
  private UUID key;

  public DataHandler(String name) {
    this.name = name;
    resetKey();
  }

  public DataHandler(String name, UUID key) {
    this.name = name;
    this.key = key;
  }

  public UUID resetKey() {
    key = UUID.randomUUID();
    return key;
  }

}
