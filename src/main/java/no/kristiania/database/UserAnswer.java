package no.kristiania.database;

public class UserAnswer {
    private Long id;
    /**
     * Can example be the cookieId
     */
    private String userId;
    private Long questionId;
    private Question question;
    private Long answerOptionId;
    private AnswerOption answerOption;
    private Boolean selected;
}
