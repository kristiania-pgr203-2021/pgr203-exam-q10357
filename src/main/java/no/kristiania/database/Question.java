package no.kristiania.database;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private Long id;
    private Long surveyId;
    private String title;
    private String description;
    private String lowLabel;
    private String highLabel;

    private List<AnswerOption> answerOptions = new ArrayList<>();

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

    public String getLowLabel() {
        return lowLabel;
    }

    public String getHighLabel() {
        return highLabel;
    }

    public void setLowLabel(String lowLabel) {
        this.lowLabel = lowLabel;
    }

    public void setHighLabel(String highLabel) {
        this.highLabel = highLabel;
    }

    public List<AnswerOption> getAnswerOptions() {
        return answerOptions;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    @Override
    public String toString() {
        return "Question{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
