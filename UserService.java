package com.com6103.email.service;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.AccountSchedule;

import java.util.List;

public interface UserService {
    /**
     Inserts a new account into the database.
     @param account the account to be inserted
     @return the number of rows affected by the insert statement
     */
    public int insertAccount(Account account);
    /**

     Retrieves a list of accounts associated with a specific device ID.
     @param deviceId the device ID to retrieve accounts for
     @return a list of accounts associated with the specified device ID
     */
    public List<Account> getAccountList(String deviceId);
    /**

     Retrieves an account based on the specified user ID.
     @param userId the ID of the user to retrieve the account for
     @return the account associated with the specified user ID
     */
    public Account getUserById(String userId);
    /**

     Retrieves an account based on the specified device ID and email address.
     @param deviceId the ID of the device associated with the account
     @param address the email address associated with the account
     @return the account associated with the specified device ID and email address
     */
    public Account getUserByDevAndAddr(String deviceId, String address);
    /**

     Checks if an account exists for the specified email address.
     @param address the email address to check for
     @return true if an account exists for the specified email address, false otherwise
     */
    public boolean isUserExistsByEmail(String address);
    /**

     Retrieves an account schedule based on the specified user ID.
     @param userId the ID of the user to retrieve the account schedule for
     @return the account schedule associated with the specified user ID
     */
    public AccountSchedule getUserScheduleById(String userId);
    /**

     Checks if an account schedule exists for the specified user ID.
     @param userId the ID of the user to check for an account schedule
     @return true if an account schedule exists for the specified user ID, false otherwise
     */
    public boolean isScheduleExist(String userId);
    /**

     Inserts a new account schedule into the database.
     @param accountSchedule the account schedule to be inserted
     @return the number of rows affected by the insert statement
     */
    public int addAccountSchedule(AccountSchedule accountSchedule);
    /**

     Updates an existing account schedule in the database.
     @param accountSchedule the account schedule to be updated
     @return the number of rows affected by the update statement
     */
    public int UpdateAccountSchedule(AccountSchedule accountSchedule);
    /**

     Deletes an account based on the specified user ID.
     @param userId the ID of the user to delete the account for
     @return the number of rows affected by the delete statement
     */
    public int deleteAccountById(Integer userId);
    /**

     Deletes an account schedule based on the specified user ID.
     @param userId the ID of the user to delete the account schedule for
     @return the number of rows affected by the delete statement
     */
    public int deleteScheduleById(Integer userId);

}
