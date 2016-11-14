package com.example.guidertm;

/**
 * Created by 경석 on 2016-11-14.
 */
public class Custom_List_Data
{
    private int     Image_ID;
    private String  Main_Title;
    private String  Sub_Title;

    public Custom_List_Data(int _Image_ID, String _Main_Title, String _Sub_Title)
    {
        this.setImage_ID(_Image_ID);
        this.setMain_Title(_Main_Title);
        this.setSub_Title(_Sub_Title);
    }

    public int getImage_ID()
    {
        return Image_ID;
    }

    public void setImage_ID(int image_ID)
    {
        Image_ID = image_ID;
    }

    public String getMain_Title()
    {
        return Main_Title;
    }

    public void setMain_Title(String main_Title)
    {
        Main_Title = main_Title;
    }

    public String getSub_Title()
    {
        return Sub_Title;
    }

    public void setSub_Title(String sub_Title)
    {
        Sub_Title = sub_Title;
    }

}

