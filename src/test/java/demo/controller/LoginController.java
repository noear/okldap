package demo.controller;

import demo.dso.service.LoginService;
import demo.model.User;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Result;

/**
 * @author noear
 */
@Controller
public class LoginController {

    @Inject
    LoginService loginService;

    @Mapping("/login.do")
    public Result login(String userName, String passWord) throws Exception {
        User user = loginService.tryLogin1(userName, passWord);

        if (user == null) {
            return Result.failure();
        } else {
            return Result.succeed(user);
        }
    }
}
