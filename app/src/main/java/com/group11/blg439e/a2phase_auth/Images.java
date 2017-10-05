package com.group11.blg439e.a2phase_auth;


public class Images
{
    private Transaction transaction;

    private Attributes attributes;

    public Transaction getTransaction ()
    {
        return transaction;
    }

    public void setTransaction (Transaction transaction)
    {
        this.transaction = transaction;
    }

    public Attributes getAttributes ()
    {
        return attributes;
    }

    public void setAttributes (Attributes attributes)
    {
        this.attributes = attributes;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [transaction = "+transaction+", attributes = "+attributes+"]";
    }
}

