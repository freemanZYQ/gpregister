package com.googleplay.register;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.google.network.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.TextUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by apple on 17/5/1.
 */
public class AutoRegister {

        private static final Log log = LogFactory.getLog(AutoRegister.class);

        WebDriver driver;
        String[] zh={"张","测试零零1","test001zhang","test001zhang001","test001zhang001","1990","10","17098215908","test001zhang@cs.com"};

        @Before
        public void beforeTest(){
            //driver = new HtmlUnitDriver(true);
            System.setProperty("webdriver.chrome.driver", "chromedriver");
            DesiredCapabilities cap = DesiredCapabilities.chrome();
            ChromeOptions options = new ChromeOptions();
            cap.setCapability(ChromeOptions.CAPABILITY, options);
            options.addArguments("test-type");
            options.addArguments("--incognito");
            options.addArguments("start-maximized");

            driver = new ChromeDriver(cap);

//		System.setProperty("webdriver.gecko.driver", "src\\geckodriver.exe");
//		Desiredcap cap = Desiredcap.firefox();
//		cap.setCapability("marionette", true);
//		cap.setCapability("firefox_binary", "D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"); //for windows
//		driver = new FirefoxDriver(cap);
            //	driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS); // 识别元素时的超时时间
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); // 页面加载时的超时时间
            driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS); // 异步JS脚本的超时时间
        }

        @Test
        public void test1(){
            Navigation navigation=driver.navigate();
            navigation.to("https://accounts.google.com/SignUp?service=accountsettings&continue=https%3A%2F%2Fmyaccount.google.com%2Fintro%3Futm_source%3DOGB&lp=1&hl=zh-TW");
            getElement(By.id("LastName")).clear();
            getElement(By.id("LastName")).sendKeys(zh[0]);
            getElement(By.id("FirstName")).clear();
            getElement(By.id("FirstName")).sendKeys(zh[1]);
            getElement(By.id("GmailAddress")).clear();
            getElement(By.id("GmailAddress")).sendKeys(zh[2]);
            getElement(By.id("Passwd")).clear();
            getElement(By.id("Passwd")).sendKeys(zh[3]);
            getElement(By.id("PasswdAgain")).clear();
            getElement(By.id("PasswdAgain")).sendKeys(zh[4]);
            getElement(By.id("BirthYear")).clear();
            getElement(By.id("BirthYear")).sendKeys(zh[5]);
            getElement(By.id("month-label")).click();
            getElement(By.id(":1")).click();
            getElement(By.id("BirthDay")).clear();
            getElement(By.id("BirthDay")).sendKeys(zh[6]);
            getElement(By.id("Gender")).click();
            getElement(By.id(":g")).click();
//		getElement(By.xpath(".//*[@class='i18n_phone_number_input-menu i18n_phone_number_input-loRes']")).click();
//		sleep(3000);
//		scrollTo(getElement(By.xpath(".//*[text()='中国']")));
//		sleep(1000);
//		getElement(By.xpath(".//*[text()='中国']")).click();

            getElement(By.id("RecoveryPhoneNumber")).clear();
            getElement(By.id("RecoveryPhoneNumber")).sendKeys("+86");
            getElement(By.id("RecoveryEmailAddress")).clear();
            getElement(By.id("RecoveryEmailAddress")).sendKeys(zh[8]);
            //getElement(By.id("CountryCode")).click();
            //getElement(By.xpath(".//div[contains(text(),'美国')]")).click();//不能选中国，否则后面手机无法验证
            sleep(2000);
            getElement(By.id("submitbutton")).click();
            sleep(2000);
            getElement(By.xpath(".//div[@onclick=\"scrollOnePage()\"]")).click();
            sleep(1000);
            getElement(By.xpath(".//div[@onclick=\"scrollOnePage()\"]")).click();
            sleep(1500);
            getElement(By.id("iagreebutton")).click();
            sleep(1500);
            getElement(By.xpath(".//*[@class='i18n_phone_number_input-menu i18n_phone_number_input-loRes']")).click();
            sleep(3000);
            scrollTo(getElement(By.xpath(".//*[text()='中国']")));
            sleep(1000);
            getElement(By.xpath(".//*[text()='中国']")).click();

            String token = null;
            String phoneNumber = null;
            boolean hasGetPhoneNumber = false;
            int getPhoneNumber = 0;
            boolean isGetPhoneError = false;
            while (!hasGetPhoneNumber) {
                getPhoneNumber++;
                if (getPhoneNumber > 8) {
                    log.info("获取电话超过8次，退出");
                    isGetPhoneError = true;
                    break;
                }
                log.info("获取电话号码" + getPhoneNumber);
                token = getEMALoginToken();
                phoneNumber = getEMAPhoneNumber(token);
                if (!TextUtils.isEmpty(phoneNumber)) {
                    hasGetPhoneNumber = true;
                }
            }

            if(isGetPhoneError){
                log.info("获取电话时候出现错误，退出");
                driver.quit();
            }


            if (TextUtils.isEmpty(phoneNumber)) {
                log.info("获取电话为空，退出");
                driver.quit();
            }
            phoneNumber = phoneNumber.substring(0, phoneNumber.length() - 1);
            log.info("获取电话号码是:" + phoneNumber);


            getElement(By.id("signupidvinput")).sendKeys(phoneNumber);
            getElement(By.id("next-button")).click();
            sleep(1500);


            String code = null;
            log.info("等待收到短信");
            int tryNumber = 0;
            String result = null;
            boolean isGetCodeError = false;
            while (true) {
                tryNumber++;
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                result = getCode(token);
                if (!TextUtils.isEmpty(result)) {
                    break;
                }
                if (tryNumber > 6) {
                    isGetCodeError = true;
                    log.info("尝试次数过多");
                    log.info("异常情况解除号码");
                    releasePhoneNumber(token, phoneNumber);
                    break;
                }
            }

            if (isGetCodeError) {
                log.info("获取短信出现异常，退出");
                driver.quit();
            }

            log.info("解除号码");
            releasePhoneNumber(token, phoneNumber);

            log.info(".............验证码是:" + result);
            int index = result.indexOf("G-");

            code = result.substring(index + 2, index + 8);
            log.info("验证码是:" + code);
            boolean isMatch = code.matches("([0-9]{6})");
            if (!isMatch) {
                log.info("验证码格式不匹配");
                driver.quit();
            }


            getElement(By.id("verify-phone-input")).sendKeys(code);//手机验证码
            getElement(By.xpath(".//*[@name='VerifyPhone']")).click();

        }

        //@After
        public void afterTest(){
            driver.close();
            driver.quit();
        }

        /**
         *  滚动到指定的元素。
         * @param webElement WebElement元素。
         */
        public void scrollTo(WebElement webElement)  {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
        }

        public WebElement getElement(By by){
            return driver.findElement(by);
        }

        public void sleep(int m){
            try {
                Thread.sleep(m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public static void releasePhoneNumber(String token,String phone){
            String url = "http://api.ema666.com/Api/userReleasePhone?token=%s&phoneList=%s-133&Code=UTF8";
            url = String.format(url, token,phone);
            RequestEntity<Void> entity = new RequestEntity<Void>(url,Request.Method.GET,Void.class,new HashMap(),null);
            ProtoRequest<Void> request = new ProtoRequest<>(entity);
            HttpStack httpStack = new HurlStack();
            ((HurlStack)httpStack).setUseProxy(false);
            BasicNetwork network = new BasicNetwork(httpStack);
            try {
                NetworkResponse response = network.performRequest(request);
                String result = new String(response.data);
                if(!TextUtils.isEmpty(result) && result.equals("Null")){
                    result = null;
                }
                log.info("解除电话"+result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        public static void releaseAllPhoneNumber(String token){
            String url = "http://api.ema666.com/Api/userReleaseAllPhone?token=%s";
            url = String.format(url,token);
            RequestEntity<Void> entity = new RequestEntity<Void>(url,Request.Method.GET,Void.class,new HashMap(),null);
            ProtoRequest<Void> request = new ProtoRequest<>(entity);
            HttpStack httpStack = new HurlStack();
            ((HurlStack)httpStack).setUseProxy(false);
            BasicNetwork network = new BasicNetwork(httpStack);
            try {
                NetworkResponse response = network.performRequest(request);
                String result = new String(response.data);
                if(!TextUtils.isEmpty(result) && result.equals("Null")){
                    result = null;
                }
                log.info("释放所有电话号码："+result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public static String  getCode(String token){
            String result = null;
            String url = "http://api.ema666.com/Api/userGetMessage?token=%s&Code=UTF8";
            url = String.format(url, token);
            RequestEntity<Void> entity = new RequestEntity<Void>(url,Request.Method.GET,Void.class,new HashMap(),null);
            ProtoRequest<Void> request = new ProtoRequest<>(entity);
            HttpStack httpStack = new HurlStack();
            ((HurlStack)httpStack).setUseProxy(false);
            BasicNetwork network = new BasicNetwork(httpStack);
            try {
                NetworkResponse response = network.performRequest(request);
                result = new String(response.data);
                if(!TextUtils.isEmpty(result) && result.equals("Null")){
                    result = null;
                }
                if(!result.contains("G-")){
                    result = null;
                }
                log.info("获取验证码是"+result);
                //MSG&133&13075765945&【Google】“G-179452”是您的 Google 验证码。[End]
//            result.matches("([0-9]{6})");
            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        public static String getEMAPhoneNumber(String token){
            String phoneNumber = null;
            if(TextUtils.isEmpty(token)){
                log.info("获取token失败");
            }
//            String url = "http://api.ema666.com/Api/userGetPhone?ItemId=133&token=%s&PhoneType=0&Code=UTF8";
            String url = "http://api.ema666.com/Api/userGetPhone?ItemId=133&token="+token+"&PhoneType=0&Area=%E6%B5%B7%E5%8D%97";
//            url = String.format(url, token);
            RequestEntity<Void> entity = new RequestEntity<Void>(url, Request.Method.GET,Void.class,new HashMap(),null);
            ProtoRequest<Void> request = new ProtoRequest<>(entity);
            HttpStack httpStack = new HurlStack();
            ((HurlStack)httpStack).setUseProxy(false);
            BasicNetwork network = new BasicNetwork(httpStack);
            try {
                NetworkResponse response = network.performRequest(request);
                phoneNumber = new String(response.data);
                log.info("获取电话是："+phoneNumber);
            }catch (Exception e){
                e.printStackTrace();
            }
            return phoneNumber;
        }

        public static String  getEMALoginToken() {
            String token = null;
            String url = "http://api.ema666.com/Api/userLogin?uName=freeman&pWord=300304youmi&Developer=CxpQgWDcztcwjMjIomW2NA%3d%3d&Code=UTF8";
            RequestEntity<Void> entity = new RequestEntity<Void>(url, Request.Method.GET, Void.class, new HashMap(), null);
            ProtoRequest<Void> request = new ProtoRequest<>(entity);
            HttpStack httpStack = new HurlStack();
            ((HurlStack) httpStack).setUseProxy(false);
            BasicNetwork network = new BasicNetwork(httpStack);
            try {
                NetworkResponse response = network.performRequest(request);
                String result = new String(response.data);
                if (!TextUtils.isEmpty(result)) {
                    String[] array = result.split("&");
                    token = array[0];
                }
                log.info("登录ema令牌：" + token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return token;
        }
}
