package no.kristiania.database.daos;

import no.kristiania.TestData;
import no.kristiania.database.AnswerOption;
import no.kristiania.database.SessionUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SessionUserDaoTest {
    private static final DataSource dataSource = TestData.testDataSource(SessionUserDaoTest.class.getName());
    private static SessionUserDao sessionUserDao;

    @BeforeAll
    public static void fillDataBase() throws SQLException {
        sessionUserDao = TestData.fillSessionUserTable(dataSource);
    }
    @Test
    void saveNewSessionUserShouldHaveId() throws SQLException {
        SessionUser user = new SessionUser();
        user.setCookieId("testCookie");

        sessionUserDao.save(user);

        Assertions.assertThat(user.getId())
                .isNotZero()
                .isNotNegative();
    }

    @Test
    public void shouldRetrievedSavedUser() throws SQLException {
        SessionUser user = new SessionUser();
        user.setCookieId("testCookie");

        sessionUserDao.save(user);
        SessionUser retrievedUser = sessionUserDao.retrieve(user.getId());

        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(retrievedUser);
    }

    @Test
    public void shouldListAllUsers() throws SQLException {
        SessionUser user = TestData.exampleSessionsUser();
        sessionUserDao.save(user);

        List<SessionUser> users = sessionUserDao.listAll();

        assertThat(!users.isEmpty());
        Assertions.assertThat(users)
                .extracting(SessionUser::getId)
                .contains(user.getId());

    }
}
