# account-service

## Overview

The account service api is used to add account for a customer (Ideally, customers should be managed through
customer-service but for simplicity, added here). You can manage customer, accounts and transactions in that account
using account-service. The details about all the api/endpoints in account service are published on
swagger (http://localhost:8080/account-service/v2/api-docs?group=internal).

The api's security is managed using spring basic authentication and no authorization checks has been added. The
account-service security required to be enhanced.

Also, the database used currently is in-memory db and hence all the data will get lost once we restart the app. The
proper DB configuration should be added in the future.

## Guideline

Required software to build/run: JDK 11+, Gradle 7+, Docker (Optional)

1. Run command 'gradle clean build' (Please make sure that you are in the account-service directory)
2. You can start application using terminal or docker

   a. Terminal: Run 'cd build/libs' and then 'java --jar account-service.jar'. You can see spring application logs on
   terminal.

   b. Docker: Create docker image using 'docker build -t replace-this-with-image-name-you-want .' and then run
   'docker run -d -p 8080:8080 image-name-you-gave-in-previous-step'
3. You can access app context/api using http://localhost:8080/account-service. Please note that, except docs url, all
   other resources are protected. User username 'account-service' and password 'passw0rd'
4. Alternatively you can use request header 'Authorization: Basic YWNjb3VudC1zZXJ2aWNlOnBhc3N3MHJk'

