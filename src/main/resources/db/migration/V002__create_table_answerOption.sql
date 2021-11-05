create table answerOption
(
    id          serial primary key,
    question_id INTEGER REFERENCES question (id),
    text        varchar(200)
)