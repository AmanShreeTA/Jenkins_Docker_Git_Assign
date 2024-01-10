# Jenkins_Docker_Git_Assign

## Integrate Jenkins with GitHub, Docker, and Docker Hub
### Exercises:
 - Build a simple demo Java console application and host the application code on GitHub and set up Jenkins in Docker (by using Docker Image).
 - Automate the building, testing, dockerizing, and deploying of our application Docker image to Docker Hub after every commit made to our application repository hosted on GitHub.

### Assignment Steps:
 **Deploy Jenkin as a docker container and configure it:**
  - Write a Dockerfile  
  - Build the docker image
    - Use jenkins/jenkins:lts  as the base image
    - Use root user to install all the required packages
    - Add apt -repo https://download.docker.com/linux/debian  to download the docker-ce edition
    - Install docker and other required packages
    - Finally add the jenkins user to docker group
  - Build the docker image
  - Run the docker container: docker run -it -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --restart unless-stopped <docker image>
  - Visit localhost 
    - http://localhost:8080
    - use the initial password which is returned by the above commands and login
    - customize jenkins by installing the required plugins such as docker and Kubernetes, git etc.
  - JDK config and mvn config
    - Jenkins container comes with an OpenJDK. To find it, we need to enter the container’s bash shell to get the JAVA_HOME path.
    
          $ docker exec -it <container_name/container_id> /bin/bash
      
          echo $JAVA_HOME
    - system configuration and go to global configurations
    - add JDK installations by including path of the JAVA_HOME
    - similarly add the maven config
  
 **Jenkins project:**
  - select Freestyle project:
  - select source Code Management (or SCM for short), select Git, add the remote Git repository URL of the project, and leave the branch field empty so any commit made to any branch triggers our entire Jenkins process:
  - Configure the GitHub project and add the project URL
  - Configure Build Triggers accordingly. (select Poll SCM, which checks whether we made changes and then rebuilds our project)
  - In the Build window, add two Invoke top-level Maven targets steps.
  - Build the project and make sure the build succeeds.
  - Make some changes in the source code and push it to the central repo. Make sure Jenkins will automatically build our Freestyle project(we don’t need to manually click Build Now)

 **Configure Jenkins to build the Docker image of our Java application and deploy that image to Docker Hub:**
  - To perform this task, we need a few Jenkins plugins to be installed. In Manage Jenkins, select Manage Plugins under System Configurations, search and install the following plugins:docker-build-step , and CloudBees Docker Build and Publish
  - Create a Dockerfile with the below content

              FROM openjdk:8
    
              ADD target/java-jenkins-docker.jar java-jenkins-docker.jar

              ENTRYPOINT ["java", "-jar”, “java-jenkins-docker.jar"]
    
              EXPOSE 8080

  - Add the new file and then commit the changes to the GitHub repository. This will trigger a Jenkins post-commit build process as we configured.
  - Provide Docker Hub access to Jenkin:
    - To give Jenkins access, we need to login to our Docker Hub account inside our Jenkins container through the command line, as shown below:
    -  $ docker exec -it <container_name/container_id> /bin/bash
    -  Then inside the container run the "docker login" command, followed by the username and password/access token of your dockerhub account.
  - then add another build steps to build and deploy a Java application’s Docker image. In the Jenkins build step set:
          Repository name: \<your repo\>/\<docker-image\>
    
          The <your repo> will be replaced by the name of your dockerhub user name.
  - Go back to your project and click Build Now, then navigate to the console output. The console output should show the build to be as SUCCESS.


 ## Steps Performed for the assignment:
  - Create a Dockerhub account on https://hub.docker.com/
  - Starte the docker daemon to run docker commands from the terminal

          dockerd
  - Write a Dockerfile for running Jenkins inside a docker container:

              FROM jenkins/jenkins:lts
              USER root
              RUN apt-get update -qq \
                  && apt-get install -qqy apt-transport-https ca-certificates curl gnupg2 software-properties-common
              RUN curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add -
              RUN add-apt-repository \
                 "deb [arch=amd64] https://download.docker.com/linux/debian \
                 $(lsb_release -cs) \
                 stable"
              RUN apt-get update  -qq \
                  && apt-get install docker-ce -y
              RUN usermod -aG docker jenkins
  - Build the docker image from the Dockerfile using the command : docker build -t my-jenkins-image .
  - Run the docker container using the image created:
    
          docker run -it -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --restart unless-stopped my-jenkins-image
  - Visit localhost:8080 to access the jenkins GUI and configured jenkins with the default suggested plugins and the  required plugins: git, github, docker, docker-build-step, CloudBees Docker Build and Publish.
  - After configuring jenkins on start-up, go to Manage Jenkins > Tools. Under the JDK Installation section, click on "Add JDK" and give a name to it, and in the "JAVA_HOME" box enter -> /opt/java/openjdk.
  - Then on the same page, under the Maven Installation section, click on "Add Maven" and give a name inside the "Name" box, and select the "Install automatically" checkbox, and then select the version of Maven you want to install from the dropdown list.
  - Scroll to the bottom of the page and click on "Apply" and then "Save".
  - Go to Jenkins Dashboard and select New Item, give a name to the new job, select Freestyle project.
  - In the configure page of the job, select Github project and enter the project url , then select SCM as Git and provide the URL of the github repository and leave the branch specifier as blank. For build trigger select Poll SCM and give the schedule as "* * * * *" to poll the github repo every minute.
  - In the build steps section , add two "Invoke top-level Maven targets": test and install.
  - Click on Apply and then on Save.
  - Build the project by pressing the Build Now button. Make sure the build succeeds.
  - Make some changes in the github repository and commit those changes and the job is triggered on its own because of the Poll SCM trigger.
  - Create a Dockerfile to build the Docker image of our Java application and deploy that image to Docker Hub:
    
          FROM openjdk:8
          ADD target/java-jenkins-docker.jar java-jenkins-docker.jar
          ENTRYPOINT ["java", "-jar”, “java-jenkins-docker.jar"]
          EXPOSE 8080
  - Publish this Dockerfile on the github repo. This will trigger a Jenkins post-commit build process as we configured.
  - To deploy the docker image to Dockerhub we need to give access to Jenkins of the Dockerhub account. To do this enter the terminal of the jenkins container using the command:
    
        docker exec -it <container_name/container_id> /bin/bash
  - Then login to docker using the command: docker login -> followed by the login credentials
  - Exit the containers terminal using the command "exit".
  - Go to Jenkins Dashboard and then go to the configure page of the Jenkins job created earlier.
  - Inside the Build Step section , click on Add Build Step and select "Docker Build and Publish".
  - In the Repository name box enter: \<your repo\>/\<docker-image\>
  - Example : amanshreeta/my-jenkins-image
  - Then click on Apply and Save.
  - No Click on Build Now button of the Project to Build the Job and push the image to Dockerhub.
  - The build status Screenshot of the Project is below:
![Screenshot (30)](https://github.com/AmanShreeTA/Jenkins_Docker_Git_Assign/assets/155889933/eb8aac61-d504-43d5-9f9d-671ea5f98b96)

  - The Screenshot of the Jenkins Project is below:
![Screenshot (31)](https://github.com/AmanShreeTA/Jenkins_Docker_Git_Assign/assets/155889933/06dd1493-10c5-445a-9c29-becfc777cf45)
![Screenshot (32)](https://github.com/AmanShreeTA/Jenkins_Docker_Git_Assign/assets/155889933/5e7dd338-9bdf-4423-8420-da52cfe7f921)

  - The Screenshot of the pushed image to Dockerhub is below:
![Screenshot (33)](https://github.com/AmanShreeTA/Jenkins_Docker_Git_Assign/assets/155889933/14ff954c-41d4-4f66-a1e3-82a584889edd)
