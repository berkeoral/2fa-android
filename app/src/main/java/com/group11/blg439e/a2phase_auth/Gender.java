package com.group11.blg439e.a2phase_auth;

/**
 * Created by berke on 10/1/2017.
 */
public class Gender
{
    private String type;

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [type = "+type+"]";
    }
}

