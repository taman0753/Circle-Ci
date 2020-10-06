## Spring Application - A complete workflow

#### This is will help you to build , test , deploy and post-deploy check a java spring application using maven via Circle Ci.

Step 1) Uplaod the complete java project on Github. 

You can refer to this to build a basic spring application - https://spring.io/guides/gs/spring-boot/#initial

Step 2) Login into Circle Ci and setup a project of the repo where you have pushed the java project.

(This will create a circle-ci branch in your repo)

Step 3) You have to now add yml file to define the workflow.

(You can refer to the .circle-ci folder in my repo for the yml file)

Step 4) Understanding the jobs :

##### Build- In this job we have used docker-image as a executor.

chmod +x mvnw - To give permission to run maven commands.

./mvnw -Dmaven.test.skip=true package - To build

##### Test- In this job we have used docker-image as a executor

chmod +x mvnw - To give permission to run maven commands.

./mvnw test -To run test script.

##### Deploy - In this job we have used ubuntu-image as a executor.  (On packagecloud)

Follow the below steps:

1)Add the following code to .circleci.settings.xml in your Maven project:

```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>packagecloud-examplecorp</id>
      <password>${env.EXAMPLECORP_PACKAGECLOUD_API_TOKEN}</password>
    </server>
  </servers>
</settings> 
```

2)If you haven’t already, get a free Maven repository from packagecloud. (https://packagecloud.io/l/maven-repository)

In the <distributionManagement/> section of the project’s pom.xml file, we tell Maven to deploy to our newly-created, private Maven repository:

```
<distributionManagement>
  <repository>
    <id>packagecloud-examplecorp</id>
    <url>packagecloud+https://packagecloud.io/exampleCorp/core</url>
  </repository>
  <snapshotRepository>
    <id>packagecloud-examplecorp</id>
    <url>packagecloud+https://packagecloud.io/exampleCorp/core</url>
  </snapshotRepository>
</distributionManagement>
```

###### Note:  that id is set to packagecloud-examplecorp, which is what we used in our .circleci.settings.xml above. Also in the url add thr url to your repo on packagecloud.

3)Finally, in the <build/> section of pom.xml, add the plugin that lets Maven natively deploy artifacts to packagecloud repositories.

```
<build>
  <extensions>
    <extension>
      <groupId>io.packagecloud.maven.wagon</groupId>
      <artifactId>maven-packagecloud-wagon</artifactId>
      <version>0.0.4</version>
    </extension>
  </extensions>
</build>
```

4)Circle ci setup:

Copy your packagecloud API Token(https://packagecloud.io/api_token) . Then in  Circle-Ci, click the gear icon next to your project’s name to access build settings.
Once you’re in build settings, navigate to the ‘Environment Variables’ section.

5)Click the ‘Add Variable’ button. Then, enter EXAMPLECORP_PACKAGECLOUD_API_TOKEN into the ‘Name’ field and paste your API token into the ‘Value’ field.

6)On the next commit, the variable should be uploaded to your packagecloud Maven repository.

![Screenshot (349)](https://user-images.githubusercontent.com/46739055/95198086-4375a780-07f8-11eb-8f8d-c273d950987c.png)


##### Post-deploy:
In this job we have used the curl command to check.

