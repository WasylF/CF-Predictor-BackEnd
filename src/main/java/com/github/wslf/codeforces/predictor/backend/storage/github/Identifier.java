package com.github.wslf.codeforces.predictor.backend.storage.github;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Identifier {
  String repository;
  String branch;
  String path;
  Type type;
}
