package de.ubleipzig.constructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class CanvasBuilderTest {

    @Test
    void buildCanvasTest() {
        List<VPMetadata> inputList = processInputFile(CanvasBuilderTest.class.getResourceAsStream("/sk2-titles-tabs.csv"));
        try (Stream<Path> paths = Files.walk(Paths.get("/media/christopher/OVAUBIMG/UBiMG/images/ubleipzig_sk2"))) {
            paths.forEach(i ->  {

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<VPMetadata> processInputFile(InputStream is) {
        List<VPMetadata> inputList = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            inputList = br.lines().map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } return inputList ;
    }


    private Function<String, VPMetadata> mapToItem = (line) -> {
        String[] p = line.split("\t", -1);
        VPMetadata item = new VPMetadata();
        item.setGroupNumber(Integer.parseInt(p[0]));
        item.setGroupTag1(p[1]);
        item.setGroupTag2(p[2]);
        item.setGroupTag3(p[3]);
        item.setGroupTag4(p[4]);
        item.setGroupTag5(p[5]);
        item.setGroupTag6(p[6]);
        item.setGroupImageSequenceBegin(Integer.parseInt(p[7]));
        item.setGroupSize(Integer.parseInt(p[8]));
        return item;
    };

}
