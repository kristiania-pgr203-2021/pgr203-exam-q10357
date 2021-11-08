package no.kristiania.HttpTest;

import no.kristiania.TestData;
import no.kristiania.database.daos.QuestionDao;
import no.kristiania.database.daos.QuestionDaoTest;
import no.kristiania.http.HttpClient;
import no.kristiania.http.HttpServer;
import no.kristiania.http.controllers.ListQuestionController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private HttpServer server;
    private static final DataSource dataSource = TestData.testDataSource("HttpServerTest");
    private static final QuestionDao questionDao = new QuestionDao(dataSource);

    @BeforeEach
    public void setup() throws IOException{
        server = new HttpServer(0, dataSource);
        server.start();
        server.setRoot(Paths.get("src/main/resources"));
    }

    @Test
    void shouldReturnRolesFromServer() throws IOException, SQLException {
        questionDao.save(TestData.exampleQuestion());
        server.addController("/api/questionOptions", new ListQuestionController(questionDao));
        HttpClient client = new HttpClient("localhost", server.getActualPort(), "/api/questionOptions");
        assertEquals(
                "<option value=" + questionDao.retrieve(1l).getId() + ">" + questionDao.retrieve(1L).getDescription() + "</option>",

                client.responseMessage.messageBody
        );
    }
}
