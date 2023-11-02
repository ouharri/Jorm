package org.jorm.Jdbc.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class fieldsHandler {


    public static void getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(List.of(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

    }

    public static Object getObjectWithAllFields(Object obj) {
        try {
            Class<?> clazz = obj.getClass();
            List<Field> allFields = getAllFields(clazz);

            for (Field field : allFields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                field.set(obj, value);
                field.setAccessible(false);
            }

            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'objet avec tous les champs et valeurs.", e);
        }
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null) {
            fields.addAll(List.of(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }

}
