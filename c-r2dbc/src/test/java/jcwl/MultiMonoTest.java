//package jcwl;
//
//import org.reactivestreams.Subscriber;
//import reactor.core.CorePublisher;
//import reactor.core.CoreSubscriber;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.util.function.Tuple3;
//
//import java.time.Duration;
//
//public class MultiMonoTest {
//
//        public void processData(Object...params)
//        {
//            Webservice webservice = getWebservice();
//            // As soon as Mono<Masterdata_A> emits its result AND Flux<Masterdata_B> emits its first result than the first inner flatMap should be executed
//            // to fetch some extra data from the service based on the actual masterdata.
//            // For building the ProcessingResult we need access to all data available in the actual context.
//            webservice.getMonoMasterdata_A().<Masterdata_A>subscribe(( masterdataA) -> {
//                        webservice.getFluxMasterdata_B()
//                                .delayElements(Duration.ofSeconds(1))
//                                .flatMap((MasterdataRecord_B masterdataB) -> {
//                                    Mono<Data_A> monoA = webservice.getMonoData_A(masterdataA);
//                                    Mono<Data_B> monoB = webservice.getMonoData_B(masterdataB);
//                                    Mono<Data_C> monoC = webservice.getMonoData_C(masterdataA, masterdataB);
//                                    // wait for result of all Monos
//                                    return Mono.zip(monoA, monoB, monoC);
//                                })
//                                .flatMap((Tuple3<Data_A, Data_B, Data_C> data) -> {
//                                    Data_A dataA = data.getT1();
//                                    Data_B dataB = data.getT2();
//                                    Data_C dataC = data.getT3();
//
//                                    // create result from masterdataA, masterdataB, dataA, dataB, dataC
//                                    ProcessingResult result = new ProcessingResult();
//                                    return Mono.just(result);
//                                })
//                                .subscribe(processingResult -> {
//                                    // store result to db/filesystem
//                                });
//                    });
//
//    }
//
//    private Webservice<Masterdata_A> getWebservice() {
//            return new Webservice<Masterdata_A>();
//    }
//
//    private class Webservice<T> {
//        public  CorePublisher<T> getMonoMasterdata_A() {
//            return new CorePublisher<>() {
//                @Override
//                public void subscribe(CoreSubscriber<? super T> subscriber) {
//
//                }
//
//                @Override
//                public void subscribe(Subscriber<? super T> s) {
//
//                }
//            };
//        }
//
//        public Flux<Object> getFluxMasterdata_B() {
//        }
//
//        public Mono<Data_A> getMonoData_A(Masterdata_A masterdataA) {
//            return Mono.just(new Data_A());
//        }
//
//        public Mono<Data_B> getMonoData_B(MasterdataRecord_B masterdataB) {
//            return Mono.just(new Data_B());
//        }
//
//        public Mono<Data_C> getMonoData_C(Masterdata_A masterdataA, MasterdataRecord_B masterdataB) {
//            return Mono.just(new Data_C());
//        }
//    }
//
//    private class MasterdataRecord_B {
//    }
//
//    private class Data_A {
//    }
//
//    private class Data_B {
//    }
//
//    private class Data_C {
//    }
//
//    private class ProcessingResult {
//    }
//
//    private class Masterdata_A {
//    }
//}
