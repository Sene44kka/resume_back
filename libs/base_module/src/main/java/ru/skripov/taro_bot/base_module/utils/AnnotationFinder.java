package ru.skripov.resume_back.base_module.utils;

import lombok.extern.log4j.Log4j2;
import ru.skripov.resume_back.base_module.enums.MatchMode;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Log4j2
public class AnnotationFinder {
    /**
     * Находит экземпляр с указанной аннотацией и значением
     * @param instances Список экземпляров для поиска
     * @param annotationClass Класс аннотации
     * @param expectedValue Ожидаемое значение аннотации
     * @param <T> Тип объектов в списке
     * @return Найденный экземпляр
     * @throws IllegalStateException Если экземпляр не найден или найдено несколько
     */
    public static <T> T findByAnnotation(List<T> instances, Class<? extends Annotation> annotationClass, String expectedValue) {
        List<T> matchingInstances = instances.stream()
                .filter(instance -> {
                    Annotation annotation = instance.getClass().getAnnotation(annotationClass);
                    Optional<String> value = getAnnotationStringValue(annotation);
                    MatchMode matchMode = getAnnotationMathMode(annotation).orElse(MatchMode.EXACT);
                    return filteredByMatchMode(value, expectedValue, matchMode);
                })
                .toList();

        if (matchingInstances.isEmpty()) {
            throw new IllegalStateException("Не найден экземпляр с @" + annotationClass.getSimpleName() + "(\"" + expectedValue + "\")");
        }

        if (matchingInstances.size() > 1) {
            throw new IllegalStateException("Найдено несколько экземпляров с @" + annotationClass.getSimpleName() + "(\"" + expectedValue + "\"): " + matchingInstances);
        }

        return matchingInstances.get(0);
    }

    private static boolean filteredByMatchMode(Optional<String> value, String expectedValue, MatchMode matchMode) {
        if (value.isEmpty()) {
            return false;
        }

        switch (matchMode) {
            case EXACT -> {
                //пытаемся разбить коллбэк на два слова
                List<String> expectedValuePreEnd = Arrays.stream(expectedValue.split(" ")).toList();
                //если второго слова нет(параметра старта) делаем простое сравнение
                if (expectedValuePreEnd.size() <= 1) {
                    return expectedValue.equals(value.get());
                }
                //если параметр старта есть - ищем action по нему
                String endWithExpectedValue = expectedValuePreEnd.get(1);
                return endWithExpectedValue.equals(value.get());
            }
            case PREFIX -> {
                List<String> valuePreEnd = Arrays.stream(value.toString().split(" ")).toList();
                List<String> expectedValuePreEnd = Arrays.stream(expectedValue.split(" ")).toList();
                String prefixValue = valuePreEnd.get(0);
                String prefixExpectedValue = expectedValuePreEnd.get(0);
                String endWithValue = valuePreEnd.get(1);
                String endWithExpectedValue = expectedValuePreEnd.get(1);
                return prefixExpectedValue.equals(prefixValue)
                        && endWithExpectedValue.equals(endWithValue);
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Получает строковое значение из аннотации
     */
    private static Optional<String> getAnnotationStringValue(Annotation annotation) {

        if (annotation == null) {
            return Optional.empty();
        }

        try {
            //Пытаемся получить значение через метод value()
            String value = (String) annotation.annotationType()
                    .getMethod("value")
                    .invoke(annotation);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static Optional<MatchMode> getAnnotationMathMode(Annotation annotation) {

        if (annotation == null) {
            return Optional.empty();
        }

        try {
            MatchMode matchMode = (MatchMode) annotation.annotationType()
                    .getMethod("matchMode")
                    .invoke(annotation);
            return Optional.ofNullable(matchMode);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}