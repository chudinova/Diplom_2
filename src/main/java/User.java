import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder
public class User {

    private String email;
    private String password;
    private String name;

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Step("Create user with random credentials")
    public static User getRandom() {

        final String email = RandomStringUtils.randomAlphabetic(8) + "@mail.ru";
        final String password = RandomStringUtils.randomAlphabetic(8);
        final String name = RandomStringUtils.randomAlphabetic(8);

        Allure.addAttachment("Login", email);
        Allure.addAttachment("Password", password);
        Allure.addAttachment("Name", name);

        return new User(email, password, name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getRandomEmail() {

        return RandomStringUtils.randomAlphabetic(8) + "@mail.ru";
    }

    public static String getRandomData() {

        return RandomStringUtils.randomAlphabetic(8);
    }
}