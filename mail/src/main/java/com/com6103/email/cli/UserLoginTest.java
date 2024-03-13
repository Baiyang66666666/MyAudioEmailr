package com.com6103.email.cli;

import com.com6103.email.controller.LoginController;
import com.com6103.email.entity.AccountSchedule;
import com.com6103.email.service.UserService;
import com.com6103.email.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("user")
public class UserLoginTest {

    @Autowired
    UserService userService;

    /**
     * To test whether we can get the user by user id
     */
    @ShellMethod(key = "ts userid")
    public void testLogin() {
        var user = userService.getUserById("3");
        try{
            assert "3".equals(user.getUserId());
            System.out.println("Pass");
        }catch (java.lang.AssertionError e){
            System.out.println("Failure");
        }

    }

    /**
     * To test whether we can get the user by device id and address
     */
    @ShellMethod (key = "ts getUserByDevAndAddr")
    public void testGetUserByDev(){
        String deviceId = "202305021622-aemqo";
        String address = "crowds723@gmail.com";
        var user = userService.getUserByDevAndAddr(deviceId,address);
        try{
            assert deviceId.equals(user.getDeviceId()) && address.equals(user.getAddress());
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Failure");
        }

    }

    /**
     * To test whether we can detect the existence of the user by given address
     */
    @ShellMethod (key = "ts isExist")
    public void testisExist(){
        String address1 = "crowds723@gmail.com";
        String address2 = "no exist";
        try{
            var flag1 = userService.isUserExistsByEmail(address1);
            var flag2 = userService.isUserExistsByEmail(address2);
            assert flag1 == true && flag2 == false;
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

    /**
     * To test whether we can get the schedule of a user by the user ID
     */
    @ShellMethod (key = "ts getUserSchedule")
    public void testGetUserSch(){
        try{
            //TODO: There is something wrong with initialising or getting the account schedule

            AccountSchedule accountSchedule = new AccountSchedule();
            String userId = "3";
            accountSchedule.setSchedule_time("12:00");
            accountSchedule.setUserId(userId);

            int i = userService.addAccountSchedule(accountSchedule);

            var schedule = userService.getUserScheduleById(userId); //null
            assert accountSchedule.getSchedule_time().equals(schedule.getSchedule_time());
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

    /**
     * To test whether we can add schedule and detect the existence of the schedule of a user by the user ID
     */
    @ShellMethod(key = "ts isScheduleExist")
    public void testIsScheduleExist(){
        try{
            AccountSchedule accountSchedule = new AccountSchedule();
            String userId = "3";
            accountSchedule.setUserId(userId);
            int i = userService.addAccountSchedule(accountSchedule);

            assert userService.isScheduleExist(userId);
            System.out.println("Pass");

        }catch(Exception e){
            System.out.println("Fail");
        }
    }

    /**
     * To test we can update schedule
     */
    @ShellMethod (key = "ts updateSchedule")
    public void testUpdateSchedule(){
        try{
            //TODO: There is something wrong with initialising or getting the account schedule

            AccountSchedule accountSchedule1 = new AccountSchedule();
            accountSchedule1.setSchedule_time("12:00");
            accountSchedule1.setUserId("3");
            int i = userService.addAccountSchedule(accountSchedule1);

            AccountSchedule accountSchedule2 = new AccountSchedule();
            accountSchedule2.setSchedule_time("16:00");
            accountSchedule2.setUserId("3");
            i = userService.UpdateAccountSchedule(accountSchedule2);

            assert userService.getUserScheduleById("3").getSchedule_time().equals("16:00");
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

}