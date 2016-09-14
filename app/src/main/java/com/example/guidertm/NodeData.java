package com.example.guidertm;

/**
 * Created by 경석 on 2016-09-14.
 */
public class NodeData {

    String index="";
    String nodeType="";
    String coordinate="";
    String turntype="";

    NodeData(String index,String nodeType,String coordinate,String turntype){ //Turntype = Point 일떄
        this.index=index;
        this.nodeType=nodeType;
        this.coordinate=coordinate;
        this.turntype=turntype;
    }
    NodeData(String index,String nodeType,String coordinate){ //Turntype = Line 일때
        this.index=index;
        this.nodeType=nodeType;
        this.coordinate=coordinate;
    }
}
