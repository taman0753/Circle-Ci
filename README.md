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

##### Sonar Code Analysis:

To generate a token, to go User > My Account > Security (on your sonar account) Your existing tokens are listed here, each with a Revoke button.

The form at the bottom of the page allows you to generate new tokens. Once you click the Generate button, you will see the token value. 

Copy it immediately ; once you dismiss the notification you will not be able to retrieve it.

Add it to env variables in Circle ci "Sonar_Token"

Also you will need to setup

sonar.organization=name of organization

sonar.projectKey=project-key

sonar.java.binaries=Comma-separated paths to directories containing the compiled bytecode files corresponding to your source files. 

sonar.java.test.binaries=Comma-separated paths to directories containing the compiled bytecode files corresponding to your test files

in your repo in a file "sonar-project.properties"

##### Note  : Off the automatic analysis from SonarQube settings.

##### POSTMAN API TESTING:

With the Newman orb for CircleCI, we have built a readymade integration where you can trigger collection runs in your CircleCI pipelines with just a configuration.

The Newman orb exposes the newman/newman-run command that you can use in your CircleCI configuration

Assuming you have your collection file exported to ./collection.json at the root of your repository,

```
version: 2.1
orbs:
  newman: postman/newman@0.0.2
jobs:
  newman-collection-run:
    executor: newman/postman-newman-docker
    steps:
      - checkout
      - newman/newman-run:
          collection: ./collection.json
```

#### Percy-testing:

In your CircleCI project, go to Project settings > Environment Variables.(Create a project in percy if not from : https://percy.io/ by signing up)

Then set PERCY_TOKEN to the write-only token from your Percy project. This token can be found in your Percy project's settings.

We first import the percy/agent orb and then once all tests have finished, execute percy/finalize_all command from the Orb.(See the config.yml file for the code)

When you save all the changes refresh your percy project dashboard and you will see the visual-results of the test.
