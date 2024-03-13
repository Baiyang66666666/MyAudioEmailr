package com.com6103.email.cli;

import com.com6103.email.entity.Account;
import com.com6103.email.entity.Mail;
import com.com6103.email.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class EmailServiceImplTest {


    @Autowired
    EmailServiceImpl emailService;

    /**
     * To test whether we can initialise emails by the account
     */
    @ShellMethod (key = "ts initEmails")
    public void testInitEmails(){

        String name = "mq";
        String senderAddress = "crowds723@gmail.com";
        String receiverAddress = "mzhang122@sheffield.ac.uk";
        String password = "bbtzgijwwyrtfhkq";
        String smtp = "smtp.gmail.com";
        String imap = "imap.gmail.com";
        String deviceId = "202303281710-diive";

        Account account = new Account(name,senderAddress,receiverAddress,password,smtp,imap,deviceId);

        try{
            emailService.initEmails(account);
            var mailList = emailService.listReceivedEmails(account.getAddress());
            assert mailList.size() == 10;
            System.out.println("Pass");

        }catch (Exception e){
            System.out.println("Fail");
        }

    }

    /**
     * To test whether we can parse the content of the email
     */
    @ShellMethod (key = "ts browseEmail")
    public void testBrowseEmail(){
        String emailId = "1";
        //region
        String emailContent = """
                <div dir="ltr">12312312123213</div>
                """.trim();
        //endregion
        try{
            var mail = emailService.browseEmail(emailId);
            assert emailContent.equals(mail.getContent().trim());
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

    /**
     * To test whether we can sync the emails
     */
    @ShellMethod (key = "ts sync")
    public void testSyncEmails(){
        String name = "mq";
        String senderAddress = "mzhang122@sheffield.ac.uk";
        String receiverAddress = "crowds723@gmail.com";
        String password = "bbtzgijwwyrtfhkq";
        String smtp = "smtp.gmail.com";
        String imap = "imap.gmail.com";
        String deviceId = "202303281710-diive";

        Account account = new Account(name,senderAddress,receiverAddress,password,smtp,imap,deviceId);

        try {
          //  emailService.initEmails(account);
            List<Mail> mailListBefore = emailService.listReceivedEmails(receiverAddress);
            Mail mail = new Mail();
//            mail.setContent("Test content");
//            mail.setSubject("Test subject");
//            emailService.sendEmail(account, mail);
            emailService.syncEmails(account);
            List<Mail> mailListAfter = emailService.listReceivedEmails(receiverAddress);

            assert mailListBefore.size()<mailListAfter.size();
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

    /**
     * To test whether we can change the status to read
     */
    @ShellMethod (key = "ts changeStatus")
    public void testChangeStatus(){
        String name = "mq";
        String senderAddress = "crowds723@gmail.com";
        String receiverAddress = "mzhang122@sheffield.ac.uk";
        String password = "bbtzgijwwyrtfhkq";
        String smtp = "smtp.gmail.com";
        String imap = "imap.gmail.com";
        String deviceId = "202303281710-diive";

        Account account = new Account(name,senderAddress,receiverAddress,password,smtp,imap,deviceId);

        try{
            List<String> mailIdList = new ArrayList<>();
            mailIdList.add("1"); mailIdList.add("2");
            emailService.changeStatus(mailIdList);
            var mailList = emailService.listReceivedEmails(account.getAddress());
            List<String> readFlagList= new ArrayList<>();

            for (String mailId : mailIdList){
                readFlagList.add(getReadFlagByMailId(mailId,mailList));
            }

            for (String flag : readFlagList){
                assert flag.equals("1");
            }
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

    /**
     * Returns the read_flag if given mailList has a mail object with given mailId
     * @param mailId the mail id to search
     * @param mailList the mail list to search with a given mail id
     * @return read_flag of searched mail object by a mail id (if there are no mail with given mail id, returns null)
     */
    static String getReadFlagByMailId(String mailId, List<Mail> mailList){

        for (Mail mail : mailList){
            if((mail.getMail_id().equals(mailId))){
               return mail.getRead_flag();
            }
        }

        return null;
    }


    /**
     * To test whether we can delete an email
     */
    @ShellMethod (key = "ts deleteEmail")
    public void testDeleteEmail(){
        String name = "mq";
        String senderAddress = "crowds723@gmail.com";
        String receiverAddress = "mzhang122@sheffield.ac.uk";
        String password = "bbtzgijwwyrtfhkq";
        String smtp = "smtp.gmail.com";
        String imap = "imap.gmail.com";
        String deviceId = "202303281710-diive";

        Account account = new Account(name,senderAddress,receiverAddress,password,smtp,imap,deviceId);

        try{

            List<String> mailIdList = new ArrayList<>();
            mailIdList.add("1"); mailIdList.add("2");
            String userId = "4";
            var n = emailService.deleteEmail(mailIdList,userId);
            var mailList = emailService.listReceivedEmails(account.getAddress());

            int flag = 0;

            for(int i=0; i<mailList.size(); i++){
                for(String id : mailIdList){
                    if(mailList.get(i).getMail_id().equals(id)){
                        flag++;
                    }
                }
            }

            assert flag == 0;
            System.out.println("Pass");
        }catch (Exception e){
            System.out.println("Fail");
        }
    }

}
