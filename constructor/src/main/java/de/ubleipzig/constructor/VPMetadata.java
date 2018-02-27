package de.ubleipzig.constructor;

import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import org.apache.log4j.Logger;

public class VPMetadata {
    private static Logger logger = getLogger(VPMetadata.class);
    private int groupNumber;
    private String groupTag1;
    private String groupTag2;
    private String groupTag3;
    private String groupTag4;
    private String groupTag5;
    private String groupTag6;
    private int size;
    private int sequenceBegin;


    public VPMetadata() {}

    public ArrayList<TemplateMetadata> getInfo() {
        ArrayList<TemplateMetadata> meta = new ArrayList<>();
        meta.add(new TemplateMetadata(null, null));
        return meta;
    }

    public void setGroupNumber(int gropuNumber ){
        this.groupTag1 = groupTag1;
    }

    public void setGroupTag1(String groupTag1 ){
        this.groupTag1 = groupTag1;
    }

    public void setGroupTag2(String groupTag2 ){
        this.groupTag2 = groupTag2;
    }

    public void setGroupTag3(String groupTag3){
        this.groupTag3 = groupTag3;
    }

    public void setGroupTag4(String groupTag4){
        this.groupTag4 = groupTag4;
    }

    public void setGroupTag5(String groupTag5){
        this.groupTag5 = groupTag5;
    }

    public void setGroupTag6(String groupTag6){
        this.groupTag6 = groupTag6;
    }

    public void setGroupSize(int size ){
        this.size = size;
    }

    public void setGroupImageSequenceBegin(int sequenceBegin ){
        this.sequenceBegin = sequenceBegin;
    }
}
