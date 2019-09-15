package com.github.wslf.codeforces.predictor.backend.storage.github;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Type;

@Data
@AllArgsConstructor
public class Identifier {
  String repository;
  String branch;
  String path;
  Type type;
}
