package ar_g.flickrcourseclient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;

public class Rx {
  public interface Observer<T> {

    void onSubscribe(@NonNull Disposable d);

    void onNext(@NonNull T t);

    void onError(@NonNull Throwable e);

    void onComplete();
  }

  @Test
  public void creation() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> subscriber) {
        System.out.println("create");
        subscriber.onNext(5);
        subscriber.onNext(6);
        subscriber.onNext(7);
        subscriber.onComplete();
        System.out.println("Complete");
      }
    }).subscribe(new Consumer<Integer>() {
      @Override public void accept(Integer integer) throws Exception {
        System.out.println(integer);
      }
    });


    System.out.println("done");
  }

  @Test
  public void scheduling() throws InterruptedException {
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> subscriber) {
        System.out.println("Observable thread " + Thread.currentThread().getName());
        System.out.println("create");
        subscriber.onNext(5);
        subscriber.onNext(6);
        subscriber.onNext(7);
        subscriber.onComplete();
        System.out.println("Complete");
      }
    }).subscribeOn(Schedulers.io())
      .observeOn(Schedulers.newThread())
      .subscribe(new Consumer<Integer>() {
        @Override public void accept(Integer integer) throws Exception {
          System.out.println("Subscriber thread " + Thread.currentThread().getName());

          System.out.println(integer);
        }
      });


    Thread.sleep(4000);
  }

  @Test
  public void observableAndObserver() {
    Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> subscriber) {
        System.out.println("create");
        subscriber.onNext(5);
        subscriber.onError(new RuntimeException());
        subscriber.onNext(6);
        subscriber.onNext(7);
        System.out.println("Complete");
      }
    }).subscribe(new io.reactivex.Observer<Integer>() {
      @Override public void onSubscribe(Disposable d) {
        System.out.println("onSubscribe");
      }
      @Override public void onNext(Integer integer) {
        System.out.println(integer);
      }
      @Override public void onError(Throwable e) {
        System.out.println("onError");
      }
      @Override public void onComplete() {
        System.out.println("onComplete");
      }
    });
  }

  @Test
  public void subscription() throws InterruptedException {
    Disposable subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
      @Override public void subscribe(ObservableEmitter<Integer> subscriber) throws InterruptedException {
        System.out.println("create");
        subscriber.onNext(5);
        subscriber.onNext(6);
        subscriber.onNext(7);
        System.out.println("Complete");
      }
    })
      .delay(200, TimeUnit.SECONDS)
      .subscribeOn(Schedulers.io())
      .subscribe(new Consumer<Integer>() {
        @Override public void accept(Integer integer) throws Exception {
          System.out.println(integer);
        }
      });
    subscribe.dispose();


    Thread.sleep(1000);
  }

  @Test
  public void iterableVsStream() {
    List<Integer> numbers = Arrays.asList(2, 112, 211, 33, 43, 53, 63, 7, 8);
    System.out.println(numbers);

    Observable.fromIterable(numbers)
      .concatMap((Function<Integer, ObservableSource<Integer>>) integer -> Observable.just(integer)
        .delay(1000, TimeUnit.MILLISECONDS, Schedulers.trampoline()))
      .subscribe(integer -> System.out.println(integer));
  }

  @Test
  public void eagerVsLazy() {
    final List<Integer> numbers = Arrays.asList(2, 112, 211, 33, 43, 53, 63, 7, 8);
    System.out.println("Eager");
    System.out.println(numbers);

    System.out.println("Lazy");
    Observable<Integer> streamOfIntegers = Observable.create(emitter -> {
      for (Integer number : numbers) {
        emitter.onNext(number);
      }
      emitter.onComplete();
      System.out.println();
    });
    System.out.println("done");
  }


  @Test
  public void transformationsOverTime() {
    List<String> strings = Arrays.asList("23", "8", "ndf", "34");

    List<Integer> numbers = new ArrayList<>();
    for (int i = 0; i < strings.size(); i++) {
      try {
        numbers.add(Integer.parseInt(strings.get(i)));
      } catch (NumberFormatException ignored) {
      }
    }
    System.out.println(numbers);


    Observable.fromIterable(strings)
      .concatMap(s -> Observable.just(s)
        .delay(1000, TimeUnit.MILLISECONDS, Schedulers.trampoline())
        .map(string -> {
          try {
            return Integer.parseInt(string);
          } catch (NumberFormatException ignored){
            return -1;
          }
        })
      )
      .filter(number -> number != -1)
      .subscribe(System.out::println);
  }
}