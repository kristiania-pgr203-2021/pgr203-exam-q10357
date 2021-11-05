package no.kristiania.database;

public class AnswerOption {
    private Long id;
    private Long questionId;
    private Question question;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.setQuestionId(question.getId());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


