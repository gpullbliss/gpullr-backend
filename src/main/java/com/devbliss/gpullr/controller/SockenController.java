package com.devbliss.gpullr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SockenController {

  @RequestMapping("/socke")
  public String greeting() {
    return "socke";
  }
}
