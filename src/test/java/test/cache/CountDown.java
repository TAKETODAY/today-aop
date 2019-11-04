package test.cache;

import java.util.concurrent.CountDownLatch;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.StandardApplicationContext;
import test.demo.domain.User;
import test.demo.service.UserService;

public class CountDown {

    private static final int NUM = 3000;

    private static CountDownLatch latch = new CountDownLatch(NUM);

    //	static ApplicationContext applicationContext = new StandardApplicationContext("", "test","cn.taketoday");
    static ApplicationContext applicationContext = new StandardApplicationContext("", "");
    static UserService bean = applicationContext.getBean(UserService.class);
    static User user = new User();

    static {
        user.setEmail("taketoday@foxmail.com");
        user.setPassword("130447AD788ACD4E5A06BF83136E78CB");
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        //		bean.login(user);
        for (int i = 0; i < NUM; i++) {
            new Thread(new Thread_()).start();
            latch.countDown();
        }
        latch.await();
        System.err.println(System.currentTimeMillis() - start);
    }

    static class Thread_ implements Runnable {

        @Override
        public void run() {
            try {
                latch.await();
            }
            catch (InterruptedException e) {}

            bean.login(user);
        }
    }

}
