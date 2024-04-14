import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.remote.http.Contents;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.openqa.selenium.remote.http.HttpMethod.GET;

public class TestMain {
    ChromeDriver driver;
    DevTools devTools;
    JavascriptExecutor js;
    int counter = 1;

    @DataProvider(name = "dp")
    public static Object[][] provide() {
        Object[] array = new Object[]{-1, 0, 1, 549, 999, 1000, 1001, 2487, 9999, 10_000, 10_001, 56_800, 99_999, 100_000,
                100_001, 205_601, 999_999, 1_000_000, 1_000_001, 4_856_123, 999_999_999, 1_000_000_000, 1_000_000_001,
                3_100_000_000L, 999_999_999_999L, 1_000_000_000_000L, 1_000_000_000_001L, 292_100_000_000_567L,
                999_999_999_999_999L, 1_000_000_000_000_000L, 1_000_000_000_000_001L, 349_912_000_000_000_000L,
                0.87354, 10.150, 3.4893, 100_000.0, null, "\"seven\"", "\"ОДИН\"", "\"\"", "\" \"", "\"`~!@#$%^&*()_+=-[]}{|?/.,:;'\""};
        Object[][] result = new Object[array.length * 3][2];

        for (int j = 0; j < 3; j++) {
            String xpath = "";
            String counterName = "";

            switch (j) {
                case (0):
                    xpath = Page.WATER_XPATH;
                    counterName = "water";
                    break;
                case (1):
                    xpath = Page.CO2_XPATH;
                    counterName = "co2";
                    break;
                case (2):
                    xpath = Page.ENERGY_XPATH;
                    counterName = "energy";
                    break;
            }
            for (int i = 0; i < array.length; i++) {
                int position = i + (array.length * j);
                result[position][0] = new String[]{xpath, counterName};
                result[position][1] = array[i];
            }
        }

        return result;
    }

    @BeforeTest
    public void initialize() {
        driver = new ChromeDriver();
        devTools = driver.getDevTools();
        devTools.createSession();
        js = driver;
        driver.manage().window().maximize();
    }

    @Test(dataProvider = "dp")
    public void avitoTest(String[] stringData, Object value) throws InterruptedException {
        changeResponse(stringData[1], value);
        driver.get(Page.URL);

        WebElement element = (new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(stringData[0]))));
        js.executeScript("arguments[0].scrollIntoView(false);", element);
        Thread.sleep(1000);
        File screenshotFile = element.getScreenshotAs(OutputType.FILE);
        String screenshotPath = "output/" + counter + ".png";
        try {
            BufferedImage screenshot = ImageIO.read(screenshotFile);
            ImageIO.write(screenshot, "png", new File(screenshotPath));
            counter++;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @AfterTest
    public void cleanUp() {
        driver.close();
    }

    public void changeResponse(String stringToChange, Object value) {
        StringBuilder json = new StringBuilder("{\n" +
                "    \"result\": {\n" +
                "        \"blocks\": {\n" +
                "            \"personalImpact\": {\n" +
                "                \"data\": {\n" +
                "                    \"co2\": 0,\n" +
                "                    \"energy\": 0,\n" +
                "                    \"materials\": 0,\n" +
                "                    \"pineYears\": 0,\n" +
                "                    \"water\": 0\n" +
                "                }\n" +
                "            }\n" +
                "        },\n" +
                "        \"isAuthorized\": false\n" +
                "    },\n" +
                "    \"status\": \"ok\"\n" +
                "}");
        int startIndex = json.indexOf(stringToChange) + stringToChange.length();
        json.delete(startIndex + 3, startIndex + 4).insert(startIndex + 2, value);
        new NetworkInterceptor(driver, Route.matching(req -> GET == req.getMethod() && req.getUri().endsWith("/init"))
                .to(() -> req -> new HttpResponse()
                        .setStatus(200)
                        .addHeader("Content-type", "application/json")
                        .setContent(Contents.utf8String(json))
                ));
    }
}