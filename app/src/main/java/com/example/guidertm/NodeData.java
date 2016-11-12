package com.example.guidertm;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by 경석 on 2016-09-14.
 */
public class NodeData implements Serializable {
    String index;
    String nodeType;
    String coordinate;
    String turntype;


    public NodeData(String index,String nodeType,String coordinate,String turntype){ //Turntype = Point 일떄
        this.index=index;
        this.nodeType=nodeType;
        this.coordinate=coordinate;
        this.turntype=turntype;
    }

}
