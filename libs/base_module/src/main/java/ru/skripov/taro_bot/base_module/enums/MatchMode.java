package ru.skripov.resume_back.base_module.enums;

/// Список вариантов обработки value для аннотации AnswerType
public enum MatchMode {
    /// Обычное сравнение через equals
    EXACT,
    /// Сравнение по startWith и equals второй части value
    /// Т.е. должно быть полное совпадение и префикса и второй части value. "start PRACTICE" в аннотации ожидает от пользователя "start PRACTICE"
    PREFIX,
    REGEX
}
