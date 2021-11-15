create table userAnswer
(
    id              serial primary key,
    question_id     INTEGER REFERENCES question (id),
    answerOption_id INTEGER REFERENCES answerOption (id),
    sessionUser_id  INTEGER REFERENCES sessionUser (id),
    value           integer
)