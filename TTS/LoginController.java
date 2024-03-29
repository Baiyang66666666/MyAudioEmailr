package TTS;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.AccountSchedule;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    UserService userService;
    EmailService emailService;
    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }


    @GetMapping("/loading")
    public String loading(Model model) {
        return "loading";
    }

    @PostMapping("/deviceId")
    public String getDeviceId(HttpSession httpSession, @RequestBody Map<String, Object> rq) {
        httpSession.setAttribute("deviceId", rq.get("deviceId").toString());
        return "redirect:setAccount";
    }

    @GetMapping("/setAccount")
    public String setAccount(final ModelMap model, HttpSession httpSession) {
        var deviceId = httpSession.getAttribute("deviceId");
        var loginAccountList = userService.getAccountList(deviceId.toString());
        if (loginAccountList.size() == 0) {
            return "inputAccount";
        } else {
            model.addAttribute("loginAccountList", loginAccountList);
            return ("selectAccount");
        }
    }

    @PostMapping("/verify")
    public String accountVerify(final ModelMap model, HttpSession httpSession, String flag,
                                String address, String password, String smtp, String imap, String accountType, String userId) {
        var deviceId = httpSession.getAttribute("deviceId").toString();
        // Select account
        if (flag.equals("1")) {
            var account = userService.getUserById(userId);
            emailService.syncEmails(account);
            httpSession.setAttribute("userId", userId);
        } else if (flag.equals("0")) {
            String username = address.substring(0, address.indexOf("@"));
            Account account = new Account();
            account.setAddress(address);
            // Salt password when setting it
            passwordSalter pw_salter = new passwordSalter();
            String salted_pw = pw_salter.passwordSalter(password);
            // salted password is returned as string
            account.setPassword(salted_pw);
            account.setSmtp(smtp);
            account.setImap(imap);
            account.setAccountType(accountType);
            account.setDeviceId(deviceId);
            account.setUsername(username);
            if (userService.isUserExistsByEmail(address)) {
                emailService.syncEmails(account);
            } else {
                emailService.initEmails(account);
            }
            userService.insertAccount(account);

            // Used to get information automatically generated by the database such as account_id
            var account2 = userService.getUserByDevAndAddr(deviceId, address);
            var accountUserId = account2.getUserId();

            var accountSchedule = new AccountSchedule();
            accountSchedule.setUserId(accountUserId);
            accountSchedule.setVoice_type("co.uk");
            accountSchedule.setSchedule_time("00:00");

            userService.addAccountSchedule(accountSchedule);

            httpSession.setAttribute("userId", accountUserId);
        }
        return "redirect:/inbox/email";
    }

    /**
     * Click Add, when add a new account
     */
    @GetMapping("/addAccount")
    public String addAccount() {
        return "inputAccount";
    }
}
