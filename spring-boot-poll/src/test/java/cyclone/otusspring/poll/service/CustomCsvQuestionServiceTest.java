package cyclone.otusspring.poll.service;

import cyclone.otusspring.poll.config.CsvProperties;
import cyclone.otusspring.poll.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {CsvQuestionService.class}
        , properties = {"spring.config.location=classpath:/custom-csv.yml"})
@EnableConfigurationProperties(CsvProperties.class)
class CustomCsvQuestionServiceTest extends AbstractCsvQuestionServiceTest {

    @Autowired
    public CustomCsvQuestionServiceTest(CsvQuestionService csvQuestionService) {
        super(csvQuestionService);
    }

    static final Question[] TEST_QUESTIONS = new Question[]{
            new Question("First question?", "aa1", "aa1", "bb1", "cc1", "dd1")
            , new Question("Second question with | separator?", "bb2", "aa2", "bb2", "cc2", "dd2")
            , new Question("Third question after comment?", "cc3", "aa3", "bb3", "cc3", "dd3")
            , new Question("--Fourth starts with comment symbol?", "dd4", "aa4", "bb4", "cc4", "dd4")
            , new Question("Fifth question with separator in answers?", "c|c5", "aa5", "bb5", "c|c5", "dd5")
    };

    @Override
    protected Question[] getExpectedQuestions() {
        return TEST_QUESTIONS;
    }
}
