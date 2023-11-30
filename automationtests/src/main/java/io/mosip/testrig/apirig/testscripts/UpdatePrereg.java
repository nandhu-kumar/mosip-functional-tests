package io.mosip.testrig.apirig.testscripts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestException;
import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.admin.fw.util.TestCaseDTO;
import io.mosip.testrig.apirig.authentication.fw.dto.OutputValidationDto;
import io.mosip.testrig.apirig.authentication.fw.util.AuthenticationTestException;
import io.mosip.testrig.apirig.authentication.fw.util.OutputValidationUtil;
import io.mosip.testrig.apirig.authentication.fw.util.ReportUtil;
import io.mosip.testrig.apirig.kernel.util.ConfigManager;
import io.mosip.testrig.apirig.service.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.HealthChecker;
import io.restassured.response.Response;

public class UpdatePrereg extends AdminTestUtil implements ITest {
	private static final Logger logger = Logger.getLogger(UpdatePrereg.class);
	protected String testCaseName = "";
	String pathParams = null;
	public Response response = null;

	@BeforeClass
	public static void setLogLevel() {
		if (ConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	/**
	 * get current testcaseName
	 */
	@Override
	public String getTestName() {
		return testCaseName;
	}

	/**
	 * Data provider class provides test case list
	 * 
	 * @return object of data provider
	 */
	@DataProvider(name = "testcaselist")
	public Object[] getTestCaseList(ITestContext context) {
		String ymlFile = context.getCurrentXmlTest().getLocalParameters().get("ymlFile");
		pathParams = context.getCurrentXmlTest().getLocalParameters().get("pathParams");
		logger.info("Started executing yml: " + ymlFile);
		return getYmlTestData(ymlFile);
	}

	/**
	 * Test method for OTP Generation execution
	 * 
	 * @param objTestParameters
	 * @param testScenario
	 * @param testcaseName
	 * @throws AuthenticationTestException
	 * @throws AdminTestException
	 */
	@Test(dataProvider = "testcaselist")
	public void test(TestCaseDTO testCaseDTO) throws AuthenticationTestException, AdminTestException {
		testCaseName = testCaseDTO.getTestCaseName();
		testCaseDTO.setInputTemplate(AdminTestUtil.generateHbsForPrereg(true));
		String[] templateFields = testCaseDTO.getTemplateFields();
		if (HealthChecker.signalTerminateExecution) {
			throw new SkipException("Target env health check failed " + HealthChecker.healthCheckFailureMapS);
		}
		
		if (testCaseDTO.getTestCaseName().contains("vid") || testCaseDTO.getTestCaseName().contains("VID") || testCaseDTO.getTestCaseName().contains("Vid")) {
			if (!BaseTestCase.getSupportedIdTypesValueFromActuator().contains("VID")
					&& !BaseTestCase.getSupportedIdTypesValueFromActuator().contains("vid")) {
				throw new SkipException("Idtype VID is not supported. Hence skipping the testcase");
			}
		}

		String outputJson = getJsonFromTemplate(testCaseDTO.getOutput(), testCaseDTO.getOutputTemplate());
		String inputJson = getJsonFromTemplate(testCaseDTO.getInput(), testCaseDTO.getInputTemplate(), false);
		if (BaseTestCase.getLanguageList().size() == 2) {
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"FR\" }", "");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"Test Book appointment\" }",
					"");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"MLE\" }", "");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"RSK\" }", "");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"KTA\" }", "");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"KNT\" }", "");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"BNMR\" }", "");
			inputJson = inputJson.replace(", { \"language\": \"$3RDLANG$\", \"value\": \"NFR\" }", "");
		} else if (BaseTestCase.getLanguageList().size() == 1) {
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"FR\" }, { \"language\": \"$3RDLANG$\", \"value\": \"FR\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"NFR\" }, { \"language\": \"$3RDLANG$\", \"value\": \"NFR\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"Test Book appointment\" }, { \"language\": \"$3RDLANG$\", \"value\": \"Test Book appointment\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"MLE\" }, { \"language\": \"$3RDLANG$\", \"value\": \"MLE\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"RSK\" }, { \"language\": \"$3RDLANG$\", \"value\": \"RSK\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"KTA\" }, { \"language\": \"$3RDLANG$\", \"value\": \"KTA\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"KNT\" }, { \"language\": \"$3RDLANG$\", \"value\": \"KNT\" }",
					"");
			inputJson = inputJson.replace(
					", { \"language\": \"$2NDLANG$\", \"value\": \"BNMR\" }, { \"language\": \"$3RDLANG$\", \"value\": \"BNMR\" }",
					"");
		}

		if (testCaseDTO.getTemplateFields() != null && templateFields.length > 0) {
			ArrayList<JSONObject> inputtestCases = AdminTestUtil.getInputTestCase(testCaseDTO);
			ArrayList<JSONObject> outputtestcase = AdminTestUtil.getOutputTestCase(testCaseDTO);
			for (int i = 0; i < languageList.size(); i++) {
				response = putWithPathParamsBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(),
						getJsonFromTemplate(inputtestCases.get(i).toString(), testCaseDTO.getInputTemplate()),
						COOKIENAME, testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), pathParams);

				Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
						response.asString(),
						getJsonFromTemplate(outputtestcase.get(i).toString(), testCaseDTO.getOutputTemplate()),
						testCaseDTO.isCheckErrorsOnlyInResponse(), response.getStatusCode());
				Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

				if (!OutputValidationUtil.publishOutputResult(ouputValid))
					throw new AdminTestException("Failed at output validation");
			}
		}

		else {
			response = putWithPathParamsBodyAndCookie(ApplnURI + testCaseDTO.getEndPoint(), inputJson, COOKIENAME,
					testCaseDTO.getRole(), testCaseDTO.getTestCaseName(), pathParams);

			Map<String, List<OutputValidationDto>> ouputValid = OutputValidationUtil.doJsonOutputValidation(
					response.asString(), outputJson, testCaseDTO.isCheckErrorsOnlyInResponse(),
					response.getStatusCode());
			Reporter.log(ReportUtil.getOutputValidationReport(ouputValid));

			if (!OutputValidationUtil.publishOutputResult(ouputValid))
				throw new AdminTestException("Failed at output validation");
		}

	}

	/**
	 * The method ser current test name to result
	 * 
	 * @param result
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}
}
