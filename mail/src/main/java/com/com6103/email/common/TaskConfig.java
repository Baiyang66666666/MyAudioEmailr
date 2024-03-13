package com.com6103.email.common;


import com.com6103.email.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;


@Component
public class TaskConfig {
    private TaskScheduler taskScheduler;
    private SyncMailTask syncMailTask;
    private ReadMailTask readMailTask;

    private TransferMailTask transferMailTask;


    @Autowired
    public TaskConfig(SyncMailTask syncMailTask, TaskScheduler taskScheduler, ReadMailTask readMailTask, TransferMailTask transferMailTask) {
        this.syncMailTask = syncMailTask;
        this.taskScheduler = taskScheduler;
        this.readMailTask = readMailTask;
        this.transferMailTask = transferMailTask;
    }

    /**
     * Start Sync Task
     * @param account
     */
    public void startSyncMailTask(Account account) {
        CronTrigger cronTrigger = new CronTrigger("0 */10 * * * ?");
        syncMailTask.setAccount(account);
        taskScheduler.schedule(syncMailTask, cronTrigger);
    }

    /**
     * Start Read Mail Task
     * @param expression
     * @param account
     */
    public void startReadMailTask(String expression, Account account) {
        CronTrigger cronTrigger = new CronTrigger(expression);
        readMailTask.setAccount(account);
        readMailTask.startTask(cronTrigger);
    }

    /**
     * Start Transfer Mail Task
     * @param expression
     * @param account
     */
    public void startTransferMailTask(String expression, Account account){
        CronTrigger cronTrigger = new CronTrigger(expression);
        transferMailTask.setAccount(account);
        transferMailTask.startTask(cronTrigger);
    }
}
