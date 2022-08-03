package trello;

import base.BaseTest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
public class OrganizationTest extends BaseTest {
    private static Stream<Arguments> createOrganizationData(){

        return Stream.of(
                Arguments.of(DISPLAYNAMETEXT, NAMETEXT, DESCRIPTIONTEXT, WEBSIDETEXT, 200),
                Arguments.of(DISPLAYNAMETEXT, "aa", DESCRIPTIONTEXT, WEBSIDETEXT, 400),
                Arguments.of(DISPLAYNAMETEXT, "THIS IS NAME", DESCRIPTIONTEXT, WEBSIDETEXT, 400),
                Arguments.of(DISPLAYNAMETEXT, "*this is name$", DESCRIPTIONTEXT, WEBSIDETEXT, 400),
                Arguments.of(DISPLAYNAMETEXT, NAMETEXT, DESCRIPTIONTEXT, "xttps://akademiaqa.pl/szk/", 400),
                Arguments.of(DISPLAYNAMETEXT, NAMETEXT, DESCRIPTIONTEXT, "1://akademiaqa.pl/szk/", 400));
    }
    @DisplayName("Create organization with valid data")
    @ParameterizedTest(name = "DisplayName: {0}, name: {1}, desc: {2}, webside: {3}, code {4}")
    @MethodSource("createOrganizationData")
    public void createOrganizationWithInvalidData(String displayName, String name, String desc, String webside, Integer code){

        Organization organization = new Organization();
        organization.setDisplayName(displayName);
        organization.setName(name);
        organization.setDesc(desc);
        organization.setWebside(webside);

        Response response = given()
                .contentType(ContentType.JSON)
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .queryParam("displayName", organization.getDisplayName())
                .queryParam("name", organization.getName())
                .queryParam("desc", organization.getDesc())
                .queryParam("website", organization.getWebside())
                .when()
                .post(BASE_URL + "/" + ORGANIZATIONS)
                .then()
                .statusCode(code)
                .extract()
                .response();

        JsonPath json = response.jsonPath();

        final String organizationId = json.getString("id");

        Assertions.assertThat(json.getString("displayName")).isEqualTo(displayName);

        given()
                .contentType(ContentType.JSON)
                .queryParam("key", KEY)
                .queryParam("token", TOKEN)
                .when()
                .delete(BASE_URL + "/" + ORGANIZATIONS + "/" + organizationId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}