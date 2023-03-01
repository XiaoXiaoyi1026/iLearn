import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 使用BCrypt对密码进行加密, 单向Hash加密算法, 每一次加密都使用随机盐对明文进行加密
 * 密文部分(60位):
 * $2a$: 代表使用的加密算法是BCrypt
 * $10$: 代表cost = 10, 即加密一共进行了2^10次
 * $最后一部分则是明文(31位)+盐(22位)对应的密文
 *
 * @author xiaoxiaoyi
 * @version 1.0
 * @description BCrypt加密算法测试
 * @date 2/26/2023 2:00 PM
 */
public class BCryptPasswordEncoderTest {

    @Test
    public void testBCryptPasswordEncoder() {
        String password = "1026";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        for (int i = 0; i < 10; i++) {
            // 每一次加密生成的hash密码都不一样
            String hashPassword = bCryptPasswordEncoder.encode(password);
            System.out.println(hashPassword);
            // 虽然每次密文都不一样, 但是都可以通过校验
            boolean matches = bCryptPasswordEncoder.matches(password, "$2a$10$aIN2OQJrwGyFJ1/h5UJIqunxtMJ8SY9PY0QUDtVb3ud5G19BkAlJC");
            System.out.println(matches);
        }
    }



}
