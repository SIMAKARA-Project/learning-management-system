package org.simakara.learning_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simakara.learning_management_system.dto.request.CreateCourseRequest;
import org.simakara.learning_management_system.dto.request.UpdateCourseRequest;
import org.simakara.learning_management_system.dto.response.CourseResponse;
import org.simakara.learning_management_system.handler.ValidatorHandler;
import org.simakara.learning_management_system.mapper.CourseResponseMapper;
import org.simakara.learning_management_system.model.Course;
import org.simakara.learning_management_system.repository.CourseRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.simakara.learning_management_system.mapper.CourseResponseMapper.toCourseResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceIMPL implements CourseService{

    private final CourseRepository courseRepo;

    private final ValidatorHandler validatorHandler;

    @Override
    public CourseResponse createCourse(CreateCourseRequest request) {

        log.info("Request received, validating...");

        validatorHandler.validate(request);

        log.info("Validation done, creating course...");

        Course createdCourse = Course
                .builder()
                .name(request.name())
                .description(request.description())
                .build();

        log.info("Saving course to database...");

        courseRepo.save(createdCourse);

        log.info("Done saving!");

        return toCourseResponse(createdCourse);
    }

    @Override
    public CourseResponse getCourseInfo(String code) {

        log.info("Searching course with code {}...", code);

        Course course = courseRepo.findByCode(code)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course doesn't exist")
                );

        log.info("Course found!");

        return toCourseResponse(course);
    }

    @Override
    public List<CourseResponse> getCourses(String name, int page, int content) {

        if (name.isBlank()) {

            log.info("Getting all courses");

            return courseRepo
                    .findAll(PageRequest.of(page, content))
                    .stream()
                    .map(CourseResponseMapper::toCourseResponse)
                    .toList();
        }

        log.info("Getting courses");

        return courseRepo.findByNameContainingIgnoreCase(
                name,
                PageRequest.of(page, content)
                )
                .stream()
                .map(CourseResponseMapper::toCourseResponse)
                .toList();
    }

    @Override
    public CourseResponse updateCourse(UpdateCourseRequest request, String code) {

        Course course = courseRepo
                .findByCode(code)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course doesn't exist")
                );

        if (!request.name().isBlank()) {
            log.info("Setting new name");
            course.setName(request.name());
        }

        if (!request.description().isBlank()) {
            log.info("Setting new description");
            course.setDescription(request.description());
        }

        log.info("Saving new course info...");

        courseRepo.save(course);

        return toCourseResponse(course);
    }

    @Override
    public void deleteCourse(String code) {
        Course course = courseRepo
                .findByCode(code)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course doesn't exist")
                );

        log.info("Deleting course {}...", code);

        courseRepo.delete(course);
    }
}
