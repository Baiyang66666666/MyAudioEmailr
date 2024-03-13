# README

This is the branch of doc. It is used to store the documents organized during the development process.

For other team-related documents, please refer to [Google Drive](https://drive.google.com/drive/folders/1L_B7ax4ZjqrkdyzbJagLgAt-GhVxp-QS?usp=sharing)


## AudioEmail Configuration

### Install MySQL

* [Download MySQL Installer](https://dev.mysql.com/downloads/installer/) Chose (mysql-installer-community-8.0.32.0.msi 437MB)

When installing, you need to set the password (`root` - `YourPassword`)

Execute the SQL script after the installation is complete:  [mail/Database initialisation script.sql](https://git.shefcompsci.org.uk/com6103-2022-23/team02/project/-/blob/project/mail/Database%20initialisation%20script.sql)


### Run AudioMail(Java)

You can run Java code through Intellij. Or run the packaged jar file (located in the test folder under the TTS branch).

If you want to run it from a JAR file, then you need to modify the [test/config/config.properties](https://git.shefcompsci.org.uk/com6103-2022-23/team02/project/-/blob/project/TTS/test/config/config.properties)

```shell
spring.datasource.username=root
spring.datasource.password= <YOUR SQL PASSWORD>
audio.handler=/audio/**
audio.locations=<file:/PATH/TO/WAV/FOLDER>
```

After the setup is complete, run the jar file through the terminal.(Before you do this, make sure you have java installed)

```shell
C:/DIR/OF/JAR> java -jar email-0.0.1-SNAPSHOT.jar
```

### Run TTS

First modify [Constant.py](https://git.shefcompsci.org.uk/com6103-2022-23/team02/project/-/blob/project/TTS/Constant.py), setting the path to `PATH/TO/WAV/FOLER ​` , note that this section needs to be the same as the path set in `audio.locations` (but does not need to contain `file:`). After the setup is complete, run [main.py](https://git.shefcompsci.org.uk/com6103-2022-23/team02/project/-/blob/project/TTS/main.py) by Pycharm (or terminal)
