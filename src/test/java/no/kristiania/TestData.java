package no.kristiania;


import no.kristiania.database.AnswerOption;
import no.kristiania.database.Question;
import no.kristiania.database.SessionUser;
import no.kristiania.database.UserAnswer;
import org.flywaydb.core.Flyway;
import org.h2.engine.Session;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestData {
    public static DataSource testDataSource(String sourceDb){
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:" + sourceDb + ";DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }


    public static String pickOne(String... alternatives){
        return alternatives[new Random().nextInt(alternatives.length)];
    }

    //Static methods to generate elements
    public static AnswerOption exampleOption(Question question){
        AnswerOption option = new AnswerOption();
        option.setText(TestData.pickOne("Something", "AnotherThing", "This", "That", "Whatever"));
        option.setQuestionId(question.getId());
        return option;
    }

    public static Question sqlInjectionAttempt(){
        Question question = new Question();
        question.setTitle("SqlInjection");
        question.setDescription("'); DROP TABLE question; --");
        question.setLowLabel("1");
        question.setHighLabel("10");
        return question;
    }

    public static UserAnswer exampleUserAnswer(Question q, AnswerOption ao, SessionUser user){
        UserAnswer answer = new UserAnswer();
        answer.setQuestionId(q.getId());
        answer.setAnswerOptionId(ao.getId());
        answer.setSessionUserId(user.getId());
        answer.setValue(new Random().nextInt(11));
        return answer;
    }

    public static Question exampleQuestion() {
        Question question = new Question();
        question.setTitle(TestData.pickOne("Food", "Cats", "Test", "Fails..", "Pass!", "Give us an A?"));
        question.setDescription(TestData.pickOne("Are you hungry?","Do you like cats?", "Do you like A's?",
                "Why are you failing?", "Have you heard about ENJIN coin?"));
        question.setLowLabel("1");
        question.setHighLabel("10");
        return question;
    }



}
