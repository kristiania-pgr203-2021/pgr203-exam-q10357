package no.kristiania;


import no.kristiania.database.*;
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

    public static Question sqlInjectionAttempt(Survey survey){
        //Adding surveyID to question to avoid CONSTRAINT conflicts
        Question question = new Question();
        question.setSurveyId(survey.getId());

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

    public static Survey exampleSurvey(){
        Survey survey = new Survey();

        survey.setName(pickOne("One survey", "Second survey", "Third Survey"));
        return survey;
    }

    public static Question exampleQuestion(Survey survey) {
        Question question = new Question();
        question.setSurveyId((survey.getId()));
        question.setTitle(pickOne("Food", "Cats", "Test", "Fails..", "Pass!", "Give us an A?"));
        question.setDescription(pickOne("Are you hungry?","Do you like cats?", "Do you like A's?",
                "Why are you failing?", "Have you heard about ENJIN coin?"));
        question.setLowLabel(pickOne("Helt uenig", "Ikke sant", "Lite", "Svært lite"));
        question.setHighLabel(pickOne("Helt enig", "Veldig sant", "Mye", "Svært mye"));
        return question;
    }



}
