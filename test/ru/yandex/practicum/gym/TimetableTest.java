package ru.yandex.practicum.gym;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.*;

public class TimetableTest {

    private Timetable timetable;
    private Group group;
    private Coach coach;

    @BeforeEach
    void init() {
        timetable = new Timetable();
        group = new Group("Акробатика для детей", Age.CHILD, 60);
        coach = new Coach("Васильев", "Николай", "Сергеевич");
    }

    @Test
    @DisplayName("Одна тренировка в день")
    void testGetTrainingSessionsForDaySingleSession() {
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        Assertions.assertEquals(1, mondaySessions.size());
        List<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        Assertions.assertTrue(tuesdaySessions.isEmpty());
    }

    @Test
    @DisplayName("Несколько тренировок в разные дни с сортировкой по времени")
    void testGetTrainingSessionsForDayMultipleSessions() {
        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        TrainingSession mondayChildTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        List<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        Assertions.assertEquals(1, mondaySessions.size());

        List<TrainingSession> thursdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        Assertions.assertEquals(2, thursdaySessions.size());
        Assertions.assertEquals(thursdayChildTrainingSession, thursdaySessions.get(0));
        Assertions.assertEquals(thursdayAdultTrainingSession, thursdaySessions.get(1));

        List<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        Assertions.assertTrue(tuesdaySessions.isEmpty());
    }

    @Test
    @DisplayName("Получение тренировки по дню и времени")
    void testGetTrainingSessionsForDayAndTime() {
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> mondayAtOneHour = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        Assertions.assertEquals(1, mondayAtOneHour.size());
        List<TrainingSession> mondayAtTwoHours = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0));
        Assertions.assertTrue(mondayAtTwoHours.isEmpty());
    }

    @Test
    @DisplayName("Две тренировки в одно и то же время")
    void testTwoSessionsAtSameTime() {
        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        Coach anotherCoach = new Coach("Семёнов", "Виктор", "Константинович");

        TrainingSession childSession = new TrainingSession(group, coach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(18, 0));
        TrainingSession adultSession = new TrainingSession(groupAdult, anotherCoach,
                DayOfWeek.WEDNESDAY, new TimeOfDay(18, 0));

        timetable.addNewTrainingSession(childSession);
        timetable.addNewTrainingSession(adultSession);

        List<TrainingSession> wednesdayAtSix = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.WEDNESDAY, new TimeOfDay(18, 0));
        Assertions.assertEquals(2, wednesdayAtSix.size());
        Assertions.assertTrue(wednesdayAtSix.contains(childSession));
        Assertions.assertTrue(wednesdayAtSix.contains(adultSession));

        List<TrainingSession> wednesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.WEDNESDAY);
        Assertions.assertEquals(2, wednesdaySessions.size());
    }

    @Test
    @DisplayName("Сортировка тренировок в рамках дня вне зависимости от очередности добавления")
    void testDaySortedByTime() {
        TrainingSession eveningSession = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(18, 0));
        TrainingSession morningSession = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(10, 0));
        TrainingSession lateAfternoonSession = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(14, 30));
        TrainingSession afternoonSession = new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(14, 0));

        timetable.addNewTrainingSession(eveningSession);
        timetable.addNewTrainingSession(morningSession);
        timetable.addNewTrainingSession(lateAfternoonSession);
        timetable.addNewTrainingSession(afternoonSession);

        List<TrainingSession> fridaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY);
        Assertions.assertEquals(4, fridaySessions.size());
        Assertions.assertEquals(morningSession, fridaySessions.get(0));
        Assertions.assertEquals(afternoonSession, fridaySessions.get(1));
        Assertions.assertEquals(lateAfternoonSession, fridaySessions.get(2));
        Assertions.assertEquals(eveningSession, fridaySessions.get(3));
    }

    @Test
    @DisplayName("Пустое расписание должно вернуть пустые списки")
    void testEmptyTimetable() {
        for (DayOfWeek day : DayOfWeek.values()) {
            Assertions.assertTrue(timetable.getTrainingSessionsForDay(day).isEmpty());
            Assertions.assertTrue(timetable.getTrainingSessionsForDayAndTime(day, new TimeOfDay(12, 0)).isEmpty());
        }
    }

    @Test
    @DisplayName("Подсчёт тренировок по тренерам при пустом расписании")
    void testCountEmptyTimetable() {
        List<CounterOfTrainings> counters = timetable.getCountByCoaches();
        Assertions.assertTrue(counters.isEmpty());
    }

    @Test
    @DisplayName("Подсчёт тренировок у одного тренера")
    void testCountSingleCoach() {
        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.MONDAY, new TimeOfDay(18, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(18, 0)));

        List<CounterOfTrainings> counters = timetable.getCountByCoaches();
        Assertions.assertEquals(1, counters.size());
        Assertions.assertEquals(coach, counters.getFirst().getCoach());
        Assertions.assertEquals(3, counters.getFirst().getCount());
    }

    @Test
    @DisplayName("Список тренеров упорядочен по убыванию кол-ва занятий")
    void testCountSortedDesc() {
        Coach coachWithThreeSessions = new Coach("Семёнов", "Виктор", "Константинович");
        Coach coachWithTwoSessions = new Coach("Измайлов", "Кирилл", "Владимирович");

        timetable.addNewTrainingSession(new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(10, 0)));

        timetable.addNewTrainingSession(new TrainingSession(group, coachWithThreeSessions,
                DayOfWeek.MONDAY, new TimeOfDay(12, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachWithThreeSessions,
                DayOfWeek.WEDNESDAY, new TimeOfDay(12, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachWithThreeSessions,
                DayOfWeek.FRIDAY, new TimeOfDay(12, 0)));

        timetable.addNewTrainingSession(new TrainingSession(group, coachWithTwoSessions,
                DayOfWeek.TUESDAY, new TimeOfDay(15, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachWithTwoSessions,
                DayOfWeek.THURSDAY, new TimeOfDay(15, 0)));

        List<CounterOfTrainings> counters = timetable.getCountByCoaches();
        Assertions.assertEquals(3, counters.size());

        Assertions.assertEquals(coachWithThreeSessions, counters.get(0).getCoach());
        Assertions.assertEquals(3, counters.get(0).getCount());

        Assertions.assertEquals(coachWithTwoSessions, counters.get(1).getCoach());
        Assertions.assertEquals(2, counters.get(1).getCount());

        Assertions.assertEquals(coach, counters.get(2).getCoach());
        Assertions.assertEquals(1, counters.get(2).getCount());
    }

    @Test
    @DisplayName("Тренеры с одинаковыми ФИО считаются одним")
    void testCountSameCoachByName() {
        Coach sameCoachAnotherObject = new Coach("Васильев", "Николай", "Сергеевич");

        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, sameCoachAnotherObject, DayOfWeek.TUESDAY, new TimeOfDay(10, 0)));

        List<CounterOfTrainings> counters = timetable.getCountByCoaches();
        Assertions.assertEquals(1, counters.size());
        Assertions.assertEquals(2, counters.getFirst().getCount());
    }
}
