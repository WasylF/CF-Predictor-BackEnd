package com.github.wslf.codeforces.controllers;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {

  @Data
  class Echo {
    final String response;
  }

  @RequestMapping("/echo")
  public Echo echo(@RequestParam(value = "param", defaultValue = "Hi!") String param) {
    return new Echo(param);
  }
}
