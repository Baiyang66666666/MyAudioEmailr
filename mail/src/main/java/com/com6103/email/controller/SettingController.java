package com.com6103.email.controller;

import com.com6103.email.common.TaskConfig;
import com.com6103.email.dto.SettingDto;
import com.com6103.email.entity.AccountSchedule;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import com.com6103.email.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.ParseException;

@Controller
@RequestMapping("/setting")
public class SettingController {

    EmailService emailService;
    TaskConfig taskConfig;
    UserService userService;

    @Autowired
    public SettingController(EmailService emailService, UserService userService, TaskConfig taskConfig) {
        this.emailService = emailService;
        this.userService = userService;
        this.taskConfig = taskConfig;
    }

    /**
     * Starts the setting page
     * @param model a model to be set up the attributes
     * @param httpSession
     * @return
     * @throws ParseException
     */
    @GetMapping("/basicSetting")
    public String inbox(Model model, HttpSession httpSession) throws ParseException {
        var userId = httpSession.getAttribute("userId").toString();
        var account = userService.getUserById(userId);
        var accountSchedule = userService.getUserScheduleById(userId);
        var settingDto = new SettingDto();
        settingDto.setAddress(account.getAddress());
        settingDto.setUserId(userId);
        settingDto.setUserName(account.getUsername());

        settingDto.setVoiceType(accountSchedule.getVoice_type());
        var scheduleTimeStr = accountSchedule.getSchedule_time();
        settingDto.setScheduleTime(scheduleTimeStr);

        model.addAttribute("accountInfo",account);
        model.addAttribute("settingDto", settingDto);
        return "setting";
    }

    /**
     * Updates the schedule
     * @param model a model to be set up the attributes
     * @param userName username of the current user
     * @param address email address of the current user
     * @param userId user ID of the current user
     * @param scheduleTime scheduled time to read unread emails
     * @param voiceType voice type that emails are read with
     * @return
     * @throws ParseException
     */
    @PostMapping("/addOrUpdate")
    public String addOrUpdate(Model model, String userName, String address, String userId, String scheduleTime, String voiceType) throws ParseException {
        boolean scheduleExist = userService.isScheduleExist(userId);
        var account = userService.getUserById(userId);
        // Update
        AccountSchedule accountSchedule = new AccountSchedule();
        accountSchedule.setUserId(userId);
        accountSchedule.setVoice_type(voiceType);
        accountSchedule.setSchedule_time(scheduleTime);
        if (scheduleExist) {
            //update
            int i = userService.UpdateAccountSchedule(accountSchedule);
        } else {
            //add
            int i = userService.addAccountSchedule(accountSchedule);
        }
        if(scheduleTime.length()!=0){
            var cronExpression = Utils.convertToCron(scheduleTime);
            String timeMinusOneHour = Utils.convertToCronMinus(scheduleTime);
            taskConfig.startReadMailTask(cronExpression,account);
            taskConfig.startTransferMailTask(timeMinusOneHour,account);

        }
        SettingDto settingDto = new SettingDto();
        settingDto.setUserId(userId);
        settingDto.setUserName(userName);
        settingDto.setAddress(address);
        settingDto.setVoiceType(accountSchedule.getVoice_type());

        String scheduleTimeStr = accountSchedule.getSchedule_time();
        settingDto.setScheduleTime(scheduleTimeStr);

        settingDto.setScheduleTime(scheduleTimeStr);
        model.addAttribute("accountInfo",account);
        model.addAttribute("settingDto", settingDto);
        return "setting";
    }
}
