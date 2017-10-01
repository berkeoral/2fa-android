package com.group11.blg439e.a2phase_auth;

/**
 * Created by berke on 10/1/2017.
 */
public class Attributes
{
    private String other;

    private String glasses;

    private String age;

    private String white;

    private String lips;

    private Gender gender;

    private String black;

    private String hispanic;

    private String asian;

    public String getOther ()
    {
        return other;
    }

    public void setOther (String other)
    {
        this.other = other;
    }

    public String getGlasses ()
    {
        return glasses;
    }

    public void setGlasses (String glasses)
    {
        this.glasses = glasses;
    }

    public String getAge ()
    {
        return age;
    }

    public void setAge (String age)
    {
        this.age = age;
    }

    public String getWhite ()
    {
        return white;
    }

    public void setWhite (String white)
    {
        this.white = white;
    }

    public String getLips ()
    {
        return lips;
    }

    public void setLips (String lips)
    {
        this.lips = lips;
    }

    public Gender getGender ()
    {
        return gender;
    }

    public void setGender (Gender gender)
    {
        this.gender = gender;
    }

    public String getBlack ()
    {
        return black;
    }

    public void setBlack (String black)
    {
        this.black = black;
    }

    public String getHispanic ()
    {
        return hispanic;
    }

    public void setHispanic (String hispanic)
    {
        this.hispanic = hispanic;
    }

    public String getAsian ()
    {
        return asian;
    }

    public void setAsian (String asian)
    {
        this.asian = asian;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [other = "+other+", glasses = "+glasses+", age = "+age+", white = "+white+", lips = "+lips+", gender = "+gender+", black = "+black+", hispanic = "+hispanic+", asian = "+asian+"]";
    }
}
