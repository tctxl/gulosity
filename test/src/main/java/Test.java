import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class Test {

    public void init() throws InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
    public static void main(String[] args) throws InterruptedException {
        new Test().init();
    }

}
