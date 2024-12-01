package br.com.gabezy.cashcard;

import br.com.gabezy.cashcard.domain.CashCard;
import br.com.gabezy.cashcard.utils.RestTemplateUtils;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.net.URI;

import static br.com.gabezy.cashcard.utils.RestTemplateUtils.RestTemplateCashCardFilter;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Start Spring boot and make it available for our test to perform requests to it
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashCardApplicationTests {

    @Autowired
    RestTemplateUtils restTemplateUtils;

    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    private static final String CASHCARDS_ENDPOINT = "cashcards";
    private static final String SARAH_USERNAME = "sarah1";
    private static final String SARAH_PASSWORD = "abc123";

    @Test
    @DirtiesContext
    void shouldCreateANewCashCard() {
        CashCard cashCard = new CashCard(null, 250D, null);

        var filter = new RestTemplateCashCardFilter.Builder()
                .body(cashCard)
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT)
                .post(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationURI = response.getHeaders().getLocation();

        filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(locationURI.getPath())
                .build();

        ResponseEntity<String> getResponse = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(cashCard.amount());
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingCashCard() {
        CashCard cashCard = new CashCard(null, 19.99, null);
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .body(cashCard)
                .put(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .build();

        ResponseEntity<String> getResponse = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isEqualTo(99);
        assertThat(amount).isEqualTo(19.99);
    }

    @Test
    void shouldNotUpdateACashCardThatDoesNotExist() {
        CashCard cashCard = new CashCard(null, 19.99, null);
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/9999999")
                .body(cashCard)
                .put(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse() {
        CashCard cashCard = new CashCard(null, 333.33, null);
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/102")
                .body(cashCard)
                .put(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCashCard() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .delete(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .build();

        ResponseEntity<String> getResponse = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteACashCardThatDoesNotExist() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/9999999")
                .delete(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowDeletionOfCashCardsTheyDoNotOwn() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/102")
                .delete(true)
                .build();

        ResponseEntity<Void> response = restTemplateUtils.buildRequestRest(Void.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        filter = new RestTemplateCashCardFilter.Builder()
                .username("kumar2")
                .password("xyz789")
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/102")
                .build();

        ResponseEntity<String> getResponse = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    void shouldReturnACashCardWhenDataIsSaved() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");

        Double amount = documentContext.read("$.amount");

        assertThat(id).isEqualTo(99);
        assertThat(amount).isEqualTo(123.45);
    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/1000")
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldReturnAllCashCardsWhenListIsRequested() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT)
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int cashCardCount = documentContext.read("$.length()");
        assertThat(cashCardCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
    }

    @Test
    void shouldReturnAPageOfCashCards() {
        String uriParameters = "?page=0&size=1";
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT.concat(uriParameters))
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page).hasSize(1);
    }

    @Test
    void shouldReturnASortedPageOfCashCards() {
        String uriParameters = "?page=0&size=1&sort=amount,desc";
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT.concat(uriParameters))
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page).hasSize(1);

        Double amount = documentContext.read("$[0].amount");
        assertThat(amount).isEqualTo(150.00);
    }

    @Test
    void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT)
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page).hasSize(3);

        JSONArray amounts = documentContext.read("$..amount");
        assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
    }

    @Test
    void shouldNotReturnACashCardWhenUsingBadCredentials() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username("BAD-USER")
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password("badpassword")
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .build();

        response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldRejectUserWhoAreNotCardOwners() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username("hank-owns-no-cards")
                .password("qrs456")
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/99")
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
        var filter = new RestTemplateCashCardFilter.Builder()
                .username(SARAH_USERNAME)
                .password(SARAH_PASSWORD)
                .port(port)
                .endpoint(CASHCARDS_ENDPOINT + "/102")
                .build();

        ResponseEntity<String> response = restTemplateUtils.buildRequestRest(String.class, restTemplate, filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
