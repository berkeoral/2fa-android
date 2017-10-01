package com.group11.blg439e.a2phase_auth;

/**
 * Created by berke on 10/1/2017.
 */
public class Errors
{
    private String Message;

    private String ErrCode;

    public String getMessage ()
    {
        return Message;
    }

    public void setMessage (String Message)
    {
        this.Message = Message;
    }

    public String getErrCode ()
    {
        return ErrCode;
    }

    public void setErrCode (String ErrCode)
    {
        this.ErrCode = ErrCode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Message = "+Message+", ErrCode = "+ErrCode+"]";
    }
}
