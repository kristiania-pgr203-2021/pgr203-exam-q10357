package no.kristiania;


import no.kristiania.database.*;
import no.kristiania.database.daos.*;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TestData {
    private static int low = 5;
    private static int max = 10;

    public static DataSource testDataSource(String sourceDb){
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:" + sourceDb + ";DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }


    public static SurveyDao fillSurveyTable(DataSource dataSource) {
        SurveyDao surveyDao = new SurveyDao(dataSource);

        for(int i = 0; i < generateRandomNumber(low, max); i++){
            Survey survey = exampleSurvey();
            try {
                surveyDao.save(survey);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return surveyDao;
    }

    public static QuestionDao fillQuestionTable(DataSource dataSource, SurveyDao surveyDao) {
        QuestionDao questionDao = new QuestionDao(dataSource);

        try {
            for(Survey s: surveyDao.listAll()){
                for(int i = 0; i < generateRandomNumber(low, max); i++){
                    Question question = exampleQuestion(s);
                    questionDao.save(question);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questionDao;
    }

    public static AnswerOptionDao fillOptionTable(DataSource dataSource, QuestionDao questionDao) {
        AnswerOptionDao answerOptionDao = new AnswerOptionDao(dataSource);

        try {
            for(Question q: questionDao.listAll()){
                for(int i = 0; i < generateRandomNumber(low, max); i++){
                    AnswerOption option = exampleOption(q);
                    answerOptionDao.save(option);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answerOptionDao;
    }

    public static UserAnswerDao fillUserAnswerTable(DataSource dataSource, AnswerOptionDao answerOptionDao, SessionUserDao sessionUserDao) throws SQLException {
        UserAnswerDao userAnswerDao = new UserAnswerDao(dataSource);
        try {
            for(AnswerOption ao: answerOptionDao.listAll()){
                for(int i = 0; i < generateRandomNumber(low, max); i++){
                    UserAnswer userAnswer = exampleUserAnswer(answerOptionDao, sessionUserDao);
                    sessionUserDao.retrieve(generateRandomNumber(low,sessionUserDao.listAll().size()));
                    userAnswerDao.save(userAnswer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userAnswerDao;
    }

    public static SessionUserDao fillSessionUserTable(DataSource dataSource){
        SessionUserDao sessionUserDao = new SessionUserDao(dataSource);

        for(int i = 0; i < generateRandomNumber(low, max); i++){
            try {
                SessionUser sessionUser = exampleSessionsUser();
                sessionUserDao.save(sessionUser);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sessionUserDao;
    }

    public static int generateRandomNumber(int low, int max){
        return ThreadLocalRandom.current().nextInt(low, max);
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

    public static SessionUser exampleSessionsUser(){
        SessionUser sessionUser = new SessionUser();
        sessionUser.setCookieId("EXAMPLEUSER");
        return sessionUser;
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

    public static UserAnswer exampleUserAnswer(AnswerOptionDao answerOptionDao, SessionUserDao sessionUserDao) throws SQLException {
        UserAnswer answer = new UserAnswer();
        answer.setAnswerOptionId(answerOptionDao.retrieve(generateRandomNumber(low, answerOptionDao.listAll().size())).getId());
        answer.setSessionUserId(sessionUserDao.retrieve(2).getId());
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
        question.setLowLabel(pickOne("Totally disagree", "Disagree", "Not one bit", "No"));
        question.setHighLabel(pickOne("Agree", "Totally agree", "Yes", "A lot"));
        return question;
    }



}
