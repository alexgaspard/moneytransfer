package org.example.moneytransfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.rest.model.AccountJSON;
import org.example.moneytransfer.rest.model.EntityJSON;
import org.example.moneytransfer.rest.model.TransactionJSON;
import org.example.moneytransfer.rest.model.TransactionTypeJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestIT {
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Test
    void testRestAPI() throws IOException {
        // Test accounts creation
        String accountsUrl = "http://localhost:8080/api/accounts";
        HttpUriRequest accountCreate = new HttpPost(accountsUrl);
        assertEquals(HttpStatus.SC_CREATED, HttpClientBuilder.create().build().execute(accountCreate).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_CREATED, HttpClientBuilder.create().build().execute(accountCreate).getStatusLine().getStatusCode());
        HttpUriRequest accountsGet = new HttpGet(accountsUrl);
        HttpResponse accountsGetResponse = HttpClientBuilder.create().build().execute(accountsGet);
        assertOKAndJSONMimeType(accountsGetResponse);

        // Test accounts read
        Collection<Account> accounts = deserialize(accountsGetResponse, Collection.class);
        assertEquals(2, accounts.size());

        // Test transactions creation
        String account1Url = accountsUrl + "/1";
        String transactionsUrl = account1Url + "/transactions";
        int otherAccountId = 2;
        BigDecimal amount = new BigDecimal(20);
        TransactionTypeJSON type = TransactionTypeJSON.WITHDRAWAL;
        HttpPost transactionCreate = post(transactionsUrl, new TransactionJSON(0, amount, type, otherAccountId, null));
        assertEquals(HttpStatus.SC_CREATED, HttpClientBuilder.create().build().execute(transactionCreate).getStatusLine().getStatusCode());
        HttpPost transactionCreate2 = post(transactionsUrl, new TransactionJSON(0, new BigDecimal(30), TransactionTypeJSON.DEPOSIT, otherAccountId, null));
        assertEquals(HttpStatus.SC_CREATED, HttpClientBuilder.create().build().execute(transactionCreate2).getStatusLine().getStatusCode());

        // Test account read
        HttpUriRequest account1Get = new HttpGet(account1Url);
        HttpResponse account1GetResponse = HttpClientBuilder.create().build().execute(account1Get);
        assertOKAndJSONMimeType(account1GetResponse);
        AccountJSON account = deserialize(account1GetResponse, AccountJSON.class);
        assertEquals(2, account.getTransactions().size());
        assertEquals(1, account.getId());
        assertEquals(new BigDecimal(10), account.getBalance());

        // Test transaction read
        HttpUriRequest transaction1Get = new HttpGet(transactionsUrl + "/1");
        HttpResponse transaction1GetResponse = HttpClientBuilder.create().build().execute(transaction1Get);
        assertOKAndJSONMimeType(transaction1GetResponse);
        TransactionJSON transaction = deserialize(transaction1GetResponse, TransactionJSON.class);
        assertEquals(1, transaction.getId());
        assertEquals(otherAccountId, transaction.getOtherAccount());
        assertEquals(amount, transaction.getAmount());
        assertEquals(type, transaction.getType());
        assertNotNull(transaction.getTransactionTime());

        // Test errors
        assertEquals(HttpStatus.SC_NOT_FOUND, HttpClientBuilder.create().build().execute(new HttpGet(accountsUrl + "/100")).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, HttpClientBuilder.create().build().execute(new HttpPut(accountsUrl)).getStatusLine().getStatusCode());
        HttpPost forbiddenTransaction = post(transactionsUrl, new TransactionJSON(0, new BigDecimal(120), TransactionTypeJSON.WITHDRAWAL, otherAccountId, null));
        assertEquals(HttpStatus.SC_BAD_REQUEST, HttpClientBuilder.create().build().execute(forbiddenTransaction).getStatusLine().getStatusCode());
    }

    private HttpPost post(String url, EntityJSON body) throws JsonProcessingException {
        HttpPost transactionCreate = new HttpPost(url);
        transactionCreate.setEntity(new StringEntity(mapper.writeValueAsString(body), ContentType.APPLICATION_JSON));
        return transactionCreate;
    }

    private <T> T deserialize(HttpResponse response, Class<T> clazz) throws IOException {
        String jsonResponse = EntityUtils.toString(response.getEntity());
//        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonResponse, clazz);
    }

    private void assertOKAndJSONMimeType(HttpResponse response) {
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals("application/json", mimeType);
    }
}