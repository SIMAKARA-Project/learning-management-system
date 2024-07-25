package org.simakara.learning_management_system.service;

import org.simakara.learning_management_system.dto.request.CreateQuizRequest;
import org.simakara.learning_management_system.dto.request.UpdateQuizRequest;
import org.simakara.learning_management_system.dto.response.QuizResponse;

import java.util.List;

public interface QuizService {

    QuizResponse createQuiz(CreateQuizRequest request);

    QuizResponse getQuizInfo(String code);

    List<QuizResponse> getQuizzes(String name, int page, int content);

    QuizResponse updateQuiz(UpdateQuizRequest request, String code);

    void deleteQuiz(String code);
}
