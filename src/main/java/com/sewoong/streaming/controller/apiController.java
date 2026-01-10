package com.sewoong.streaming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class apiController {

  @GetMapping("/api/test")
  public String test() {
    return "test!";
  }

  
}
