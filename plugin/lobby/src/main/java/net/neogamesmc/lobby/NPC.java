package net.neogamesmc.lobby;

import net.neogamesmc.core.issue.Issues;

import java.lang.reflect.Field;

/**
 * Created by nokoa on 6/30/2017.
 */
public class NPC {
    public static Object privateField(String fieldName, Class clazz, Object object)
    {
        Field _field;
        Object _object = null;

        try
        {
            _field = clazz.getDeclaredField(fieldName);

            _field.setAccessible(true);
            _object = _field.get(object);
        }
        catch (Exception e)
        {
            Issues.handle("Private field from class", e);
        }

        return _object;
    }
}
