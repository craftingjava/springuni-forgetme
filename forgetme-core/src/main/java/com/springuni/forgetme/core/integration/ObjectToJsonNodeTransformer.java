package com.springuni.forgetme.core.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.transformer.GenericTransformer;

@Slf4j
@RequiredArgsConstructor
public class ObjectToJsonNodeTransformer implements GenericTransformer<Object, JsonNode> {

  private final ObjectMapper objectMapper;

  @Override
  public JsonNode transform(Object json) {
    if (json == null) {
      return null;
    }

    try {
      if (json instanceof String) {
        return this.objectMapper.readTree((String) json);
      } else if (json instanceof byte[]) {
        return this.objectMapper.readTree((byte[]) json);
      } else if (json instanceof File) {
        return this.objectMapper.readTree((File) json);
      } else if (json instanceof URL) {
        return this.objectMapper.readTree((URL) json);
      } else if (json instanceof InputStream) {
        return this.objectMapper.readTree((InputStream) json);
      } else if (json instanceof Reader) {
        return this.objectMapper.readTree((Reader) json);
      } else {
        throw new IllegalArgumentException("unsupported argument class: " + json.getClass());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
