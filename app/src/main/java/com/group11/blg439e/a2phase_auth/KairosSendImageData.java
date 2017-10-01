package com.group11.blg439e.a2phase_auth;

/**
 * Created by berke on 10/1/2017.
 */
public class KairosSendImageData {
    private String subject_id;

    private String gallery_name;

    public String getSubject_id ()
    {
        return subject_id;
    }

    public void setSubject_id (String subject_id)
    {
        this.subject_id = subject_id;
    }

    public String getGallery_name ()
    {
        return gallery_name;
    }

    public void setGallery_name (String gallery_name)
    {
        this.gallery_name = gallery_name;
    }

    @Override
    public String toString()
    {
        return "KairosSendImageData [subject_id = "+subject_id+", gallery_name = "+gallery_name+"]";
    }
}
