package com.com6103.email.common;

import com.com6103.email.entity.Account;
import com.com6103.email.handler.SocketHandler;
import com.com6103.email.entity.AccountSchedule;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import com.com6103.email.service.VoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Component
public class ReadMailTask implements Runnable, DisposableBean {

    private ConfigurableListableBeanFactory beanFactory;
    private TaskScheduler taskScheduler;
    private VoiceService voiceService;
    private EmailService emailService;
    private SocketHandler socketHandler;

    private UserService userService;
    private Account account;
    private ScheduledFuture<?> future;

    Logger logger = LoggerFactory.getLogger(ReadMailTask.class);

    @Autowired
    public ReadMailTask(VoiceService voiceService, EmailService emailService
            , UserService userService,SocketHandler socketHandler, TaskScheduler taskScheduler) {
        this.voiceService = voiceService;
        this.emailService = emailService;
        this.userService = userService;
        this.taskScheduler = taskScheduler;
        this.socketHandler = socketHandler;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * TTS needs to be started or it will always return null
     */
    @Override
    public void run() {
        AccountSchedule accountSchedule = userService.getUserScheduleById(account.getUserId());
        var pathList = voiceService.playUnreadEmails(account,accountSchedule.getVoice_type());
        String destination = "/schedule/" + account.getUserId();
        logger.info("Send msg to " + destination);
        logger.info("Msg content, pathList.size() : " + pathList.size());
        socketHandler.sendMessageToUser(pathList, account.getUserId());
    }

    @Override
    public void destroy() throws Exception {
        if (future != null) {
            future.cancel(true);
            logger.info("Canceled previous scheduled task for account {}", account.getUserId());
        }
    }

    /**
     * Start the task and cancel last task.
     * @param cronTrigger
     */
    public void startTask(CronTrigger cronTrigger) {
        if (future != null) {
            future.cancel(true);
            logger.info("Canceled previous scheduled task for account {}", account.getUserId());
        }
        future = taskScheduler.schedule(this, cronTrigger);
        logger.info("Scheduled task for account {} with expression {}", account.getUserId(), cronTrigger.getExpression());
    }
}
