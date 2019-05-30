package mondo.orderingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Naruto on 8/19/2016.
 */
public class SaveSharedPreference
{
    static final String PREF_USER_Email= "email";
    static final String PREF_USER_Password= "password";
    static final String PREF_Employee_id= "employee_id";
    static final String PREF_Employee_FN= "employee_first_name";
    static final String PREF_Employee_LN= "employee_last_name";
    static final String PREF_Company_name= "company_name";
    static final String PREF_Customer_id= "customer_id";
    static final String PREF_GCM_REG_ID= "gcm_red_id";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setEmail(Context ctx, String email)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_Email, email);
        editor.commit();
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_Email, "");
    }

    public static void setPassword(Context ctx, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_Password, password);
        editor.commit();
    }

    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_Password, "");
    }

    public static void setEmployeeId(Context ctx, String employee_id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_Employee_id, employee_id);
        editor.commit();
    }

    public static String getEmployeeId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_Employee_id, "");
    }

    public static void setEmployeeFN(Context ctx, String employee_fn)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_Employee_FN, employee_fn);
        editor.commit();
    }

    public static String getEmployeeFN(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_Employee_FN, "");
    }

    public static void setEmployeeLN(Context ctx, String employee_ln)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_Employee_LN, employee_ln);
        editor.commit();
    }

    public static String getEmployeeLN(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_Employee_LN, "");
    }


    public static void setCompany_name(Context ctx, String Company_name)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_Company_name, Company_name);
        editor.commit();
    }

    public static String getCompany_name(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_Company_name, "");
    }

    public static void setCustomer_id(Context ctx, String Customer_id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_Customer_id, Customer_id);
        editor.commit();
    }

    public static String getCustomer_id(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_Customer_id, "");
    }

    public static void setReg_id(Context ctx, String reg_id)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_GCM_REG_ID, reg_id);
        editor.commit();
    }

    public static String getReg_id(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_GCM_REG_ID, "");
    }
}
