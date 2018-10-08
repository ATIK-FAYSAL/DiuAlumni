package com.atik_faysal.diualumni.important;

import android.content.Context;

public class DataValidator
{
    private Context context;

    public DataValidator(Context context)
    {
        this.context = context;
    }

    public boolean userDataValidator(String name,String stdId,String email,String pass,String address,String type,String batch,String gender) {

        try {
            if (name.isEmpty())
                return false;
            if (stdId.isEmpty())
                return false;
            if (email.isEmpty())
                return false;
            if (pass.isEmpty())
                return false;
            if (address.isEmpty())
                return false;
            if (type.isEmpty())
                return false;
            if (batch.isEmpty())
                return false;
            if(gender.isEmpty())
                return false;

            if (name.length() < 8 || name.length() > 30)
                return false;
            else {
                for (int i = 0; i < name.length(); i++) {
                    if (name.charAt(i) >= '0' && name.charAt(i) <= '9' ||
                         name.charAt(i) == ':' ||
                         name.charAt(i) == '*' ||
                         name.charAt(i) == '@' ||
                         name.charAt(i) == '!' ||
                         name.charAt(i) == '#' ||
                         name.charAt(i) == '&') return false;
                }
            }

            if (stdId.length() < 8 || stdId.length() > 15)
                return false;
            for (int i = 0; i < stdId.length(); i++) {
                if ((stdId.charAt(i) >= '0' && stdId.charAt(i) <= '9') || stdId.charAt(i) == '-') {

                } else
                    return false;
            }

            return    address.length() >= 15 &&
                 address.length() <= 50 &&
                 pass.length() >= 8 &&
                 pass.length() <= 16 &&
                 email.length() >= 15 &&
                 email.length() <= 50 &&
                 email.contains("@") &&
                 email.contains(".");

        }catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
