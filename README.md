# RabbitMQ Services
Allows the creation of RabbitMQ Consumer and Producers for use as Smart Services.

## Generel Set-up

- Upload the keystore certificate as an Appian document and make a note of the id
- Upload the truststore certificate as an Appian document and make a note of the id
- Create a new third party credential key with the values as specified in the following table.
- Create a new table to store the consumed messages for further processing, this table acts as a
  staging table, an appropriate clean-up process should be implemented to remove processed messages

### Third-Party Credential Store setup
| Variable            | Use                                           |
|---------------------|-----------------------------------------------|
| jksdocument         |  JKS Certificate document id                  |
| jkspassword         |  JKS Password                                 |
| truststoredocument  |  Truststore certificate document id           |
| truststorepassword  |  Truststore password                          |
| tlsprotocol         |  TLS Protocol (default tls1.2)                |
| host                |  Hostname of RabbitMQ endpoint                |

## Rabbit Consumer Smart Service
Allows the creation of RabbitMQ consumer for retrieving messages from a RabbitMQ message queue.
### Data Tab
####Input Parameters
|             | Data Type | Required | Multiple | Description |
| ------------------- |:---------:|:--------:|:--------:| ----------- |
| SCSCredential       | Text      | Yes      | No       | My Description |
| Input Two           | Text      | No       | No       | My Description |
####Output Parameters
|       | Data Type | Multiple | Description |
| --------------------|:---------:|:--------:| ----------- |
| Output One          | Document  | No       | My Description |
| Output Two          | Boolean   | No       | My Description |

---

## The RabbitMQ  Message Producer
Smart Service for populating messages to a queue

Multiple Rabbit MQ endpoints can be used by creating the relevant TP credentials key.


### Data Tab
####Input Parameters
|             | Data Type | Required | Multiple | Description |
| ------------------- |:---------:|:--------:|:--------:| ----------- |
| SCSCredential       | Text      | Yes      | No       | My Description |
| Input Two           | Text      | No       | No       | My Description |
####Output Parameters
|       | Data Type | Multiple | Description |
| --------------------|:---------:|:--------:| ----------- |
| Output One          | Document  | No       | My Description |
| Output Two          | Boolean   | No       | My Description |

---

### Service Authentication
The `Service Authentication` inputs allow additional authentication configuration to be supplied to RabbitMQ. It takes the format: `={key1: value1, key2: value2}` where the keys are one of the following:

* **keystoreDocument:** The Java Key Store that contains the self-signed HTTPS certificate of the service. This must be the Appian document id of the Java Key Store.
* **keystoreSystemKey:** The key from the Secure Credential Store that contains the `password` for the Java Key Store.
* **tlsProtocol:** The [TLS Protocol](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SSLContext) to use (uses SSLv3 if not specified).

## NAME Function
FUNCTION DESCRIPTION

### Syntax ###
`myFunctionName(inputOne, inputTwo)`

### Inputs ###
| Input               | Data Type | Required | Multiple | Description |
| ------------------- |:---------:|:--------:|:--------:| ----------- |
| Input One           | Boolean   | Yes      | No       | My Description |
| Input Two           | Text      | No       | No       | My Description |

### Returns ###
FUNCTION'S OUTPUT DATA TYPE
