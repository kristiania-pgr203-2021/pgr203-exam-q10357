create table userAnswer (
    question_id INTEGER REFERENCES question(id),
    answerOption_id INTEGER REFERENCES answerOption(id),
    sessionUser_id INTEGER REFERENCES sessionUser(id),
    value integer
)