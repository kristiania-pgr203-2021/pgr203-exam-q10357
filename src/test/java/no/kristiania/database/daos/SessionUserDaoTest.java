package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.SessionUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SessionUserDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(SessionUserDaoTest.class.getName());
    private static final SessionUserDao userDao = new SessionUserDao(dataSource);

    @Test
    void saveNewSessionUserShouldHaveId() throws SQLException {
        SessionUser user = new SessionUser();
        user.setCookieId("testCookie");

        userDao.save(user);

        Assertions.assertThat(user.getId())
                .isNotZero()
                .isNotNegative();
    }

    @Test
    public void shouldRetrievedSavedUser() throws SQLException {
        SessionUser user = new SessionUser();
        user.setCookieId("testCookie");

        userDao.save(user);
        SessionUser retrievedUser = userDao.retrieve(user.getId());

        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(retrievedUser);
    }
}
