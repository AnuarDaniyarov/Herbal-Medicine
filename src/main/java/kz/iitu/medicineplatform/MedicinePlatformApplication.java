package kz.iitu.medicineplatform;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicinePlatformApplication {

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir"))
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();
            // Dotenv-java 3.x автоматически устанавливает переменные в System.getenv
        } catch (Exception e) {
            // .env файл не найден или ошибка чтения — продолжаем работу
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MedicinePlatformApplication.class, args);
    }

}
