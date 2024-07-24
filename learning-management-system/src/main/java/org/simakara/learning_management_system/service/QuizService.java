package org.simakara.learning_management_system.service;

import org.simakara.learning_management_system.dto.request.CreateCourseRequest;
import org.simakara.learning_management_system.dto.request.UpdateQuizRequest;
import org.simakara.learning_management_system.dto.response.QuizResponse;

import java.util.List;

public interface QuizService {

    QuizResponse createQuiz(CreateCourseRequest request);

    QuizResponse getQuizInfo(String code);

    List<QuizResponse> getQuizzes(String name, int page, int content);

    QuizResponse updateQuiz(UpdateQuizRequest request);

    void deleteQuiz(String code);
}
