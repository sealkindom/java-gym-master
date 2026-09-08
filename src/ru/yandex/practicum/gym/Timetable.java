package ru.yandex.practicum.gym;

import java.util.*;

public class Timetable {

    private final Map<DayOfWeek, TreeMap<TimeOfDay, List<TrainingSession>>> timetable = new HashMap<>();

    public void addNewTrainingSession(TrainingSession trainingSession) {
        TimeOfDay timeTraining = trainingSession.getTimeOfDay();
        DayOfWeek dayTraining = trainingSession.getDayOfWeek();

        TreeMap<TimeOfDay, List<TrainingSession>> dayMap = timetable.computeIfAbsent(dayTraining, dayOfWeek -> new TreeMap<>());

        List<TrainingSession> sessionsTimeList = dayMap.computeIfAbsent(timeTraining, timeOfDay -> new ArrayList<>());

        sessionsTimeList.add(trainingSession);
    }

    public List<TrainingSession> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        TreeMap<TimeOfDay, List<TrainingSession>> innerMap = timetable.get(dayOfWeek);
        List<TrainingSession> result = new ArrayList<>();
        if (innerMap == null) {
            return result;
        }

        for (List<TrainingSession> sessionsAtTime : innerMap.values()) {
            result.addAll(sessionsAtTime);
        }

        return result;
    }

    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        TreeMap<TimeOfDay, List<TrainingSession>> innerMap = timetable.get(dayOfWeek);
        List<TrainingSession> emptyList = new ArrayList<>();
        if (innerMap == null) {
            return emptyList;
        }

        return innerMap.getOrDefault(timeOfDay, emptyList);

    }

    public List<CounterOfTrainings> getCountByCoaches() {
        Map<Coach, Integer> counts = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            for (TrainingSession session : getTrainingSessionsForDay(day)) {
                Coach coach = session.getCoach();
                int count = counts.getOrDefault(coach, 0);
                counts.put(coach, count + 1);
            }
        }

        List<CounterOfTrainings> result = new ArrayList<>();
        for (Map.Entry<Coach, Integer> entry : counts.entrySet()) {
            result.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));
        }

        result.sort(new CounterOfTrainingsComparator());
        return result;

    }
}
