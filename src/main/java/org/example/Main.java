package org.example;

import java.util.*;

public class Main {
    // Параметры задачи
    private static final int NUM_ITEMS = 50;          // Количество предметов
    private static final int MAX_WEIGHT = 10;         // Максимальный вес предмета
    private static final int CONTAINER_CAPACITY = 50; // Вместимость одного контейнера
    private static final int POPULATION_SIZE = 100;   // Размер популяции
    private static final int NUM_GENERATIONS = 200;   // Количество поколений
    private static final double MUTATION_RATE = 0.1;  // Вероятность мутации

    private static final Random random = new Random();

    public static void main(String[] args) {
        // Генерация предметов
        List<Integer> items = generateItems();

        // Запуск генетического алгоритма
        List<Integer> bestSolution = geneticAlgorithm(items);
        int bestFitness = fitness(bestSolution);

        System.out.println("Лучшее найденное решение: " + bestFitness + " контейнеров");
    }

    private static List<Integer> generateItems() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < NUM_ITEMS; i++) {
            items.add(random.nextInt(MAX_WEIGHT) + 1);
        }
        System.out.println("Список предметов: " + items);
        return items;
    }


    private static List<Integer> geneticAlgorithm(List<Integer> items) {
        // Инициализируем популяцию случайными решениями
        List<List<Integer>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(generateSolution(items));
        }
        System.out.println("Вся популяция: " + population);
        for (int generation = 0; generation < NUM_GENERATIONS ; generation++) {
            //Селекция и создание нового поколения
            List<List<Integer>> newPopulation = new ArrayList<>();
            for (int i = 0; i < POPULATION_SIZE; i++) {
                List<Integer> parent1 = selection(population);
                List<Integer> parent2 = selection(population);
                System.out.println("Отобранные родители: " + parent1 + ", " + parent2);
                List<Integer> child = crossover(parent1, parent2);
                mutate(child);
                System.out.println("Ребёнок после кроссовера родителей и мутации: " + child);
                // добавляем ребёнка в новое поколение
                newPopulation.add(child);
            }

            // Старое поколение умирает, появляется новое
            population = newPopulation;
            System.out.println("Новое поколение: " + population);

            // Находим наилучшее решение в текущей популяции
            List<Integer> bestSolution = population.stream().min(Comparator.comparingInt(Main::fitness)).orElse(null);
            assert bestSolution != null;
            int bestFitness = fitness(bestSolution);

            System.out.println("Поколение " + generation + ": лучшее решение требует " + bestFitness + " контейнеров");
        }
        // Возвращаем лучшее найденное решение
        return population.stream().min(Comparator.comparingInt(Main::fitness)).orElse(null);
    }

    // Функция для оценки приспособленности (число контейнеров, необходимых для данного распределения)
    private static int fitness(List<Integer> solution) {
        List<List<Integer>> containers = new ArrayList<>();
        for (int weight : solution) {
            boolean placed = false;
            for (List<Integer> container : containers) {
                if (container.stream().mapToInt(Integer::intValue).sum() + weight <= CONTAINER_CAPACITY) {
                    container.add(weight);
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                List<Integer> newContainer = new ArrayList<>();
                newContainer.add(weight);
                containers.add(newContainer);
            }
        }
        return containers.size();
    }

    // Мутация: случайная перестановка двух элементов
    private static void mutate(List<Integer> solution) {
        if (random.nextDouble() < MUTATION_RATE) {
            int idx1 = random.nextInt(NUM_ITEMS);
            int idx2 = random.nextInt(NUM_ITEMS);
            Collections.swap(solution, idx1, idx2);
        }
    }

    // Кроссовер: комбинируем две родительские особи для создания новой
    private static List<Integer> crossover(List<Integer> parent1, List<Integer> parent2) {
        int crossoverPoint = random.nextInt(NUM_ITEMS - 1) + 1;
        List<Integer> child = new ArrayList<>(parent1.subList(0, crossoverPoint));
        child.addAll(parent2.subList(crossoverPoint, NUM_ITEMS));
        return child;
    }

    // Отбор родителей
    private static List<Integer> selection(List<List<Integer>> population) {
        List<List<Integer>> tournament = new ArrayList<>();
        int tournamentSize = 5;
        for (int i = 0; i < tournamentSize; i++) {
            tournament.add(population.get(random.nextInt(population.size())));
        }
        return tournament.stream().min(Comparator.comparingInt(Main::fitness)).orElseThrow();
    }

    // Генерация случайного решения
    private static List<Integer> generateSolution(List<Integer> items) {
        List<Integer> solution = new ArrayList<>(items);
        Collections.shuffle(solution);
        return solution;
    }
}