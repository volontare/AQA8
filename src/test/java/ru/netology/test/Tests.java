package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.SQL.SqlData;
import ru.netology.data.DataHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class Tests {
    DataHelper.UserData user = new DataHelper().getUser();

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