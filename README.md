
Money Transfer
==============
Simple REST API to transfer money between accounts.

Run server with:
```
mvn exec:java
```
Unit tests are available in src/test, integration tests are in it/test and should be executed with a server running on localhost:8080 without any previous data.


**Version:** 1.0.0

**License:** [MIT](https://opensource.org/licenses/mit-license.php)

### /accounts
---
##### ***POST***
**Summary:** Create a new account

**Description:** 

**Responses**

| Code | Description |
| ---- | ----------- |
| 201 | Created |

##### ***GET***
**Summary:** Get accounts

**Description:** 

**Responses**

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [ [Account](#account) ] |
| 404 | Account not found |

### /accounts/{id}
---
##### ***GET***
**Summary:** Get account by its identifier

**Description:** 

**Parameters**

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| id | path | Account unique identifier. | Yes | integer |

**Responses**

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [Account](#account) |
| 404 | Account not found |

### /accounts/{id}/transactions
---
##### ***POST***
**Summary:** Create a new transaction for this account

**Description:** 

**Parameters**

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| id | path | Account unique identifier. | Yes | integer |
| body | body | Transaction to create | Yes | [Transaction](#transaction) |

**Responses**

| Code | Description |
| ---- | ----------- |
| 201 | Created |
| 400 | Bad request, e.g. if you exceed the overdraft limit |
| 404 | Account not found |

##### ***GET***
**Summary:** Get transactions for this account

**Description:** 

**Parameters**

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| id | path | Account unique identifier. | Yes | integer |

**Responses**

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [ [Transaction](#transaction) ] |
| 404 | Account not found |

### /accounts/{id}/transactions/{id2}
---
##### ***GET***
**Summary:** Get transaction by its identifier

**Description:** 

**Parameters**

| Name | Located in | Description | Required | Schema |
| ---- | ---------- | ----------- | -------- | ---- |
| id | path | Account unique identifier. | Yes | integer |
| id2 | path | Transaction unique identifier. | Yes | integer |

**Responses**

| Code | Description | Schema |
| ---- | ----------- | ------ |
| 200 | Success | [Transaction](#transaction) |
| 404 | Account or transaction not found (transaction must be related to this account) |

### Models
---

### Account  

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | long | Account unique identifier. | Yes |
| balance | number | Account balance. | No |
| transactions | [ [Transaction](#transaction) ] | Transactions list related to this account. | No |

### Transaction  

| Name | Type | Description | Required |
| ---- | ---- | ----------- | -------- |
| id | long | Transaction unique identifier. | Yes |
| amount | number | Amount transfered by this transaction. | Yes |
| type | string | Transaction type. | Yes |
| related | long | Related account unique identifier. | Yes |
| date | dateTime | Transaction date. | No |


---
API doc generated using [swagger-markdown](https://github.com/syroegkin/swagger-markdown)
