import static io.restassured.RestAssured.*;
import com.sun.org.glassfish.external.probe.provider.annotations.ProbeListener;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Step;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class Login_test {

    String userData = "{\n"
            + "  \"userName\": \"Test_Testing\",\n"
            + "  \"password\": \"Test1234@\"\n"
            + "}";
    @Test
    public void addBookTest (){
        baseURI = "https://demoqa.com/account/v1"; //used for GenerateToken and Login
        String token = generateToken();
        String userId = login();
        deleteBook(token, userId); //to be able to add the book we need to delete it from the last run
        addBook(token, userId);
        checkBookAdded (token, userId);  //checking the array of books that it's not empty after adding the book

    }
    @Step ("Token Generation")
//Method that generated bearer token
    private String generateToken () {
       return given().
                    contentType("application/json").
                    body(userData).
                    post("/GenerateToken").
                then().
                    statusCode(200).
                    extract().path("token");
           }

    @Step ("Login and userId generation")
//Method that generates and returns userID
    private String login () {
        return given().
                    contentType("application/json").
                    body(userData).
                    post("/login").
                 then().
                    statusCode(200).
                    extract().path("userId");
    }

    @Step ("adding the book")
//Method that adds the book to user's collection
    private void addBook (String token, String userId) {
        given().
                contentType("application/json").
                header("Authorization", "Bearer " + token).
                body("{\n"
                        + "  \"userId\": \""+ userId + "\",\n"
                        + "  \"collectionOfIsbns\": [\n"
                        + "    {\n"
                        + "      \"isbn\": \"9781449325862\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}").
                post("https://demoqa.com/BookStore/v1/Books").
        then().
                statusCode(201);

    }
    @Step ("deleting the book")
    //Method that deletes the book from collection
    private void deleteBook (String token, String userId) {
        given().
                contentType("application/json").
                header("Authorization", "Bearer " + token).
                body("{\n" +
                        "  \"isbn\": \"9781449325862\",\n" +
                                "  \"userId\": \""+ userId +"\"\n" +
                                "}").
                delete("https://demoqa.com/BookStore/v1/Book").
        then().
                statusCode(204);
    }
// Checking that the book is in the collection (tbd)
    @Step ("checking the book")
    private void checkBookAdded (String token, String userId) {
        given().
                contentType("application/json").
                header("Authorization", "Bearer " + token).
                get("User/" + userId).
        then().
                //body("Books.title", equalTo("Git Pocket Guide")).
                statusCode(200);


    }

}
