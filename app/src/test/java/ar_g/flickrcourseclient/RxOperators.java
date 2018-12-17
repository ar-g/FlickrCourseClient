package ar_g.flickrcourseclient;

import org.junit.Test;

import io.reactivex.Observable;

public class RxOperators {

  @Test
  public void map() {
    String greeting = "Hello";
    String yelling = greeting.toUpperCase();


    /*Observable<String> greeting = Observable.just("Hello");
    Observable<String> yelling = greeting.map(s -> s.toUpperCase());*/
  }

  @Test
  public void flatMap() {
    String greeting = "Hello, World!";
    String[] words = greeting.split(" ");

   /* Observable<String> greeting = Observable.just("Hello, World!");
    Observable<String[]> words = greeting.map(s -> s.split(" "));

    Observable<String> greeting = Observable.just("Hello, World!");
    Observable<Observable<String>> words =
      greeting.map(s -> Observable.fromArray(s.split(" ")));

    Observable<String> greeting = Observable.just("Hello, World!");
    Observable<String> words =
      greeting.flatMap(s -> Observable.fromArray(s.split(" ")));*/
  }

  @Test
  public void transformation() {

  }

  @Test
  public void combination() {

  }

}
