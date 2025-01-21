package ru.alexds.ccoshop.service;


import com.google.common.base.Preconditions;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

public class AdjustedCosineSimilarity extends AbstractItemSimilarity {

    public AdjustedCosineSimilarity(DataModel dataModel) throws TasteException {
        super(dataModel);
        Preconditions.checkArgument(dataModel.hasPreferenceValues(), "DataModel не имеет необходимых значений ");
    }

    @Override
    public double itemSimilarity(long itemID1, long itemID2) throws TasteException {
        DataModel dataModel = getDataModel();
        PreferenceArray prefs1 = dataModel.getPreferencesForItem(itemID1);
        PreferenceArray prefs2 = dataModel.getPreferencesForItem(itemID2);

        int length1 = prefs1.length();
        int length2 = prefs2.length();

        if (length1 == 0 || length2 == 0) {
            return Double.NaN; // Если нет данных по одному из товаров, вернуть NaN
        }

        int index1 = 0;
        int index2 = 0;

        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;

        while (index1 < length1 && index2 < length2) {
            long userID1 = prefs1.getUserID(index1);
            long userID2 = prefs2.getUserID(index2);

            if (userID1 == userID2) { // Совпадают ли пользователи
                double rating1 = prefs1.getValue(index1);
                double rating2 = prefs2.getValue(index2);

                double meanUserRating = getUserMeanRating(userID1, dataModel);

                double adjRating1 = rating1 - meanUserRating; // Вычитается средний рейтинг пользователя
                double adjRating2 = rating2 - meanUserRating;

                numerator += adjRating1 * adjRating2; // Числитель: произведение скорректированных значений
                denominator1 += adjRating1 * adjRating1; // Первая часть знаменателя
                denominator2 += adjRating2 * adjRating2; // Вторая часть знаменателя

                index1++;
                index2++;
            } else if (userID1 < userID2) {
                index1++;
            } else {
                index2++;
            }
        }

        double denominator = Math.sqrt(denominator1) * Math.sqrt(denominator2); // Полный знаменатель

        if (denominator == 0.0) {
            return Double.NaN; // Если знаменатель равен нулю, возвращаем NaN
        }

        return numerator / denominator; // Косинусное сходство
    }

    @Override
    public double[] itemSimilarities(long itemID1, long[] itemID2s) throws TasteException {
        double[] results = new double[itemID2s.length];
        for (int i = 0; i < itemID2s.length; i++) {
            results[i] = itemSimilarity(itemID1, itemID2s[i]);
        }
        return results;
    }

    /**
     * Расчет среднего рейтинга для пользователя
     */
    private double getUserMeanRating(long userID, DataModel dataModel) throws TasteException {
        PreferenceArray userPreferences = dataModel.getPreferencesFromUser(userID);
        double total = 0.0;
        for (Preference preference : userPreferences) {
            total += preference.getValue();
        }
        return total / userPreferences.length();
    }
}
