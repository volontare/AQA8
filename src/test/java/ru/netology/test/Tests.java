package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.SQL.SqlData;
import ru.netology.data.DataHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class Tests {
    DataHelper.UserData user = new DataHelper().getUser();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @AfterAll
    static void clean() {
        SqlData.cleanDefaultData();
    }

    @Test
    void shouldSuccessAuthorization() {
        SqlData.createUser(user);

        new LoginPage().validLogin(user.getLogin(), user.getPassword()).validVerify(SqlData.getVerificationCode(user.getId()));
    }

    @Test
    void shouldBlockUserAfterThirdInvalidPassword() {
        SqlData.createUser(user);

        new LoginPage()
                .invalidLogin(user.getLogin())
                .clearFields()
                .invalidLogin(user.getLogin())
                .clearFields()
                .invalidLogin(user.getLogin());

        String status = SqlData.getUserStatus(user.getId());

        Assertions.assertEquals("blocked", status);
    }
}