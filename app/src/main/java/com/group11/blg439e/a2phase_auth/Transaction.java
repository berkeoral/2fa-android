package com.group11.blg439e.a2phase_auth;

public class Transaction
{
    private String timestamp;

    private String topLeftY;

    private String topLeftX;

    private String height;

    private String status;

    private String subject_id;

    private String face_id;

    private String width;

    private String quality;

    private String confidence;

    private String gallery_name;

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTopLeftY ()
    {
        return topLeftY;
    }

    public void setTopLeftY (String topLeftY)
    {
        this.topLeftY = topLeftY;
    }

    public String getTopLeftX ()
    {
        return topLeftX;
    }

    public void setTopLeftX (String topLeftX)
    {
        this.topLeftX = topLeftX;
    }

    public String getHeight ()
    {
        return height;
    }

    public void setHeight (String height)
    {
        this.height = height;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getSubject_id ()
    {
        return subject_id;
    }

    public void setSubject_id (String subject_id)
    {
        this.subject_id = subject_id;
    }

    public String getFace_id ()
    {
        return face_id;
    }

    public void setFace_id (String face_id)
    {
        this.face_id = face_id;
    }

    public String getWidth ()
    {
        return width;
    }

    public void setWidth (String width)
    {
        this.width = width;
    }

    public String getQuality ()
    {
        return quality;
    }

    public void setQuality (String quality)
    {
        this.quality = quality;
    }

    public String getConfidence ()
    {
        return confidence;
    }

    public void setConfidence (String confidence)
    {
        this.confidence = confidence;
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
        return "ClassPojo [timestamp = "+timestamp+", topLeftY = "+topLeftY+", topLeftX = "+topLeftX+", height = "+height+", status = "+status+", subject_id = "+subject_id+", face_id = "+face_id+", width = "+width+", quality = "+quality+", confidence = "+confidence+", gallery_name = "+gallery_name+"]";
    }
}
