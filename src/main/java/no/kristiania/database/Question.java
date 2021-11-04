package no.kristiania.database;

import java.util.List;

public class Question {
    private Long id;
    private String title;
    private String description;
    private List<AnswerOption> answerOptions;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAnswerOptions(List<AnswerOption> answerOptions) {
        this.answerOptions = answerOptions;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}