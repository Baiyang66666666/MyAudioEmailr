package com.com6103.email.common;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.AccountSchedule;
import com.com6103.email.handler.SocketHandler;
import com.com6103.email.service.EmailService;
import com.com6103.email.service.UserService;
import com.com6103.email.service.VoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class TransferMailTask implements Runnable, DisposableBean {

    private TaskScheduler taskScheduler;
    private VoiceService voiceService;
    private EmailService emailService;
    private SocketHandler socketHandler;

    private UserService userService;
    private Account account;


    private ScheduledFuture<?> future;

    Logger logger = LoggerFactory.getLogger(ReadMailTask.class);

    @Autowired
    public TransferMailTask(VoiceService voiceService, EmailService emailService
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

    @Override
    public void run() {
        AccountSchedule accountSchedule = userService.getUserScheduleById(account.getUserId());
        voiceService.transferUnreadMail(account,accountSchedule.getVoice_type());
    }

    @Override
    public void destroy() throws Exception {
        if (future != null) {
            future.cancel(true);
            logger.info("Canceled previous scheduled task for account {}", account.getUserId());
        }
    }

    /**
     * Convert Mail to Voice in advance
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
