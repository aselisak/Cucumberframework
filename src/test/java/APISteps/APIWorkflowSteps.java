package APISteps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import utils.APIConstants;
import utils.APIPayloadConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class APIWorkflowSteps{
    RequestSpecification request;
    Response response;
   public static String employee_id;

    @Given("a request is prepared to create an employee")
    public void a_request_is_prepared_to_create_an_employee() {
         request = given().header(APIConstants.HEADER_CONTENT_TYPE,APIConstants.HEADER_CONTENT_TYPE_VALUE).
                 header(APIConstants.HEADER_AUTHORIZATION,GenerateTokenSteps.token).
                 body(APIPayloadConstants.createEmployeePayload());

    }

    @When("a POST call is made to create an employee")
    public void a_post_call_is_made_to_create_an_employee() {
          response = request.when().post(APIConstants.CREATE_EMPLOYEE_URI);
          response.prettyPrint();
    }

    @Then("the status code for the created employee is {int}")
    public void the_status_code_for_the_created_employee_is(Integer code) {
         response.then().assertThat().statusCode(code);

    }
    @Then("the employee created contains kew {string} and value {string}")
    public void the_employee_created_contains_kew_and_value(String key, String value) {
     response.then().assertThat().body(key, equalTo(value));
    }

    @Then("the employee id {string} is stored as a global variable to be used for other calls")
    public void the_employee_id_is_stored_as_a_global_variable_to_be_used_for_other_calls(String empId) {
        //data comes from feature file
      employee_id = response.jsonPath().getString(empId);
        System.out.println(employee_id);
    }
    //----------------------------------------------------------------------------------------
    @Given("a request is prepared to get the employee")
    public void a_request_is_prepared_to_get_the_employee() {
       request = given().header(APIConstants.HEADER_CONTENT_TYPE,APIConstants.HEADER_CONTENT_TYPE_VALUE).
               header(APIConstants.HEADER_AUTHORIZATION,GenerateTokenSteps.token).
               queryParam("employee_id",employee_id);
    }

    @When("a GET call is made to retrieve the created employee")
    public void a_get_call_is_made_to_retrieve_the_created_employee() {
      response = request.when().get(APIConstants.GET_ONE_EMPLOYEE_URI);
    }
    @Then("the status call for this employee is {int}")
    public void the_status_call_for_this_employee_is(Integer statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    @Then("the employee we are getting having ID {string} must match with the globally stored employee id")
    public void the_employee_we_are_getting_having_id_must_match_with_the_globally_stored_employee_id(String empId) {
       String tempId = response.jsonPath().getString(empId);
        Assert.assertEquals(tempId,employee_id);
    }
    @Then("the retrieved data at {string} object matches the data used to create the employee having employee id {string}")
    public void the_retrieved_data_at_object_matches_the_data_used_to_create_the_employee_having_employee_id(String empObj, String resEmpId, DataTable dataTable) {
        //data comes from feature file
      List<Map<String,String>> expectedData= dataTable.asMaps(String.class,String.class);

      //data comes from get call
      Map<String,String> actualData = response.body().jsonPath().get(empObj);
        for(Map<String,String> singlePairOfData : expectedData){
            //it will return the set of key from the map
            Set<String> keys = singlePairOfData.keySet();
            for(String key : keys){
            String expectedValue = singlePairOfData.get(key);
            String actualValue = actualData.get(key);
            Assert.assertEquals(expectedValue,actualValue);
            }
        }
    }
}
