package com.jayway.restassured.itest.java;

import com.jayway.restassured.exception.AssertionFailedException;
import com.jayway.restassured.itest.support.WithJetty;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class JSONGetITest extends WithJetty {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void simpleJSONAndHamcrestMatcher() throws Exception {
        expect().body("hello", equalTo("Hello Scalatra")).when().get("/hello");
    }

    @Test
    public void ognlJSONAndHamcrestMatcher() throws Exception {
        expect().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void ognlAssertionWithHamcrestMatcherAndJSONResturnsArray() throws Exception {
        expect().body("lotto.winners.winnerId", hasItems(23, 54)).when().get("/lotto");
    }

    @Test
    public void parameterSupportWithStandardHashMap() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("firstName", "John");
        parameters.put("lastName", "Doe");
        given().parameters(parameters).then().expect().body("greeting", equalTo("Greetings John Doe")).when().get("/greet");
    }

    @Test
    public void parameterSupportWithMapBuilder() throws Exception {
      with().parameters("firstName", "John", "lastName", "Doe").expect().body("greeting", equalTo("Greetings John Doe")).when().get("/greet");
    }

    @Test
    public void newSyntax() throws Exception {
        expect().content("lotto.lottoId", equalTo(5)).when().get("lotto");
    }

    @Test
    public void newSyntaxWithParameters() throws Exception {
        expect().content("greeting", equalTo("Greetings John Doe")).with().parameters("firstName", "John", "lastName", "Doe").when().get("/greet");
    }

    @Test
    public void newSyntaxWithWrongStatusCode() throws Exception {
        // Given
        exception.expect(AssertionFailedException.class);
        exception.expectMessage(equalTo("Expected status code <300> doesn't match actual status code <200>."));

        // When
        expect().response().statusCode(300).and().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void newSyntaxWithCorrectStatusCodeUsingInt() throws Exception {
        expect().statusCode(200).and().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void newSyntaxWithCorrectStatusCodeUsingHamcrestMatcher() throws Exception {
        expect().statusCode(allOf(greaterThanOrEqualTo(200), lessThan(300))).and().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void newSyntaxWithWrongStatusLine() throws Exception {
        // Given
        exception.expect(AssertionFailedException.class);
        exception.expectMessage(equalTo("Expected status line \"300\" doesn't match actual status line \"HTTP/1.1 200 OK\"."));

        // When
        expect().statusLine(equalTo("300")).and().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void newSyntaxWithCorrectStatusLineUsingHamcrestMatcher() throws Exception {
        expect().statusLine(containsString("200 OK")).and().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void newSyntaxWithCorrectStatusLineUsingStringMatching() throws Exception {
        expect().statusLine("HTTP/1.1 200 OK").and().body("lotto.lottoId", equalTo(5)).when().get("/lotto");
    }

    @Test
    public void jsonHamcrestEqualBody() throws Exception {
        final String expectedBody = "{\"lotto\":{\"lottoId\":5,\"winning-numbers\":[2,45,34,23,7,5,3],\"winners\":[{\"winnerId\":23,\"numbers\":[2,45,34,23,3,5]},{\"winnerId\":54,\"numbers\":[52,3,12,11,18,22]}]}}";
        expect().body(equalTo(expectedBody)).when().get("/lotto");
    }

    @Test
    public void restAssuredSupportsFullyQualifiedURI() throws Exception {
        final String expectedBody = "{\"lotto\":{\"lottoId\":5,\"winning-numbers\":[2,45,34,23,7,5,3],\"winners\":[{\"winnerId\":23,\"numbers\":[2,45,34,23,3,5]},{\"winnerId\":54,\"numbers\":[52,3,12,11,18,22]}]}}";
        expect().body(equalTo(expectedBody)).when().get("http://localhost:8080/lotto");
    }

    @Test
    public void supportsHeaderStringMatching() throws Exception {
        expect().response().header("Content-Type", "application/json; charset=UTF-8").when().get("/lotto");
    }

    @Test
    public void multipleHeaderStatementsAreConcatenated() throws Exception {
        expect().response().header("Content-Type", "application/json; charset=UTF-8").and().header("Content-Length", "160").when().get("/lotto");
    }

    @Test
    public void multipleHeadersShortVersionUsingPlainStrings() throws Exception {
        expect().response().headers("Content-Type", "application/json; charset=UTF-8", "Content-Length", "160").when().get("/lotto");
    }

    @Test
    public void multipleHeadersShortVersionUsingHamcrestMatching() throws Exception {
        expect().response().headers("Content-Type", containsString("application/json"), "Content-Length", equalTo("160")).when().get("/lotto");
    }

    @Test
    public void multipleHeadersShortVersionUsingMixOfHamcrestMatchingAndStringMatching() throws Exception {
        expect().response().headers("Content-Type", containsString("application/json"), "Content-Length", "160").when().get("/lotto");
    }

    @Test
    public void multipleHeadersUsingMap() throws Exception {
        Map expectedHeaders = new HashMap();
        expectedHeaders.put("Content-Type", "application/json; charset=UTF-8");
        expectedHeaders.put("Content-Length", "160");

        expect().response().headers(expectedHeaders).when().get("/lotto");
    }

    @Test
    public void multipleHeadersUsingMapWithHamcrestMatcher() throws Exception {
        Map expectedHeaders = new HashMap();
        expectedHeaders.put("Content-Type", containsString("application/json; charset=UTF-8"));
        expectedHeaders.put("Content-Length", equalTo("160"));

        expect().response().headers(expectedHeaders).when().get("/lotto");
    }

    @Test
    public void multipleHeadersUsingMapWithMixOfStringAndHamcrestMatcher() throws Exception {
        Map expectedHeaders = new HashMap();
        expectedHeaders.put("Content-Type", containsString("application/json; charset=UTF-8"));
        expectedHeaders.put("Content-Length", "160");

        expect().response().headers(expectedHeaders).when().get("/lotto");
    }

    @Test
    public void whenExpectedHeaderDoesntMatchAnAssertionThenAssertionFailedExceptionIsThrown() throws Exception {
        exception.expect(AssertionFailedException.class);
        exception.expectMessage(equalTo("Expected header \"Content-Length\" was not \"161\", was \"160\"."));

        expect().response().header("Content-Length", "161").when().get("/lotto");
    }

    @Test
    public void whenExpectedHeaderIsNotFoundThenAnAssertionFailedExceptionIsThrown() throws Exception {
        exception.expect(AssertionFailedException.class);
        exception.expectMessage(equalTo("Header \"Not-Defined\" was not defined in the response. Headers are: \n" +
                "Content-Type: application/json; charset=UTF-8\n" +
                "Content-Length: 160\n" +
                "Server: Jetty(6.1.14)"));

        expect().response().header("Not-Defined", "160").when().get("/lotto");
    }
}