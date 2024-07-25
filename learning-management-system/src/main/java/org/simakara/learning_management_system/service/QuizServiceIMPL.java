package org.simakara.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simakara.learning_management_system.dto.request.CreateQuizRequest;
import org.simakara.learning_management_system.dto.request.UpdateQuizRequest;
import org.simakara.learning_management_system.dto.response.QuizResponse;
import org.simakara.learning_management_system.handler.ValidatorHandler;
import org.simakara.learning_management_system.mapper.QuizResponseMapper;
import org.simakara.learning_management_system.model.Quiz;
import org.simakara.learning_management_system.model.enums.QuizType;
import org.simakara.learning_management_system.repository.QuizRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.simakara.learning_management_system.mapper.QuizResponseMapper.toQuizResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizServiceIMPL implements QuizService {

    private final QuizRepository quizRepo;

    private final ValidatorHandler validatorHandler;

    @Override
    public QuizResponse createQuiz(CreateQuizRequest request) {

        log.info("Create quiz request received, validating...");

        validatorHandler.validate(request);

        if (quizRepo.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quiz: " + request.name() + " already exist."
            );
        }

        if (
                QuizType.TRYOUT.equals(request.type())
                        && Objects.isNull(request.expiration())
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expiration date is required for tryouts"
            );
        }

        if (request.accessible().isAfter(request.expiration())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Expiration cannot be before accessible date"
            );
        }

        log.info("Validated. Creating quiz...");

        Quiz quiz = Quiz.builder()
                .name(request.name())
                .description(request.description())
                .type(request.type())
                .accessibleAt(request.accessible())
                .expiredAt(request.expiration())
                .build();

        log.info("Quiz Created");

        quizRepo.save(quiz);

        return toQuizResponse(quiz);
    }

    @Override
    public QuizResponse getQuizInfo(String code) {

        Quiz quiz = quizRepo
                .findByCode(code)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Course doesn't exist."
                        )
                );

        return toQuizResponse(quiz);
    }

    @Override
    public List<QuizResponse> getQuizzes(String name, int page, int content) {

        if (name.isBlank()) {

            log.info("Finding all quizzes");

            return quizRepo
                    .findAll(PageRequest.of(page, content))
                    .stream()
                    .map(QuizResponseMapper::toQuizResponse)
                    .toList();
        }

        log.info("Finding quizzes with name containing {}", name);

        return quizRepo
                .findByNameContainingIgnoreCase(name, PageRequest.of(page, content))
                .stream()
                .map(QuizResponseMapper::toQuizResponse)
                .toList();
    }

    @Override
    public QuizResponse updateQuiz(UpdateQuizRequest request, String code) {

        validatorHandler.validate(request);

        Quiz quiz = quizRepo.findByCode(code)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz doesn't exist.")
                );

        String name = request.name();
        String description = request.description();
        QuizType type = request.type();
        LocalDateTime expiration = request.expiration();

        if (!name.isBlank()) {
            quiz.setName(name);
        }

        if (!description.isBlank()) {
            quiz.setDescription(description);
        }

        if (Objects.nonNull(type) && !type.toString().isBlank()) {
            quiz.setType(type);
        }

        if (Objects.nonNull(expiration) && !expiration.toString().isBlank()) {
            if (quiz.getAccessibleAt().isAfter(expiration)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Expiration cannot be before accessible date"
                );
            }

            quiz.setExpiredAt(expiration);
        }

        quizRepo.save(quiz);

        return toQuizResponse(quiz);
    }

    @Override
    public void deleteQuiz(String code) {
        Quiz quiz = quizRepo.findByCode(code)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Quiz doesn't exist"
                        )
                );

        quizRepo.delete(quiz);
    }
}
