package jcwl;

import reactor.core.publisher.Mono;

public class MyClass {

      Mono myMethod() {
          System.out.println("execute myMethod");

          return Mono.just("Value");
      }
  }
