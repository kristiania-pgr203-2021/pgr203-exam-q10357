package no.kristiania.database;

public class UserAnswer {
    private Long id;
    /**
     * Can example be the cookieId
     */
    private String userId;
    private Long questionId;
    private Long answerOptionId;
    private AnswerOption answerOption;
    private Boolean selected;

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getQuestionId() {
        return questionId;
    }


    public Long getAnswerOptionId() {
        return answerOptionId;
    }

    public AnswerOption getAnswerOption() {
        return answerOption;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setAnswerOptionId(Long answerOptionId) {
        this.answerOptionId = answerOptionId;
    }

    public void setAnswerOption(AnswerOption answerOption) {
        this.answerOption = answerOption;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
