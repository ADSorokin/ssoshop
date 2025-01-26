
package ru.alexds.ccoshop.service;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import java.util.Arrays;

/**
 * Класс для вычисления скорректированного косинусного сходства между товарами.
 * Этот класс расширяет AbstractItemSimilarity и предоставляет реализацию для
 * расчета схожести на основе скорректированных рейтингов пользователей.
 */
@Slf4j
public class AdjustedCosineSimilarity extends AbstractItemSimilarity {

    /**
     * Конструктор класса AdjustedCosineSimilarity.
     *
     * @param dataModel Модель данных Mahout, используемая для получения предпочтений пользователей
     * @throws TasteException если модель данных не содержит необходимых значений
     */
    public AdjustedCosineSimilarity(DataModel dataModel) throws TasteException {
        super(dataModel);
        // Проверка наличия значений предпочтений в модели данных
        Preconditions.checkArgument(dataModel.hasPreferenceValues(), "DataModel не имеет необходимых значений");
    }

    /**
     * Вычисляет скорректированное косинусное сходство между двумя товарами.
     *
     * @param itemID1 Идентификатор первого товара
     * @param itemID2 Идентификатор второго товара
     * @return Значение скорректированного косинусного сходства
     * @throws TasteException если произошла ошибка при получении данных из DataModel
     */
    @Override
    public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
        log.debug("Calculating adjusted cosine similarity between items: {} and {}", itemID1, itemID2);

        DataModel dataModel = getDataModel();
        PreferenceArray prefs1 = dataModel.getPreferencesForItem(itemID1); // Получаем рейтинги для первого товара
        PreferenceArray prefs2 = dataModel.getPreferencesForItem(itemID2); // Получаем рейтинги для второго товара

        int length1 = prefs1.length(); // Количество рейтингов для первого товара
        int length2 = prefs2.length(); // Количество рейтингов для второго товара

        if (length1 == 0 || length2 == 0) {
            log.warn("No ratings found for one or both items: {} and {}", itemID1, itemID2);
            return Double.NaN; // Если нет данных по одному из товаров, вернуть NaN
        }

        int index1 = 0;
        int index2 = 0;
        double numerator = 0.0; // Числитель для формулы скорректированного косинусного сходства
        double denominator1 = 0.0; // Первая часть знаменателя
        double denominator2 = 0.0; // Вторая часть знаменателя

        while (index1 < length1 && index2 < length2) {
            long userID1 = prefs1.getUserID(index1); // ID пользователя из первого массива предпочтений
            long userID2 = prefs2.getUserID(index2); // ID пользователя из второго массива предпочтений

            if (userID1 == userID2) { // Если пользователи совпадают
                double rating1 = prefs1.getValue(index1); // Рейтинг пользователя для первого товара
                double rating2 = prefs2.getValue(index2); // Рейтинг пользователя для второго товара

                double meanUserRating = getUserMeanRating(userID1, dataModel); // Средний рейтинг пользователя
                double adjRating1 = rating1 - meanUserRating; // Корректируем первый рейтинг
                double adjRating2 = rating2 - meanUserRating; // Корректируем второй рейтинг

                numerator += adjRating1 * adjRating2; // Обновляем числитель
                denominator1 += adjRating1 * adjRating1; // Обновляем первую часть знаменателя
                denominator2 += adjRating2 * adjRating2; // Обновляем вторую часть знаменателя

                index1++;
                index2++;
            } else if (userID1 < userID2) {
                index1++; // Переходим к следующему пользователю в первом массиве предпочтений
            } else {
                index2++; // Переходим к следующему пользователю во втором массиве предпочтений
            }
        }

        double denominator = Math.sqrt(denominator1) * Math.sqrt(denominator2); // Полный знаменатель
        if (denominator == 0.0) {
            log.warn("Denominator is zero for items: {} and {}", itemID1, itemID2);
            return Double.NaN; // Если знаменатель равен нулю, возвращаем NaN
        }

        double similarity = numerator / denominator; // Финальное значение скорректированного косинусного сходства
        log.debug("Calculated similarity between items: {} and {}: {}", itemID1, itemID2, similarity);
        return similarity;
    }

    /**
     * Вычисляет скорректированное косинусное сходство для нескольких товаров.
     *
     * @param itemID1 Идентификатор первого товара
     * @param itemID2s Массив идентификаторов других товаров
     * @return Массив значений скорректированного косинусного сходства
     * @throws TasteException если произошла ошибка при вычислении сходства
     */
    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
        log.debug("Calculating similarities for item: {} with other items", itemID1);

        double[] results = new double[itemID2s.length];
        for (int i = 0; i < itemID2s.length; i++) {
            results[i] = itemSimilarity(itemID1, itemID2s[i]);
        }

        log.debug("Calculated similarities: {}", Arrays.toString(results));
        return results;
    }

    /**
     * Расчет среднего рейтинга для пользователя.
     *
     * @param userID   Идентификатор пользователя
     * @param dataModel Модель данных Mahout, используемая для получения предпочтений пользователя
     * @return Среднее значение рейтингов пользователя
     * @throws TasteException если произошла ошибка при получении данных из DataModel
     */
    private double getUserMeanRating(long userID, DataModel dataModel) throws TasteException {
        log.debug("Calculating mean rating for user ID: {}", userID);

        PreferenceArray userPreferences = dataModel.getPreferencesFromUser(userID); // Получаем все предпочтения пользователя
        double total = 0.0;
        for (Preference preference : userPreferences) {
            total += preference.getValue(); // Суммируем все значения рейтингов
        }

        double meanRating = total / userPreferences.length(); // Считаем средний рейтинг
        log.debug("Calculated mean rating for user ID {}: {}", userID, meanRating);
        return meanRating;
    }
}