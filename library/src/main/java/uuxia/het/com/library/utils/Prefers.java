package uuxia.het.com.library.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by Android Studio.
 * Author: uuxia
 * Date: 2016-01-12 19:42
 * Description:
 */
/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: Prefers.java
 * Create: 2016/1/12 19:42
 */
public class Prefers {
    private static Prefers sInstance;
    private Context mContext;
    private static String prefersName = "daemon";

    public Prefers(Context context) {
        mContext = context;
    }

    /**
     * Prefer file: shared preference file, save, read
     * or remove value via this class.
     */
    public class PreferFile {
        SharedPreferences sp;

        public PreferFile(SharedPreferences sp) {
            this.sp = sp;
        }

        /**
         * 针对复杂类型存储<对象>
         *
         * @param key
         * @param object
         */
        @TargetApi(Build.VERSION_CODES.FROYO)
        public void setObject(String key, Object object) {
            if (mContext == null) {
                throw new IllegalArgumentException("context cannot be null");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {

                out = new ObjectOutputStream(baos);
                out.writeObject(object);
                String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, objectVal);
                editor.commit();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.FROYO)
        public <T> T getObject(String key, Class<T> clazz) {
            if (mContext == null) {
                throw new IllegalArgumentException("context cannot be null");
            }
            if (sp.contains(key)) {
                String objectVal = sp.getString(key, null);
                byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(bais);
                    T t = (T) ois.readObject();
                    if (t != null) {
                        System.out.println(t.toString());
                    }
                    return t;
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bais != null) {
                            bais.close();
                        }
                        if (ois != null) {
                            ois.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        public void save(String key, int value) {
            sp.edit().putInt(key, value).commit();
        }

        public void save(String key, long value) {
            sp.edit().putLong(key, value).commit();
        }

        public void save(String key, float value) {
            sp.edit().putFloat(key, value).commit();
        }

        public void save(String key, boolean value) {
            sp.edit().putBoolean(key, value).commit();
        }

        public void save(String key, String value) {
            sp.edit().putString(key, value).commit();
        }

        public int getInt(String key, int defValue) {
            return sp.getInt(key, defValue);
        }

        public long getLong(String key, long defValue) {
            return sp.getLong(key, defValue);
        }

        public float getFloat(String key, float defValue) {
            return sp.getFloat(key, defValue);
        }

        public boolean getBoolean(String key, boolean defValue) {
            return sp.getBoolean(key, defValue);
        }

        public String getString(String key, String defValue) {
            return sp.getString(key, defValue);
        }

        public void saveArray(String key, int[] array) {
            JSONArray json = new JSONArray();
            for (int value : array) {
                json.put(value);
            }

            save(key, json.toString());
        }

        public void saveArray(String key, Integer[] array) {
            JSONArray json = new JSONArray();
            for (int value : array) {
                json.put(value);
            }

            save(key, json.toString());
        }

        public void saveArray(String key, long[] array) {
            JSONArray json = new JSONArray();
            for (long value : array) {
                json.put(value);
            }

            save(key, json.toString());
        }

        public void saveArray(String key, float[] array) {
            JSONArray json = new JSONArray();
            for (float value : array) {
                try {
                    json.put(value);
                } catch (JSONException e) {
                    return;
                }
            }

            save(key, json.toString());
        }

        public void saveArray(String key, boolean[] array) {
            JSONArray json = new JSONArray();
            for (boolean value : array) {
                json.put(value);
            }

            save(key, json.toString());
        }

        public void saveArray(String key, String[] array) {
            JSONArray json = new JSONArray();
            for (String value : array) {
                json.put(value);
            }

            save(key, json.toString());
        }

        public int[] getIntArray(String key, int[] defValue) {
            int[] array;
            String json = getString(key, null);
            if (json == null) {
                return defValue;
            }

            try {
                JSONArray jsonArr = new JSONArray(json);
                array = new int[jsonArr.length()];
                for (int i = 0; i < array.length; i ++) {
                    array[i] = jsonArr.getInt(i);
                }
            } catch (JSONException e) {
                return defValue;
            }

            return array;
        }

        public Integer[] getIntArray(String key) {
            Integer[] array;
            String json = getString(key, null);
            if (json == null) {
                return null;
            }

            try {
                JSONArray jsonArr = new JSONArray(json);
                array = new Integer[jsonArr.length()];
                for (int i = 0; i < array.length; i ++) {
                    array[i] = jsonArr.getInt(i);
                }
            } catch (JSONException e) {
                return null;
            }
            return array;
        }

        public long[] getLongArray(String key, long[] defValue) {
            long[] array;
            String json = getString(key, null);
            if (json == null) {
                return defValue;
            }

            try {
                JSONArray jsonArr = new JSONArray(json);
                array = new long[jsonArr.length()];
                for (int i = 0; i < array.length; i ++) {
                    array[i] = jsonArr.getLong(i);
                }
            } catch (JSONException e) {
                return defValue;
            }

            return array;
        }

        public float[] getFloatArray(String key, float[] defValue) {
            float[] array;
            String json = getString(key, null);
            if (json == null) {
                return defValue;
            }

            try {
                JSONArray jsonArr = new JSONArray(json);
                array = new float[jsonArr.length()];
                for (int i = 0; i < array.length; i ++) {
                    array[i] = (float)jsonArr.getDouble(i);
                }
            } catch (JSONException e) {
                return defValue;
            }

            return array;
        }

        public boolean[] getBooleanArray(String key, boolean[] defValue) {
            boolean[] array;
            String json = getString(key, null);
            if (json == null) {
                return defValue;
            }

            try {
                JSONArray jsonArr = new JSONArray(json);
                array = new boolean[jsonArr.length()];
                for (int i = 0; i < array.length; i ++) {
                    array[i] = jsonArr.getBoolean(i);
                }
            } catch (JSONException e) {
                return defValue;
            }

            return array;
        }

        public String[] getStringArray(String key, String[] defValue) {
            String[] array;
            String json = getString(key, null);
            if (json == null) {
                return defValue;
            }

            try {
                JSONArray jsonArr = new JSONArray(json);
                array = new String[jsonArr.length()];
                for (int i = 0; i < array.length; i ++) {
                    array[i] = jsonArr.getString(i);
                }
            } catch (JSONException e) {
                return defValue;
            }

            return array;
        }

        public boolean contains(String key) {
            return sp.contains(key);
        }

        public void remove(String key) {
            sp.edit().remove(key).commit();
        }

        public void clear() {
            sp.edit().clear().commit();
        }
    }

    /**
     * The global default shared preference util instance.
     * This instance is automatically created with this method.
     *
     * @param  context context
     * @return         the single instance
     */
    public static Prefers with(Context context) {
        synchronized (Prefers.class) {
            if (sInstance == null) {
                sInstance = new Prefers(context);
            }
        }

        return sInstance;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private SharedPreferences getPreferHoney(Context context, String preferName) {
        return context.getSharedPreferences(preferName, Context.MODE_MULTI_PROCESS);
    }

    /** get shared preferences according to api */
    private SharedPreferences getPrefer(Context context, String preferName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return getPreferHoney(context, preferName);
        }

        return context.getSharedPreferences(preferName, Context.MODE_PRIVATE);
    }

    /**
     * Load default shared preferences(packagename_preferences).
     *
     * @return {@link PreferFile}
     */
    public PreferFile load() {
        if (sInstance == null) {
            throw new IllegalStateException("call with(context) first");
        }

        if (mContext == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        return new PreferFile(sp);
    }

    /**
     * Load shared preferences according to prefer name.
     *
     * @param  preferName the name of shared preference
     * @return            {@link PreferFile}
     */
    public PreferFile load(String preferName) {
        if (sInstance == null) {
            throw new IllegalStateException("call with(context) first");
        }

        if (mContext == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        SharedPreferences sp = getPrefer(mContext, preferName);

        return new PreferFile(sp);
    }

    public static PreferFile init(Context context) {
        return with(context).load(prefersName);
    }


}
