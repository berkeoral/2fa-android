package com.group11.blg439e.a2phase_auth;

import java.util.ArrayList;


public class KairosResponse
{
    private String face_id;

    private Images[] images;

    private Errors[] Errors;

    public String getFace_id ()
    {
        return face_id;
    }

    public void setFace_id (String face_id)
    {
        this.face_id = face_id;
    }

    public Images[] getImages ()
    {
        return images;
    }

    public void setImages (Images[] images)
    {
        this.images = images;
    }

    public Errors[] getErrors ()
    {
        return Errors;
    }

    public void setErrors (Errors[] Errors)
    {
        this.Errors = Errors;
    }

    @Override
    public String toString()
    {
        return "KairosResponse [face_id = "+face_id+", images = "+images+", Errors = "+Errors+"]";
    }
}
