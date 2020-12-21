package com.binggr.glmall.search.thread;

import java.util.concurrent.*;

/**
 * @author: bing
 * @date: 2020/12/2 16:45
 */

public class ThreadTest {
    public static  ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");

//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果："+i);
//        },service);


        //方法完成后的感知
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).whenComplete((res,exception)->{
//            System.out.println("异步任务完成了......结果是："+res+"异常是："+exception);
//        }).exceptionally(throwable->{
//            //感知异常同时返回默认值
//            return 10;
//        });

        //方法执行完成后的处理 无论成功失败
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).handle((res,thr)->{
//            if(res!=null){
//                return  res*2;
//            }
//            if(thr!=null){
//                return 0;
//            }
//            return 0;
//        });

        /**
         * 线程串行化  Async异步
         * 1）、thenRun 不能获取到上一步的执行结果，无返回值
         *  .thenRunAsync(()->{
         *             System.out.println("任务2启动了...");
         *         },service);
         * 2）、thenAccept能接收上一步结果，无返回值
         * .thenAcceptAsync(res->{
         *             System.out.println("任务2启动了...");
         *         },service);
         * 3）、thenApply能接收上一步结果，有返回值
         */
//        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).thenApplyAsync(res -> {
//            System.out.println("任务2启动了...");
//            return "Hello" + res;
//        }, service);

        /**
         * 两个都完成
         */
//        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("任务1结束：" + i);
//            return i;
//        }, service);
//
//        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程：" + Thread.currentThread().getId());
//            try {
//                Thread.sleep(3000);
//                System.out.println("任务2结束：" );
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello";
//        }, service);

//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务3开始!...");
//        },service);

//        future01.thenAcceptBothAsync(future02,(f1,f2)->{
//            System.out.println("任务3开始!...之前的结果"+f1+"-->>"+f2);
//        },service);
//        CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
//            return f1 + ":" + f2 + "->World";
//        }, service);

        /**
         * 两个完成一个 就执行任务3
         * runAfterEitherAsync:不感知结果，自己也无返回值
         * acceptEitherAsync 感知结果，无返回值
         * applyToEitherAsync 感知结果，有返回值
         */
//        future01.runAfterEitherAsync(future02,()->{
//            System.out.println("任务3开始...");
//        },service);

//        future01.acceptEitherAsync(future02,(res)->{
//            System.out.println("任务3开始..."+res);
//        },service);
//        CompletableFuture<String> future = future01.applyToEitherAsync(future02, (res) -> {
//            System.out.println("任务3开始..." + res);
//            return res.toString() + ">>哈哈";
//        }, service);

        /**
         * 多任务组合
         *
         */

        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查看商品的图片信息");
            return "hello.jpg";
        }, service);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查看商品的属性");
            return "黑色+256G";
        }, service);

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("查看商品介绍");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "华为";
        }, service);

//        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
//        allOf.get();//等待所有结果完成
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);

        System.out.println("main...end..."+anyOf.get());

    }

    public  void thread(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 四种线程创建方法
         * 1、继承Thread
         *         Thread01 thread01 = new Thread01();
         *         thread01.start();
         * 2、实现Runnable接口
         *         Runnable01 runnable01 = new Runnable01();
         *         new Thread(runnable01).start();
         * 3、实现callable接口 + FutureTask JDK1.5之后 允许返回值 FutureTask也接受Runnable
         *         FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
         *         new Thread(integerFutureTask).start();
         *         //阻塞 等待整个完成，获得返回结果
         *         Integer integer = integerFutureTask.get();
         *         System.out.println("main...end..."+integer);
         * 4、线程池[ExecutorService]
         *          给线程池提交任务
         *          service.execute(new Runnable01());
         *          1、创建
         *              1）、Executors
         *              2）、new ThreadPoolExecutor(); 原生
         *
         *          Future: 可以获取异步结果
         *
         * 区别:
         *     1、2 不能得到返回值。3可以得到返回值
         *     1、2、3都不能资源控制 4可以控制资源，性能稳定。
         *
         *     线程池作用 降低资源消耗 提高响应速度 提高线程的可管理性
         */

        //我们以后在业务代码中，以上三种启动线程的方式都不用。应该将所有的多线程异步任务都交给线程池执行。资源控制
        //当前系统只有一两个，每个异步任务，提交给线程池让他自己去执行
        /**
         * 七大参数
         * int corePoolSize：核心线程数【一直存在，除非设置allowCoreThreadTimeOut 超时】，线程创建好以后就准备就绪的线程数，就等待来接收异步任务去执行
         *            5个  Thread thread = new Thread();  thread.start();
         * int maximumPoolSize：【200】最大线程数量；控制资源
         * long keepAliveTime：存活时间。如果当前正在运行的线程数量大于核心数量，释放空闲线程（maximumPoolSize - corePoolSize）。只要线程空闲时间大于指定的keepAliveTime；
         * TimeUnit unit：时间单位
         * BlockingQueue<Runnable> workQueue：阻塞队列。如果任务有很多，就会将目前多的任务放在队列里面。只要线程空闲，就会去队列里取出新的任务。
         * ThreadFactory threadFactory：线程的创建工厂 默认的
         * RejectedExecutionHandler handler：如果队列满了，按照我们指定的拒绝策略，拒绝执行任务
         *
         * 工作顺序：
         * 1）、线程池创建，准备好core数量的核心线程，准备接受任务
         * 1.1、核心线程满了，就将再进来的任务放入阻塞队列中。空闲的core会自己去阻塞队列获取任务执行
         * 1.2、阻塞队列满了，就会开新线程执行，最大只能开到max指定的数量
         * 1.3、max满了就用RejectedExecutionHandler拒绝任务
         * 1.4、max都执行了，有很多空闲。在指定的时间keepAliveTime以后，释放max-core这些线程
         *      new LinkedBlockingDeque<>() 默认Integer的最大值。内存不够。
         *
         * 一个线程池 core 7 max 20 queue 50 100并发 执行流程
         * 7个立即执行，50个进入队列，再开13个线程进行执行。剩下的30个就使用拒绝策略。 一般为丢弃
         * 如果不想抛弃还要执行，CallerRunsPolicy：询问的方式执行
         */

        /**
         * 常见的线程池
         * Executors.newCachedThreadPool() 核心线程0    所有都可回收 灵活回收空闲线程
         * Executors.newFixedThreadPool()  核心固定大小 core=max：都不可以回收
         * Executors.newScheduledThreadPool() 定时任务的线程池
         * Executors.newSingleThreadExecutor() 单线程的线程池，后台从队列里面获取任务，挨个执行
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


        System.out.println("main...start...");

        System.out.println("main...end...");
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果："+i);
        }
    }

    public static class Runnable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果："+i);
        }
    }

    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果："+i);
            return i;
        }
    }
}
