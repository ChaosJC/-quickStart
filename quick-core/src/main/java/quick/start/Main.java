package quick.start;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "quick.start.*")
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}