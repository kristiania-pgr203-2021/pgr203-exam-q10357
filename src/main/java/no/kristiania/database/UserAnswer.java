package no.kristiania.database;

public class UserAnswer {
    private Long id;

    private Long questionId;
    private Question question;

    private Long answerOptionId;
    private AnswerOption answerOption;

    private Long sessionUserId;
    private SessionUser sessionUser;

    private Integer Value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        setQuestionId(question.getId());
    }

    public Long getAnswerOptionId() {
        return answerOptionId;
    }

    public void setAnswerOptionId(Long answerOptionId) {
        this.answerOptionId = answerOptionId;
    }

    public AnswerOption getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(AnswerOption answerOption) {
        this.answerOption = answerOption;
        setAnswerOptionId(answerOption.getId());
    }

    public Long getSessionUserId() {
        return sessionUserId;
    }

    public void setSessionUserId(Long sessionUserId) {
        this.sessionUserId = sessionUserId;
    }

    public SessionUser getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(SessionUser sessionUser) {
        this.sessionUser = sessionUser;
        setSessionUserId(sessionUser.getId());
    }

    public Integer getValue() {
        return Value;
    }

    public void setValue(Integer value) {
        Value = value;
    }

    @Override
    public String toString() {
        return "UserAnswer{" +
                "questionId=" + questionId +
                ", answerOptionId=" + answerOptionId +
                ", sessionUserId=" + sessionUserId +
                ", Value=" + Value +
                '}';
    }
}
